package xyz.jpenilla.wanderingtrades.util;

import lombok.Getter;
import org.bukkit.event.HandlerList;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.listener.*;

public class Listeners {
    private final WanderingTrades plugin;

    @Getter private GuiListener guiListener;
    @Getter private JoinQuitListener joinQuitListener;
    @Getter private AcquireTradeListener acquireTradeListener;
    @Getter private TraderSpawnListener traderSpawnListener;
    @Getter private ProtectTradersListener protectTradersListener;
    @Getter private RefreshTradesListener refreshTradesListener;

    public Listeners(WanderingTrades inst) {
        plugin = inst;
    }

    public void register() {
        guiListener = new GuiListener();
        joinQuitListener = new JoinQuitListener(plugin);
        acquireTradeListener = new AcquireTradeListener(plugin);
        traderSpawnListener = new TraderSpawnListener(plugin);
        protectTradersListener = new ProtectTradersListener(plugin);
        refreshTradesListener = new RefreshTradesListener(plugin);

        plugin.getServer().getPluginManager().registerEvents(guiListener, plugin);
        plugin.getServer().getPluginManager().registerEvents(joinQuitListener, plugin);

        if (plugin.getCfg().isEnabled()) {
            plugin.getServer().getPluginManager().registerEvents(acquireTradeListener, plugin);
            plugin.getServer().getPluginManager().registerEvents(traderSpawnListener, plugin);
            plugin.getServer().getPluginManager().registerEvents(protectTradersListener, plugin);
        }

        if (plugin.getCfg().isRefreshCommandTraders()) {
            plugin.getServer().getPluginManager().registerEvents(refreshTradesListener, plugin);
        }
    }

    public void reload() {
        HandlerList.unregisterAll(plugin);
        register();
    }
}
