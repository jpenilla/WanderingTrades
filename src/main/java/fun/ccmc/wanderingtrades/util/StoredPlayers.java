package fun.ccmc.wanderingtrades.util;

import fun.ccmc.jmplib.TextUtil;
import fun.ccmc.wanderingtrades.WanderingTrades;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class StoredPlayers {
    @Getter
    private final ArrayList<UUID> players = new ArrayList<>();

    public void load() {
        players.clear();
        OfflinePlayer[] op = Bukkit.getOfflinePlayers().clone();
        Arrays.stream(op).forEach(key -> {
            long lastLogout = key.getLastSeen();
            LocalDateTime logout = Instant.ofEpochMilli(lastLogout)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            LocalDateTime cutoff = LocalDateTime.now().minusDays(WanderingTrades.getInstance().getCfg().getPlayerHeadConfig().getDays());
            if (logout.isAfter(cutoff) || WanderingTrades.getInstance().getCfg().getPlayerHeadConfig().getDays() == -1) {
                if (!TextUtil.containsCaseInsensitive(key.getName(), WanderingTrades.getInstance().getCfg().getPlayerHeadConfig().getUsernameBlacklist())) {
                    players.add(key.getUniqueId());
                }
            }
        });
    }
}
