package fun.ccmc.wanderingtrades.gui;

import fun.ccmc.wanderingtrades.util.Gui;
import fun.ccmc.wanderingtrades.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public abstract class GuiHolder implements InventoryHolder {
    protected Inventory inventory;
    public final ItemStack backButton = Gui.buildLore(Material.BARRIER, "&4Back", "&7&o  Click to go back");
    public final ItemStack closeButton = Gui.buildLore(Material.BARRIER, "&4Close", "&7&o  Click to close");

    public GuiHolder(String name, int size) {
        inventory = Bukkit.createInventory(this, size, TextUtil.colorize(name));
    }

    public abstract void onInventoryClick(InventoryClickEvent event);

    public void onInventoryDrag(InventoryDragEvent event) {
    }

    public void onInventoryOpen(InventoryOpenEvent event) {
    }

    public void onInventoryClose(InventoryCloseEvent event) {
    }

    public void open(Player p) {
        p.openInventory(getInventory());
    }
}
