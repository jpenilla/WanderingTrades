package fun.ccmc.wt.listener;

import fun.ccmc.wt.WanderingTrades;
import fun.ccmc.wt.util.Config;
import fun.ccmc.wt.util.Log;
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
        Log.debug("VillagerAcquireTradeEvent");

        if(e.getEntityType().equals(EntityType.WANDERING_TRADER) && e.getEntity().getRecipes().size() == 0) {
            AbstractVillager trader = e.getEntity();

            Log.debug("First VillagerAcquireTradeEvent");
            Log.debug(trader.getLocation().toString());

            ArrayList<MerchantRecipe> j = new ArrayList<>();

            if(Config.getRandomSetPerTrader()) {
                int r = new Random().nextInt(Config.getTrades().size());
                j.addAll(addTrades(r));
            } else {
                for(List<MerchantRecipe> l : Config.getTrades()) {
                    int index = Config.getTrades().indexOf(l);
                    j.addAll(addTrades(index));
                }
            }

            trader.setRecipes(j);

            if(Config.getDebug()) {
                int x = 0;
                while(x < trader.getRecipes().size()) {
                    Log.debug(x + " " + trader.getRecipe(x).getIngredients().toString() + " " + trader.getRecipe(x).getResult().toString());
                    x++;
                }
            }
        }
    }

    private static List<MerchantRecipe> pickTrades(List<MerchantRecipe> lst, int n) {
        List<MerchantRecipe> copy = new LinkedList<>(lst);
        Collections.shuffle(copy);
        return copy.subList(0, n);
    }

    private static ArrayList addTrades(int index) {
        ArrayList h = new ArrayList();

        if(Config.getEnabled().get(index)) {
            if(Config.getRandomized().get(index)) {
                h.addAll(pickTrades(Config.getTrades().get(index), Config.getRandomAmount().get(index)));
            } else {
                h.addAll(Config.getTrades().get(index));
            }
        }

        return h;
    }

}
