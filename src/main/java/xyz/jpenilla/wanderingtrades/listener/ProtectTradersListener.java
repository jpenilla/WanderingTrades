package xyz.jpenilla.wanderingtrades.listener;

import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NullMarked;
import xyz.jpenilla.wanderingtrades.util.Constants;

@NullMarked
public final class ProtectTradersListener implements Listener {
    @EventHandler
    public void onDamageByEntity(final EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity();
        if (entity instanceof AbstractVillager) {
            if (entity.getPersistentDataContainer().has(Constants.PROTECT, PersistentDataType.STRING)) {
                if (!event.getDamager().hasPermission("wanderingtrades.damage")) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
