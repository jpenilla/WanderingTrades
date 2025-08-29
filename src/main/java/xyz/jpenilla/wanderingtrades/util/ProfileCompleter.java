package xyz.jpenilla.wanderingtrades.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import it.unimi.dsi.fastutil.Pair;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import org.bukkit.scheduler.BukkitRunnable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

@NullMarked
final class ProfileCompleter extends BukkitRunnable {

    private final Queue<Pair<PlayerProfile, Consumer<PlayerProfile>>> completionQueue = new ConcurrentLinkedQueue<>();
    private final WanderingTrades plugin;

    ProfileCompleter(final WanderingTrades plugin) {
        this.plugin = plugin;
    }

    public void submitProfile(final PlayerProfile profile, final Consumer<PlayerProfile> callback) {
        this.completionQueue.add(Pair.of(profile, callback));
    }

    public void clearQueue() {
        this.completionQueue.clear();
    }

    @Override
    public void run() {
        final @Nullable Pair<PlayerProfile, Consumer<PlayerProfile>> pair = this.completionQueue.poll();
        if (pair == null) {
            return;
        }
        try {
            final PlayerProfile updatedProfile = pair.first().update().join();
            pair.second().accept(updatedProfile);
        } catch (final Exception e) {
            this.plugin.getSLF4JLogger().warn("Failed to complete player profile {}", pair.first(), e);
        }
    }

}
