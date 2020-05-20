package fun.ccmc.wt;

import fun.ccmc.wt.listener.VillagerAcquireTradeEventListener;
import fun.ccmc.wt.util.CommandWanderingTrades;
import fun.ccmc.wt.util.Config;
import fun.ccmc.wt.util.Log;
import org.bukkit.plugin.java.JavaPlugin;

public final class WanderingTrades extends JavaPlugin {

    public static WanderingTrades plugin;

    @Override
    public void onEnable() {
        plugin = this;

        Log.info("&d[STARTING]");

        Config.init(this);

        if(Config.getEnabled()) {
            getServer().getPluginManager().registerEvents(new VillagerAcquireTradeEventListener(this), this);
        }

        this.getCommand("wanderingtrades").setExecutor(new CommandWanderingTrades());

        Log.info("&d[ON]");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
