package xyz.jpenilla.wanderingtrades.listener;

import xyz.jpenilla.jmplib.TextUtil;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinQuitListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (!WanderingTrades.getInstance().getStoredPlayers().getPlayers().contains(e.getPlayer().getUniqueId()) && !TextUtil.containsCaseInsensitive(e.getPlayer().getName(), WanderingTrades.getInstance().getCfg().getPlayerHeadConfig().getUsernameBlacklist())) {
            WanderingTrades.getInstance().getStoredPlayers().getPlayers().add(e.getPlayer().getUniqueId());
        }
    }
}
