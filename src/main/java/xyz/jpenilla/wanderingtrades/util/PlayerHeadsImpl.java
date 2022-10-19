package xyz.jpenilla.wanderingtrades.util;

import io.papermc.lib.PaperLib;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitTask;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import xyz.jpenilla.pluginbase.legacy.TextUtil;
import xyz.jpenilla.pluginbase.legacy.itembuilder.HeadBuilder;
import xyz.jpenilla.pluginbase.legacy.itembuilder.ItemBuilder;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.PlayerHeadConfig;

@DefaultQualifier(NonNull.class)
final class PlayerHeadsImpl implements PlayerHeads {
    private final WanderingTrades plugin;
    private final Map<UUID, MerchantRecipe> uuidMerchantRecipeMap = new ConcurrentHashMap<>();
    private final @Nullable ProfileCompleter profileCompleter;
    private @Nullable BukkitTask updateTask;

    PlayerHeadsImpl(final WanderingTrades plugin) {
        this.plugin = plugin;
        this.profileCompleter = PaperLib.isPaper() ? new ProfileCompleter(plugin) : null;
        if (this.profileCompleter != null) {
            this.profileCompleter.runTaskTimerAsynchronously(plugin, 0L, 40L);
        }
        this.scheduleCacheUpdateTimer();
    }

    private void cancelCacheUpdateTimer() {
        if (this.updateTask != null) {
            this.updateTask.cancel();
            this.updateTask = null;
        }
    }

    @Override
    public List<MerchantRecipe> randomlySelectPlayerHeads() {
        final Map<UUID, MerchantRecipe> recipes = Map.copyOf(this.uuidMerchantRecipeMap);
        final List<UUID> uuids = new ArrayList<>(recipes.keySet());
        final int amount = this.plugin.configManager().playerHeadConfig().getRandAmount();
        final Collection<UUID> selected = new ArrayList<>();

        if (uuids.size() < 2) {
            selected.addAll(uuids);
        } else {
            for (int i = 0; selected.size() < amount; i++) {
                final UUID uuid = uuids.get(ThreadLocalRandom.current().nextInt(0, uuids.size() - 1));
                if (selected.contains(uuid)) {
                    continue;
                }
                selected.add(uuid);
                if (i > 10 * amount) {
                    break;
                }
            }
        }

        return selected.stream().map(recipes::get).toList();
    }

    @Override
    public void handleLogin(final Player player) {
        if (!this.plugin.configManager().playerHeadConfig().playerHeadsFromServer()) {
            return;
        }

        this.addHeadIfPermissible(player);
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
        this.cancelCacheUpdateTimer();
        this.scheduleCacheUpdateTimer();
    }

    private void scheduleCacheUpdateTimer() {
        if (this.plugin.configManager().playerHeadConfig().playerHeadsFromServer() && this.updateTask == null) {
            this.updateTask = this.plugin.getServer().getScheduler()
                .runTaskTimer(this.plugin, () -> this.load(), 0L, 864000L);
        }
    }

    private MerchantRecipe getHeadRecipe(final OfflinePlayer player, final String name) {
        final PlayerHeadConfig playerHeadConfig = this.plugin.configManager().playerHeadConfig();
        ItemBuilder<?, ?>.MiniMessageContext headBuilder = new HeadBuilder(player.getUniqueId())
            .stackSize(playerHeadConfig.headsPerTrade())
            .miniMessageContext()
            .lore(playerHeadConfig.lore());
        if (playerHeadConfig.name() != null) {
            headBuilder = headBuilder
                .customName(playerHeadConfig.name().replace("{PLAYER}", name));
        }
        final ItemStack head = headBuilder.exitAndBuild();

        if (this.profileCompleter != null) {
            final SkullMeta meta = (SkullMeta) head.getItemMeta();
            if (meta != null) {
                this.profileCompleter.submitSkullMeta(meta);
            }
        }

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
        return recipe;
    }

    private void load() {
        this.uuidMerchantRecipeMap.clear();
        if (this.profileCompleter != null) {
            this.profileCompleter.clearQueue();
        }
        for (final OfflinePlayer offlinePlayer : List.copyOf(Arrays.asList(Bukkit.getOfflinePlayers()))) {
            try {
                this.load(offlinePlayer);
            } catch (final Exception ex) {
                Logging.logger().warn("Failed to load recipe for OfflinePlayer with UUID '{}'", offlinePlayer.getUniqueId(), ex);
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void load(final OfflinePlayer offlinePlayer) {
        final PlayerHeadConfig playerHeadConfig = this.plugin.configManager().playerHeadConfig();
        final @Nullable String username = offlinePlayer.getName();
        final boolean usernameAllowed = username != null && !username.isEmpty()
            && !TextUtil.containsCaseInsensitive(username, playerHeadConfig.usernameBlacklist());
        if (!usernameAllowed) {
            return;
        }

        final LocalDateTime logout = Instant.ofEpochMilli(offlinePlayer.getLastPlayed())
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();
        final LocalDateTime cutoff = LocalDateTime.now()
            .minusDays(playerHeadConfig.days());
        final boolean time = logout.isAfter(cutoff) || playerHeadConfig.days() == -1;
        if (!time) {
            return;
        }

        this.addOfflineHead(offlinePlayer, username);
    }

    private void addOfflineHead(final OfflinePlayer offlinePlayer, final String username) {
        if (this.plugin.isVaultPermissions()) {
            if (this.plugin.configManager().playerHeadConfig().permissionWhitelist()) {
                this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
                    if (this.plugin.vaultHook().permissions().playerHas(null, offlinePlayer, Constants.Permissions.WANDERINGTRADES_HEADAVAILABLE)) {
                        this.plugin.getServer().getScheduler().runTask(
                            this.plugin,
                            () -> this.uuidMerchantRecipeMap.put(offlinePlayer.getUniqueId(), this.getHeadRecipe(offlinePlayer, username))
                        );
                    }
                });
                return;
            }
        }
        this.uuidMerchantRecipeMap.put(offlinePlayer.getUniqueId(), this.getHeadRecipe(offlinePlayer, username));
    }

    private void addHead(final Player player) {
        if (!this.uuidMerchantRecipeMap.containsKey(player.getUniqueId())
            && !TextUtil.containsCaseInsensitive(player.getName(), this.plugin.configManager().playerHeadConfig().usernameBlacklist())) {
            this.uuidMerchantRecipeMap.put(player.getUniqueId(), this.getHeadRecipe(player, player.getName()));
        }
    }

    private void addHeadIfPermissible(final Player player) {
        if (this.plugin.isVaultPermissions()) {
            if (this.plugin.configManager().playerHeadConfig().permissionWhitelist()) {
                if (player.hasPermission(Constants.Permissions.WANDERINGTRADES_HEADAVAILABLE)) {
                    this.addHead(player);
                }
                return;
            }
        }
        this.addHead(player);
    }
}
