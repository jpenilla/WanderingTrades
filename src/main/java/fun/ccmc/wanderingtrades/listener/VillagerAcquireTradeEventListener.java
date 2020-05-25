package fun.ccmc.wanderingtrades.listener;

import fun.ccmc.wanderingtrades.WanderingTrades;
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
                HashMap<String, TradeConfig> m = plugin.getCfg().getTradeConfigs();
                int r = new Random().nextInt(m.size());
                String randomName = (String) m.keySet().toArray()[r];
                newTrades.addAll(m.get(randomName).getTrades(false));
            } else {
                Iterator it = plugin.getCfg().getTradeConfigs().entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    newTrades.addAll(((TradeConfig) pair.getValue()).getTrades(false));
                    it.remove();
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
