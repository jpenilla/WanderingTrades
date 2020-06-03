package fun.ccmc.wanderingtrades.gui;

import fun.ccmc.jmplib.Gui;
import fun.ccmc.jmplib.TextUtil;
import fun.ccmc.wanderingtrades.WanderingTrades;
import fun.ccmc.wanderingtrades.config.Lang;
import fun.ccmc.wanderingtrades.config.LangConfig;
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
    public final LangConfig lang = WanderingTrades.getInstance().getLang();
    public final String gui_toggle_lore = lang.get(Lang.GUI_TOGGLE_LORE);
    public final ItemStack backButton = Gui.buildLore(Material.BARRIER, lang.get(Lang.GUI_BACK), lang.get(Lang.GUI_BACK_LORE));
    public final ItemStack closeButton = Gui.buildLore(Material.BARRIER, lang.get(Lang.GUI_CLOSE), lang.get(Lang.GUI_CLOSE_LORE));

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
