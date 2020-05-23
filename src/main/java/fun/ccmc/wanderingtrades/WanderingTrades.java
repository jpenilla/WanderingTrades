package fun.ccmc.wanderingtrades;

import co.aikar.commands.PaperCommandManager;
import fun.ccmc.wanderingtrades.command.CommandWanderingTrades;
import fun.ccmc.wanderingtrades.listener.VillagerAcquireTradeEventListener;
import fun.ccmc.wanderingtrades.util.Config;
import fun.ccmc.wanderingtrades.util.Log;
import fun.ccmc.wanderingtrades.util.UpdateChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public final class WanderingTrades extends JavaPlugin {

    public static WanderingTrades plugin;

    @Override
    public void onEnable() {
        plugin = this;

        Log.info("&d[STARTING]");

        Config.init(this);

        if(Config.getPluginEnabled()) {
            getServer().getPluginManager().registerEvents(new VillagerAcquireTradeEventListener(this), this);
        }

        PaperCommandManager manager = new PaperCommandManager(this);
        manager.enableUnstableAPI("help");
        manager.registerCommand(new CommandWanderingTrades(this));

        int pluginId = 7597;
        Metrics metrics = new Metrics(this, pluginId);

        new UpdateChecker(this, 79068).getVersion(version -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                Log.info("&aYou are running the latest version of " + this.getName() + "! :)");
            } else if(this.getDescription().getVersion().contains("SNAPSHOT")) {
                Log.info("&e[!] &6You are running a development build of " + this.getName() + " &e[!]");
            } else {
                Log.info("&e[!] &6You are running an outdated version of " + this.getName() + " (" + this.getDescription().getVersion() + ") &e[!]");
                Log.info("&bVersion " + version + " is available at &b&ohttps://www.spigotmc.org/resources/wanderingtrades.79068/");
            }
        });

        Log.info("&d[ON]");
    }

    @Override
    public void onDisable() {
    }
}
