package xyz.jpenilla.wanderingtrades.util;

import org.bukkit.event.HandlerList;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.listener.*;

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
