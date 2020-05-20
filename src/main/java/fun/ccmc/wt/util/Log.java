package fun.ccmc.wt.util;

import fun.ccmc.wt.WanderingTrades;

public class Log {
    public static void info(String s) {
        WanderingTrades.plugin.getLogger().info(TextFormatting.colorize(s));
    }

    public static void warn(String s) {
        WanderingTrades.plugin.getLogger().warning(TextFormatting.colorize(s));
    }

    public static void err(String s) {
        WanderingTrades.plugin.getLogger().severe(TextFormatting.colorize(s));
    }

    public static void debug(String s) {
        if(Config.getDebug()) {
            WanderingTrades.plugin.getLogger().info(TextFormatting.colorize("&e[DEBUG] &r" + s));
        }
    }
}
