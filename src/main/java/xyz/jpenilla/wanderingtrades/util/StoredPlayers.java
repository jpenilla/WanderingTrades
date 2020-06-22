package xyz.jpenilla.wanderingtrades.util;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import xyz.jpenilla.jmplib.TextUtil;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.UUID;

public class StoredPlayers {
    private final WanderingTrades wanderingTrades;
    @Getter
    private final ArrayList<UUID> players = new ArrayList<>();

    public StoredPlayers(WanderingTrades wanderingTrades) {
        this.wanderingTrades = wanderingTrades;
    }

    public void load() {
        players.clear();
        OfflinePlayer[] op = Bukkit.getOfflinePlayers().clone();

        for (OfflinePlayer offlinePlayer : op) {
            long lastLogout = offlinePlayer.getLastSeen();
            LocalDateTime logout = Instant.ofEpochMilli(lastLogout)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            LocalDateTime cutoff = LocalDateTime.now().minusDays(wanderingTrades.getCfg().getPlayerHeadConfig().getDays());
            if (logout.isAfter(cutoff) || wanderingTrades.getCfg().getPlayerHeadConfig().getDays() == -1) {
                if (!TextUtil.containsCaseInsensitive(offlinePlayer.getName(), wanderingTrades.getCfg().getPlayerHeadConfig().getUsernameBlacklist())) {
                    if (!wanderingTrades.isVaultPermissions()) {
                        players.add(offlinePlayer.getUniqueId());
                    } else {
                        if (wanderingTrades.getCfg().getPlayerHeadConfig().isPermissionWhitelist()) {
                            if (wanderingTrades.getVault().getPerms().playerHas(null, offlinePlayer, "wanderingtrades.headavailable")) {
                                players.add(offlinePlayer.getUniqueId());
                            }
                        } else {
                            players.add(offlinePlayer.getUniqueId());
                        }
                    }
                }
            }
        }
    }
}
