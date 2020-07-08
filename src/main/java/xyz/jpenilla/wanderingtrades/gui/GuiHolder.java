package xyz.jpenilla.wanderingtrades.gui;

import lombok.NonNull;
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
import xyz.jpenilla.jmplib.ItemBuilder;
import xyz.jpenilla.jmplib.MiniMessageUtil;
import xyz.jpenilla.jmplib.TextUtil;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Lang;
import xyz.jpenilla.wanderingtrades.config.LangConfig;

public abstract class GuiHolder implements InventoryHolder {
    protected Inventory inventory;
    public final LangConfig lang = WanderingTrades.getInstance().getLang();
    public final String gui_toggle_lore = lang.get(Lang.GUI_TOGGLE_LORE);
    public final ItemStack backButton = new ItemBuilder(Material.BARRIER).setName(lang.get(Lang.GUI_BACK)).setLore(lang.get(Lang.GUI_BACK_LORE)).build();
    public final ItemStack closeButton = new ItemBuilder(Material.BARRIER).setName(lang.get(Lang.GUI_CLOSE)).setLore(lang.get(Lang.GUI_CLOSE_LORE)).build();
    public final ItemStack filler = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").build();

    public GuiHolder(String name, int size) {
        inventory = Bukkit.createInventory(this, size, MiniMessageUtil.miniMessageToLegacy(name));
    }

    public abstract void onInventoryClick(InventoryClickEvent event);

    public void onInventoryDrag(InventoryDragEvent event) {
    }

    public void onInventoryOpen(InventoryOpenEvent event) {
    }

    public void onInventoryClose(InventoryCloseEvent event) {
    }

    public void open(@NonNull Player p) {
        p.openInventory(getInventory());
    }
}
