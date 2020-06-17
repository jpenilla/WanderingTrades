package fun.ccmc.wanderingtrades.compat;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import fun.ccmc.jmplib.TextUtil;
import fun.ccmc.wanderingtrades.WanderingTrades;
import org.bukkit.Location;

public class WorldGuardCompat {
    private final WanderingTrades plugin;

    public WorldGuardCompat(WanderingTrades instance) {
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
        if (getRegions(loc).size() == 0) {
            if (!plugin.getCfg().isWgWhitelist()) {
                passed = true;
            }
        }
        return passed;
    }
}
