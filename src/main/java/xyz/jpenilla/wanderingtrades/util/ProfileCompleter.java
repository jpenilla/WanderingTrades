package xyz.jpenilla.wanderingtrades.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;
import org.bukkit.scheduler.BukkitRunnable;
import org.jspecify.annotations.NullMarked;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

@NullMarked
final class ProfileCompleter extends BukkitRunnable {

    private record QueueEntry(PlayerProfile profile, Consumer<PlayerProfile> callback) {}

    private final WanderingTrades plugin;
    private final Queue<QueueEntry> completionQueue = new ConcurrentLinkedQueue<>();
    private final Semaphore permit = new Semaphore(1);

    ProfileCompleter(final WanderingTrades plugin) {
        this.plugin = plugin;
    }

    public void submitProfile(final PlayerProfile profile, final Consumer<PlayerProfile> callback) {
        this.completionQueue.add(new QueueEntry(profile, callback));
    }

    public void clearQueue() {
        this.completionQueue.clear();
    }

    @Override
    public void run() {
        if (!this.permit.tryAcquire()) {
            return;
        }

        try {
            final QueueEntry entry = this.completionQueue.poll();
            if (entry == null) {
                return;
            }
            try {
                final PlayerProfile updatedProfile = entry.profile().update().join();
                entry.callback().accept(updatedProfile);
            } catch (final Exception e) {
                this.plugin.getSLF4JLogger().warn("Failed to complete player profile {}", entry.profile(), e);
            }
        } finally {
            this.permit.release();
        }
    }

}
