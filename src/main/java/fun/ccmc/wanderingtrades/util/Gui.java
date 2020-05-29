package fun.ccmc.wanderingtrades.util;

import com.deanveloper.skullcreator.SkullCreator;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;

public class Gui {

    private static ItemStack build(ItemStack is, String name, ArrayList<String> lore) {
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(TextUtil.colorize(name));
        meta.setDisplayName(TextUtil.colorize(name));
        if (lore != null) {
            meta.setLore(TextUtil.colorize(lore));
        }
        is.setItemMeta(meta);
        return is;
    }

    public static ItemStack build(Material m, String name, ArrayList<String> lore) {
        return build(new ItemStack(m), name, lore);
    }

    public static ItemStack buildLore(Material m, String name, String lore) {
        ArrayList<String> lores = new ArrayList<>(Collections.singletonList(lore));
        return build(m, name, lores);
    }

    public static ItemStack build(Material m, String name) {
        return build(m, name, null);
    }

    public static ItemStack build(Material m) {
        return build(m, " ");
    }

    public static ItemStack buildHead(String name, ArrayList<String> lore, String base64) {
        ItemStack is = SkullCreator.itemFromBase64(base64);
        return build(is, name, lore);
    }

    public static ItemStack buildHeadLore(String name, String lore, String base64) {
        ArrayList<String> lores = new ArrayList<>(Collections.singletonList(lore));
        return buildHead(name, lores, base64);
    }

    public static ItemStack buildHead(String name, String base64) {
        return buildHead(name, null, base64);
    }

}
