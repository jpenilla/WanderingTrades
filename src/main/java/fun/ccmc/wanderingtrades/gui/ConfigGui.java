package fun.ccmc.wanderingtrades.gui;

import fun.ccmc.jmplib.Gui;
import fun.ccmc.jmplib.GuiHolder;
import fun.ccmc.jmplib.TextUtil;
import fun.ccmc.wanderingtrades.WanderingTrades;
import fun.ccmc.wanderingtrades.config.Config;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class ConfigGui extends GuiHolder {
    private final ItemStack enabledEnabled = Gui.buildLore(Material.LIME_STAINED_GLASS_PANE, "&aAdding Trades to Natural Spawned Traders", "  &7&oClick to toggle");
    private final ItemStack enabledDisabled = Gui.buildLore(Material.RED_STAINED_GLASS_PANE, "&4Not Adding Trades to Natural Spawned Traders", "  &7&oClick to toggle");

    private final ItemStack allowMultipleSets = Gui.buildLore(Material.LIME_STAINED_GLASS_PANE, "&aAllowing more than one trade config per trader", "  &7&oClick to toggle");
    private final ItemStack disallowMultipleSets = Gui.buildLore(Material.RED_STAINED_GLASS_PANE, "&4Not allowing more than one trade config per trader", "  &7&oClick to toggle");

    private final ItemStack removeOriginalTradesEnabled = Gui.buildLore(Material.LIME_STAINED_GLASS_PANE, "&aRemoving original Wandering Trader trades", "  &7&oClick to toggle");
    private final ItemStack removeOriginalTradesDisabled = Gui.buildLore(Material.RED_STAINED_GLASS_PANE, "&4Not removing original Wandering Trader trades", "  &7&oClick to toggle");

    private final ItemStack refreshTradesEnabled = Gui.buildLore(Material.LIME_STAINED_GLASS_PANE, "&aAuto-refreshing trades of /wt summoned traders", "  &7&oClick to toggle");
    private final ItemStack refreshTradesDisabled = Gui.buildLore(Material.RED_STAINED_GLASS_PANE, "&4Not auto-refreshing trades of /wt summoned traders", "  &7&oClick to toggle");

    private final ItemStack wgWhitelist = Gui.buildLore(Material.WHITE_STAINED_GLASS_PANE, "&bMode&7: &fWorldGuard Region Whitelist", "  &7&oClick to toggle");
    private final ItemStack wgBlacklist = Gui.buildLore(Material.BEDROCK, "&bMode&7: &fWorldGuard Region Blacklist", "  &7&oClick to toggle");

    private final ItemStack wgList = Gui.build(Material.PAPER, "&bList of regions for White/Black list");

    private final ItemStack refreshTradersMinutes = Gui.build(Material.LIGHT_BLUE_STAINED_GLASS_PANE, "&aHow many minutes between trade refreshes if enabled");

    public ConfigGui() {
        super("&d&lWanderingTrades&7: &fConfig", 45);
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

        ItemMeta refreshMinsMeta = refreshTradersMinutes.getItemMeta();
        ArrayList<String> refreshLore =
                new ArrayList<>(Arrays.asList(
                        "Value&7: &b" + c.getRefreshCommandTradersMinutes() + " minutes",
                        "  &7&oClick to edit"
                ));
        refreshMinsMeta.setLore(TextUtil.colorize(refreshLore));
        refreshTradersMinutes.setItemMeta(refreshMinsMeta);
        inventory.setItem(28, refreshTradersMinutes);

        if (c.isWgWhitelist()) {
            inventory.setItem(30, wgWhitelist);
        } else {
            inventory.setItem(30, wgBlacklist);
        }

        ItemMeta wgListMeta = wgList.getItemMeta();
        ArrayList<String> wgListLore = new ArrayList<String>(Arrays.asList(
                "  &7Left-click to add, Right-click to remove",
                ""
        ));
        c.getWgRegionList().forEach(region -> wgListLore.add(" &b- &f" + region));
        wgListMeta.setLore(TextUtil.colorize(wgListLore));
        wgList.setItemMeta(wgListMeta);
        inventory.setItem(32, wgList);

        inventory.setItem(inventory.getSize() - 1, closeButton);

        IntStream.range(0, inventory.getSize()).forEach(slot -> {
            if (inventory.getItem(slot) == null) {
                inventory.setItem(slot, Gui.build(Material.GRAY_STAINED_GLASS_PANE));
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
                                return AnvilGUI.Response.text("Number must be >= 0");
                            } else {
                                c.setRefreshCommandTradersMinutes(i);
                            }
                        } catch (NumberFormatException ex) {
                            return AnvilGUI.Response.text("Enter a number");
                        }
                        return AnvilGUI.Response.close();
                    })
                    .text(c.getRefreshCommandTradersMinutes() + "")
                    .item(new ItemStack(Material.WRITABLE_BOOK))
                    .title("Set Refresh Minutes Delay")
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
                                return AnvilGUI.Response.text("No spaces");
                            }
                            return AnvilGUI.Response.close();
                        })
                        .text("Region name here")
                        .item(new ItemStack(Material.WRITABLE_BOOK))
                        .title("New list item")
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
