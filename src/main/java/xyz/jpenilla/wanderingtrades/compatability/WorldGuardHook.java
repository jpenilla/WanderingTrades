package xyz.jpenilla.wanderingtrades.compatability;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
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
        RegionContainer container = getWG().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        return query.getApplicableRegions(BukkitAdapter.adapt(loc));
    }

    public boolean passesWhiteBlackList(Location loc) {
        boolean passed = false;
        for (ProtectedRegion region : getRegions(loc)) {
            if (TextUtil.containsCaseInsensitive(region.getId(), plugin.getCfg().getWgRegionList())) {
                if (plugin.getCfg().isWgWhitelist()) {
                    passed = true;
                }
            } else if (!plugin.getCfg().isWgWhitelist()) {
                passed = true;
            }
        }
        if (getRegions(loc).size() == 0 && !plugin.getCfg().isWgWhitelist()) {
            passed = true;
        }
        return passed;
    }
}
