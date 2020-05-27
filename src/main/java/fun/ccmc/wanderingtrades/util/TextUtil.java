package fun.ccmc.wanderingtrades.util;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class TextUtil {
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

    public static ArrayList<String> colorize(List<String> stringArray) {
        ArrayList<String> colorizedArray = new ArrayList<>();
        stringArray.forEach(s -> {
            colorizedArray.add(colorize(s));
        });
        return colorizedArray;
    }

    public static boolean containsCaseInsensitive(String s, List<String> l){
        return l.stream().anyMatch(x -> x.equalsIgnoreCase(s));
    }
}
