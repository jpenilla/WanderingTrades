package fun.ccmc.wt.util;

import org.bukkit.ChatColor;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TextFormatting {
    public static String colorize(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String[] colorize(String[] s) {
        int index = 0;
        for (String e: s) {
            s[index] = colorize(e);
            index++;
        }

        return s;
    }
}
