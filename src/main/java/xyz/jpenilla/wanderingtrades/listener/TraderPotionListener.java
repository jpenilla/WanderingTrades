package xyz.jpenilla.wanderingtrades.listener;

import io.papermc.lib.PaperLib;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.persistence.PersistentDataType;
import xyz.jpenilla.wanderingtrades.util.Constants;

public class TraderPotionListener implements Listener {

    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent e) {
        if (!PaperLib.isPaper()) {
            return;
        }

        final Entity entity = e.getEntity();
        if (entity.getType() == EntityType.WANDERING_TRADER && e.getCause() == EntityPotionEffectEvent.Cause.POTION_DRINK && e.getAction() == EntityPotionEffectEvent.Action.ADDED) {
            if (entity.getPersistentDataContainer().has(Constants.PREVENT_INVISIBILITY, PersistentDataType.STRING)) {
                final WanderingTrader wanderingTrader = (WanderingTrader) entity;
                wanderingTrader.setCanDrinkPotion(false);
                wanderingTrader.clearActiveItem();
                e.setCancelled(true);
            }
        }
    }
}
