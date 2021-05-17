package xyz.jpenilla.wanderingtrades.listener;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;
import xyz.jpenilla.wanderingtrades.util.Constants;
import xyz.jpenilla.wanderingtrades.util.VillagerReflection;

import java.util.logging.Level;

public class BrainModificationListener implements Listener {
    private final WanderingTrades plugin;

    public BrainModificationListener(final @NonNull WanderingTrades plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAddToWorld(final @NonNull EntityAddToWorldEvent event) {
        if (event.getEntityType() == EntityType.VILLAGER) {
            this.modifyBrain((Villager) event.getEntity());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSpawn(final @NonNull CreatureSpawnEvent event) {
        if (event.getEntityType() == EntityType.VILLAGER) {
            this.modifyBrain((Villager) event.getEntity());
        }
    }

    private void modifyBrain(final @NonNull Villager villager) {
        final String configName = villager.getPersistentDataContainer().get(Constants.CONFIG_NAME, PersistentDataType.STRING);
        final TradeConfig tradeConfig = this.plugin.config().tradeConfigs().get(configName);
        if (configName == null || tradeConfig == null || !tradeConfig.disableHeroOfTheVillageGifts()) {
            return;
        }
        try {
            VillagerReflection.removeHeroOfTheVillageGiftBrainBehavior(villager);
        } catch (final Throwable throwable) {
            this.plugin.getLogger().log(Level.WARNING, "Failed to modify villager brain", throwable);
        }
    }
}
