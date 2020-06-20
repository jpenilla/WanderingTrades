package xyz.jpenilla.wanderingtrades.config;

import xyz.jpenilla.wanderingtrades.WanderingTrades;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@FieldNameConstants
public class Config {
    private final WanderingTrades plugin;

    @Getter @Setter
    private boolean debug;
    @Getter @Setter
    private boolean enabled;
    @Getter @Setter
    private boolean removeOriginalTrades;
    @Getter @Setter
    private boolean allowMultipleSets;
    @Getter @Setter
    private boolean refreshCommandTraders;
    @Getter @Setter
    private boolean wgWhitelist;
    @Getter
    private boolean updateLang;
    @Getter
    private String language;
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
        load();
    }

    public void load() {
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        debug = config.getBoolean(Fields.debug);
        enabled = config.getBoolean(Fields.enabled);
        removeOriginalTrades = config.getBoolean(Fields.removeOriginalTrades);
        allowMultipleSets = config.getBoolean(Fields.allowMultipleSets);
        refreshCommandTraders = config.getBoolean(Fields.refreshCommandTraders);
        refreshCommandTradersMinutes = config.getInt(Fields.refreshCommandTradersMinutes);
        wgRegionList = config.getStringList(Fields.wgRegionList);
        wgWhitelist = config.getBoolean(Fields.wgWhitelist);
        language = config.getString(Fields.language);
        updateLang = config.getBoolean(Fields.updateLang);

        loadTradeConfigs();
        loadPlayerHeadConfig();
    }

    public void save() {
        FileConfiguration config = plugin.getConfig();

        config.set(Fields.debug, debug);
        config.set(Fields.enabled, enabled);
        config.set(Fields.removeOriginalTrades, removeOriginalTrades);
        config.set(Fields.allowMultipleSets, allowMultipleSets);
        config.set(Fields.refreshCommandTraders, refreshCommandTraders);
        config.set(Fields.refreshCommandTradersMinutes, refreshCommandTradersMinutes);
        config.set(Fields.wgRegionList, wgRegionList);
        config.set(Fields.wgWhitelist, wgWhitelist);

        String path = plugin.getDataFolder() + "/config.yml";
        try {
            config.save(path);
        } catch (IOException e) {
            plugin.getLog().warn(e.getMessage());
        }

        load();
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
