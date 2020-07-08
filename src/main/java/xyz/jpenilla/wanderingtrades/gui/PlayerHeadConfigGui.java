package xyz.jpenilla.wanderingtrades.gui;

import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.jpenilla.jmplib.HeadBuilder;
import xyz.jpenilla.jmplib.ItemBuilder;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Lang;
import xyz.jpenilla.wanderingtrades.config.PlayerHeadConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class PlayerHeadConfigGui extends TradeGui {
    private final ItemStack enabledStack = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_PH_CONFIG_ENABLED)).setLore(gui_toggle_lore).build();
    private final ItemStack disabledStack = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_PH_CONFIG_DISABLED)).setLore(gui_toggle_lore).build();
    private final ItemStack permissionWhitelistStack = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_PH_CONFIG_PWL_ENABLED)).setLore(gui_toggle_lore, lang.get(Lang.GUI_PH_CONFIG_PWL_LORE)).build();
    private final ItemStack noPermissionsWhitelistStack = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_PH_CONFIG_PWL_DISABLED)).setLore(gui_toggle_lore, lang.get(Lang.GUI_PH_CONFIG_PWL_LORE)).build();
    private final ItemStack amountTradesStack = new ItemBuilder(Material.LIGHT_BLUE_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_PH_CONFIG_AMOUNT)).build();
    private final ItemStack amountHeadsStack = new ItemBuilder(Material.LIGHT_BLUE_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_PH_CONFIG_AMOUNT_HEADS)).build();
    private final ItemStack days = new ItemBuilder(Material.LIGHT_BLUE_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_PH_CONFIG_DAYS)).build();
    private final ItemStack chanceStack = new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_PH_CONFIG_CHANCE)).build();
    private final ItemStack blacklistStack = new ItemBuilder(Material.PAPER).setName(lang.get(Lang.GUI_PH_CONFIG_BLACKLIST)).build();
    private final ItemStack loreStack = new ItemBuilder(Material.PAPER).setName(lang.get(Lang.GUI_PH_CONFIG_RESULT_LORE)).build();
    private final ItemStack notch = new HeadBuilder("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTBiNTk0MzgwMTNlYTk1MzYyYzZmMTIyNGI3YzViYjZjMjc5MmIwYjljOWNlZmQ2ZDcwODc2N2ZkOTFlYyJ9fX0=").build();
    private final ItemStack customName = new ItemBuilder(Material.PINK_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_TC_EDIT_CUSTOM_NAME)).build();

    public PlayerHeadConfigGui() {
        super(WanderingTrades.getInstance().getLang().get(Lang.GUI_PH_CONFIG_TITLE), null);
        PlayerHeadConfig config = WanderingTrades.getInstance().getCfg().getPlayerHeadConfig();
        i1 = config.getIngredient1();
        if (config.getIngredient2() != null) {
            i2 = config.getIngredient2();
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        inventory.clear();
        inventory.setItem(inventory.getSize() - 1, closeButton);
        inventory.setItem(inventory.getSize() - 9, new ItemBuilder(saveButton).setLore(lang.get(Lang.GUI_PH_CONFIG_SAVE_LORE)).build());

        PlayerHeadConfig config = WanderingTrades.getInstance().getCfg().getPlayerHeadConfig();

        if (config.isPermissionWhitelist()) {
            inventory.setItem(9, permissionWhitelistStack);
        } else {
            inventory.setItem(9, noPermissionsWhitelistStack);
        }

        if (config.isPlayerHeadsFromServer()) {
            inventory.setItem(10, enabledStack);
        } else {
            inventory.setItem(10, disabledStack);
        }

        if (config.isExperienceReward()) {
            inventory.setItem(11, experienceEnabled);
        } else {
            inventory.setItem(11, experienceDisabled);
        }

        ItemStack k = new ItemBuilder(days).setLore(lang.get(Lang.GUI_VALUE_LORE) + "<color:#0092FF>" + config.getDays(), lang.get(Lang.GUI_EDIT_LORE), lang.get(Lang.GUI_PH_CONFIG_DAYS_LORE)).build();
        inventory.setItem(12, k);

        ItemStack a = new ItemBuilder(amountTradesStack).setLore(lang.get(Lang.GUI_VALUE_LORE) + "<color:#0092FF>" + config.getPlayerHeadsFromServerAmount(), lang.get(Lang.GUI_EDIT_LORE)).build();
        inventory.setItem(13, a);

        ItemStack f = new ItemBuilder(amountHeadsStack).setLore(lang.get(Lang.GUI_VALUE_LORE) + "<color:#0092FF>" + config.getHeadsPerTrade(), lang.get(Lang.GUI_EDIT_LORE)).build();
        inventory.setItem(14, f);

        ItemStack e = new ItemBuilder(maxUsesStack).setLore(lang.get(Lang.GUI_VALUE_LORE) + "<color:#0092FF>" + config.getMaxUses(), lang.get(Lang.GUI_EDIT_LORE)).build();
        inventory.setItem(15, e);

        ItemStack b = new ItemBuilder(chanceStack).setLore(lang.get(Lang.GUI_VALUE_LORE) + "<color:#0092FF>" + config.getPlayerHeadsFromServerChance(), lang.get(Lang.GUI_EDIT_LORE)).build();
        inventory.setItem(16, b);

        ItemStack g = new ItemBuilder(customName).setLore(lang.get(Lang.GUI_VALUE_LORE) + config.getName(), lang.get(Lang.GUI_EDIT_LORE)).build();
        inventory.setItem(17, g);

        ArrayList<String> resultLore = new ArrayList<>(Arrays.asList(
                lang.get(Lang.GUI_CONFIG_WG_LIST_LORE),
                "<white>------------"
        ));
        resultLore.addAll(config.getLore());
        ItemStack h = new ItemBuilder(loreStack).setLore(resultLore).build();
        inventory.setItem(25, h);

        inventory.setItem(28, i1);
        inventory.setItem(29, plus);
        inventory.setItem(30, i2);
        inventory.setItem(31, equals);
        inventory.setItem(32, new ItemBuilder(notch).setName(config.getName().replace("{PLAYER}", "Notch"))
                .setLore(config.getLore()).setAmount(config.getHeadsPerTrade()).build());

        ArrayList<String> blacklistLore = new ArrayList<>(Arrays.asList(
                lang.get(Lang.GUI_CONFIG_WG_LIST_LORE),
                ""
        ));
        config.getUsernameBlacklist().forEach(name -> blacklistLore.add(" <red>-</red> <white>" + name));
        ItemStack d = new ItemBuilder(blacklistStack).setLore(blacklistLore).build();
        inventory.setItem(34, d);

        IntStream.range(0, inventory.getSize()).forEach(slot -> {
            if (inventory.getItem(slot) == null) {
                inventory.setItem(slot, filler);
            }
        });

        return inventory;
    }

    @Override
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

        PlayerHeadConfig config = WanderingTrades.getInstance().getCfg().getPlayerHeadConfig();

        if (enabledStack.isSimilar(item)) {
            config.setPlayerHeadsFromServer(false);
        } else if (disabledStack.isSimilar(item)) {
            config.setPlayerHeadsFromServer(true);
        }

        if (experienceEnabled.isSimilar(item)) {
            config.setExperienceReward(false);
        } else if (experienceDisabled.isSimilar(item)) {
            config.setExperienceReward(true);
        }

        if (permissionWhitelistStack.isSimilar(item)) {
            config.setPermissionWhitelist(false);
        } else if (noPermissionsWhitelistStack.isSimilar(item)) {
            config.setPermissionWhitelist(true);
        }

        if (maxUsesStack.isSimilar(item)) {
            p.closeInventory();
            new AnvilGUI.Builder()
                    .onClose(this::reOpen)
                    .onComplete((player, text) -> {
                        try {
                            int i = Integer.parseInt(text);
                            if (i < 1) {
                                return AnvilGUI.Response.text(lang.get(Lang.GUI_ANVIL_NUMBER_GT_0));
                            } else {
                                config.setMaxUses(i);
                                config.save();
                            }
                        } catch (NumberFormatException ex) {
                            return AnvilGUI.Response.text(lang.get(Lang.GUI_ANVIL_ENTER_NUMBER));
                        }
                        return AnvilGUI.Response.close();
                    })
                    .text(String.valueOf(config.getMaxUses()))
                    .item(new ItemStack(Material.WRITABLE_BOOK))
                    .title(lang.get(Lang.GUI_ANVIL_SET_MAX_USES))
                    .plugin(WanderingTrades.getInstance())
                    .open(p);
        }

        if (chanceStack.isSimilar(item)) {
            p.closeInventory();
            new AnvilGUI.Builder()
                    .onClose(this::reOpen)
                    .onComplete((player, text) -> {
                        try {
                            double d = Double.parseDouble(text);
                            if (d < 0 || d > 1) {
                                return AnvilGUI.Response.text(lang.get(Lang.GUI_ANVIL_NUMBER_0T1));
                            } else {
                                config.setPlayerHeadsFromServerChance(d);
                                config.save();
                            }
                        } catch (NumberFormatException ex) {
                            return AnvilGUI.Response.text(lang.get(Lang.GUI_ANVIL_ENTER_NUMBER));
                        }
                        return AnvilGUI.Response.close();
                    })
                    .text(String.valueOf(config.getPlayerHeadsFromServerChance()))
                    .item(new ItemStack(Material.WRITABLE_BOOK))
                    .title(lang.get(Lang.GUI_ANVIL_SET_CHANCE_TITLE))
                    .plugin(WanderingTrades.getInstance())
                    .open(p);
        }

        if (amountHeadsStack.isSimilar(item)) {
            p.closeInventory();
            new AnvilGUI.Builder()
                    .onClose(this::reOpen)
                    .onComplete((player, text) -> {
                        try {
                            int i = Integer.parseInt(text);
                            if (i < 1) {
                                return AnvilGUI.Response.text(lang.get(Lang.GUI_ANVIL_NUMBER_GT_0));
                            } else {
                                config.setHeadsPerTrade(i);
                                config.save();
                            }
                        } catch (NumberFormatException ex) {
                            return AnvilGUI.Response.text(lang.get(Lang.GUI_ANVIL_ENTER_NUMBER));
                        }
                        return AnvilGUI.Response.close();
                    })
                    .text(String.valueOf(config.getHeadsPerTrade()))
                    .item(new ItemStack(Material.WRITABLE_BOOK))
                    .title(lang.get(Lang.GUI_ANVIL_SET_HEADS_AMOUNT_TITLE))
                    .plugin(WanderingTrades.getInstance())
                    .open(p);
        }

        if (amountTradesStack.isSimilar(item)) {
            p.closeInventory();
            new AnvilGUI.Builder()
                    .onClose(this::reOpen)
                    .onComplete((player, text) -> {
                        try {
                            int i = Integer.parseInt(text);
                            if (i < 1) {
                                return AnvilGUI.Response.text(lang.get(Lang.GUI_ANVIL_NUMBER_GT_0));
                            } else {
                                config.setPlayerHeadsFromServerAmount(i);
                                config.save();
                            }
                        } catch (NumberFormatException ex) {
                            return AnvilGUI.Response.text(lang.get(Lang.GUI_ANVIL_ENTER_NUMBER));
                        }
                        return AnvilGUI.Response.close();
                    })
                    .text(String.valueOf(config.getPlayerHeadsFromServerAmount()))
                    .item(new ItemStack(Material.WRITABLE_BOOK))
                    .title(lang.get(Lang.GUI_ANVIL_SET_HEADS_TRADES_AMOUNT_TITLE))
                    .plugin(WanderingTrades.getInstance())
                    .open(p);
        }

        if (customName.isSimilar(item)) {
            p.closeInventory();
            new AnvilGUI.Builder()
                    .onClose(this::reOpen)
                    .onComplete((player, text) -> {
                        config.setName(text);
                        config.save();
                        return AnvilGUI.Response.close();
                    })
                    .text(config.getName())
                    .item(new ItemStack(Material.WRITABLE_BOOK))
                    .title(lang.get(Lang.GUI_ANVIL_CREATE_TITLE))
                    .plugin(WanderingTrades.getInstance())
                    .open(p);
        }

        if (blacklistStack.isSimilar(item)) {
            if (click.isRightClick()) {
                List<String> l = config.getUsernameBlacklist();
                l.remove(l.size() - 1);
                config.setUsernameBlacklist(l);
                WanderingTrades.getInstance().getStoredPlayers().load();
            } else {
                new AnvilGUI.Builder()
                        .onClose(this::reOpen)
                        .onComplete((player, text) -> {
                            if (!text.contains(" ")) {
                                List<String> temp = config.getUsernameBlacklist();
                                temp.add(text);
                                config.setUsernameBlacklist(temp);
                                config.save();
                                WanderingTrades.getInstance().getStoredPlayers().load();
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

        if (loreStack.isSimilar(item)) {
            if (click.isRightClick()) {
                List<String> l = config.getLore();
                l.remove(l.size() - 1);
                config.setLore(l);
            } else {
                new AnvilGUI.Builder()
                        .onClose(this::reOpen)
                        .onComplete((player, text) -> {
                            List<String> temp = config.getLore();
                            temp.add(text);
                            config.setLore(temp);
                            config.save();
                            return AnvilGUI.Response.close();
                        })
                        .text(lang.get(Lang.GUI_ANVIL_TYPE_HERE))
                        .item(new ItemStack(Material.WRITABLE_BOOK))
                        .title(lang.get(Lang.GUI_ANVIL_NEW_LIST_ITEM))
                        .plugin(WanderingTrades.getInstance())
                        .open(p);
            }
        }

        if (days.isSimilar(item)) {
            p.closeInventory();
            new AnvilGUI.Builder()
                    .onClose(this::reOpen)
                    .onComplete((player, text) -> {
                        try {
                            int i = Integer.parseInt(text);
                            if (i < -1) {
                                return AnvilGUI.Response.text(lang.get(Lang.GUI_ANVIL_NUMBER_GTE_N1));
                            } else {
                                config.setDays(i);
                                config.save();
                                WanderingTrades.getInstance().getStoredPlayers().load();
                            }
                        } catch (NumberFormatException ex) {
                            return AnvilGUI.Response.text(lang.get(Lang.GUI_ANVIL_ENTER_NUMBER));
                        }
                        return AnvilGUI.Response.close();
                    })
                    .text(String.valueOf(config.getDays()))
                    .item(new ItemStack(Material.WRITABLE_BOOK))
                    .title(lang.get(Lang.GUI_ANVIL_SET_HEADS_DAYS_TITLE))
                    .plugin(WanderingTrades.getInstance())
                    .open(p);
        }

        int rS = event.getRawSlot();
        if (rS == 28) {
            i1 = updateSlot(event, ingredient1);
        } else if (rS == 30) {
            i2 = updateSlot(event, ingredient2);
        }

        if (saveButton.isSimilar(item)) {
            if (!i1.equals(ingredient1)) {
                ItemStack temp = null;
                if (!i2.equals(ingredient2)) {
                    temp = i2;
                }
                config.setIngredient1(i1);
                config.setIngredient2(temp);
                config.save();
                WanderingTrades.getInstance().getCfg().load();
            }
        }

        config.save();

        getInventory();
    }
}
