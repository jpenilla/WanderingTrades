package xyz.jpenilla.wanderingtrades.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.jpenilla.jmplib.TextUtil;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

public class JoinQuitListener implements Listener {

    private final WanderingTrades wanderingTrades;

    public JoinQuitListener(WanderingTrades wanderingTrades) {
        this.wanderingTrades = wanderingTrades;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (wanderingTrades.isVaultPermissions()) {
            if (wanderingTrades.getCfg().getPlayerHeadConfig().isPermissionWhitelist()) {
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

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        if (wanderingTrades.isVaultPermissions() && wanderingTrades.getCfg().getPlayerHeadConfig().isPermissionWhitelist()) {
            if (!e.getPlayer().hasPermission("wanderingtrades.headavailable") && wanderingTrades.getStoredPlayers().getPlayers().contains(e.getPlayer().getUniqueId())) {
                wanderingTrades.getStoredPlayers().getPlayers().remove(e.getPlayer().getUniqueId());
            }
        }
    }

    private void addHead(Player player) {
        if (!wanderingTrades.getStoredPlayers().getPlayers().contains(player.getUniqueId()) && !TextUtil.containsCaseInsensitive(player.getName(), wanderingTrades.getCfg().getPlayerHeadConfig().getUsernameBlacklist())) {
            wanderingTrades.getStoredPlayers().getPlayers().add(player.getUniqueId());
        }
    }
}
