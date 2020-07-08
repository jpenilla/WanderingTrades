package xyz.jpenilla.wanderingtrades.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.util.Constants;

public class ProtectTradersListener implements Listener {
    private final WanderingTrades plugin;

    public ProtectTradersListener(WanderingTrades instance) {
        plugin = instance;
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent e) {
        EntityType type = e.getEntityType();
        if (type == EntityType.WANDERING_TRADER || type == EntityType.VILLAGER) {
            Entity entity = e.getEntity();
            String i = entity.getPersistentDataContainer().get(Constants.PROTECT, PersistentDataType.STRING);
            if (i != null) {
                if (!e.getDamager().hasPermission("wanderingtrades.damage")) {
                    e.setCancelled(true);
                }
            }
        }
    }
}
