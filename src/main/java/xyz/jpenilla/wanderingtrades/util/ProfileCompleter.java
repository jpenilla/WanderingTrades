package xyz.jpenilla.wanderingtrades.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

final class ProfileCompleter extends BukkitRunnable {

    private final Queue<SkullMeta> completionQueue = new ConcurrentLinkedQueue<>();
    private final WanderingTrades plugin;

    ProfileCompleter(final WanderingTrades plugin) {
        this.plugin = plugin;
    }

    public void submitSkullMeta(final @NonNull SkullMeta meta) {
        if (meta.getPlayerProfile() == null) {
            return;
        }
        this.completionQueue.add(meta);
    }

    public void clearQueue() {
        this.completionQueue.clear();
    }

    @Override
    public void run() {
        final SkullMeta meta = this.completionQueue.poll();
        if (meta == null) {
            return;
        }
        final @Nullable PlayerProfile profile = meta.getPlayerProfile();
        if (profile != null) {
            try {
                profile.complete();
                final @Nullable MerchantRecipe recipe = ((PlayerHeadsImpl) this.plugin.playerHeads()).recipeMap().get(profile.getId());
                if (recipe != null) {
                    meta.setPlayerProfile(profile);
                    recipe.getResult().setItemMeta(meta);
                }
            } catch (final Exception e) {
                this.plugin.debug(String.format("Failed to cache player head skin for player: [username=%s,uuid=%s]", profile.getName(), profile.getId()));
            }
        }
    }

}
