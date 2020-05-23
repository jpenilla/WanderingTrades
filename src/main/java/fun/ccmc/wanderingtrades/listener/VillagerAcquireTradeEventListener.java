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

import java.util.ArrayList;
import java.util.Random;

public class VillagerAcquireTradeEventListener implements Listener {
    WanderingTrades plugin;
    public VillagerAcquireTradeEventListener(WanderingTrades p) {
        plugin = p;
    }

    @EventHandler
    public void onAcquireTrade(VillagerAcquireTradeEvent e) {
        Log.debug("VillagerAcquireTradeEvent");

        if(e.getEntityType().equals(EntityType.WANDERING_TRADER) && e.getEntity().getRecipes().size() == 0) {
            AbstractVillager trader = e.getEntity();

            Log.debug("First VillagerAcquireTradeEvent");
            Log.debug(trader.getLocation().toString());

            ArrayList<MerchantRecipe> newTrades = new ArrayList<>();

            if(Config.getRandomSetPerTrader()) {
                int r = new Random().nextInt(Config.getTradeConfigs().size());
                newTrades.addAll(Config.getTradeConfigs().get(r).getTrades());
            } else {
                for(TradeConfig tc : Config.getTradeConfigs()) {
                    newTrades.addAll(tc.getTrades());
                }
            }

            trader.setRecipes(newTrades);

            if(Config.getDebug()) {
                int x = 0;
                while(x < trader.getRecipes().size()) {
                    Log.debug(x + " " + trader.getRecipe(x).getIngredients().toString() + " " + trader.getRecipe(x).getResult().toString());
                    x++;
                }
            }
        }
    }
}
