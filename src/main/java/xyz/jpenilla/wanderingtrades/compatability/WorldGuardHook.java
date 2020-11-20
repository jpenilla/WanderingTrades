package xyz.jpenilla.wanderingtrades.compatability;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import xyz.jpenilla.jmplib.TextUtil;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

public class WorldGuardHook {

    private final WanderingTrades plugin;

    public WorldGuardHook(WanderingTrades instance) {
        plugin = instance;
    }

    public WorldGuard getWG() {
        return WorldGuard.getInstance();
    }

    public ApplicableRegionSet getRegions(final Location loc) {
        final RegionContainer container = getWG().getPlatform().getRegionContainer();
        return container.createQuery().getApplicableRegions(BukkitAdapter.adapt(loc));
    }

    public boolean passesWhiteBlackList(Location loc) {
        if (!plugin.getCfg().isWgWhitelist()) {
            return true;
        }
        final ApplicableRegionSet regions = getRegions(loc);
        for (ProtectedRegion region : regions) {
            if (TextUtil.containsCaseInsensitive(region.getId(), plugin.getCfg().getWgRegionList())) {
                return true;
            }
        }
        return false;
    }

}
