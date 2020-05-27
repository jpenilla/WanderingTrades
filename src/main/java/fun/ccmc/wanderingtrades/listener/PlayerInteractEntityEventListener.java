package fun.ccmc.wanderingtrades.listener;

import fun.ccmc.wanderingtrades.WanderingTrades;
import fun.ccmc.wanderingtrades.config.TradeConfig;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.persistence.PersistentDataType;

public class PlayerInteractEntityEventListener implements Listener {
    private final WanderingTrades plugin;

    public PlayerInteractEntityEventListener(WanderingTrades instance) {
        plugin = instance;
    }

    @EventHandler
    public void onClick(PlayerInteractEntityEvent e) {
        EntityType type = e.getRightClicked().getType();
        if(type == EntityType.WANDERING_TRADER || type == EntityType.VILLAGER) {
            if(plugin.getWorldGuard() != null) {
                if(plugin.getWorldGuard().passesWhiteBlackList(e.getRightClicked().getLocation())) {
                    updateTrades((AbstractVillager) e.getRightClicked());
                }
            } else {
                updateTrades((AbstractVillager) e.getRightClicked());
            }
        }
    }

    private void updateTrades(AbstractVillager entity) {
        if(entity.getTicksLived() / 20L / 60L >= plugin.getCfg().getRefreshCommandTradersMinutes()) {
            NamespacedKey key = new NamespacedKey(plugin, "wtConfig");
            String s = entity.getPersistentDataContainer().get(key, PersistentDataType.STRING);
            if(s != null) {
                TradeConfig tc = plugin.getCfg().getTradeConfigs().get(s);
                entity.setRecipes(tc.getTrades(true));
                entity.setTicksLived(1);
            }
        }
    }
}
