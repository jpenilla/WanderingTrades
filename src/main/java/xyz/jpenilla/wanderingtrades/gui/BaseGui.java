package xyz.jpenilla.wanderingtrades.gui;

import java.util.function.DoubleFunction;
import java.util.function.IntFunction;
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
import xyz.jpenilla.pluginbase.legacy.ItemBuilder;
import xyz.jpenilla.pluginbase.legacy.MiniMessageUtil;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Lang;
import xyz.jpenilla.wanderingtrades.config.LangConfig;

public abstract class BaseGui implements InventoryHolder {
    protected final LangConfig lang;
    protected final String gui_toggle_lore;
    protected final ItemStack backButton;
    protected final ItemStack closeButton;
    protected final ItemStack filler = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").build();
    protected final WanderingTrades plugin;
    protected Inventory inventory;

    public BaseGui(WanderingTrades plugin, String name, int size) {
        this.plugin = plugin;
        this.inventory = plugin.getServer().createInventory(this, size, MiniMessageUtil.miniMessageToLegacy(name));

        this.lang = this.plugin.langConfig();

        this.gui_toggle_lore = this.lang.get(Lang.GUI_TOGGLE_LORE);
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

    public void open(final @NonNull Player p) {
        p.openInventory(getInventory());
    }

    public abstract void reOpen(final Player p);

    public boolean validateIntRange(final Player p, final String s) {
        if (s.contains(":")) {
            try {
                String[] split = s.split(":");
                if (validateIntForRange(null, split[0]) && validateIntForRange(null, split[1])) {
                    return true;
                } else {
                    this.plugin.chat().sendParsed(p, this.plugin.langConfig().get(Lang.MESSAGE_ENTER_NUMBER_OR_RANGE));
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        } else {
            return validateIntForRange(p, s);
        }
    }

    private boolean validateIntForRange(final Player player, final String input) {
        try {
            int i = Integer.parseInt(input);
            if (i < 0) {
                this.plugin.chat().sendParsed(player, this.plugin.langConfig().get(Lang.MESSAGE_NUMBER_GTE_0));
                return false;
            }
        } catch (NumberFormatException ex) {
            this.plugin.chat().sendParsed(player, this.plugin.langConfig().get(Lang.MESSAGE_ENTER_NUMBER_OR_RANGE));
            return false;
        }
        return true;
    }

    private boolean validateInt(
        final @NonNull Player player,
        final @NonNull String input,
        final @NonNull IntFunction<@NonNull Boolean> validator
    ) {
        try {
            int i = Integer.parseInt(input);
            return validator.apply(i);
        } catch (final NumberFormatException ex) {
            this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_ENTER_NUMBER));
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
            this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_ENTER_NUMBER));
            return false;
        }
    }

    public boolean onValidateIntGT0(final @NonNull Player player, final @NonNull String input) {
        return this.validateInt(player, input, i -> {
            if (i >= 1) {
                return true;
            }
            this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_NUMBER_GT_0));
            return false;
        });
    }

    public boolean onValidateIntGTE0(final @NonNull Player player, final @NonNull String input) {
        return this.validateInt(player, input, i -> {
            if (i >= 0) {
                return true;
            }
            this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_NUMBER_GTE_0));
            return false;
        });
    }

    public boolean onValidateIntGTEN1(final @NonNull Player player, final @NonNull String input) {
        return this.validateInt(player, input, i -> {
            if (i >= -1) {
                return true;
            }
            this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_NUMBER_GTE_N1));
            return false;
        });
    }

    public boolean onValidateDouble0T1(final @NonNull Player player, final @NonNull String input) {
        return this.validateDouble(player, input, d -> {
            if (d < 0 || d > 1) {
                this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_NUMBER_0T1));
                return false;
            }
            return true;
        });
    }

    public String onConfirmYesNo(final Player player, final String s) {
        this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_YOU_ENTERED) + s);
        this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_YES_NO));
        return "";
    }

    public void onEditCancelled(final Player p, final String s) {
        this.plugin.chat().sendParsed(p, this.lang.get(Lang.MESSAGE_EDIT_CANCELLED));
        this.open(p);
    }
}
