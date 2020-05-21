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

    private static ArrayList<TradeConfig> tradeConfigs = new ArrayList<>();

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

        loadTradeConfigs(plugin);
    }

    private static void loadTradeConfigs(JavaPlugin plugin) {
        tradeConfigs.clear();

        String path = plugin.getDataFolder() + "/trades";

        File folder = new File(path);
        if(!folder.exists()) {
            if(folder.mkdir()) {
                Log.info("Creating trades folder");
            }
        }
        if(folder.listFiles().length == 0) {
            Log.info("No trade configs found, copying example.yml");
            plugin.saveResource("trades/example.yml", false);
        }

        File[] tradeConfigFiles = new File(path).listFiles();

        for(File f : tradeConfigFiles) {
            FileConfiguration data = YamlConfiguration.loadConfiguration(f);
            tradeConfigs.add(new TradeConfig(data));
        }
    }

    public static ItemStack getStack(FileConfiguration config, String key) {
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

    public static boolean getRandomSetPerTrader() {
        return randomSetPerTrader;
    }

    public static ArrayList<TradeConfig> getTradeConfigs() {
        return tradeConfigs;
    }
}
