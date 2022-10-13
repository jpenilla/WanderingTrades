package xyz.jpenilla.wanderingtrades.gui;

import java.util.function.DoublePredicate;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import xyz.jpenilla.pluginbase.legacy.ItemBuilder;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Lang;
import xyz.jpenilla.wanderingtrades.config.LangConfig;
import xyz.jpenilla.wanderingtrades.util.Inventories;

@DefaultQualifier(NonNull.class)
public abstract class BaseGui implements InventoryHolder {
    protected final LangConfig lang;
    protected final String toggleLore;
    protected final ItemStack backButton;
    protected final ItemStack closeButton;
    protected final ItemStack filler = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").build();
    protected final WanderingTrades plugin;
    protected final Inventory inventory;

    public BaseGui(final WanderingTrades plugin, final String name, final int size) {
        this.plugin = plugin;
        this.inventory = Inventories.createInventory(this, size, name);

        this.lang = this.plugin.langConfig();

        this.toggleLore = this.lang.get(Lang.GUI_TOGGLE_LORE);
        this.backButton = new ItemBuilder(Material.BARRIER)
            .setName(this.lang.get(Lang.GUI_BACK))
            .setLore(this.lang.get(Lang.GUI_BACK_LORE))
            .build();
        this.closeButton = new ItemBuilder(Material.BARRIER)
            .setName(this.lang.get(Lang.GUI_CLOSE))
            .setLore(this.lang.get(Lang.GUI_CLOSE_LORE))
            .build();
    }

    public final void handleClick(final InventoryClickEvent event) {
        if (this.onInventoryClick0(event)) {
            this.onInventoryClick(event);
        }
    }

    protected boolean onInventoryClick0(final InventoryClickEvent event) {
        final ClickType click = event.getClick();
        if (event.getSlot() != event.getRawSlot()) {
            if (click.isKeyboardClick() || click.isShiftClick()) {
                event.setCancelled(true);
            }
            return false;
        }
        event.setCancelled(true);
        return true;
    }

    protected abstract void onInventoryClick(final InventoryClickEvent event);

    public void onInventoryDrag(final InventoryDragEvent event) {
    }

    public void onInventoryOpen(final InventoryOpenEvent event) {
    }

    public void onInventoryClose(final InventoryCloseEvent event) {
    }

    public void open(final Player player) {
        player.openInventory(this.getInventory());
    }

    public abstract void reOpen(final Player p);

    protected void fillEmptySlots() {
        IntStream.range(0, this.inventory.getSize()).forEach(slot -> {
            if (this.inventory.getItem(slot) == null) {
                this.inventory.setItem(slot, this.filler);
            }
        });
    }

    protected boolean validateIntRange(final Player player, final String s) {
        if (s.contains(":")) {
            try {
                String[] split = s.split(":");
                return this.validateIntGTE0(player, split[0]) && this.validateIntGTE0(player, split[1]);
            } catch (Exception e) {
                return false;
            }
        } else {
            return this.validateIntGTE0(player, s);
        }
    }

    private boolean validateInt(
        final String input,
        final IntPredicate validator
    ) {
        try {
            int i = Integer.parseInt(input);
            return validator.test(i);
        } catch (final NumberFormatException ex) {
            return false;
        }
    }

    private boolean validateDouble(
        final String input,
        final DoublePredicate validator
    ) {
        try {
            double d = Double.parseDouble(input);
            return validator.test(d);
        } catch (final NumberFormatException ex) {
            return false;
        }
    }

    protected boolean validateIntGT0(final Player player, final String input) {
        return this.validateInt(input, i -> {
            if (i >= 1) {
                return true;
            }
            this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_NUMBER_GT_0));
            return false;
        });
    }

    protected boolean validateIntGTE0(final Player player, final String input) {
        return this.validateInt(input, i -> {
            if (i >= 0) {
                return true;
            }
            this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_NUMBER_GTE_0));
            return false;
        });
    }

    protected boolean validateIntGTEN1(final Player player, final String input) {
        return this.validateInt(input, i -> {
            if (i >= -1) {
                return true;
            }
            this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_NUMBER_GTE_N1));
            return false;
        });
    }

    protected boolean validateDouble0T1(final Player player, final String input) {
        return this.validateDouble(input, d -> {
            if (d < 0 || d > 1) {
                this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_NUMBER_0T1));
                return false;
            }
            return true;
        });
    }

    protected String confirmYesNo(final Player player, final String s) {
        this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_YOU_ENTERED) + s);
        this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_YES_NO));
        return "";
    }

    protected void editCancelled(final Player p, final String s) {
        this.plugin.chat().sendParsed(p, this.lang.get(Lang.MESSAGE_EDIT_CANCELLED));
        this.open(p);
    }
}
