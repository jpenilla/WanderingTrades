package fun.ccmc.wanderingtrades.gui;

import fun.ccmc.jmplib.ItemBuilder;
import fun.ccmc.wanderingtrades.WanderingTrades;
import fun.ccmc.wanderingtrades.config.Config;
import fun.ccmc.wanderingtrades.config.Lang;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class ConfigGui extends GuiHolder {
    private final ItemStack enabledEnabled = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_CONFIG_ENABLED)).setLore(gui_toggle_lore).build();
    private final ItemStack enabledDisabled = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_CONFIG_DISABLED)).setLore(gui_toggle_lore).build();

    private final ItemStack allowMultipleSets = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_CONFIG_ALLOW_MULTIPLE_SETS)).setLore(gui_toggle_lore).build();
    private final ItemStack disallowMultipleSets = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_CONFIG_DISALLOW_MULTIPLE_SETS)).setLore(gui_toggle_lore).build();

    private final ItemStack removeOriginalTradesEnabled = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_CONFIG_REMOVE_ORIGINAL)).setLore(gui_toggle_lore).build();
    private final ItemStack removeOriginalTradesDisabled = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_CONFIG_KEEP_ORIGINAL)).setLore(gui_toggle_lore).build();

    private final ItemStack refreshTradesEnabled = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_CONFIG_REFRESH)).setLore(gui_toggle_lore).build();
    private final ItemStack refreshTradesDisabled = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_CONFIG_NO_REFRESH)).setLore(gui_toggle_lore).build();

    private final ItemStack wgWhitelist = new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_CONFIG_WG_WHITE)).setLore(gui_toggle_lore).build();
    private final ItemStack wgBlacklist = new ItemBuilder(Material.BEDROCK).setName(lang.get(Lang.GUI_CONFIG_WG_BLACK)).setLore(gui_toggle_lore).build();

    private final ItemStack wgList = new ItemBuilder(Material.PAPER).setName(lang.get(Lang.GUI_CONFIG_WG_LIST)).build();

    private final ItemStack refreshTradersMinutes = new ItemBuilder(Material.LIGHT_BLUE_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_CONFIG_REFRESH_MINUTES)).build();

    public ConfigGui() {
        super(WanderingTrades.getInstance().getLang().get(Lang.GUI_CONFIG_TITLE), 45);
    }

    public Inventory getInventory() {
        inventory.clear();

        Config c = WanderingTrades.getInstance().getCfg();

        if (c.isEnabled()) {
            inventory.setItem(10, enabledEnabled);
        } else {
            inventory.setItem(10, enabledDisabled);
        }

        if (c.isAllowMultipleSets()) {
            inventory.setItem(12, allowMultipleSets);
        } else {
            inventory.setItem(12, disallowMultipleSets);
        }

        if (c.isRemoveOriginalTrades()) {
            inventory.setItem(14, removeOriginalTradesEnabled);
        } else {
            inventory.setItem(14, removeOriginalTradesDisabled);
        }

        if (c.isRefreshCommandTraders()) {
            inventory.setItem(16, refreshTradesEnabled);
        } else {
            inventory.setItem(16, refreshTradesDisabled);
        }

        ArrayList<String> refreshLore =
                new ArrayList<>(Arrays.asList(
                        lang.get(Lang.GUI_CONFIG_REFRESH_MINUTES_LORE).replace("{VALUE}", String.valueOf(c.getRefreshCommandTradersMinutes())),
                        lang.get(Lang.GUI_EDIT_LORE)
                ));
        inventory.setItem(28, new ItemBuilder(refreshTradersMinutes).setLore(refreshLore).build());

        if (c.isWgWhitelist()) {
            inventory.setItem(30, wgWhitelist);
        } else {
            inventory.setItem(30, wgBlacklist);
        }

        ArrayList<String> wgListLore = new ArrayList<>(Arrays.asList(
                lang.get(Lang.GUI_CONFIG_WG_LIST_LORE),
                ""
        ));
        c.getWgRegionList().forEach(region -> wgListLore.add(" &b- &f" + region));
        inventory.setItem(32, new ItemBuilder(wgList).setLore(wgListLore).build());

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

        Config c = WanderingTrades.getInstance().getCfg();

        if (enabledEnabled.isSimilar(item)) {
            c.setEnabled(false);
        } else if (enabledDisabled.isSimilar(item)) {
            c.setEnabled(true);
        }

        if (allowMultipleSets.isSimilar(item)) {
            c.setAllowMultipleSets(false);
        } else if (disallowMultipleSets.isSimilar(item)) {
            c.setAllowMultipleSets(true);
        }

        if (removeOriginalTradesEnabled.isSimilar(item)) {
            c.setRemoveOriginalTrades(false);
        } else if (removeOriginalTradesDisabled.isSimilar(item)) {
            c.setRemoveOriginalTrades(true);
        }

        if (refreshTradesEnabled.isSimilar(item)) {
            c.setRefreshCommandTraders(false);
        } else if (refreshTradesDisabled.isSimilar(item)) {
            c.setRefreshCommandTraders(true);
        }

        if (wgBlacklist.isSimilar(item)) {
            c.setWgWhitelist(true);
        } else if (wgWhitelist.isSimilar(item)) {
            c.setWgWhitelist(false);
        }

        if (refreshTradersMinutes.isSimilar(item)) {
            p.closeInventory();
            new AnvilGUI.Builder()
                    .onClose(this::reOpen)
                    .onComplete((player, text) -> {
                        try {
                            int i = Integer.parseInt(text);
                            if (i < 0) {
                                return AnvilGUI.Response.text(lang.get(Lang.GUI_ANVIL_NUMBER_GTE_0));
                            } else {
                                c.setRefreshCommandTradersMinutes(i);
                            }
                        } catch (NumberFormatException ex) {
                            return AnvilGUI.Response.text(lang.get(Lang.GUI_ANVIL_ENTER_NUMBER));
                        }
                        return AnvilGUI.Response.close();
                    })
                    .text(String.valueOf(c.getRefreshCommandTradersMinutes()))
                    .item(new ItemStack(Material.WRITABLE_BOOK))
                    .title(lang.get(Lang.GUI_ANVIL_SET_REFRESH_DELAY_TITLE))
                    .plugin(WanderingTrades.getInstance())
                    .open(p);
        }

        if (wgList.isSimilar(item)) {
            if (click.isRightClick()) {
                List<String> l = c.getWgRegionList();
                l.remove(l.size() - 1);
                c.setWgRegionList(c.getWgRegionList());
            } else {
                new AnvilGUI.Builder()
                        .onClose(this::reOpen)
                        .onComplete((player, text) -> {
                            if (!text.contains(" ")) {
                                List<String> temp = c.getWgRegionList();
                                temp.add(text);
                                c.setWgRegionList(temp);
                                c.save();
                            } else {
                                return AnvilGUI.Response.text(lang.get(Lang.GUI_ANVIL_NO_SPACES));
                            }
                            return AnvilGUI.Response.close();
                        })
                        .text(lang.get(Lang.GUI_ANVIL_TYPE_HERE))
                        .item(new ItemStack(Material.WRITABLE_BOOK))
                        .title(lang.get(Lang.GUI_ANVIL_NEW_LIST_ITEM))
                        .plugin(WanderingTrades.getInstance())
                        .open(p);
            }
        }

        c.save();

        getInventory();
    }

    private void reOpen(Player p) {
        Bukkit.getServer().getScheduler().runTaskLater(WanderingTrades.getInstance(), () -> new ConfigGui().open(p), 1L);
    }
}
