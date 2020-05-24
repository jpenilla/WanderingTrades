package fun.ccmc.wanderingtrades.listener;

import fun.ccmc.wanderingtrades.WanderingTrades;
import fun.ccmc.wanderingtrades.util.Config;
import fun.ccmc.wanderingtrades.util.Log;
import fun.ccmc.wanderingtrades.util.TradeConfig;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.MerchantRecipe;

import java.util.*;

public class VillagerAcquireTradeEventListener implements Listener {
    WanderingTrades plugin;
    public VillagerAcquireTradeEventListener(WanderingTrades p) {
        plugin = p;
    }

    @EventHandler
    public void onAcquireTrade(VillagerAcquireTradeEvent e) {
        plugin.getLog().debug("VillagerAcquireTradeEvent");

        if(e.getEntityType().equals(EntityType.WANDERING_TRADER) && e.getEntity().getRecipes().size() == 0) {
            AbstractVillager trader = e.getEntity();

            plugin.getLog().debug("First VillagerAcquireTradeEvent");
            plugin.getLog().debug(trader.getLocation().toString());

            ArrayList<MerchantRecipe> newTrades = new ArrayList<>();

            if(plugin.getCfg().isRandomSetPerTrader()) {
                int r = new Random().nextInt(Config.getTradeConfigs().size());
                newTrades.addAll(Config.getTradeConfigs().get(r).getTrades());
            } else {
                for(TradeConfig tc : Config.getTradeConfigs()) {
                    newTrades.addAll(tc.getTrades());
                }
            }

            trader.setRecipes(newTrades);

            if(plugin.getCfg().isDebug()) {
                int x = 0;
                while(x < trader.getRecipes().size()) {
                    plugin.getLog().debug(x + " " + trader.getRecipe(x).getIngredients().toString() + " " + trader.getRecipe(x).getResult().toString());
                    x++;
                }
            }
        }
    }
}
