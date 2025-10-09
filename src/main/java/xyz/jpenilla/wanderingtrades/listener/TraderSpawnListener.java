package xyz.jpenilla.wanderingtrades.listener;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.TraderSpawnNotificationOptions;
import xyz.jpenilla.wanderingtrades.util.Constants;

@NullMarked
public final class TraderSpawnListener implements Listener {
    private final WanderingTrades plugin;

    public TraderSpawnListener(final WanderingTrades plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityPortal(final EntityPortalEvent event) {
        if (event.getEntityType() == EntityType.WANDERING_TRADER) {
            event.getEntity().getPersistentDataContainer().set(Constants.TEMPORARY_BLACKLISTED, PersistentDataType.BYTE, (byte) 1);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onSpawn(final CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof final WanderingTrader trader) || event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.MOUNT) {
            return;
        }
        if (trader.getPersistentDataContainer().has(Constants.TEMPORARY_BLACKLISTED, PersistentDataType.BYTE)) {
            trader.getPersistentDataContainer().remove(Constants.TEMPORARY_BLACKLISTED);
            return;
        }

        // Delay by 1 tick so entity is in world
        this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
            if (trader.isValid()) {
                this.notifyPlayers(trader);
            }
        });

        if (this.plugin.config().traderWorldWhitelist()) {
            if (this.plugin.config().traderWorldList().contains(event.getEntity().getWorld().getName())) {
                this.plugin.tradeApplicator().addTrades(trader);
            }
        } else {
            if (!this.plugin.config().traderWorldList().contains(event.getEntity().getWorld().getName())) {
                this.plugin.tradeApplicator().addTrades(trader);
            }
        }
    }

    private void notifyPlayers(final WanderingTrader entity) {
        final TraderSpawnNotificationOptions options = this.plugin.config().traderSpawnNotificationOptions();
        if (!options.enabled()) {
            return;
        }
        for (final String command : options.commands()) {
            this.plugin.getServer().dispatchCommand(
                this.plugin.getServer().getConsoleSender(),
                applyNotifyCommandReplacements(entity, null, command)
            );
        }
        for (final Player player : options.notifyPlayers().find(entity)) {
            if (!player.hasPermission(Constants.Permissions.TRADER_SPAWN_NOTIFICATIONS)) {
                continue;
            }
            for (final String command : options.perPlayerCommands()) {
                this.plugin.getServer().dispatchCommand(
                    this.plugin.getServer().getConsoleSender(),
                    applyNotifyCommandReplacements(entity, player, command)
                );
            }
        }
    }

    private static String applyNotifyCommandReplacements(final WanderingTrader entity, final @Nullable Player player, String command) {
        if (player != null) {
            command = command.replace("{player}", player.getName());
            if (player.getWorld().equals(entity.getWorld())) {
                command = command.replace("{distance}", String.valueOf(Math.round(player.getLocation().distance(entity.getLocation()))));
            }
        }
        return command.replace("{world-name}", entity.getWorld().getName())
            .replace("{x-pos}", String.valueOf(entity.getLocation().getBlockX()))
            .replace("{y-pos}", String.valueOf(entity.getLocation().getBlockY()))
            .replace("{z-pos}", String.valueOf(entity.getLocation().getBlockZ()))
            .replace("{trader-uuid}", entity.getUniqueId().toString());
    }
}
