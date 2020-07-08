package xyz.jpenilla.wanderingtrades.util;

import org.bukkit.NamespacedKey;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

public class Constants {
    public static final NamespacedKey CONFIG = new NamespacedKey(WanderingTrades.getInstance(), "wtConfig");
    public static final NamespacedKey PROTECT = new NamespacedKey(WanderingTrades.getInstance(), "wtProtect");
    public static final NamespacedKey REFRESH_NATURAL = new NamespacedKey(WanderingTrades.getInstance(), "wtRefreshNatural");
}
