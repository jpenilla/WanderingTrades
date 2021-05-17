package xyz.jpenilla.wanderingtrades.listener;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

public class AcquireTradeListener implements Listener {
    private final WanderingTrades plugin;

    public AcquireTradeListener(WanderingTrades p) {
        plugin = p;
    }

    @EventHandler
    public void onAcquireTrade(VillagerAcquireTradeEvent e) {
        if (e.getEntityType().equals(EntityType.WANDERING_TRADER)) {
            if (plugin.config().removeOriginalTrades()) {
                e.setCancelled(true);
            }
        }
    }
}
