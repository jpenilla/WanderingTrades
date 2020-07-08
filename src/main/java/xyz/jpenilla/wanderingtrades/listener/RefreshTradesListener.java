package xyz.jpenilla.wanderingtrades.listener;

import org.bukkit.Material;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;
import xyz.jpenilla.wanderingtrades.util.Constants;

public class RefreshTradesListener implements Listener {
    private final WanderingTrades plugin;

    public RefreshTradesListener(WanderingTrades instance) {
        plugin = instance;
    }

    @EventHandler
    public void onClick(PlayerInteractEntityEvent e) {
        EntityType type = e.getRightClicked().getType();
        if (type == EntityType.WANDERING_TRADER || type == EntityType.VILLAGER) {
            // Block Name Tags
            PersistentDataContainer persistentDataContainer = e.getRightClicked().getPersistentDataContainer();
            if (persistentDataContainer.has(Constants.CONFIG, PersistentDataType.STRING) || persistentDataContainer.has(Constants.PROTECT, PersistentDataType.STRING)) {
                PlayerInventory p = e.getPlayer().getInventory();
                if ((p.getItemInMainHand().getType() == Material.NAME_TAG) || (p.getItemInOffHand().getType() == Material.NAME_TAG)) {
                    e.setCancelled(true);
                }
            }

            // Update trades
            if (plugin.getWorldGuard() != null) {
                if (plugin.getWorldGuard().passesWhiteBlackList(e.getRightClicked().getLocation())) {
                    updateTrades((AbstractVillager) e.getRightClicked());
                }
            } else {
                updateTrades((AbstractVillager) e.getRightClicked());
            }
        }
    }

    private void updateTrades(AbstractVillager entity) {
        if (entity.getTicksLived() / 20L / 60L >= plugin.getCfg().getRefreshCommandTradersMinutes()) {
            PersistentDataContainer persistentDataContainer = entity.getPersistentDataContainer();
            if (persistentDataContainer.has(Constants.CONFIG, PersistentDataType.STRING) || persistentDataContainer.has(Constants.REFRESH_NATURAL, PersistentDataType.STRING)) {
                String config = persistentDataContainer.get(Constants.CONFIG, PersistentDataType.STRING);
                if (config != null) {
                    TradeConfig tc = plugin.getCfg().getTradeConfigs().get(config);
                    entity.setRecipes(tc.getTrades(true));
                }
                if (persistentDataContainer.has(Constants.REFRESH_NATURAL, PersistentDataType.STRING) && EntityType.WANDERING_TRADER.equals(entity.getType())) {
                    plugin.getListeners().getEntitySpawnListener().addTrades((WanderingTrader) entity, true);
                }
                entity.setTicksLived(1);
            }
        }
    }
}
