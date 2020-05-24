package fun.ccmc.wanderingtrades.util;

import com.deanveloper.skullcreator.SkullCreator;
import fun.ccmc.wanderingtrades.WanderingTrades;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;

public class Config {
    private final WanderingTrades plugin;

    @Getter
    private boolean debug;
    @Getter
    private boolean pluginEnabled;
    @Getter
    private boolean randomSetPerTrader;
    @Getter
    private final static ArrayList<TradeConfig> tradeConfigs = new ArrayList<>();

    public Config(WanderingTrades instance) {
        plugin = instance;
        plugin.saveDefaultConfig();
        reload();
    }

    public void reload() {
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        debug = config.getBoolean("debug");
        pluginEnabled = config.getBoolean("enabled");
        randomSetPerTrader = config.getBoolean("randomSetPerTrader");

        loadTradeConfigs();
    }

    private void loadTradeConfigs() {
        tradeConfigs.clear();

        String path = plugin.getDataFolder() + "/trades";

        File folder = new File(path);
        if(!folder.exists()) {
            if(folder.mkdir()) {
                plugin.getLog().info("Creating trades folder");
            }
        }
        if(folder.listFiles().length == 0) {
            plugin.getLog().info("No trade configs found, copying example.yml");
            plugin.saveResource("trades/example.yml", false);
        }

        File[] tradeConfigFiles = new File(path).listFiles();

        for(File f : tradeConfigFiles) {
            FileConfiguration data = YamlConfiguration.loadConfiguration(f);
            tradeConfigs.add(new TradeConfig(plugin, data));
        }
    }
}
