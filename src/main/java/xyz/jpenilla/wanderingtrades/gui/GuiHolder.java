package xyz.jpenilla.wanderingtrades.gui;

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
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.jpenilla.jmplib.ItemBuilder;
import xyz.jpenilla.jmplib.MiniMessageUtil;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Lang;
import xyz.jpenilla.wanderingtrades.config.LangConfig;

public abstract class GuiHolder implements InventoryHolder {
    public final LangConfig lang = WanderingTrades.getInstance().getLang();
    public final String gui_toggle_lore = lang.get(Lang.GUI_TOGGLE_LORE);
    public final ItemStack backButton = new ItemBuilder(Material.BARRIER).setName(lang.get(Lang.GUI_BACK)).setLore(lang.get(Lang.GUI_BACK_LORE)).build();
    public final ItemStack closeButton = new ItemBuilder(Material.BARRIER).setName(lang.get(Lang.GUI_CLOSE)).setLore(lang.get(Lang.GUI_CLOSE_LORE)).build();
    public final ItemStack filler = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").build();
    protected Inventory inventory;

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

    public abstract void reOpen(Player p);

    public boolean onValidateIntGT0(Player player, String input) {
        try {
            int i = Integer.parseInt(input);
            if (i < 1) {
                WanderingTrades.getInstance().getChat().sendParsed(player, lang.get(Lang.MESSAGE_NUMBER_GT_0));
                return false;
            }
        } catch (NumberFormatException ex) {
            WanderingTrades.getInstance().getChat().sendParsed(player, lang.get(Lang.MESSAGE_ENTER_NUMBER));
            return false;
        }
        return true;
    }

    public boolean onValidateIntGTE0(Player player, String input) {
        try {
            int i = Integer.parseInt(input);
            if (i < 0) {
                WanderingTrades.getInstance().getChat().sendParsed(player, lang.get(Lang.MESSAGE_NUMBER_GTE_0));
                return false;
            }
        } catch (NumberFormatException ex) {
            WanderingTrades.getInstance().getChat().sendParsed(player, lang.get(Lang.MESSAGE_ENTER_NUMBER));
            return false;
        }
        return true;
    }

    public boolean onValidateIntGTEN1(Player player, String input) {
        try {
            int i = Integer.parseInt(input);
            if (i < -1) {
                WanderingTrades.getInstance().getChat().sendParsed(player, lang.get(Lang.MESSAGE_NUMBER_GTE_N1));
                return false;
            }
        } catch (NumberFormatException ex) {
            WanderingTrades.getInstance().getChat().sendParsed(player, lang.get(Lang.MESSAGE_ENTER_NUMBER));
            return false;
        }
        return true;
    }

    public boolean onValidateDouble0T1(Player player, String input) {
        try {
            double d = Double.parseDouble(input);
            if (d < 0 || d > 1) {
                WanderingTrades.getInstance().getChat().sendParsed(player, lang.get(Lang.MESSAGE_NUMBER_0T1));
                return false;
            }
        } catch (NumberFormatException ex) {
            WanderingTrades.getInstance().getChat().sendParsed(player, lang.get(Lang.MESSAGE_ENTER_NUMBER));
            return false;
        }
        return true;
    }

    public String onConfirmYesNo(Player player, String s) {
        WanderingTrades.getInstance().getChat().sendParsed(player, lang.get(Lang.MESSAGE_YOU_ENTERED) + s);
        WanderingTrades.getInstance().getChat().sendParsed(player, lang.get(Lang.MESSAGE_YES_NO));
        return "";
    }

    public void onEditCancelled(Player p, String s) {
        WanderingTrades.getInstance().getChat().sendParsed(p, lang.get(Lang.MESSAGE_EDIT_CANCELLED));
        open(p);
    }
}
