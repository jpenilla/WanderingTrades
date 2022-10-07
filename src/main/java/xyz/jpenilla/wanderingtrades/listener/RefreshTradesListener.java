package xyz.jpenilla.wanderingtrades.listener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.bukkit.Material;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.jetbrains.annotations.Nullable;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.compatability.WorldGuardHook;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;
import xyz.jpenilla.wanderingtrades.util.Constants;

@DefaultQualifier(NonNull.class)
public final class RefreshTradesListener implements Listener {
    private final WanderingTrades plugin;

    public RefreshTradesListener(final WanderingTrades instance) {
        this.plugin = instance;
    }

    @EventHandler
    public void onClick(final PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof AbstractVillager abstractVillager)) {
            return;
        }

        // Block Name Tags
        final PlayerInventory playerInventory = event.getPlayer().getInventory();
        if (playerInventory.getItemInMainHand().getType() == Material.NAME_TAG
            || playerInventory.getItemInOffHand().getType() == Material.NAME_TAG) {
            final PersistentDataContainer persistentDataContainer = abstractVillager.getPersistentDataContainer();
            if (persistentDataContainer.has(Constants.CONFIG_NAME, PersistentDataType.STRING)
                || persistentDataContainer.has(Constants.PROTECT, PersistentDataType.STRING)) {
                event.setCancelled(true);
            }
        }

        // Update trades
        final @Nullable WorldGuardHook worldGuard = this.plugin.worldGuardHook();
        if (worldGuard == null || worldGuard.passesWhiteBlackList(abstractVillager.getLocation())) {
            this.updateTrades(abstractVillager);
        }
    }

    private void updateTrades(final AbstractVillager abstractVillager) {
        final PersistentDataContainer persistentDataContainer = abstractVillager.getPersistentDataContainer();
        final @Nullable String configName = persistentDataContainer.get(Constants.CONFIG_NAME, PersistentDataType.STRING);
        final boolean refreshNatural = persistentDataContainer.has(Constants.REFRESH_NATURAL, PersistentDataType.STRING);
        if (configName == null && !refreshNatural) {
            return;
        }

        final long timeAtPreviousRefresh = persistentDataContainer.getOrDefault(Constants.LAST_REFRESH, PersistentDataType.LONG, 0L);
        final LocalDateTime nextAllowedRefresh = Instant.ofEpochMilli(timeAtPreviousRefresh)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
            .plusMinutes(this.plugin.config().refreshCommandTradersMinutes());

        if (timeAtPreviousRefresh == 0L || LocalDateTime.now().isAfter(nextAllowedRefresh)) {
            if (configName != null) {
                final TradeConfig tradeConfig = this.plugin.config().tradeConfigs().get(configName);
                abstractVillager.setRecipes(tradeConfig.getTrades(true));
            }
            if (refreshNatural && abstractVillager instanceof WanderingTrader) {
                this.plugin.tradeApplicator().refreshTrades((WanderingTrader) abstractVillager);
            }
            persistentDataContainer.set(Constants.LAST_REFRESH, PersistentDataType.LONG, System.currentTimeMillis());
        }
    }
}
