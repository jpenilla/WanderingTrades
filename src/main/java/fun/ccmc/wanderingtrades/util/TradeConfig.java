package fun.ccmc.wanderingtrades.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TradeConfig {

    private final boolean randomized;
    private final boolean enabled;
    private final int randomAmount;
    private final List<MerchantRecipe> trades;

    public TradeConfig(FileConfiguration config) {
        List<MerchantRecipe> rs = new ArrayList<>();

        String parent = "trades";
        for(String key : config.getConfigurationSection(parent).getKeys(false)) {
            String prefix = parent + "." + key + ".";

            ItemStack result = Config.getStack(config, prefix + "result");

            int m = 1;
            if (config.getInt(prefix + "maxUses") != 0) {
                m = config.getInt(prefix + "maxUses");
            }
            MerchantRecipe recipe = new MerchantRecipe(result, 0, m, config.getBoolean(prefix + "experienceReward"));

            int i = 1;
            while( i < 3 ) {
                ItemStack stack = Config.getStack(config, prefix + "ingredients." + i);
                if(stack != null) {
                    recipe.addIngredient(stack);
                }
                i++;
            }

            rs.add(recipe);
        }

        trades = rs;
        randomized = config.getBoolean("randomized");
        randomAmount = config.getInt("randomAmount");
        enabled = config.getBoolean("enabled");
    }

    private List<MerchantRecipe> pickTrades(List<MerchantRecipe> lst, int n) {
        List<MerchantRecipe> copy = new LinkedList<>(lst);
        Collections.shuffle(copy);
        return copy.subList(0, n);
    }

    public ArrayList<MerchantRecipe> getTrades() {
        ArrayList<MerchantRecipe> h = new ArrayList<>();

        if(enabled) {
            if(randomized) {
                h.addAll(pickTrades(trades, randomAmount));
            } else {
                h.addAll(trades);
            }
        }

        return h;
    }
}
