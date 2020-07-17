package xyz.jpenilla.wanderingtrades;

import lombok.Getter;
import lombok.Setter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.jpenilla.jmplib.BasePlugin;
import xyz.jpenilla.wanderingtrades.command.CommandHelper;
import xyz.jpenilla.wanderingtrades.compatability.McRPGHook;
import xyz.jpenilla.wanderingtrades.compatability.VaultHook;
import xyz.jpenilla.wanderingtrades.compatability.WorldGuardHook;
import xyz.jpenilla.wanderingtrades.config.Config;
import xyz.jpenilla.wanderingtrades.config.LangConfig;
import xyz.jpenilla.wanderingtrades.util.Listeners;
import xyz.jpenilla.wanderingtrades.util.Log;
import xyz.jpenilla.wanderingtrades.util.StoredPlayers;
import xyz.jpenilla.wanderingtrades.util.UpdateChecker;

public final class WanderingTrades extends BasePlugin {
    @Getter
    private static WanderingTrades instance;

    @Getter private Config cfg;
    @Getter private LangConfig lang;
    @Getter private StoredPlayers storedPlayers;
    @Getter private Log log;
    @Getter private Listeners listeners;
    @Getter private CommandHelper commandHelper;

    @Getter private McRPGHook McRPG = null;
    @Getter private WorldGuardHook worldGuard = null;
    @Getter private VaultHook vault = null;

    @Getter @Setter
    private boolean vaultPermissions = false;

    @Override
    public void onPluginEnable() {
        instance = this;
        log = new Log(this);
        log.info("[STARTING]");

        if (getServer().getPluginManager().isPluginEnabled("Vault")) {
            vault = new VaultHook(this);
        }
        if (getServer().getPluginManager().isPluginEnabled("McRPG")) {
            McRPG = new McRPGHook();
        }
        if (getServer().getPluginManager().isPluginEnabled("WorldGuard") && getServer().getPluginManager().isPluginEnabled("WorldEdit")) {
            worldGuard = new WorldGuardHook(this);
        }

        cfg = new Config(this);
        lang = new LangConfig(this);

        storedPlayers = new StoredPlayers(this);
        class RefreshPlayers extends BukkitRunnable {
            @Override
            public void run() {
                storedPlayers.load();
            }
        }
        new RefreshPlayers().runTaskTimer(this, 0L, 20L * 60L * 60L * 12L);

        commandHelper = new CommandHelper(this);

        listeners = new Listeners(this);
        listeners.register();

        new UpdateChecker(this, 79068).getVersion(version ->
                UpdateChecker.updateCheck(version, true));
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> new UpdateChecker(instance, 79068).getVersion(UpdateChecker::updateCheck), 20L * 60L * 30L, 20L * 60L * 120L);

        int pluginId = 7597;
        Metrics metrics = new Metrics(this, pluginId);
        metrics.addCustomChart(new Metrics.SimplePie("player_heads", () -> cfg.getPlayerHeadConfig().isPlayerHeadsFromServer() ? "On" : "Off"));
        metrics.addCustomChart(new Metrics.SimplePie("plugin_language", () -> cfg.getLanguage()));
        metrics.addCustomChart(new Metrics.SimplePie("amount_of_trade_configs", () -> String.valueOf(cfg.getTradeConfigs().size())));
        log.info("[ON]");
    }

    @Override
    public void onDisable() {
    }
}
