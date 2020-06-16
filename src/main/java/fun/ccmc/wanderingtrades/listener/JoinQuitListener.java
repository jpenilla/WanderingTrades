package fun.ccmc.wanderingtrades.listener;

import fun.ccmc.jmplib.TextUtil;
import fun.ccmc.wanderingtrades.WanderingTrades;
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
