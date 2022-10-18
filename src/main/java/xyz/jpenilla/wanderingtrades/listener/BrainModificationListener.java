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
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;
import xyz.jpenilla.wanderingtrades.util.Constants;
import xyz.jpenilla.wanderingtrades.util.Logging;
import xyz.jpenilla.wanderingtrades.util.VillagerReflection;

@DefaultQualifier(NonNull.class)
public final class BrainModificationListener implements Listener {
    private final WanderingTrades plugin;

    public BrainModificationListener(final WanderingTrades plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAddToWorld(final EntityAddToWorldEvent event) {
        if (event.getEntityType() == EntityType.VILLAGER) {
            this.modifyBrain((Villager) event.getEntity());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSpawn(final CreatureSpawnEvent event) {
        if (event.getEntityType() == EntityType.VILLAGER) {
            this.modifyBrain((Villager) event.getEntity());
        }
    }

    private void modifyBrain(final Villager villager) {
        final @Nullable String configName = villager.getPersistentDataContainer().get(Constants.CONFIG_NAME, PersistentDataType.STRING);
        final @Nullable TradeConfig tradeConfig = configName == null ? null : this.plugin.configManager().tradeConfigs().get(configName);
        if (configName == null || tradeConfig == null || !tradeConfig.disableHeroOfTheVillageGifts()) {
            return;
        }
        try {
            VillagerReflection.removeHeroOfTheVillageGiftBrainBehavior(villager);
        } catch (final Throwable throwable) {
            Logging.logger().warn("Failed to modify villager brain", throwable);
        }
    }
}
