package xyz.jpenilla.wanderingtrades;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Merchant;

public final class TradeSessionManager implements Listener {
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    private record Session(CompletableFuture<Merchant> merchant, long lifetimeTicks) {}

    TradeSessionManager(final WanderingTrades plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public CompletableFuture<Merchant> getOrCreateSession(final String sessionId, final long lifetimeTicks, final Supplier<CompletableFuture<Merchant>> merchant) {
        return this.sessions.computeIfAbsent(sessionId, id -> new Session(merchant.get(), lifetimeTicks)).merchant();
    }

    @EventHandler
    public void onTick(final ServerTickEndEvent event) {
        this.tick();
    }

    public void tick() {
        for (final String id : Set.copyOf(this.sessions.keySet())) {
            this.sessions.compute(id, (key, session) -> {
                if (session == null) {
                    return null;
                }
                final long newLifetime = session.lifetimeTicks() - 1;
                if (newLifetime <= 0) {
                    return null;
                }
                return new Session(session.merchant(), newLifetime);
            });
        }
    }
}
