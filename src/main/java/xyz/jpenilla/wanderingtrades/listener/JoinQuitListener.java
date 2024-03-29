package xyz.jpenilla.wanderingtrades.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

@DefaultQualifier(NonNull.class)
public final class JoinQuitListener implements Listener {
    private final WanderingTrades plugin;

    public JoinQuitListener(final WanderingTrades plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        this.plugin.playerHeads().handleLogin(event.getPlayer());
    }

    @EventHandler
    public void onLeave(final PlayerQuitEvent event) {
        this.plugin.playerHeads().handleLogout(event.getPlayer());
    }
}
