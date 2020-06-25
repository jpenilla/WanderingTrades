package xyz.jpenilla.wanderingtrades;

import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import lombok.Setter;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.jpenilla.wanderingtrades.command.CommandHelper;
import xyz.jpenilla.wanderingtrades.command.CommandWanderingTrades;
import xyz.jpenilla.wanderingtrades.compat.McRPG;
import xyz.jpenilla.wanderingtrades.compat.VaultCompat;
import xyz.jpenilla.wanderingtrades.compat.WorldGuardCompat;
import xyz.jpenilla.wanderingtrades.config.Config;
import xyz.jpenilla.wanderingtrades.config.LangConfig;
import xyz.jpenilla.wanderingtrades.util.Listeners;
import xyz.jpenilla.wanderingtrades.util.Log;
import xyz.jpenilla.wanderingtrades.util.StoredPlayers;
import xyz.jpenilla.wanderingtrades.util.UpdateChecker;

public final class WanderingTrades extends JavaPlugin {
    @Getter
    private static WanderingTrades instance;

    @Getter private Config cfg;
    @Getter private LangConfig lang;
    @Getter private StoredPlayers storedPlayers;
    @Getter private Log log;
    @Getter private Listeners listeners;
    @Getter private CommandHelper commandHelper;

    @Getter private xyz.jpenilla.wanderingtrades.compat.McRPG McRPG = null;
    @Getter private WorldGuardCompat worldGuard = null;
    @Getter private VaultCompat vault = null;

    @Getter private PaperCommandManager commandManager;

    @Getter @Setter
    private boolean vaultPermissions = false;

    @Override
    public void onEnable() {
        instance = this;
        log = new Log(this);
        log.info("[STARTING]");

        if (getServer().getPluginManager().isPluginEnabled("Vault")) {
            vault = new VaultCompat(this);
        }
        if (getServer().getPluginManager().isPluginEnabled("McRPG")) {
            McRPG = new McRPG();
        }
        if (getServer().getPluginManager().isPluginEnabled("WorldGuard") && getServer().getPluginManager().isPluginEnabled("WorldEdit")) {
            worldGuard = new WorldGuardCompat(this);
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
        new RefreshPlayers().runTaskTimer(this, 20L * 60L * 60L * 12L, 20L * 60L * 60L * 12L);

        commandManager = new PaperCommandManager(this);
        commandManager.enableUnstableAPI("help");
        commandManager.setDefaultHelpPerPage(5);
        commandHelper = new CommandHelper(this);
        commandHelper.register();
        commandManager.registerCommand(new CommandWanderingTrades(this));

        listeners = new Listeners(this);
        listeners.register();

        new UpdateChecker(this, 79068).getVersion(version ->
                UpdateChecker.updateCheck(version, true));
        class UpdateCheck extends BukkitRunnable {
            @Override
            public void run() {
                new UpdateChecker(instance, 79068).getVersion(UpdateChecker::updateCheck);
            }
        }
        new UpdateCheck().runTaskTimer(this, 20L * 60L * 30L, 20L * 60L * 120L);

        int pluginId = 7597;
        Metrics metrics = new Metrics(this, pluginId);
        metrics.addCustomChart(new Metrics.SimplePie("player_heads", () -> {
            if (cfg.getPlayerHeadConfig().isPlayerHeadsFromServer()) {
                return "On";
            } else {
                return "Off";
            }
        }));
        metrics.addCustomChart(new Metrics.SimplePie("plugin_language", () -> cfg.getLanguage()));
        metrics.addCustomChart(new Metrics.SimplePie("amount_of_trade_configs", () -> String.valueOf(cfg.getTradeConfigs().size())));
        log.info("[ON]");
    }

    @Override
    public void onDisable() {
    }
}
