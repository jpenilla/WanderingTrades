package xyz.jpenilla.wanderingtrades.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.jpenilla.jmplib.HeadBuilder;
import xyz.jpenilla.jmplib.InputConversation;
import xyz.jpenilla.jmplib.ItemBuilder;
import xyz.jpenilla.jmplib.TextUtil;
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
       setI1(config.getIngredient1());
        if (config.getIngredient2() != null) {
            setI2(config.getIngredient2());
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        inventory.clear();
        inventory.setItem(inventory.getSize() - 1, closeButton);
        inventory.setItem(inventory.getSize() - 9, new ItemBuilder(getSaveButton()).setLore(lang.get(Lang.GUI_PH_CONFIG_SAVE_LORE)).build());

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
            inventory.setItem(11, getExperienceEnabled());
        } else {
            inventory.setItem(11, getExperienceDisabled());
        }

        ItemStack k = new ItemBuilder(days).setLore(lang.get(Lang.GUI_VALUE_LORE) + "<color:#0092FF>" + config.getDays(), lang.get(Lang.GUI_EDIT_LORE), lang.get(Lang.GUI_PH_CONFIG_DAYS_LORE)).build();
        inventory.setItem(12, k);

        ItemStack a = new ItemBuilder(amountTradesStack).setLore(lang.get(Lang.GUI_VALUE_LORE) + "<color:#0092FF>" + config.getPlayerHeadsFromServerAmount(), lang.get(Lang.GUI_EDIT_LORE)).build();
        inventory.setItem(13, a);

        ItemStack f = new ItemBuilder(amountHeadsStack).setLore(lang.get(Lang.GUI_VALUE_LORE) + "<color:#0092FF>" + config.getHeadsPerTrade(), lang.get(Lang.GUI_EDIT_LORE)).build();
        inventory.setItem(14, f);

        ItemStack e = new ItemBuilder(getMaxUsesStack()).setLore(lang.get(Lang.GUI_VALUE_LORE) + "<color:#0092FF>" + config.getMaxUses(), lang.get(Lang.GUI_EDIT_LORE)).build();
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

        inventory.setItem(28, getI1());
        inventory.setItem(29, getPlus());
        inventory.setItem(30, getI2());
        inventory.setItem(31, getEquals());
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

        if (getExperienceEnabled().isSimilar(item)) {
            config.setExperienceReward(false);
        } else if (getExperienceDisabled().isSimilar(item)) {
            config.setExperienceReward(true);
        }

        if (permissionWhitelistStack.isSimilar(item)) {
            config.setPermissionWhitelist(false);
        } else if (noPermissionsWhitelistStack.isSimilar(item)) {
            config.setPermissionWhitelist(true);
        }

        if (getMaxUsesStack().isSimilar(item)) {
            p.closeInventory();
            new InputConversation()
                    .onPromptText(player -> {
                        WanderingTrades.getInstance().getChat().sendParsed(player,
                                lang.get(Lang.MESSAGE_SET_MAX_USES_PROMPT)
                                        + "<reset>\n" + lang.get(Lang.MESSAGE_CURRENT_VALUE) + config.getMaxUses()
                                        + "<reset>\n" + lang.get(Lang.MESSAGE_ENTER_NUMBER));
                        return "";
                    })
                    .onValidateInput(this::onValidateIntGT0)
                    .onConfirmText(this::onConfirmYesNo)
                    .onAccepted((player, s) -> {
                        config.setMaxUses(Integer.parseInt(s));
                        config.save();
                        WanderingTrades.getInstance().getChat().sendParsed(player, lang.get(Lang.MESSAGE_EDIT_SAVED));
                        open(player);
                    })
                    .onDenied(this::onEditCancelled)
                    .start(p);
        }

        if (chanceStack.isSimilar(item)) {
            p.closeInventory();
            new InputConversation()
                    .onPromptText(player -> {
                        WanderingTrades.getInstance().getChat().sendParsed(player,
                                lang.get(Lang.MESSAGE_SET_CHANCE_PROMPT)
                                        + "<reset>\n" + lang.get(Lang.MESSAGE_CURRENT_VALUE) + config.getPlayerHeadsFromServerChance()
                                        + "<reset>\n" + lang.get(Lang.MESSAGE_ENTER_NUMBER));
                        return "";
                    })
                    .onValidateInput(this::onValidateDouble0T1)
                    .onConfirmText(this::onConfirmYesNo)
                    .onAccepted((player, s) -> {
                        config.setPlayerHeadsFromServerChance(Double.parseDouble(s));
                        config.save();
                        WanderingTrades.getInstance().getChat().sendParsed(player, lang.get(Lang.MESSAGE_EDIT_SAVED));
                        open(player);
                    })
                    .onDenied(this::onEditCancelled)
                    .start(p);
        }

        if (amountHeadsStack.isSimilar(item)) {
            p.closeInventory();
            new InputConversation()
                    .onPromptText(player -> {
                        WanderingTrades.getInstance().getChat().sendParsed(player,
                                lang.get(Lang.MESSAGE_SET_HEADS_AMOUNT_PROMPT)
                                        + "<reset>\n" + lang.get(Lang.MESSAGE_CURRENT_VALUE) + config.getHeadsPerTrade()
                                        + "<reset>\n" + lang.get(Lang.MESSAGE_ENTER_NUMBER));
                        return "";
                    })
                    .onValidateInput(this::onValidateIntGT0)
                    .onConfirmText(this::onConfirmYesNo)
                    .onAccepted((player, s) -> {
                        config.setHeadsPerTrade(Integer.parseInt(s));
                        config.save();
                        WanderingTrades.getInstance().getChat().sendParsed(player, lang.get(Lang.MESSAGE_EDIT_SAVED));
                        open(player);
                    })
                    .onDenied(this::onEditCancelled)
                    .start(p);
        }

        if (amountTradesStack.isSimilar(item)) {
            p.closeInventory();
            new InputConversation()
                    .onPromptText(player -> {
                        WanderingTrades.getInstance().getChat().sendParsed(player,
                                lang.get(Lang.MESSAGE_SET_HEADS_TRADES_AMOUNT_PROMPT)
                                        + "<reset>\n" + lang.get(Lang.MESSAGE_CURRENT_VALUE) + config.getPlayerHeadsFromServerAmount()
                                        + "<reset>\n" + lang.get(Lang.MESSAGE_ENTER_NUMBER));
                        return "";
                    })
                    .onValidateInput(this::onValidateIntGT0)
                    .onConfirmText(this::onConfirmYesNo)
                    .onAccepted((player, s) -> {
                        config.setPlayerHeadsFromServerAmount(Integer.parseInt(s));
                        config.save();
                        WanderingTrades.getInstance().getChat().sendParsed(player, lang.get(Lang.MESSAGE_EDIT_SAVED));
                        open(player);
                    })
                    .onDenied(this::onEditCancelled)
                    .start(p);
        }

        if (customName.isSimilar(item)) {
            p.closeInventory();
            new InputConversation()
                    .onPromptText(player -> {
                        WanderingTrades.getInstance().getChat().sendParsed(player,
                                lang.get(Lang.MESSAGE_CUSTOM_NAME_PROMPT)
                                        + "<reset>\n" + lang.get(Lang.MESSAGE_CURRENT_VALUE) + "<reset>" + config.getName());
                        return "";
                    })
                    .onValidateInput((pl, s) -> true)
                    .onConfirmText(this::onConfirmYesNo)
                    .onAccepted((player, string) -> {
                        config.setName(string);
                        config.save();
                        open(player);
                    })
                    .onDenied(this::onEditCancelled)
                    .start(p);
        }

        if (blacklistStack.isSimilar(item)) {
            if (click.isRightClick()) {
                List<String> l = config.getUsernameBlacklist();
                if (!(l.size() - 1 < 0)) {
                    l.remove(l.size() - 1);
                }
                config.setUsernameBlacklist(l);
                WanderingTrades.getInstance().getStoredPlayers().load();
            } else {
                p.closeInventory();
                new InputConversation()
                        .onPromptText(player -> {
                            WanderingTrades.getInstance().getChat().sendParsed(player, lang.get(Lang.MESSAGE_ADD_BLACKLIST_PLAYER));
                            return "";
                        })
                        .onValidateInput((player, input) -> {
                            if (input.contains(" ")) {
                                WanderingTrades.getInstance().getChat().sendParsed(player, lang.get(Lang.MESSAGE_NO_SPACES));
                                return false;
                            }
                            if (TextUtil.containsCaseInsensitive(input, config.getUsernameBlacklist())) {
                                WanderingTrades.getInstance().getChat().sendParsed(player, lang.get(Lang.MESSAGE_CREATE_UNIQUE));
                                return false;
                            }
                            return true;
                        })
                        .onConfirmText(this::onConfirmYesNo)
                        .onAccepted((player, s) -> {
                            List<String> temp = config.getUsernameBlacklist();
                            temp.add(s);
                            config.setUsernameBlacklist(temp);
                            config.save();
                            WanderingTrades.getInstance().getChat().sendParsed(player, lang.get(Lang.MESSAGE_EDIT_SAVED));
                            open(player);
                        })
                        .onDenied(this::onEditCancelled)
                        .start(p);
            }
        }

        if (loreStack.isSimilar(item)) {
            if (click.isRightClick()) {
                List<String> l = config.getLore();
                if (!(l.size() - 1 < 0)) {
                    l.remove(l.size() - 1);
                }
                config.setLore(l);
            } else {
                p.closeInventory();
                new InputConversation()
                        .onPromptText(player -> {
                            WanderingTrades.getInstance().getChat().sendParsed(player, lang.get(Lang.MESSAGE_ADD_LORE_PROMPT));
                            return "";
                        })
                        .onValidateInput((player, input) -> true)
                        .onConfirmText(this::onConfirmYesNo)
                        .onAccepted((player, s) -> {
                            List<String> temp = config.getLore();
                            temp.add(s);
                            config.setLore(temp);
                            config.save();
                            WanderingTrades.getInstance().getChat().sendParsed(player, lang.get(Lang.MESSAGE_EDIT_SAVED));
                            open(player);
                        })
                        .onDenied(this::onEditCancelled)
                        .start(p);
            }
        }

        if (days.isSimilar(item)) {
            p.closeInventory();
            new InputConversation()
                    .onPromptText(player -> {
                        WanderingTrades.getInstance().getChat().sendParsed(player,
                                lang.get(Lang.MESSAGE_SET_HEADS_DAYS_PROMPT)
                                        + "<reset>\n" + lang.get(Lang.MESSAGE_CURRENT_VALUE) + config.getDays()
                                        + "<reset>\n" + lang.get(Lang.MESSAGE_ENTER_NUMBER));
                        return "";
                    })
                    .onValidateInput(this::onValidateIntGTEN1)
                    .onConfirmText(this::onConfirmYesNo)
                    .onAccepted((player, s) -> {
                        config.setDays(Integer.parseInt(s));
                        config.save();
                        WanderingTrades.getInstance().getStoredPlayers().load();
                        WanderingTrades.getInstance().getChat().sendParsed(player, lang.get(Lang.MESSAGE_EDIT_SAVED));
                        open(player);
                    })
                    .onDenied(this::onEditCancelled)
                    .start(p);
        }

        int rS = event.getRawSlot();
        if (rS == 28) {
            setI1(updateSlot(event, getIngredient1()));
        } else if (rS == 30) {
            setI2(updateSlot(event, getIngredient2()));
        }

        if (getSaveButton().isSimilar(item)) {
            if (!getI1().equals(getIngredient1())) {
                ItemStack temp = null;
                if (!getI2().equals(getIngredient2())) {
                    temp = getI2();
                }
                config.setIngredient1(getI1());
                config.setIngredient2(temp);
                config.save();
                WanderingTrades.getInstance().getCfg().load();
            }
        }

        config.save();

        getInventory();
    }

    @Override
    public void onClick(InventoryClickEvent e) {
    }
}
