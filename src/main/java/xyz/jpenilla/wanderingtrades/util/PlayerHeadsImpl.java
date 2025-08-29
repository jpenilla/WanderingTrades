package xyz.jpenilla.wanderingtrades.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
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
import org.bukkit.scheduler.BukkitTask;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import xyz.jpenilla.pluginbase.legacy.TextUtil;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.PlayerHeadConfig;

@NullMarked
final class PlayerHeadsImpl implements PlayerHeads {
    private final WanderingTrades plugin;
    private final Map<UUID, MerchantRecipe> uuidMerchantRecipeMap = new ConcurrentHashMap<>();
    private final ProfileCompleter profileCompleter;
    private @Nullable BukkitTask cleanupTask;

    PlayerHeadsImpl(final WanderingTrades plugin) {
        this.plugin = plugin;
        this.profileCompleter = new ProfileCompleter(plugin);
        this.profileCompleter.runTaskTimerAsynchronously(plugin, 0L, 20L * 2L);
        this.load();
        this.scheduleCleanup();
    }

    private void scheduleCleanup() {
        if (this.cleanupTask != null) {
            this.cleanupTask.cancel();
        }
        this.cleanupTask = this.plugin.getServer().getScheduler().runTaskTimer(
            this.plugin,
            this::removeExpired,
            20L * 60L * 10L,
            20L * 60L * 10L
        );
    }

    @Override
    public List<MerchantRecipe> randomlySelectPlayerHeads() {
        final Map<UUID, MerchantRecipe> recipes = Map.copyOf(this.uuidMerchantRecipeMap);
        final List<UUID> uuids = new ArrayList<>(recipes.keySet());
        final int amount = this.plugin.configManager().playerHeadConfig().getRandAmount();
        Collections.shuffle(uuids);
        final List<MerchantRecipe> selectedRecipes = new ArrayList<>();
        for (final UUID uuid : uuids) {
            if (selectedRecipes.size() >= amount) {
                break;
            }
            final MerchantRecipe recipe = recipes.get(uuid);
            final @Nullable PlayerProfile profile = ((SkullMeta) recipe.getResult().getItemMeta()).getPlayerProfile();
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

        this.addHead(player);
    }

    @Override
    public void handleLogout(final Player player) {
        if (!this.plugin.configManager().playerHeadConfig().playerHeadsFromServer()) {
            return;
        }

        if (this.plugin.vaultHook() != null && this.plugin.configManager().playerHeadConfig().permissionWhitelist()) {
            if (!player.hasPermission(Constants.Permissions.WANDERINGTRADES_HEADAVAILABLE)) {
                this.uuidMerchantRecipeMap.remove(player.getUniqueId());
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
            final @Nullable PlayerProfile profile = meta.getPlayerProfile();
            if (profile != null && !profile.hasTextures()) {
                this.profileCompleter.submitProfile(profile, updatedProfile -> {
                    this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
                        meta.setPlayerProfile(this.filterProfileProperties(updatedProfile));
                        head.setItemMeta(meta);
                    });
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
        this.uuidMerchantRecipeMap.clear();
        this.profileCompleter.clearQueue();
        if (!this.plugin.configManager().playerHeadConfig().playerHeadsFromServer()) {
            return;
        }
        for (final Player onlinePlayer : this.plugin.getServer().getOnlinePlayers()) {
            this.load(onlinePlayer);
        }
        for (final OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            if (this.uuidMerchantRecipeMap.containsKey(offlinePlayer.getUniqueId())) {
                continue;
            }
            this.load(offlinePlayer);
        }
    }

    private void removeExpired() {
        for (final UUID uuid : Set.copyOf(this.uuidMerchantRecipeMap.keySet())) {
            final OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            if (player.isOnline()) {
                continue;
            }
            if (!this.playedRecentlyEnough(player.getLastSeen())) {
                this.uuidMerchantRecipeMap.remove(uuid);
            }
        }
    }

    private void load(final OfflinePlayer player) {
        final @Nullable String username = player.getName();
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
        if (this.isUsernameBlacklisted(username) || !this.playedRecentlyEnough(offlinePlayer.getLastSeen())) {
            return;
        }
        if (this.plugin.isVaultPermissions() && this.plugin.configManager().playerHeadConfig().permissionWhitelist()) {
            this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
                if (this.plugin.vaultHook().permissions().playerHas(null, offlinePlayer, Constants.Permissions.WANDERINGTRADES_HEADAVAILABLE)) {
                    this.plugin.getServer().getScheduler().runTask(
                        this.plugin,
                        () -> this.uuidMerchantRecipeMap.put(offlinePlayer.getUniqueId(), this.getHeadRecipe(offlinePlayer, username))
                    );
                }
            });
        } else {
            this.uuidMerchantRecipeMap.put(offlinePlayer.getUniqueId(), this.getHeadRecipe(offlinePlayer, username));
        }
    }

    private void addHead(final Player player) {
        if (this.isUsernameBlacklisted(player.getName())) {
            return;
        }
        if (this.plugin.configManager().playerHeadConfig().permissionWhitelist()) {
            if (player.hasPermission(Constants.Permissions.WANDERINGTRADES_HEADAVAILABLE)) {
                this.uuidMerchantRecipeMap.put(player.getUniqueId(), this.getHeadRecipe(player, player.getName()));
            }
        } else {
            this.uuidMerchantRecipeMap.put(player.getUniqueId(), this.getHeadRecipe(player, player.getName()));
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
        final LocalDateTime logout = Instant.ofEpochMilli(lastPlayed)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();
        final LocalDateTime cutoff = LocalDateTime.now()
            .minusDays(playerHeadConfig.days());
        return logout.isAfter(cutoff) || playerHeadConfig.days() == -1;
    }
}
