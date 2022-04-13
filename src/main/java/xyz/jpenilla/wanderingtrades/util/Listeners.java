package xyz.jpenilla.wanderingtrades.util;

import io.papermc.lib.PaperLib;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.listener.*;

public class Listeners {
    private final WanderingTrades plugin;
    private final Map<Class<?>, Listener> listeners = new HashMap<>();

    public Listeners(final @NonNull WanderingTrades plugin) {
        this.plugin = plugin;
    }

    public void register() {
        this.registerListener(GuiListener.class, new GuiListener());
        this.registerListener(JoinQuitListener.class, new JoinQuitListener(this.plugin));

        if (this.plugin.config().enabled()) {
            this.registerListener(AcquireTradeListener.class, new AcquireTradeListener(this.plugin));
            this.registerListener(TraderSpawnListener.class, new TraderSpawnListener(this.plugin));
            this.registerListener(ProtectTradersListener.class, new ProtectTradersListener(this.plugin));
            this.registerListener(TraderPotionListener.class, new TraderPotionListener(this.plugin));
        }

        if (this.plugin.config().refreshCommandTraders()) {
            this.registerListener(RefreshTradesListener.class, new RefreshTradesListener(this.plugin));
        }

        if (PaperLib.isPaper()) {
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
