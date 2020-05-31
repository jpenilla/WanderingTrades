package fun.ccmc.wanderingtrades.config;

import fun.ccmc.wanderingtrades.WanderingTrades;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Config {
    private final WanderingTrades plugin;

    @Getter @Setter
    private boolean debug;
    @Getter @Setter
    private boolean pluginEnabled;
    @Getter @Setter
    private boolean removeOriginalTrades;
    @Getter @Setter
    private boolean allowMultipleSets;
    @Getter @Setter
    private boolean refreshCommandTraders;
    @Getter @Setter
    private boolean wgWhitelist;
    @Getter @Setter
    private List<String> wgRegionList;
    @Getter @Setter
    private int refreshCommandTradersMinutes;
    @Getter
    private final HashMap<String, TradeConfig> tradeConfigs = new HashMap<>();
    @Getter @Setter
    private PlayerHeadConfig playerHeadConfig;

    public Config(WanderingTrades instance) {
        plugin = instance;
        plugin.saveDefaultConfig();
        read();
    }

    public void write() {
        FileConfiguration config = plugin.getConfig();

        config.set("debug", debug);
        config.set("enabled", pluginEnabled);
        config.set("removeOriginalTrades", removeOriginalTrades);
        config.set("allowMultipleSets", allowMultipleSets);
        config.set("refreshCommandTraders", refreshCommandTraders);
        config.set("refreshCommandTradersMinutes", refreshCommandTradersMinutes);
        config.set("wgRegionList", wgRegionList);
        config.set("wgWhitelist", wgWhitelist);

        String path = plugin.getDataFolder() + "/config.yml";
        try {
            config.save(path);
        } catch (IOException e) {
            plugin.getLog().warn(e.getMessage());
        }

        read();
    }

    public void read() {
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        debug = config.getBoolean("debug");
        pluginEnabled = config.getBoolean("enabled");
        removeOriginalTrades = config.getBoolean("removeOriginalTrades");
        allowMultipleSets = config.getBoolean("allowMultipleSets");
        refreshCommandTraders = config.getBoolean("refreshCommandTraders");
        refreshCommandTradersMinutes = config.getInt("refreshCommandTradersMinutes");
        wgRegionList = config.getStringList("wgRegionList");
        wgWhitelist = config.getBoolean("wgWhitelist");

        loadTradeConfigs();
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
