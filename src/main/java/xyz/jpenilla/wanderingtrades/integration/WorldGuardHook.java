package xyz.jpenilla.wanderingtrades.integration;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.jspecify.annotations.NullMarked;
import xyz.jpenilla.pluginbase.legacy.TextUtil;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

@NullMarked
public final class WorldGuardHook {
    private final WanderingTrades plugin;

    public WorldGuardHook(final WanderingTrades plugin) {
        this.plugin = plugin;
    }

    public WorldGuard getWG() {
        return WorldGuard.getInstance();
    }

    public ApplicableRegionSet getRegions(final Location loc) {
        final RegionContainer container = this.getWG().getPlatform().getRegionContainer();
        return container.createQuery().getApplicableRegions(BukkitAdapter.adapt(loc));
    }

    public boolean passesWhiteBlackList(Location loc) {
        final boolean inListedRegion = this.getRegions(loc).getRegions().stream()
            .anyMatch(region -> TextUtil.containsCaseInsensitive(region.getId(), this.plugin.config().wgRegionList()));
        return this.plugin.config().wgWhitelist() == inListedRegion;
    }
}
