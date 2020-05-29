package fun.ccmc.wanderingtrades;

import co.aikar.commands.PaperCommandManager;
import fun.ccmc.wanderingtrades.command.CommandWanderingTrades;
import fun.ccmc.wanderingtrades.command.TabCompletions;
import fun.ccmc.wanderingtrades.compat.McRPG;
import fun.ccmc.wanderingtrades.compat.WorldGuardCompat;
import fun.ccmc.wanderingtrades.config.Config;
import fun.ccmc.wanderingtrades.util.Listeners;
import fun.ccmc.wanderingtrades.util.Log;
import fun.ccmc.wanderingtrades.util.UpdateChecker;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public final class WanderingTrades extends JavaPlugin {
    @Getter private static WanderingTrades instance;

    @Getter private Config cfg;
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

        new UpdateChecker(this, 79068).getVersion(version -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                log.info("&aYou are running the latest version of " + this.getName() + "! :)");
            } else if(this.getDescription().getVersion().contains("SNAPSHOT")) {
                log.info("&e[!] &6You are running a development build of " + this.getName() + " &e[!]");
            } else {
                log.info("&e[!] &6You are running an outdated version of " + this.getName() + " (" + this.getDescription().getVersion() + ") &e[!]");
                log.info("&bVersion " + version + " is available at &b&ohttps://www.spigotmc.org/resources/wanderingtrades.79068/");
            }
        });
        getServer().getScheduler().scheduleSyncRepeatingTask(this, () ->new UpdateChecker(this, 79068).getVersion(version -> {
            if (!this.getDescription().getVersion().equalsIgnoreCase(version)) {
                if (this.getDescription().getVersion().contains("SNAPSHOT")) {
                    log.info("&e[!] &6You are running a development build of " + this.getName() + " &e[!]");
                } else {
                    log.info("&e[!] &6You are running an outdated version of " + this.getName() + " (" + this.getDescription().getVersion() + ") &e[!]");
                    log.info("&bVersion " + version + " is available at &b&ohttps://www.spigotmc.org/resources/wanderingtrades.79068/");
                }
            }
        }), 20L * 60L * 30L, 20L * 60L * 60L);

        log.info("&d[ON]");
    }

    @Override
    public void onDisable() {
    }
}
