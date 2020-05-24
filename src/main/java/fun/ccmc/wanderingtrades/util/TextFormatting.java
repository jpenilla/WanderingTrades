package fun.ccmc.wanderingtrades.util;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

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

    public static ArrayList<String> colorize(List<String> s) {
        ArrayList<String> l = new ArrayList<>();
        for (String e: s) {
            String r = colorize(e);
            l.add(r);
        }

        return l;
    }
}
