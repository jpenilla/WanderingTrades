package xyz.jpenilla.wanderingtrades.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.jpenilla.jmplib.TextUtil;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

public class JoinQuitListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (WanderingTrades.getInstance().isVaultPermissions()) {
            if (WanderingTrades.getInstance().getCfg().getPlayerHeadConfig().isPermissionWhitelist()) {
                if (e.getPlayer().hasPermission("wanderingtrades.headavailable")) {
                    addHead(e.getPlayer());
                }
            } else {
                addHead(e.getPlayer());
            }
        } else {
            addHead(e.getPlayer());
        }
    }

    private void addHead(Player player) {
        if (!WanderingTrades.getInstance().getStoredPlayers().getPlayers().contains(player.getUniqueId()) && !TextUtil.containsCaseInsensitive(player.getName(), WanderingTrades.getInstance().getCfg().getPlayerHeadConfig().getUsernameBlacklist())) {
            WanderingTrades.getInstance().getStoredPlayers().getPlayers().add(player.getUniqueId());
        }
    }
}
