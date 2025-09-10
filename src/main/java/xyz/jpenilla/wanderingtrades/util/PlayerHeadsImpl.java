package xyz.jpenilla.wanderingtrades.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.SkullMeta;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import xyz.jpenilla.pluginbase.legacy.TextUtil;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.PlayerHeadConfig;

@NullMarked
final class PlayerHeadsImpl implements PlayerHeads {
    private final WanderingTrades plugin;
    private final Map<UUID, MerchantRecipe> recipes = new ConcurrentHashMap<>();
    private final Map<UUID, Long> offlineLastSeen = new ConcurrentHashMap<>();
    private final ProfileCompleter profileCompleter;
    private @Nullable WrappedTask cleanupTask;

    PlayerHeadsImpl(final WanderingTrades plugin) {
        this.plugin = plugin;
        this.profileCompleter = new ProfileCompleter(plugin);
        this.plugin.getFoliaLib().getScheduler().runTimerAsync(() -> this.profileCompleter.run(), 0L, 20L * 2L);
        this.load();
        this.scheduleCleanup();
    }

    private void scheduleCleanup() {
        if (this.cleanupTask != null) {
            this.cleanupTask.cancel();
        }
        this.cleanupTask = this.plugin.getFoliaLib().getScheduler().runTimer(
            this::removeExpired,
            20L * 60L * 10L,
            20L * 60L * 10L
        );
    }

    @Override
    public List<MerchantRecipe> randomlySelectPlayerHeads() {
        final Map<UUID, MerchantRecipe> recipes = Map.copyOf(this.recipes);
        final List<UUID> uuids = new ArrayList<>(recipes.keySet());
        final int amount = this.plugin.configManager().playerHeadConfig().getRandAmount();
        Collections.shuffle(uuids);
        final List<MerchantRecipe> selectedRecipes = new ArrayList<>();
        for (final UUID uuid : uuids) {
            if (selectedRecipes.size() >= amount) {
                break;
            }
            final MerchantRecipe recipe = recipes.get(uuid);
            final PlayerProfile profile = ((SkullMeta) recipe.getResult().getItemMeta()).getPlayerProfile();
            if (profile == null || !profile.hasTextures()) {
                // Profile is not yet complete
                continue;
            }
            selectedRecipes.add(recipe);
        }
        return selectedRecipes;
    }

    @Override
    public void handleLogin(final Player player) {
        if (!this.plugin.configManager().playerHeadConfig().playerHeadsFromServer()) {
            return;
        }

        this.offlineLastSeen.remove(player.getUniqueId());
        this.addHead(player);
    }

    @Override
    public void handleLogout(final Player player) {
        if (!this.plugin.configManager().playerHeadConfig().playerHeadsFromServer()) {
            return;
        }

        this.offlineLastSeen.put(player.getUniqueId(), System.currentTimeMillis());

        if (this.plugin.vaultHook() != null && this.plugin.configManager().playerHeadConfig().permissionWhitelist()) {
            if (!player.hasPermission(Constants.Permissions.WANDERINGTRADES_HEADAVAILABLE)) {
                this.recipes.remove(player.getUniqueId());
                this.offlineLastSeen.remove(player.getUniqueId());
            }
        }
    }

    @Override
    public void configChanged() {
        if (this.cleanupTask != null) {
            this.cleanupTask.cancel();
        }
        this.load();
        this.scheduleCleanup();
    }

    private MerchantRecipe getHeadRecipe(final OfflinePlayer player, final String name) {
        final PlayerHeadConfig playerHeadConfig = this.plugin.configManager().playerHeadConfig();
        ItemBuilder<?>.MiniMessageContext headBuilder = new HeadBuilder(player)
            .stackSize(playerHeadConfig.headsPerTrade())
            .miniMessageContext()
            .lore(playerHeadConfig.lore());
        if (playerHeadConfig.name() != null) {
            headBuilder = headBuilder
                .customName(playerHeadConfig.name().replace("{PLAYER}", name));
        }
        final ItemStack head = headBuilder.exitAndBuild();

        final MerchantRecipe recipe = new MerchantRecipe(
            head,
            0,
            playerHeadConfig.maxUses(),
            playerHeadConfig.experienceReward()
        );
        recipe.addIngredient(playerHeadConfig.ingredientOne());
        if (playerHeadConfig.ingredientTwo() != null) {
            recipe.addIngredient(playerHeadConfig.ingredientTwo());
        }

        final SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta != null) {
            final PlayerProfile profile = meta.getPlayerProfile();
            if (profile != null && !profile.hasTextures()) {
                this.profileCompleter.submitProfile(profile, updatedProfile -> {
                    meta.setPlayerProfile(this.filterProfileProperties(updatedProfile));
                    head.setItemMeta(meta);
                });
            } else if (profile != null && profile.hasTextures()) {
                meta.setPlayerProfile(this.filterProfileProperties(profile));
                head.setItemMeta(meta);
            }
        }

