package xyz.jpenilla.wanderingtrades.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.jpenilla.jmplib.InputConversation;
import xyz.jpenilla.jmplib.ItemBuilder;
import xyz.jpenilla.jmplib.TextUtil;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Config;
import xyz.jpenilla.wanderingtrades.config.Lang;

public class ConfigEditGui extends GuiHolder {
    private final ItemStack enabledEnabled = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_CONFIG_ENABLED)).setLore(gui_toggle_lore).build();
    private final ItemStack enabledDisabled = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_CONFIG_DISABLED)).setLore(gui_toggle_lore).build();

    private final ItemStack allowMultipleSets = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_CONFIG_ALLOW_MULTIPLE_SETS)).setLore(gui_toggle_lore).build();
    private final ItemStack disallowMultipleSets = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_CONFIG_DISALLOW_MULTIPLE_SETS)).setLore(gui_toggle_lore).build();

    private final ItemStack removeOriginalTradesEnabled = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_CONFIG_REMOVE_ORIGINAL)).setLore(gui_toggle_lore).build();
    private final ItemStack removeOriginalTradesDisabled = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_CONFIG_KEEP_ORIGINAL)).setLore(gui_toggle_lore).build();

    private final ItemStack refreshTradesEnabled = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_CONFIG_REFRESH)).setLore(gui_toggle_lore).build();
    private final ItemStack refreshTradesDisabled = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_CONFIG_NO_REFRESH)).setLore(gui_toggle_lore).build();

    private final ItemStack preventNightInvisibilityEnabled = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_CONFIG_PREVENT_NIGHT_INVISIBILITY)).setLore(gui_toggle_lore).build();
    private final ItemStack preventNightInvisibilityDisabled = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_CONFIG_ALLOW_NIGHT_INVISIBILITY)).setLore(gui_toggle_lore).build();

    private final ItemStack wgWhitelist = new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_CONFIG_WG_WHITE)).setLore(gui_toggle_lore).build();
    private final ItemStack wgBlacklist = new ItemBuilder(Material.BEDROCK).setName(lang.get(Lang.GUI_CONFIG_WG_BLACK)).setLore(gui_toggle_lore).build();

    private final ItemStack wgList = new ItemBuilder(Material.PAPER).setName(lang.get(Lang.GUI_CONFIG_WG_LIST)).build();

    private final ItemStack refreshTradersMinutes = new ItemBuilder(Material.LIGHT_BLUE_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_CONFIG_REFRESH_MINUTES)).build();

    public ConfigEditGui() {
        super(WanderingTrades.instance().langConfig().get(Lang.GUI_CONFIG_TITLE), 45);
    }

    @NonNull
    public Inventory getInventory() {
        inventory.clear();

        Config c = WanderingTrades.instance().config();

        if (c.enabled()) {
            inventory.setItem(10, enabledEnabled);
        } else {
            inventory.setItem(10, enabledDisabled);
        }

        if (c.allowMultipleSets()) {
            inventory.setItem(12, allowMultipleSets);
        } else {
            inventory.setItem(12, disallowMultipleSets);
        }

        if (c.removeOriginalTrades()) {
            inventory.setItem(14, removeOriginalTradesEnabled);
        } else {
            inventory.setItem(14, removeOriginalTradesDisabled);
        }

        if (c.refreshCommandTraders()) {
            inventory.setItem(16, refreshTradesEnabled);
        } else {
            inventory.setItem(16, refreshTradesDisabled);
        }

        if (c.preventNightInvisibility()) {
            inventory.setItem(28, preventNightInvisibilityEnabled);
        } else {
            inventory.setItem(28, preventNightInvisibilityDisabled);
        }

        ArrayList<String> refreshLore =
            new ArrayList<>(Arrays.asList(
                lang.get(Lang.GUI_CONFIG_REFRESH_MINUTES_LORE).replace("{VALUE}", String.valueOf(c.refreshCommandTradersMinutes())),
                lang.get(Lang.GUI_EDIT_LORE)
            ));
        inventory.setItem(30, new ItemBuilder(refreshTradersMinutes).setLore(refreshLore).build());

        if (c.wgWhitelist()) {
            inventory.setItem(32, wgWhitelist);
        } else {
            inventory.setItem(32, wgBlacklist);
        }

        ArrayList<String> wgListLore = new ArrayList<>(Arrays.asList(
            lang.get(Lang.GUI_CONFIG_WG_LIST_LORE),
            ""
        ));
        c.wgRegionList().forEach(region -> wgListLore.add(" <aqua>-</aqua> <white>" + region));
        inventory.setItem(34, new ItemBuilder(wgList).setLore(wgListLore).build());

        inventory.setItem(inventory.getSize() - 1, closeButton);

        IntStream.range(0, inventory.getSize()).forEach(slot -> {
            if (inventory.getItem(slot) == null) {
                inventory.setItem(slot, filler);
            }
        });

        return inventory;
    }

    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        Player p = (Player) event.getWhoClicked();
        ClickType click = event.getClick();
        if (event.getSlot() != event.getRawSlot()) {
            if (click.isKeyboardClick() || click.isShiftClick()) {
                event.setCancelled(true);
            }
            return;
        }
        event.setCancelled(true);

        if (closeButton.isSimilar(item)) {
            p.closeInventory();
        }

        Config c = WanderingTrades.instance().config();

        if (enabledEnabled.isSimilar(item)) {
            c.enabled(false);
        } else if (enabledDisabled.isSimilar(item)) {
            c.enabled(true);
        }

        if (allowMultipleSets.isSimilar(item)) {
            c.allowMultipleSets(false);
        } else if (disallowMultipleSets.isSimilar(item)) {
            c.allowMultipleSets(true);
        }

        if (removeOriginalTradesEnabled.isSimilar(item)) {
            c.removeOriginalTrades(false);
        } else if (removeOriginalTradesDisabled.isSimilar(item)) {
            c.removeOriginalTrades(true);
        }

        if (refreshTradesEnabled.isSimilar(item)) {
            c.refreshCommandTraders(false);
        } else if (refreshTradesDisabled.isSimilar(item)) {
            c.refreshCommandTraders(true);
        }

        if (preventNightInvisibilityEnabled.isSimilar(item)) {
            c.preventNightInvisibility(false);
        } else if (preventNightInvisibilityDisabled.isSimilar(item)) {
            c.preventNightInvisibility(true);
        }

        if (wgBlacklist.isSimilar(item)) {
            c.wgWhitelist(true);
        } else if (wgWhitelist.isSimilar(item)) {
            c.wgWhitelist(false);
        }

        if (refreshTradersMinutes.isSimilar(item)) {
            p.closeInventory();
            new InputConversation()
                .onPromptText(player -> {
                    WanderingTrades.instance().chat().sendParsed(player,
                        lang.get(Lang.MESSAGE_SET_REFRESH_DELAY_PROMPT)
                            + "<reset>\n" + lang.get(Lang.MESSAGE_CURRENT_VALUE) + c.refreshCommandTradersMinutes()
                            + "<reset>\n" + lang.get(Lang.MESSAGE_ENTER_NUMBER));
                    return "";
                })
                .onValidateInput(this::onValidateIntGTE0)
                .onConfirmText(this::onConfirmYesNo)
                .onAccepted((player, s) -> {
                    WanderingTrades.instance().chat().sendParsed(player, lang.get(Lang.MESSAGE_EDIT_SAVED));
                    c.refreshCommandTradersMinutes(Integer.parseInt(s));
                    c.save();
                    open(p);
                })
                .onDenied(this::onEditCancelled)
                .start(p);
        }

        if (wgList.isSimilar(item)) {
            if (click.isRightClick()) {
                List<String> l = c.wgRegionList();
                if (!(l.size() - 1 < 0)) {
                    l.remove(l.size() - 1);
                }
                c.wgRegionList(c.wgRegionList());
            } else {
                p.closeInventory();
                new InputConversation()
                    .onPromptText(player -> {
                        WanderingTrades.instance().chat().sendParsed(player, lang.get(Lang.MESSAGE_ADD_WG_REGION));
                        return "";
                    })
                    .onValidateInput((player, input) -> {
                        if (input.contains(" ")) {
                            WanderingTrades.instance().chat().sendParsed(player, lang.get(Lang.MESSAGE_NO_SPACES));
                            return false;
                        }
                        if (TextUtil.containsCaseInsensitive(input, c.wgRegionList())) {
                            WanderingTrades.instance().chat().sendParsed(player, lang.get(Lang.MESSAGE_CREATE_UNIQUE));
                            return false;
                        }
                        return true;
                    })
                    .onConfirmText(this::onConfirmYesNo)
                    .onAccepted((player, s) -> {
                        List<String> temp = c.wgRegionList();
                        temp.add(s);
                        c.wgRegionList(temp);
                        c.save();
                        WanderingTrades.instance().chat().sendParsed(player, lang.get(Lang.MESSAGE_EDIT_SAVED));
                        open(p);
                    })
                    .onDenied(this::onEditCancelled)
                    .start(p);
            }
        }

        c.save();

        getInventory();
    }

    public void reOpen(Player p) {
        new ConfigEditGui().open(p);
    }
}
