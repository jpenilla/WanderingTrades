package xyz.jpenilla.wanderingtrades.listener;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.jspecify.annotations.NullMarked;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

@NullMarked
public final class AcquireTradeListener implements Listener {
    private final WanderingTrades plugin;

    public AcquireTradeListener(final WanderingTrades plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAcquireTrade(final VillagerAcquireTradeEvent event) {
        if (event.getEntityType().equals(EntityType.WANDERING_TRADER)) {
            if (this.plugin.config().removeOriginalTrades()) {
                event.setCancelled(true);
            }
        }
    }
}
