package fun.ccmc.wanderingtrades.util;

import fun.ccmc.wanderingtrades.WanderingTrades;
import fun.ccmc.wanderingtrades.listener.EntityDamageEventListener;
import fun.ccmc.wanderingtrades.listener.PlayerInteractEntityEventListener;
import fun.ccmc.wanderingtrades.listener.VillagerAcquireTradeEventListener;
import org.bukkit.event.HandlerList;

public class Listeners {
    private WanderingTrades plugin;

    public Listeners(WanderingTrades inst) {
        plugin = inst;
    }

    public void register() {
        if (plugin.getCfg().isPluginEnabled()) {
            plugin.getServer().getPluginManager().registerEvents(new VillagerAcquireTradeEventListener(plugin), plugin);
            plugin.getServer().getPluginManager().registerEvents(new EntityDamageEventListener(plugin), plugin);
        }

        if (plugin.getCfg().isRefreshCommandTraders()) {
            plugin.getServer().getPluginManager().registerEvents(new PlayerInteractEntityEventListener(plugin), plugin);
        }
    }

    public void reload() {
        HandlerList.unregisterAll(plugin);
        register();
    }
}
