package xyz.jpenilla.wanderingtrades.listener;

import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.util.Constants;

public class ProtectTradersListener implements Listener {

    public ProtectTradersListener(WanderingTrades instance) {
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent e) {
        final Entity entity = e.getEntity();
        if (entity instanceof AbstractVillager) {
            if (entity.getPersistentDataContainer().has(Constants.PROTECT, PersistentDataType.STRING)) {
                if (!e.getDamager().hasPermission("wanderingtrades.damage")) {
                    e.setCancelled(true);
                }
            }
        }
    }
}
