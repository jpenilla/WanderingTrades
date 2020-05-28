package fun.ccmc.wanderingtrades.gui;

import fun.ccmc.wanderingtrades.util.TextUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;

public class GuiManager {

    public void openConfigListGui(Player p) {
        ConfigListGui gui = new ConfigListGui();
        p.openInventory(gui.getInventory());
    }

    public void openTradeListGui(Player p, String config) {
        TradeListGui tradeListGui = new TradeListGui(config);
        p.openInventory(tradeListGui.getInventory());
    }

    public void openConfigEditGui(Player p, String config) {
        EditConfigGui gui = new EditConfigGui(config);
        p.openInventory(gui.getInventory());
    }

    public static ItemStack build(Material m, String name, ArrayList<String> lore) {
        ItemStack is = new ItemStack(m);
        ItemMeta itemMeta = is.getItemMeta();
        itemMeta.setDisplayName(TextUtil.colorize(name));
        if (lore != null) {
            itemMeta.setLore(TextUtil.colorize(lore));
        }
        is.setItemMeta(itemMeta);
        return is;
    }

    public static ItemStack buildBlank(Material m) {
        ItemStack is = new ItemStack(m);
        ItemMeta itemMeta = is.getItemMeta();
        itemMeta.setDisplayName("");
        is.setItemMeta(itemMeta);
        return is;
    }

    public static ItemStack buildSingleLore(Material m, String name, String lore) {
        ArrayList<String> lores = new ArrayList<>(Collections.singletonList(lore));
        return build(m, name, lores);
    }

    public static ItemStack build(Material m, String name) {
        return build(m, name, null);
    }
}
