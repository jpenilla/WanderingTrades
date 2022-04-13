package xyz.jpenilla.wanderingtrades.listener;

import io.papermc.lib.PaperLib;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.util.Constants;
import xyz.jpenilla.wanderingtrades.util.VillagerReflection;

import java.util.logging.Level;

public class TraderPotionListener implements Listener {
    private final WanderingTrades plugin;

    public TraderPotionListener(final @NonNull WanderingTrades plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent e) {
        final Entity entity = e.getEntity();
        if (entity.getType() == EntityType.WANDERING_TRADER && e.getCause() == EntityPotionEffectEvent.Cause.POTION_DRINK && e.getAction() == EntityPotionEffectEvent.Action.ADDED) {
            if (entity.getPersistentDataContainer().has(Constants.PREVENT_INVISIBILITY, PersistentDataType.STRING)) {
                final WanderingTrader wanderingTrader = (WanderingTrader) entity;
                if (PaperLib.isPaper()) {
                    wanderingTrader.setCanDrinkPotion(false);
                    wanderingTrader.clearActiveItem();
                } else {
                    try {
                        VillagerReflection.removeWanderingTraderInvisibilityGoal(wanderingTrader);
                    } catch (final Throwable throwable) {
                        this.plugin.getLogger().log(Level.WARNING, "Failed to disable potion drinking goal", throwable);
                    }
                }
                e.setCancelled(true);
            }
        }
    }
}
