package xyz.jpenilla.wanderingtrades.compatability;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import xyz.jpenilla.pluginbase.legacy.TextUtil;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

@DefaultQualifier(NonNull.class)
public final class WorldGuardHook {
    private final WanderingTrades plugin;

    public WorldGuardHook(final WanderingTrades plugin) {
        this.plugin = plugin;
    }

    public WorldGuard getWG() {
        return WorldGuard.getInstance();
    }

    public ApplicableRegionSet getRegions(final Location loc) {
        final RegionContainer container = getWG().getPlatform().getRegionContainer();
        return container.createQuery().getApplicableRegions(BukkitAdapter.adapt(loc));
    }

    public boolean passesWhiteBlackList(Location loc) {
        final boolean inListedRegion = getRegions(loc).getRegions().stream()
            .anyMatch(region -> TextUtil.containsCaseInsensitive(region.getId(), this.plugin.config().wgRegionList()));
        return this.plugin.config().wgWhitelist() == inListedRegion;
    }
}
