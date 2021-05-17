package xyz.jpenilla.wanderingtrades.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

public class JoinQuitListener implements Listener {

    private final WanderingTrades wanderingTrades;

    public JoinQuitListener(WanderingTrades wanderingTrades) {
        this.wanderingTrades = wanderingTrades;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        this.wanderingTrades.storedPlayers().addHeadIfPermissible(e.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        this.wanderingTrades.storedPlayers().onLogout(e.getPlayer());
    }

}
