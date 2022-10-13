package xyz.jpenilla.wanderingtrades.config;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

@DefaultQualifier(NonNull.class)
public final class ConfigManager {
    private final WanderingTrades plugin;
    private final Map<String, TradeConfig> tradeConfigs = new ConcurrentHashMap<>();
    private @MonotonicNonNull Config config;
    private @MonotonicNonNull LangConfig lang;
    private @MonotonicNonNull PlayerHeadConfig playerHeadConfig;

    public ConfigManager(final WanderingTrades plugin) {
        this.plugin = plugin;
    }

    public void load() {
        this.config = new Config(this.plugin);
        this.lang = new LangConfig(this.plugin);
        this.loadPlayerHeadConfig();
        this.loadTradeConfigs();
    }

    public void reload() {
        this.config.load();
        this.lang.load();
        this.loadTradeConfigs();
        this.loadPlayerHeadConfig();
    }

    public Config config() {
        return this.config;
    }

    public LangConfig langConfig() {
        return this.lang;
    }

    public Map<String, TradeConfig> tradeConfigs() {
        return Collections.unmodifiableMap(this.tradeConfigs);
    }

    public PlayerHeadConfig playerHeadConfig() {
        return this.playerHeadConfig;
    }

    private void loadTradeConfigs() {
        this.tradeConfigs.clear();

        final File tradeConfigsDir = new File(this.plugin.getDataFolder(), "trades");
        if (!tradeConfigsDir.exists()) {
            if (!tradeConfigsDir.mkdirs()) {
                throw new RuntimeException("Failed to create directory '" + tradeConfigsDir + "'");
            }
        }

        if (Objects.requireNonNull(tradeConfigsDir.listFiles()).length == 0) {
            this.plugin.getLogger().info("No trade configs found, copying example configs");
            this.plugin.saveResource("trades/example.yml", false);
            this.plugin.saveResource("trades/microblocks.yml", false);
            this.plugin.saveResource("trades/hermitheads.yml", false);
        }

        for (final File file : Objects.requireNonNull(tradeConfigsDir.listFiles())) {
            final TradeConfig tradeConfig = TradeConfig.load(this.plugin, file);
            this.tradeConfigs.put(tradeConfig.configName(), tradeConfig);
        }
    }

    private void loadPlayerHeadConfig() {
        final File file = new File(this.plugin.getDataFolder(), "playerheads.yml");
        if (!file.exists()) {
            this.plugin.saveResource("playerheads.yml", false);
        }
        this.playerHeadConfig = PlayerHeadConfig.load(file);
    }
}