        return recipe;
    }

    private PlayerProfile filterProfileProperties(final PlayerProfile profile) {
        final PlayerProfile newProfile = (PlayerProfile) profile.clone();
        newProfile.clearProperties();
        for (final ProfileProperty property : profile.getProperties()) {
            if (property.getName().equals("textures")) {
                // Only copy the textures, and without the signature
                newProfile.setProperty(new ProfileProperty("textures", property.getValue()));
            }
        }
        return newProfile;
    }

    private void load() {
        this.recipes.clear();
        this.offlineLastSeen.clear();
        this.profileCompleter.clearQueue();
        if (!this.plugin.configManager().playerHeadConfig().playerHeadsFromServer()) {
            return;
        }
        for (final Player onlinePlayer : this.plugin.getServer().getOnlinePlayers()) {
            this.load(onlinePlayer);
        }
        for (final OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            if (this.recipes.containsKey(offlinePlayer.getUniqueId())) {
                continue;
            }
            this.load(offlinePlayer);
        }
    }

    private void removeExpired() {
        if (this.plugin.configManager().playerHeadConfig().days() == -1) {
            return;
        }
        for (final UUID uuid : Set.copyOf(this.offlineLastSeen.keySet())) {
            if (!this.playedRecentlyEnough(this.offlineLastSeen.get(uuid))) {
                this.offlineLastSeen.remove(uuid);
                this.recipes.remove(uuid);
            }
        }
    }

    private void load(final OfflinePlayer player) {
        final String username = player.getName();
        if (username == null || username.isBlank()) {
            return;
        }

        if (player instanceof Player onlinePlayer && onlinePlayer.isConnected()) {
            this.addHead(onlinePlayer);
        } else {
            this.addHead(player, username);
        }
    }

    private void addHead(final OfflinePlayer offlinePlayer, final String username) {
        if (this.isUsernameBlacklisted(username)) {
            return;
        }
        final long lastSeen = offlinePlayer.getLastSeen();
        if (!this.playedRecentlyEnough(lastSeen)) {
            return;
        }
        if (this.plugin.isVaultPermissions() && this.plugin.configManager().playerHeadConfig().permissionWhitelist()) {
            this.plugin.getFoliaLib().getScheduler().runAsync(asyncTask -> {
                if (this.plugin.vaultHook().permissions().playerHas(null, offlinePlayer, Constants.Permissions.WANDERINGTRADES_HEADAVAILABLE)) {
                    this.recipes.put(offlinePlayer.getUniqueId(), this.getHeadRecipe(offlinePlayer, username));
                    this.offlineLastSeen.put(offlinePlayer.getUniqueId(), lastSeen);
                }
            });
        } else {
            this.recipes.put(offlinePlayer.getUniqueId(), this.getHeadRecipe(offlinePlayer, username));
            this.offlineLastSeen.put(offlinePlayer.getUniqueId(), lastSeen);
        }
    }

    private void addHead(final Player player) {
        if (this.isUsernameBlacklisted(player.getName())) {
            return;
        }
        if (this.plugin.configManager().playerHeadConfig().permissionWhitelist()) {
            if (player.hasPermission(Constants.Permissions.WANDERINGTRADES_HEADAVAILABLE)) {
                this.recipes.put(player.getUniqueId(), this.getHeadRecipe(player, player.getName()));
            }
        } else {
            this.recipes.put(player.getUniqueId(), this.getHeadRecipe(player, player.getName()));
        }
    }

    private boolean isUsernameBlacklisted(final String username) {
        if (username.startsWith("*")) {
            // Don't even try to do anything for Geyser/Bedrock users
            return true;
        }
        return TextUtil.containsCaseInsensitive(username, this.plugin.configManager().playerHeadConfig().usernameBlacklist());
    }

    private boolean playedRecentlyEnough(final long lastPlayed) {
        final PlayerHeadConfig playerHeadConfig = this.plugin.configManager().playerHeadConfig();
        if (playerHeadConfig.days() == -1) {
            return true;
        }
        final LocalDateTime logout = Instant.ofEpochMilli(lastPlayed)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();
        final LocalDateTime cutoff = LocalDateTime.now()
            .minusDays(playerHeadConfig.days());
        return logout.isAfter(cutoff);
    }
}
