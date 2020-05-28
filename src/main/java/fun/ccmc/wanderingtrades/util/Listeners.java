package fun.ccmc.wanderingtrades.util;

import fun.ccmc.wanderingtrades.WanderingTrades;
import fun.ccmc.wanderingtrades.listener.AcquireTradeListener;
import fun.ccmc.wanderingtrades.listener.GuiListener;
import fun.ccmc.wanderingtrades.listener.ProtectTradersListener;
import fun.ccmc.wanderingtrades.listener.RefreshTradesListener;
import org.bukkit.event.HandlerList;

public class Listeners {
    private WanderingTrades plugin;

    public Listeners(WanderingTrades inst) {
        plugin = inst;
    }

    public void register() {
        plugin.getServer().getPluginManager().registerEvents(new GuiListener(), plugin);

        if (plugin.getCfg().isPluginEnabled()) {
            plugin.getServer().getPluginManager().registerEvents(new AcquireTradeListener(plugin), plugin);
            plugin.getServer().getPluginManager().registerEvents(new ProtectTradersListener(plugin), plugin);
        }

        if (plugin.getCfg().isRefreshCommandTraders()) {
            plugin.getServer().getPluginManager().registerEvents(new RefreshTradesListener(plugin), plugin);
        }
    }

    public void reload() {
        HandlerList.unregisterAll(plugin);
        register();
    }
}
