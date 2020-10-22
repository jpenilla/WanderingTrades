package xyz.jpenilla.wanderingtrades;

import kr.entree.spigradle.annotations.PluginMain;
import lombok.Getter;
import lombok.Setter;
import org.bstats.bukkit.Metrics;
import xyz.jpenilla.jmplib.BasePlugin;
import xyz.jpenilla.wanderingtrades.command.CommandManager;
import xyz.jpenilla.wanderingtrades.compatability.McRPGHook;
import xyz.jpenilla.wanderingtrades.compatability.VaultHook;
import xyz.jpenilla.wanderingtrades.compatability.WorldGuardHook;
import xyz.jpenilla.wanderingtrades.config.Config;
import xyz.jpenilla.wanderingtrades.config.LangConfig;
import xyz.jpenilla.wanderingtrades.util.Listeners;
import xyz.jpenilla.wanderingtrades.util.Log;
import xyz.jpenilla.wanderingtrades.util.StoredPlayers;
import xyz.jpenilla.wanderingtrades.util.UpdateChecker;

import java.util.logging.Level;

@PluginMain
public final class WanderingTrades extends BasePlugin {
    @Getter
    private static WanderingTrades instance;

    @Getter private Config cfg;
    @Getter private LangConfig lang;
    @Getter private StoredPlayers storedPlayers;
    @Getter private Log log;
    @Getter private Listeners listeners;
    @Getter private CommandManager commandManager;

    @Getter private McRPGHook McRPG = null;
    @Getter private WorldGuardHook worldGuard = null;
    @Getter private VaultHook vault = null;

    @Getter @Setter
    private boolean vaultPermissions = false;

    @Override
    public void onPluginEnable() {
        instance = this;
        log = new Log(this);

        if (getServer().getPluginManager().isPluginEnabled("Vault")) {
            vault = new VaultHook(this);
        }
        if (getServer().getPluginManager().isPluginEnabled("McRPG")) {
            McRPG = new McRPGHook();
        }
        if (getServer().getPluginManager().isPluginEnabled("WorldGuard")
                && getServer().getPluginManager().isPluginEnabled("WorldEdit")) {
            worldGuard = new WorldGuardHook(this);
        }

        cfg = new Config(this);
        lang = new LangConfig(this);

        storedPlayers = new StoredPlayers(this);
        getServer().getScheduler().runTaskTimer(this, storedPlayers::load, 0L, 864000L);

        try {
            commandManager = new CommandManager(this);
        } catch (Exception e) {
            getLogger().log(Level.WARNING, "Failed to initialize CommandManager", e);
        }

        listeners = new Listeners(this);
        listeners.register();
        new UpdateChecker(this, "jmanpenilla/WanderingTrades").checkVersion();

        Metrics metrics = new Metrics(this, 7597);
        metrics.addCustomChart(new Metrics.SimplePie("player_heads", () -> cfg.getPlayerHeadConfig().isPlayerHeadsFromServer() ? "On" : "Off"));
        metrics.addCustomChart(new Metrics.SimplePie("player_heads_per_trader", () -> String.valueOf(cfg.getPlayerHeadConfig().getPlayerHeadsFromServerAmount())));
        metrics.addCustomChart(new Metrics.SimplePie("plugin_language", () -> cfg.getLanguage()));
        metrics.addCustomChart(new Metrics.SimplePie("amount_of_trade_configs", () -> String.valueOf(cfg.getTradeConfigs().size())));
        log.info("Enabled");
    }
}
