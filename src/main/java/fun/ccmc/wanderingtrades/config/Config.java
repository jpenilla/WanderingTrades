package fun.ccmc.wanderingtrades.config;

import fun.ccmc.wanderingtrades.WanderingTrades;
import fun.ccmc.wanderingtrades.command.TabCompletions;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

public class Config {
    private final WanderingTrades plugin;

    @Getter private boolean debug;
    @Getter private boolean pluginEnabled;
    @Getter private boolean removeOriginalTrades;
    @Getter private boolean allowMultipleSets;
    @Getter private boolean refreshCommandTraders;
    @Getter private int refreshCommandTradersMinutes;
    @Getter private final HashMap<String, TradeConfig> tradeConfigs = new HashMap<>();
    @Getter private PlayerHeadConfig playerHeadConfig;

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
        removeOriginalTrades = config.getBoolean("removeOriginalTrades");
        allowMultipleSets = config.getBoolean("allowMultipleSets");
        refreshCommandTraders = config.getBoolean("refreshCommandTraders");
        refreshCommandTradersMinutes = config.getInt("refreshCommandTradersMinutes");

        loadTradeConfigs();
        plugin.setTabCompletions(new TabCompletions(plugin));
        plugin.getTabCompletions().register();
        loadPlayerHeadConfig();
    }

    private void loadPlayerHeadConfig() {
        File f = new File(plugin.getDataFolder() + "/playerheads.yml");
        if(!f.exists()) {
            plugin.saveResource("playerheads.yml", false);
        }
        f = new File(plugin.getDataFolder() + "/playerheads.yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(f);
        playerHeadConfig = new PlayerHeadConfig(data);
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

        Arrays.stream(tradeConfigFiles).forEach(f -> {
            FileConfiguration data = YamlConfiguration.loadConfiguration(f);
            tradeConfigs.put(f.getName().split("\\.")[0], new TradeConfig(plugin, data));
        });
    }
}
