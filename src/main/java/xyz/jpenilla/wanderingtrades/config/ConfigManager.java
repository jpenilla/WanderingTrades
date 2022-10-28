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
    private @MonotonicNonNull PlayerHeadConfig playerHeadConfig;

    public ConfigManager(final WanderingTrades plugin) {
        this.plugin = plugin;
    }

    public void load() {
        this.config = Config.load(this.plugin);
        this.loadMessages();
        this.playerHeadConfig = PlayerHeadConfig.load(this.plugin);
        this.loadTradeConfigs();
    }

    public void reload() {
        this.config.reload();
        this.loadMessages();
        this.playerHeadConfig.reload();
        this.loadTradeConfigs();
    }

    public Config config() {
        return this.config;
    }

    public Map<String, TradeConfig> tradeConfigs() {
        return Collections.unmodifiableMap(this.tradeConfigs);
    }

    public PlayerHeadConfig playerHeadConfig() {
        return this.playerHeadConfig;
    }

    private void loadMessages() {
        final File file = new File(this.plugin.getDataFolder() + "/lang/" + this.config.language() + ".yml");
        if (this.config.updateLang() || !file.exists()) {
            try {
                this.plugin.saveResource("lang/" + this.config.language() + ".yml", true);
            } catch (final IllegalArgumentException ex) {
                this.plugin.getLogger().warning("Invalid/missing language file name");
            }
        }
        Messages.load(file);
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
}
