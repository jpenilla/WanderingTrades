package fun.ccmc.wanderingtrades;

import co.aikar.commands.PaperCommandManager;
import fun.ccmc.wanderingtrades.command.CommandWanderingTrades;
import fun.ccmc.wanderingtrades.command.TabCompletions;
import fun.ccmc.wanderingtrades.compat.McRPG;
import fun.ccmc.wanderingtrades.compat.WorldGuardCompat;
import fun.ccmc.wanderingtrades.config.Config;
import fun.ccmc.wanderingtrades.config.LangConfig;
import fun.ccmc.wanderingtrades.util.Listeners;
import fun.ccmc.wanderingtrades.util.Log;
import fun.ccmc.wanderingtrades.util.UpdateChecker;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class WanderingTrades extends JavaPlugin {
    @Getter private static WanderingTrades instance;

    @Getter private Config cfg;
    @Getter private LangConfig lang;
    @Getter private Log log;
    @Getter private Listeners listeners;
    @Getter private TabCompletions tabCompletions;

    @Getter private McRPG McRPG = null;
    @Getter private WorldGuardCompat worldGuard = null;

    @Getter private PaperCommandManager commandManager;

    @Getter private boolean paper;

    @Override
    public void onEnable() {
        instance = this;
        log = new Log(this);
        log.info("&d[STARTING]");

        commandManager = new PaperCommandManager(this);
        commandManager.enableUnstableAPI("help");
        commandManager.registerCommand(new CommandWanderingTrades(this));

        if(getServer().getPluginManager().isPluginEnabled("McRPG")) {
            McRPG = new McRPG(this);
        }
        if(getServer().getPluginManager().isPluginEnabled("WorldGuard") && getServer().getPluginManager().isPluginEnabled("WorldEdit")) {
            worldGuard = new WorldGuardCompat(this);
        }

        cfg = new Config(this);
        lang = new LangConfig(this);
        tabCompletions = new TabCompletions(this);
        tabCompletions.register();

        paper = false;
        try {
            paper = Class.forName("com.destroystokyo.paper.VersionHistoryManager$VersionData") != null;
        } catch (ClassNotFoundException e) {
            getLog().debug("Paper not detected. Install Paper from https://papermc.io");
        }
        if (paper) {
            getLog().info("Got Paper!");
        }

        listeners = new Listeners(this);
        listeners.register();

        int pluginId = 7597;
        Metrics metrics = new Metrics(this, pluginId);

        new UpdateChecker(this, 79068).getVersion(version ->
                UpdateChecker.updateCheck(version, true));
        class UpdateCheck extends BukkitRunnable {
            @Override
            public void run() {
                new UpdateChecker(instance, 79068).getVersion(UpdateChecker::updateCheck);
            }
        }
        new UpdateCheck().runTaskTimer(this,20L * 60L * 30L, 20L * 60L * 120L);

        log.info("&d[ON]");
    }

    @Override
    public void onDisable() {
    }
}
