package xyz.jpenilla.wanderingtrades.config;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

public final class Config {
    private final WanderingTrades plugin;

    private boolean debug;
    private boolean enabled;
    private boolean disableCommands;
    private boolean removeOriginalTrades;
    private boolean allowMultipleSets;
    private boolean refreshCommandTraders;
    private boolean preventNightInvisibility;
    private boolean wgWhitelist;
    private boolean traderWorldWhitelist;
    private boolean updateLang;
    private boolean updateChecker = true;
    private String language;
    private List<String> wgRegionList;
    private List<String> traderWorldList;
    private int refreshCommandTradersMinutes;
    private int traderSpawnNotificationRadius = -1;
    private List<String> traderSpawnNotificationCommands;

    public Config(final WanderingTrades plugin) {
        this.plugin = plugin;
        this.plugin.saveDefaultConfig();
        this.load();
    }

    public void load() {
        this.plugin.reloadConfig();
        FileConfiguration config = this.plugin.getConfig();

        this.debug = config.getBoolean(Fields.debug);
        this.enabled = config.getBoolean(Fields.enabled);
        this.disableCommands = config.getBoolean(Fields.disableCommands);
        this.removeOriginalTrades = config.getBoolean(Fields.removeOriginalTrades);
        this.allowMultipleSets = config.getBoolean(Fields.allowMultipleSets);
        this.refreshCommandTraders = config.getBoolean(Fields.refreshCommandTraders);
        this.refreshCommandTradersMinutes = config.getInt(Fields.refreshCommandTradersMinutes);
        this.preventNightInvisibility = config.getBoolean(Fields.preventNightInvisibility);
        this.wgRegionList = config.getStringList(Fields.wgRegionList);
        this.wgWhitelist = config.getBoolean(Fields.wgWhitelist);
        this.traderWorldList = config.getStringList(Fields.traderWorldList);
        this.traderWorldWhitelist = config.getBoolean(Fields.traderWorldWhitelist);
        this.language = config.getString(Fields.language);
        this.updateLang = config.getBoolean(Fields.updateLang);
        this.updateChecker = config.getBoolean(Fields.updateChecker, this.updateChecker);
        this.traderSpawnNotificationRadius = config.getInt(Fields.traderSpawnNotificationRadius, this.traderSpawnNotificationRadius);
        this.traderSpawnNotificationCommands = config.getStringList(Fields.traderSpawnNotificationCommands);
    }

    public void save() {
        final FileConfiguration config = this.plugin.getConfig();

        config.set(Fields.debug, this.debug);
        config.set(Fields.enabled, this.enabled);
        config.set(Fields.disableCommands, this.disableCommands);
        config.set(Fields.removeOriginalTrades, this.removeOriginalTrades);
        config.set(Fields.allowMultipleSets, this.allowMultipleSets);
        config.set(Fields.refreshCommandTraders, this.refreshCommandTraders);
        config.set(Fields.refreshCommandTradersMinutes, this.refreshCommandTradersMinutes);
        config.set(Fields.preventNightInvisibility, this.preventNightInvisibility);
        config.set(Fields.wgRegionList, this.wgRegionList);
        config.set(Fields.wgWhitelist, this.wgWhitelist);
        config.set(Fields.traderWorldList, this.traderWorldList);
        config.set(Fields.traderWorldWhitelist, this.traderWorldWhitelist);
        config.set(Fields.language, this.language);
        config.set(Fields.updateLang, this.updateLang);
        config.set(Fields.updateChecker, this.updateChecker);
        config.set(Fields.traderSpawnNotificationRadius, this.traderSpawnNotificationRadius);
        config.set(Fields.traderSpawnNotificationCommands, this.traderSpawnNotificationCommands);

        final String path = this.plugin.getDataFolder() + "/config.yml";
        try {
            config.save(path);
        } catch (final IOException e) {
            this.plugin.getLogger().log(Level.WARNING, "Failed to save config", e);
        }

        this.load();
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

    public boolean preventNightInvisibility() {
        return this.preventNightInvisibility;
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

    public void preventNightInvisibility(boolean preventNightInvisibility) {
        this.preventNightInvisibility = preventNightInvisibility;
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

    public int traderSpawnNotificationRadius() {
        return this.traderSpawnNotificationRadius;
    }

    public List<String> traderSpawnNotificationCommands() {
        return this.traderSpawnNotificationCommands;
    }

    public static final class Fields {
        public static final String debug = "debug";
        public static final String enabled = "enabled";
        public static final String disableCommands = "disableCommands";
        public static final String removeOriginalTrades = "removeOriginalTrades";
        public static final String allowMultipleSets = "allowMultipleSets";
        public static final String refreshCommandTraders = "refreshCommandTraders";
        public static final String preventNightInvisibility = "preventNightInvisibility";
        public static final String wgWhitelist = "wgWhitelist";
        public static final String traderWorldWhitelist = "traderWorldWhitelist";
        public static final String updateLang = "updateLang";
        public static final String language = "language";
        public static final String wgRegionList = "wgRegionList";
        public static final String traderWorldList = "traderWorldList";
        public static final String refreshCommandTradersMinutes = "refreshCommandTradersMinutes";
        public static final String updateChecker = "updateChecker";
        public static final String traderSpawnNotificationRadius = "traderSpawnNotificationRadius";
        public static final String traderSpawnNotificationCommands = "traderSpawnNotificationCommands";
    }
}
