package xyz.jpenilla.wanderingtrades.listener;

import java.util.Collection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.util.Constants;

@DefaultQualifier(NonNull.class)
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

    @EventHandler
    public void onSpawn(final CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof final WanderingTrader trader) || event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.MOUNT) {
            return;
        }
        if (trader.getPersistentDataContainer().has(Constants.TEMPORARY_BLACKLISTED, PersistentDataType.BYTE)) {
            trader.getPersistentDataContainer().remove(Constants.TEMPORARY_BLACKLISTED);
            return;
        }

        // Delay by 1 tick so entity is in world
        this.plugin.getServer().getScheduler().runTask(this.plugin, () -> this.notifyPlayers(event));

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

    private void notifyPlayers(final CreatureSpawnEvent event) {
        final int radius = this.plugin.config().traderSpawnNotificationOptions().radius();
        if (radius < 0) {
            return;
        }
        final Collection<Player> players = event.getEntity().getWorld()
                .getNearbyPlayers(event.getEntity().getLocation(), radius);
        for (final Player player : players) {
            for (String command : this.plugin.config().traderSpawnNotificationOptions().perPlayerCommands()) {
                command = command.replace("{player}", player.getName())
                        .replace("{x-pos}", String.valueOf(event.getEntity().getLocation().getBlockX()))
                        .replace("{y-pos}", String.valueOf(event.getEntity().getLocation().getBlockY()))
                        .replace("{z-pos}", String.valueOf(event.getEntity().getLocation().getBlockZ()))
                        .replace("{trader-uuid}", event.getEntity().getUniqueId().toString())
                        .replace("{distance}", String.valueOf(Math.round(player.getLocation().distance(event.getEntity().getLocation()))));
                this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), command);
            }
        }
    }
}
