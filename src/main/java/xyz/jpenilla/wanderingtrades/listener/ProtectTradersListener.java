package xyz.jpenilla.wanderingtrades.listener;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

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
            NamespacedKey wtProtect = new NamespacedKey(plugin, "wtProtect");
            String i = entity.getPersistentDataContainer().get(wtProtect, PersistentDataType.STRING);
            if (i != null) {
                if (!e.getDamager().hasPermission("wanderingtrades.damage")) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onEntityInteractEntity(PlayerInteractEntityEvent e) {
        NamespacedKey wtConfig = new NamespacedKey(plugin, "wtConfig");
        if (e.getRightClicked().getPersistentDataContainer().has(wtConfig, PersistentDataType.STRING)) {
            PlayerInventory p = e.getPlayer().getInventory();
            if ((p.getItemInMainHand().getType() == Material.NAME_TAG) || (p.getItemInOffHand().getType() == Material.NAME_TAG)) {
                e.setCancelled(true);
            }
        }
    }
}
