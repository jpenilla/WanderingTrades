package fun.ccmc.wanderingtrades.util;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TextUtil {
    public static String colorize(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String[] colorize(String[] s) {
        return (String[]) Arrays.stream(s).map(TextUtil::colorize).toArray();
    }

    public static ArrayList<String> colorize(List<String> stringArray) {
        return stringArray.stream().map(TextUtil::colorize).collect(Collectors.toCollection(ArrayList::new));
    }

    public static boolean containsCaseInsensitive(String s, List<String> l){
        return l.stream().anyMatch(x -> x.equalsIgnoreCase(s));
    }
}
