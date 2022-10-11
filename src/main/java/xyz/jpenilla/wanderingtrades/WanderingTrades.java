package xyz.jpenilla.wanderingtrades;

import io.papermc.lib.PaperLib;
import java.util.logging.Level;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import xyz.jpenilla.pluginbase.legacy.PluginBase;
import xyz.jpenilla.wanderingtrades.command.CommandManager;
import xyz.jpenilla.wanderingtrades.compatability.VaultHook;
import xyz.jpenilla.wanderingtrades.compatability.WorldGuardHook;
import xyz.jpenilla.wanderingtrades.config.Config;
import xyz.jpenilla.wanderingtrades.config.LangConfig;
import xyz.jpenilla.wanderingtrades.util.Listeners;
import xyz.jpenilla.wanderingtrades.util.PlayerHeads;
import xyz.jpenilla.wanderingtrades.util.TradeApplicator;
import xyz.jpenilla.wanderingtrades.util.UpdateChecker;

@DefaultQualifier(NonNull.class)
public final class WanderingTrades extends PluginBase {
    private static @MonotonicNonNull WanderingTrades instance;

    private @MonotonicNonNull Config cfg;
    private @MonotonicNonNull LangConfig lang;
    private @MonotonicNonNull PlayerHeads playerHeads;
    private @MonotonicNonNull Listeners listeners;
    private @MonotonicNonNull TradeApplicator tradeApplicator;

    private @Nullable WorldGuardHook worldGuard = null;
    private @Nullable VaultHook vault = null;

    private boolean vaultPermissions = false;

    @Override
    public void enable() {
        PaperLib.suggestPaper(this, Level.WARNING);
        instance = this;

        if (this.getServer().getPluginManager().isPluginEnabled("Vault")) {
            this.vault = new VaultHook(this);
        }
        if (this.getServer().getPluginManager().isPluginEnabled("WorldGuard")
            && this.getServer().getPluginManager().isPluginEnabled("WorldEdit")) {
            this.worldGuard = new WorldGuardHook(this);
        }

        this.cfg = new Config(this);
        this.lang = new LangConfig(this);

        this.playerHeads = PlayerHeads.create(this);

        if (!this.cfg.disableCommands()) {
            try {
                new CommandManager(this);
            } catch (final Exception ex) {
                throw new RuntimeException("Failed to initialize CommandManager", ex);
            }
        }

        this.tradeApplicator = new TradeApplicator(this);

        this.listeners = new Listeners(this);
        this.listeners.register();

        if (this.cfg.updateChecker()) {
            this.getServer().getScheduler().runTask(
                this,
                () -> new UpdateChecker(this, "jpenilla/WanderingTrades").checkVersion()
            );
        }

        this.setupMetrics();
    }

    private void setupMetrics() {
        final Metrics metrics = new Metrics(this, 7597);
        metrics.addCustomChart(new SimplePie("player_heads", () -> this.cfg.playerHeadConfig().playerHeadsFromServer() ? "On" : "Off"));
        metrics.addCustomChart(new SimplePie("player_heads_per_trader", () -> String.valueOf(this.cfg.playerHeadConfig().playerHeadsFromServerAmount())));
        metrics.addCustomChart(new SimplePie("plugin_language", () -> this.cfg.language()));
        metrics.addCustomChart(new SimplePie("amount_of_trade_configs", () -> String.valueOf(this.cfg.tradeConfigs().size())));
    }

    public void reload() {
        this.config().load();
        this.langConfig().load();
        this.listeners().reload();
        this.playerHeads().configChanged();
    }

    public Config config() {
        return this.cfg;
    }

    public LangConfig langConfig() {
        return this.lang;
    }

    public PlayerHeads playerHeads() {
        return this.playerHeads;
    }

    public TradeApplicator tradeApplicator() {
        return tradeApplicator;
    }

    public Listeners listeners() {
        return this.listeners;
    }

    public @Nullable WorldGuardHook worldGuardHook() {
        return this.worldGuard;
    }

    public @Nullable VaultHook vaultHook() {
        return this.vault;
    }

    public boolean isVaultPermissions() {
        return this.vaultPermissions;
    }

    public void setVaultPermissions(final boolean vaultPermissions) {
        this.vaultPermissions = vaultPermissions;
    }

    public void debug(final String message) {
        if (this.config().debug()) {
            this.getLogger().info("[DEBUG] " + message);
        }
    }

    public static WanderingTrades instance() {
        return instance;
    }
}
