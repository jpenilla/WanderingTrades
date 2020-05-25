package fun.ccmc.wanderingtrades.util;

import com.google.common.collect.ImmutableList;
import fun.ccmc.wanderingtrades.WanderingTrades;
import fun.ccmc.wanderingtrades.command.TabCompletions;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class Config {
    private final WanderingTrades plugin;

    @Getter private boolean debug;
    @Getter private boolean pluginEnabled;
    @Getter private boolean randomSetPerTrader;
    @Getter private final HashMap<String, TradeConfig> tradeConfigs = new HashMap<>();

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
        plugin.setTabCompletions(new TabCompletions(plugin));
        plugin.getTabCompletions().register();
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
