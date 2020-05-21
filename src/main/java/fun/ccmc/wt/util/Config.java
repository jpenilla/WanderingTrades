package fun.ccmc.wt.util;

import com.deanveloper.skullcreator.SkullCreator;
import fun.ccmc.wt.WanderingTrades;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Config {
    private static boolean debug, pluginEnabled, randomSetPerTrader;
    private static ArrayList<Boolean> randomized = new ArrayList<>();
    private static ArrayList<Boolean> enabled = new ArrayList<>();
    private static ArrayList<Integer> randomAmount = new ArrayList<>();
    private static ArrayList<List<MerchantRecipe>> trades = new ArrayList<>();

    public static void init(WanderingTrades plugin) {
        plugin.saveDefaultConfig();
        reload(plugin);
    }

    public static void reload(JavaPlugin plugin) {
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        debug = config.getBoolean("debug");
        pluginEnabled = config.getBoolean("enabled");
        randomSetPerTrader = config.getBoolean("randomSetPerTrader");

        loadConfigs(plugin);
    }

    private static void loadConfigs(JavaPlugin plugin) {
        trades.clear();
        randomized.clear();
        randomAmount.clear();
        enabled.clear();

        String path = plugin.getDataFolder() + "/trades";
        File folder = new File(path);

        if(!folder.exists()) {
            folder.mkdir();
        }

        if(folder.listFiles().length == 0) {
            plugin.saveResource("trades/example.yml", false);
        }

        File[] tradeConfigs = new File(path).listFiles();

        for(File f : tradeConfigs) {
            FileConfiguration data = YamlConfiguration.loadConfiguration(f);
            loadRecipes(data);
        }
    }

    private static void loadRecipes(FileConfiguration config) {
        randomized.add(config.getBoolean("randomized"));
        randomAmount.add(config.getInt("randomAmount"));
        enabled.add(config.getBoolean("enabled"));

        List<MerchantRecipe> rs = new ArrayList<>();

        String parent = "trades";
        for(String key : config.getConfigurationSection(parent).getKeys(false)) {
            String prefix = parent + "." + key + ".";

            ItemStack result = getStack(config, prefix + "result");

            int m = 1;
            if (config.getInt(prefix + "maxUses") != 0) {
                m = config.getInt(prefix + "maxUses");
            }
            MerchantRecipe recipe = new MerchantRecipe(result, 0, m, config.getBoolean(prefix + "experienceReward"));

            int i = 1;
            while( i < 3 ) {
                ItemStack stack = getStack(config, prefix + "ingredients." + i);
                if(stack != null) {
                    recipe.addIngredient(stack);
                }
                i++;
            }

            rs.add(recipe);
        }

        trades.add(rs);
    }

    private static ItemStack getStack(FileConfiguration config, String key) {
        ItemStack is = null;

        if(config.getString(key + ".material") != null) {
            if(config.getString(key + ".material").contains("head-")) {
                is = SkullCreator.withBase64(new ItemStack(Material.PLAYER_HEAD, config.getInt(key + ".amount")), config.getString(key + ".material").replace("head-", ""));
            } else {
                if(Material.getMaterial(config.getString(key + ".material").toUpperCase()) != null) {
                    is = new ItemStack(Material.getMaterial(config.getString(key + ".material").toUpperCase()), config.getInt(key + ".amount"));
                } else {
                    Log.warn(config.getString(key + ".material") + " is not a valid material");
                }
            }

            ItemMeta iMeta = is.getItemMeta();

            if(!config.getString(key + ".customname").equals("NONE")) {
                iMeta.setDisplayName(TextFormatting.colorize(config.getString(key + ".customname")));
            }

            for (String s : config.getStringList(key + ".enchantments")) {
                if(s.contains(":")) {
                    String[] e = s.split(":");
                    Enchantment ench = EnchantmentWrapper.getByKey(NamespacedKey.minecraft(e[0].toLowerCase()));
                    if(ench != null) {
                        iMeta.addEnchant(ench, Integer.parseInt(e[1]), true);
                    }
                }
            }

            is.setItemMeta(iMeta);
        }
        return is;
    }

    public static boolean getDebug() {
        return debug;
    }

    public static boolean getPluginEnabled() {
        return pluginEnabled;
    }

    public static ArrayList<Boolean> getRandomized() {
        return randomized;
    }

    public static ArrayList<Integer> getRandomAmount() {
        return randomAmount;
    }

    public static List<List<MerchantRecipe>> getTrades() {
        return trades;
    }

    public static ArrayList<Boolean> getEnabled() {
        return enabled;
    }

    public static boolean getRandomSetPerTrader() {
        return randomSetPerTrader;
    }
}
