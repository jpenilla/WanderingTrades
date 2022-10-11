package xyz.jpenilla.wanderingtrades.listener;

import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import xyz.jpenilla.wanderingtrades.util.Constants;

@DefaultQualifier(NonNull.class)
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
