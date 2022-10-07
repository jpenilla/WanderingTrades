package xyz.jpenilla.wanderingtrades.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

class ProfileCompleter extends BukkitRunnable {

    private final Queue<PlayerProfile> completionQueue = new ConcurrentLinkedQueue<>();
    private final WanderingTrades plugin;

    ProfileCompleter(final WanderingTrades plugin) {
        this.plugin = plugin;
    }

    public void submitSkullMeta(final @NonNull SkullMeta meta) {
        final PlayerProfile playerProfile = meta.getPlayerProfile();
        if (playerProfile == null) {
            return;
        }
        this.submitProfile(playerProfile);
    }

    public void submitProfile(final @NonNull PlayerProfile profile) {
        this.completionQueue.add(profile);
    }

    public void clearQueue() {
        this.completionQueue.clear();
    }

    @Override
    public void run() {
        final PlayerProfile profile = this.completionQueue.poll();
        if (profile != null) {
            try {
                profile.complete();
            } catch (final Exception e) {
                this.plugin.debug(String.format("Failed to cache player head skin for player: [username=%s,uuid=%s]", profile.getName(), profile.getId()));
            }
        }
    }

}
