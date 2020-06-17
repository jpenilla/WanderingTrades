package fun.ccmc.wanderingtrades.util;

import fun.ccmc.wanderingtrades.WanderingTrades;
import fun.ccmc.wanderingtrades.listener.*;
import org.bukkit.event.HandlerList;

public class Listeners {
    private final WanderingTrades plugin;

    public Listeners(WanderingTrades inst) {
        plugin = inst;
    }

    public void register() {
        plugin.getServer().getPluginManager().registerEvents(new GuiListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new JoinQuitListener(), plugin);

        if (plugin.getCfg().isEnabled()) {
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
