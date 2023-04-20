package xyz.jpenilla.wanderingtrades.config;

import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

public final class Config extends DefaultedConfig {
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
    private boolean updateChecker;
    private String language;
    private List<String> wgRegionList;
    private List<String> traderWorldList;
    private int refreshCommandTradersMinutes;
    private TraderSpawnNotificationOptions traderSpawnNotificationOptions;

    private Config(final WanderingTrades plugin) {
        super(plugin, "config.yml");
        this.reload();
    }

    @Override
    protected FileConfiguration config() {
        return this.plugin.getConfig();
    }

    public void reload() {
        this.load();
        this.save();
    }

    public void load() {
        this.plugin.saveDefaultConfig();
        this.plugin.reloadConfig();
        final FileConfiguration config = this.plugin.getConfig();

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
        this.updateChecker = config.getBoolean(Fields.updateChecker);

        final int oldTraderSpawnNotificationRadius = config.getInt(Fields.traderSpawnNotificationRadius);
        final boolean sphereTraderNotificationRadius = config.getBoolean(Fields.traderSpawnSphereNotificationRadius);
        final List<String> oldTraderSpawnNotificationCommands = config.getStringList(Fields.traderSpawnNotificationCommands);
        if (oldTraderSpawnNotificationRadius != 0 || !oldTraderSpawnNotificationCommands.isEmpty()) {
            config.set(Fields.traderSpawnNotificationRadius, null);
            config.set(Fields.traderSpawnNotificationCommands, null);
            this.traderSpawnNotificationOptions = new TraderSpawnNotificationOptions(
                oldTraderSpawnNotificationRadius != -1,
                TraderSpawnNotificationOptions.Players.parse(String.valueOf(
                    oldTraderSpawnNotificationRadius == -1 ? 500 : oldTraderSpawnNotificationRadius
                ), sphereTraderNotificationRadius),
                sphereTraderNotificationRadius,
                List.of(),
                oldTraderSpawnNotificationCommands
            );
        } else {
            this.traderSpawnNotificationOptions = TraderSpawnNotificationOptions.createFrom(
                config.getConfigurationSection(Fields.traderSpawnNotifications));
        }
    }

    public void save() {
        this.set(Fields.debug, this.debug);
        this.set(Fields.enabled, this.enabled);
        this.set(Fields.disableCommands, this.disableCommands);
        this.set(Fields.removeOriginalTrades, this.removeOriginalTrades);
        this.set(Fields.allowMultipleSets, this.allowMultipleSets);
        this.set(Fields.refreshCommandTraders, this.refreshCommandTraders);
        this.set(Fields.refreshCommandTradersMinutes, this.refreshCommandTradersMinutes);
        this.set(Fields.preventNightInvisibility, this.preventNightInvisibility);
        this.set(Fields.wgRegionList, this.wgRegionList);
        this.set(Fields.wgWhitelist, this.wgWhitelist);
        this.set(Fields.traderWorldList, this.traderWorldList);
        this.set(Fields.traderWorldWhitelist, this.traderWorldWhitelist);
        this.set(Fields.language, this.language);
        this.set(Fields.updateLang, this.updateLang);
        this.set(Fields.updateChecker, this.updateChecker);
        this.traderSpawnNotificationOptions.setTo(this, Fields.traderSpawnNotifications);

        this.plugin.saveConfig();
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

    public TraderSpawnNotificationOptions traderSpawnNotificationOptions() {
        return this.traderSpawnNotificationOptions;
    }

    public static Config load(final WanderingTrades plugin) {
        return new Config(plugin);
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
        public static final String traderSpawnSphereNotificationRadius = "traderSpawnSphereNotificationRadius";
        public static final String traderSpawnNotificationCommands = "traderSpawnNotificationCommands";
        public static final String traderSpawnNotifications = "traderSpawnNotifications";
    }
}
