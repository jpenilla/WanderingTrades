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
        trades = readTrades(config);
        randomized = config.getBoolean("randomized");
        randomAmount = config.getInt("randomAmount");
        enabled = config.getBoolean("enabled");
    }

    private ArrayList<MerchantRecipe> readTrades(FileConfiguration config) {
        ArrayList<MerchantRecipe> tradeList = new ArrayList<>();
        String parent = "trades";
        for(String key : config.getConfigurationSection(parent).getKeys(false)) {
            String prefix = parent + "." + key + ".";

            int maxUses = 1;
            if (config.getInt(prefix + "maxUses") != 0) {
                maxUses = config.getInt(prefix + "maxUses");
            }

            ItemStack result = Config.getStack(config, prefix + "result");
            MerchantRecipe recipe = new MerchantRecipe(result, 0, maxUses, config.getBoolean(prefix + "experienceReward"));

            int ingredientNumber = 1;
            while( ingredientNumber < 3 ) {
                ItemStack ingredient = Config.getStack(config, prefix + "ingredients." + ingredientNumber);
                if(ingredient != null) {
                    recipe.addIngredient(ingredient);
                }
                ingredientNumber++;
            }

            tradeList.add(recipe);
        }
        return tradeList;
    }

    private List<MerchantRecipe> pickTrades(List<MerchantRecipe> lst, int amount) {
        List<MerchantRecipe> copy = new LinkedList<>(lst);
        Collections.shuffle(copy);
        return copy.subList(0, amount);
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
