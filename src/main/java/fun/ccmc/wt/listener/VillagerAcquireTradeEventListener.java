package fun.ccmc.wt.listener;

import fun.ccmc.wt.WanderingTrades;
import fun.ccmc.wt.util.Config;
import fun.ccmc.wt.util.Log;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;

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

            if(trader.getRecipes().size() == 0) {
                Log.debug("First VillagerAcquireTradeEvent");

                trader.setRecipes(Config.getTrades());

                int x = 0;
                while(x < trader.getRecipes().size()) {
                    Log.debug(x + " " + trader.getRecipe(x).getIngredients().toString() + " " + trader.getRecipe(x).getResult().toString());
                    x++;
                }
            }
        }
    }
}
