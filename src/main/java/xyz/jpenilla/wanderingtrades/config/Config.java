package xyz.jpenilla.wanderingtrades.config;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

public class Config {
    private final WanderingTrades plugin;

    private boolean debug;
    private boolean enabled;
    private boolean disableCommands;
    private boolean removeOriginalTrades;
    private boolean allowMultipleSets;
    private boolean refreshCommandTraders;
    private boolean wgWhitelist;
    private boolean traderWorldWhitelist;
    private boolean updateLang;
    private boolean updateChecker = true;
    private String language;
    private List<String> wgRegionList;
    private List<String> traderWorldList;
    private int refreshCommandTradersMinutes;
    private final HashMap<String, TradeConfig> tradeConfigs = new HashMap<>();
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
        disableCommands = config.getBoolean(Fields.disableCommands);
        removeOriginalTrades = config.getBoolean(Fields.removeOriginalTrades);
        allowMultipleSets = config.getBoolean(Fields.allowMultipleSets);
        refreshCommandTraders = config.getBoolean(Fields.refreshCommandTraders);
        refreshCommandTradersMinutes = config.getInt(Fields.refreshCommandTradersMinutes);
        wgRegionList = config.getStringList(Fields.wgRegionList);
        wgWhitelist = config.getBoolean(Fields.wgWhitelist);
        traderWorldList = config.getStringList(Fields.traderWorldList);
        traderWorldWhitelist = config.getBoolean(Fields.traderWorldWhitelist);
        language = config.getString(Fields.language);
        updateLang = config.getBoolean(Fields.updateLang);
        updateChecker = config.getBoolean(Fields.updateChecker, updateChecker);

        loadTradeConfigs();
        loadPlayerHeadConfig();
    }

    public void save() {
        FileConfiguration config = plugin.getConfig();

        config.set(Fields.debug, debug);
        config.set(Fields.enabled, enabled);
        config.set(Fields.disableCommands, disableCommands);
        config.set(Fields.removeOriginalTrades, removeOriginalTrades);
        config.set(Fields.allowMultipleSets, allowMultipleSets);
        config.set(Fields.refreshCommandTraders, refreshCommandTraders);
        config.set(Fields.refreshCommandTradersMinutes, refreshCommandTradersMinutes);
        config.set(Fields.wgRegionList, wgRegionList);
        config.set(Fields.wgWhitelist, wgWhitelist);
        config.set(Fields.traderWorldList, traderWorldList);
        config.set(Fields.traderWorldWhitelist, traderWorldWhitelist);
        config.set(Fields.language, language);
        config.set(Fields.updateLang, updateLang);
        config.set(Fields.updateChecker, updateChecker);

        String path = plugin.getDataFolder() + "/config.yml";
        try {
            config.save(path);
        } catch (IOException e) {
            this.plugin.getLogger().log(Level.WARNING, "Failed to save config", e);
        }

        load();
    }

    private void loadPlayerHeadConfig() {
        File f = new File(plugin.getDataFolder() + "/playerheads.yml");
        if (!f.exists()) {
            plugin.saveResource("playerheads.yml", false);
        }
        f = new File(plugin.getDataFolder() + "/playerheads.yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(f);
        playerHeadConfig = new PlayerHeadConfig(this.plugin, data);
    }

    private void loadTradeConfigs() {
        tradeConfigs.clear();

        String path = plugin.getDataFolder() + "/trades";

        File folder = new File(path);
        if (!folder.exists()) {
            if (folder.mkdir()) {
                this.plugin.getLogger().info("Creating trades folder");
            }
        }
        if (Objects.requireNonNull(folder.listFiles()).length == 0) {
            this.plugin.getLogger().info("No trade configs found, copying example configs");
            plugin.saveResource("trades/example.yml", false);
            plugin.saveResource("trades/microblocks.yml", false);
            plugin.saveResource("trades/hermitheads.yml", false);
        }

        File[] tradeConfigFiles = folder.listFiles();

        Arrays.stream(Objects.requireNonNull(tradeConfigFiles)).forEach(file -> {
            final FileConfiguration data = YamlConfiguration.loadConfiguration(file);
            final String configName = file.getName().split("\\.")[0];
            tradeConfigs.put(configName, new TradeConfig(plugin, configName, data));
        });
    }

    public boolean debug() {
        return this.debug;
    }

    public boolean enabled() {
        return this.enabled;
    }

    public boolean disableCommands() {
        return this.disableCommands;
    }

    public boolean removeOriginalTrades() {
        return this.removeOriginalTrades;
    }

    public boolean allowMultipleSets() {
        return this.allowMultipleSets;
    }

    public boolean refreshCommandTraders() {
        return this.refreshCommandTraders;
    }

    public boolean wgWhitelist() {
        return this.wgWhitelist;
    }

    public boolean traderWorldWhitelist() {
        return this.traderWorldWhitelist;
    }

    public boolean updateLang() {
        return this.updateLang;
    }

    public boolean updateChecker() {
        return this.updateChecker;
    }

    public String language() {
        return this.language;
    }

    public List<String> wgRegionList() {
        return this.wgRegionList;
    }

    public List<String> traderWorldList() {
        return this.traderWorldList;
    }

    public int refreshCommandTradersMinutes() {
        return this.refreshCommandTradersMinutes;
    }

    public HashMap<String, TradeConfig> tradeConfigs() {
        return this.tradeConfigs;
    }

    public PlayerHeadConfig playerHeadConfig() {
        return this.playerHeadConfig;
    }

    public void enabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void removeOriginalTrades(boolean removeOriginalTrades) {
        this.removeOriginalTrades = removeOriginalTrades;
    }

    public void allowMultipleSets(boolean allowMultipleSets) {
        this.allowMultipleSets = allowMultipleSets;
    }

    public void refreshCommandTraders(boolean refreshCommandTraders) {
        this.refreshCommandTraders = refreshCommandTraders;
    }

    public void wgWhitelist(boolean wgWhitelist) {
        this.wgWhitelist = wgWhitelist;
    }

    public void wgRegionList(List<String> wgRegionList) {
        this.wgRegionList = wgRegionList;
    }

    public void refreshCommandTradersMinutes(int refreshCommandTradersMinutes) {
        this.refreshCommandTradersMinutes = refreshCommandTradersMinutes;
    }

    public static final class Fields {
        public static final String plugin = "plugin";
        public static final String debug = "debug";
        public static final String enabled = "enabled";
        public static final String disableCommands = "disableCommands";
        public static final String removeOriginalTrades = "removeOriginalTrades";
        public static final String allowMultipleSets = "allowMultipleSets";
        public static final String refreshCommandTraders = "refreshCommandTraders";
        public static final String wgWhitelist = "wgWhitelist";
        public static final String traderWorldWhitelist = "traderWorldWhitelist";
        public static final String updateLang = "updateLang";
        public static final String language = "language";
        public static final String wgRegionList = "wgRegionList";
        public static final String traderWorldList = "traderWorldList";
        public static final String refreshCommandTradersMinutes = "refreshCommandTradersMinutes";
        public static final String tradeConfigs = "tradeConfigs";
        public static final String playerHeadConfig = "playerHeadConfig";
        public static final String updateChecker = "updateChecker";
    }
}
