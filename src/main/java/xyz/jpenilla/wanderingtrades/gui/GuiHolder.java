package xyz.jpenilla.wanderingtrades.gui;

import java.util.function.DoubleFunction;
import java.util.function.IntFunction;
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
    protected final LangConfig lang = WanderingTrades.instance().langConfig();
    protected final String gui_toggle_lore = lang.get(Lang.GUI_TOGGLE_LORE);
    protected final ItemStack backButton = new ItemBuilder(Material.BARRIER).setName(lang.get(Lang.GUI_BACK)).setLore(lang.get(Lang.GUI_BACK_LORE)).build();
    protected final ItemStack closeButton = new ItemBuilder(Material.BARRIER).setName(lang.get(Lang.GUI_CLOSE)).setLore(lang.get(Lang.GUI_CLOSE_LORE)).build();
    protected final ItemStack filler = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").build();
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

    private boolean validateInt(
        final @NonNull Player player,
        final @NonNull String input,
        final @NonNull IntFunction<@NonNull Boolean> validator
    ) {
        try {
            int i = Integer.parseInt(input);
            return validator.apply(i);
        } catch (final NumberFormatException ex) {
            WanderingTrades.instance().chat().sendParsed(player, lang.get(Lang.MESSAGE_ENTER_NUMBER));
            return false;
        }
    }

    private boolean validateDouble(
        final @NonNull Player player,
        final @NonNull String input,
        final @NonNull DoubleFunction<@NonNull Boolean> validator
    ) {
        try {
            double d = Double.parseDouble(input);
            return validator.apply(d);
        } catch (final NumberFormatException ex) {
            WanderingTrades.instance().chat().sendParsed(player, lang.get(Lang.MESSAGE_ENTER_NUMBER));
            return false;
        }
    }

    public boolean onValidateIntGT0(final @NonNull Player player, final @NonNull String input) {
        return this.validateInt(player, input, i -> {
            if (i >= 1) {
                return true;
            }
            WanderingTrades.instance().chat().sendParsed(player, lang.get(Lang.MESSAGE_NUMBER_GT_0));
            return false;
        });
    }

    public boolean onValidateIntGTE0(final @NonNull Player player, final @NonNull String input) {
        return this.validateInt(player, input, i -> {
            if (i >= 0) {
                return true;
            }
            WanderingTrades.instance().chat().sendParsed(player, lang.get(Lang.MESSAGE_NUMBER_GTE_0));
            return false;
        });
    }

    public boolean onValidateIntGTEN1(final @NonNull Player player, final @NonNull String input) {
        return this.validateInt(player, input, i -> {
            if (i >= -1) {
                return true;
            }
            WanderingTrades.instance().chat().sendParsed(player, lang.get(Lang.MESSAGE_NUMBER_GTE_N1));
            return false;
        });
    }

    public boolean onValidateDouble0T1(final @NonNull Player player, final @NonNull String input) {
        return this.validateDouble(player, input, d -> {
            if (d < 0 || d > 1) {
                WanderingTrades.instance().chat().sendParsed(player, lang.get(Lang.MESSAGE_NUMBER_0T1));
                return false;
            }
            return true;
        });
    }

    public String onConfirmYesNo(Player player, String s) {
        WanderingTrades.instance().chat().sendParsed(player, lang.get(Lang.MESSAGE_YOU_ENTERED) + s);
        WanderingTrades.instance().chat().sendParsed(player, lang.get(Lang.MESSAGE_YES_NO));
        return "";
    }

    public void onEditCancelled(Player p, String s) {
        WanderingTrades.instance().chat().sendParsed(p, lang.get(Lang.MESSAGE_EDIT_CANCELLED));
        open(p);
    }
}
