package fun.ccmc.wanderingtrades.gui;

import fun.ccmc.wanderingtrades.util.TextUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class GuiBuilder {
    @Getter
    private String title;
    @Getter
    private int size;
    @Getter
    private Inventory gui;

    public GuiBuilder(String title, int size) {
        this.title = TextUtil.colorize(title);
        this.size = size;
        gui = Bukkit.createInventory(null, size, this.title);
        addItems();
    }

    public void openGui(Player p) {
        p.openInventory(gui);
    }

    public void addItems() {
    }

    public ItemStack build(Material m, String name, ArrayList<String> lore) {
        ItemStack is = new ItemStack(m);
        ItemMeta itemMeta = is.getItemMeta();
        itemMeta.setDisplayName(TextUtil.colorize(name));
        if(lore != null) {
            itemMeta.setLore(TextUtil.colorize(lore));
        }
        is.setItemMeta(itemMeta);
        return is;
    }

    public ItemStack build(Material m, String name) {
        return build(m, name, null);
    }
}
