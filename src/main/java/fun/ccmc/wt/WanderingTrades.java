package fun.ccmc.wt;

import fun.ccmc.wt.listener.VillagerAcquireTradeEventListener;
import fun.ccmc.wt.command.CommandWanderingTrades;
import fun.ccmc.wt.util.Config;
import fun.ccmc.wt.util.Log;
import fun.ccmc.wt.util.UpdateChecker;
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

        this.getCommand("wanderingtrades").setExecutor(new CommandWanderingTrades());

        int pluginId = 7597;
        Metrics metrics = new Metrics(this, pluginId);

        new UpdateChecker(this, 79068).getVersion(version -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                Log.info("&aYou are running the latest version");
            } else if(this.getDescription().getVersion().contains("SNAPSHOT")) {
                Log.info("&eYou are running a dev build");
            } else {
                Log.info("&eThere is an update available at https://www.spigotmc.org/resources/wanderingtrades.79068/");
            }
        });

        Log.info("&d[ON]");
    }

    @Override
    public void onDisable() {
    }
}
