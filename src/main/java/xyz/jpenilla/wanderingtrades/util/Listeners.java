package xyz.jpenilla.wanderingtrades.util;

import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.listener.AcquireTradeListener;
import xyz.jpenilla.wanderingtrades.listener.BrainModificationListener;
import xyz.jpenilla.wanderingtrades.listener.GuiListener;
import xyz.jpenilla.wanderingtrades.listener.JoinQuitListener;
import xyz.jpenilla.wanderingtrades.listener.ProtectTradersListener;
import xyz.jpenilla.wanderingtrades.listener.RefreshTradesListener;
import xyz.jpenilla.wanderingtrades.listener.TraderSpawnListener;

import java.util.HashMap;
import java.util.Map;

public class Listeners {
    private final WanderingTrades plugin;
    private final Map<Class<?>, Listener> listeners = new HashMap<>();

    public Listeners(final @NonNull WanderingTrades plugin) {
        this.plugin = plugin;
    }

    public void register() {
        this.registerListener(GuiListener.class, new GuiListener());
        this.registerListener(JoinQuitListener.class, new JoinQuitListener(this.plugin));

        if (plugin.getCfg().isEnabled()) {
            this.registerListener(AcquireTradeListener.class, new AcquireTradeListener(this.plugin));
            this.registerListener(TraderSpawnListener.class, new TraderSpawnListener(this.plugin));
            this.registerListener(ProtectTradersListener.class, new ProtectTradersListener(this.plugin));
        }

        if (plugin.getCfg().isRefreshCommandTraders()) {
            this.registerListener(RefreshTradesListener.class, new RefreshTradesListener(this.plugin));
        }

        if (PaperLib.isPaper() && PaperLib.getMinecraftVersion() >= 16) {
            this.registerListener(BrainModificationListener.class, new BrainModificationListener(this.plugin));
        }
    }

    private <L extends Listener> void registerListener(final @NonNull Class<L> listenerClass, final @NonNull L listener) {
        this.listeners.put(listenerClass, listener);
        Bukkit.getServer().getPluginManager().registerEvents(listener, this.plugin);
    }

    public void reload() {
        this.listeners.forEach((clazz, listener) -> HandlerList.unregisterAll(listener));
        this.listeners.clear();
        this.register();
    }

    @SuppressWarnings("unchecked")
    public <L> @NonNull L listener(final @NonNull Class<L> listenerClass) {
        return (L) this.listeners.get(listenerClass);
    }
}
