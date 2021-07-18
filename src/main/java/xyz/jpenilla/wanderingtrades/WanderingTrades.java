package xyz.jpenilla.wanderingtrades;

import io.papermc.lib.PaperLib;
import java.util.logging.Level;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import xyz.jpenilla.jmplib.BasePlugin;
import xyz.jpenilla.wanderingtrades.command.CommandManager;
import xyz.jpenilla.wanderingtrades.compatability.VaultHook;
import xyz.jpenilla.wanderingtrades.compatability.WorldGuardHook;
import xyz.jpenilla.wanderingtrades.config.Config;
import xyz.jpenilla.wanderingtrades.config.LangConfig;
import xyz.jpenilla.wanderingtrades.util.Listeners;
import xyz.jpenilla.wanderingtrades.util.StoredPlayers;
import xyz.jpenilla.wanderingtrades.util.UpdateChecker;

public final class WanderingTrades extends BasePlugin {
    private static WanderingTrades instance;

    public static WanderingTrades instance() {
        return WanderingTrades.instance;
    }

    private Config cfg;
    private LangConfig lang;
    private StoredPlayers storedPlayers;
    private Listeners listeners;

    private WorldGuardHook worldGuard = null;
    private VaultHook vault = null;

    private boolean vaultPermissions = false;

    @Override
    public void onPluginEnable() {
        PaperLib.suggestPaper(this, Level.WARNING);
        instance = this;

        if (getServer().getPluginManager().isPluginEnabled("Vault")) {
            vault = new VaultHook(this);
        }
        if (getServer().getPluginManager().isPluginEnabled("WorldGuard")
                && getServer().getPluginManager().isPluginEnabled("WorldEdit")) {
            worldGuard = new WorldGuardHook(this);
        }

        cfg = new Config(this);
        lang = new LangConfig(this);

        this.storedPlayers = new StoredPlayers(this);

        if (!cfg.disableCommands()) {
            try {
                new CommandManager(this);
            } catch (Exception e) {
                getLogger().log(Level.SEVERE, "Failed to initialize CommandManager", e);
                this.setEnabled(false);
                return;
            }
        }

        listeners = new Listeners(this);
        listeners.register();
        new UpdateChecker(this, "jpenilla/WanderingTrades").checkVersion();

        final Metrics metrics = new Metrics(this, 7597);
        metrics.addCustomChart(new SimplePie("player_heads", () -> cfg.playerHeadConfig().playerHeadsFromServer() ? "On" : "Off"));
        metrics.addCustomChart(new SimplePie("player_heads_per_trader", () -> String.valueOf(cfg.playerHeadConfig().playerHeadsFromServerAmount())));
        metrics.addCustomChart(new SimplePie("plugin_language", () -> cfg.language()));
        metrics.addCustomChart(new SimplePie("amount_of_trade_configs", () -> String.valueOf(cfg.tradeConfigs().size())));
    }

    public @NonNull Config config() {
        return this.cfg;
    }

    public @NonNull LangConfig langConfig() {
        return this.lang;
    }

    public @NonNull StoredPlayers storedPlayers() {
        return this.storedPlayers;
    }

    public @NonNull Listeners listeners() {
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
}
