package xyz.jpenilla.wanderingtrades.gui;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import xyz.jpenilla.pluginbase.legacy.HeadBuilder;
import xyz.jpenilla.pluginbase.legacy.InputConversation;
import xyz.jpenilla.pluginbase.legacy.ItemBuilder;
import xyz.jpenilla.pluginbase.legacy.TextUtil;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Lang;
import xyz.jpenilla.wanderingtrades.config.PlayerHeadConfig;

@DefaultQualifier(NonNull.class)
public final class PlayerHeadConfigGui extends TradeGui {
    private final ItemStack enabledStack = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
        .setName(this.lang.get(Lang.GUI_PH_CONFIG_ENABLED))
        .setLore(this.toggleLore)
        .build();
    private final ItemStack disabledStack = new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
        .setName(this.lang.get(Lang.GUI_PH_CONFIG_DISABLED))
        .setLore(this.toggleLore)
        .build();
    private final ItemStack permissionWhitelistStack = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
        .setName(this.lang.get(Lang.GUI_PH_CONFIG_PWL_ENABLED))
        .setLore(this.toggleLore, this.lang.get(Lang.GUI_PH_CONFIG_PWL_LORE))
        .build();
    private final ItemStack noPermissionsWhitelistStack = new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
        .setName(this.lang.get(Lang.GUI_PH_CONFIG_PWL_DISABLED))
        .setLore(this.toggleLore, this.lang.get(Lang.GUI_PH_CONFIG_PWL_LORE))
        .build();
    private final ItemStack amountTradesStack = new ItemBuilder(Material.LIGHT_BLUE_STAINED_GLASS_PANE)
        .setName(this.lang.get(Lang.GUI_PH_CONFIG_AMOUNT))
        .build();
    private final ItemStack amountHeadsStack = new ItemBuilder(Material.LIGHT_BLUE_STAINED_GLASS_PANE)
        .setName(this.lang.get(Lang.GUI_PH_CONFIG_AMOUNT_HEADS))
        .build();
    private final ItemStack days = new ItemBuilder(Material.LIGHT_BLUE_STAINED_GLASS_PANE)
        .setName(this.lang.get(Lang.GUI_PH_CONFIG_DAYS))
        .build();
    private final ItemStack chanceStack = new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE)
        .setName(this.lang.get(Lang.GUI_PH_CONFIG_CHANCE))
        .build();
    private final ItemStack blacklistStack = new ItemBuilder(Material.PAPER)
        .setName(this.lang.get(Lang.GUI_PH_CONFIG_BLACKLIST))
        .build();
    private final ItemStack loreStack = new ItemBuilder(Material.PAPER)
        .setName(this.lang.get(Lang.GUI_PH_CONFIG_RESULT_LORE))
        .build();
    private final ItemStack notch = new HeadBuilder("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTBiNTk0MzgwMTNlYTk1MzYyYzZmMTIyNGI3YzViYjZjMjc5MmIwYjljOWNlZmQ2ZDcwODc2N2ZkOTFlYyJ9fX0=")
        .build();
    private final ItemStack customName = new ItemBuilder(Material.PINK_STAINED_GLASS_PANE)
        .setName(this.lang.get(Lang.GUI_TC_EDIT_CUSTOM_NAME))
        .build();

    public PlayerHeadConfigGui(final WanderingTrades plugin) {
        super(plugin, plugin.langConfig().get(Lang.GUI_PH_CONFIG_TITLE), null);
        final PlayerHeadConfig config = this.plugin.configManager().playerHeadConfig();
        this.setI1(config.ingredientOne());
        if (config.ingredientTwo() != null) {
            this.setI2(config.ingredientTwo());
        }
    }

    @Override
    public Inventory getInventory() {
        this.inventory.clear();
        this.inventory.setItem(this.inventory.getSize() - 1, this.closeButton);
        this.inventory.setItem(this.inventory.getSize() - 9, new ItemBuilder(this.getSaveButton()).setLore(this.lang.get(Lang.GUI_PH_CONFIG_SAVE_LORE)).build());

        final PlayerHeadConfig config = this.plugin.configManager().playerHeadConfig();

        this.inventory.setItem(9, config.permissionWhitelist() ? this.permissionWhitelistStack : this.noPermissionsWhitelistStack);
        this.inventory.setItem(10, config.playerHeadsFromServer() ? this.enabledStack : this.disabledStack);
        this.inventory.setItem(11, config.experienceReward() ? this.getExperienceEnabled() : this.getExperienceDisabled());

        final ItemStack k = new ItemBuilder(this.days).setLore(this.lang.get(Lang.GUI_VALUE_LORE) + "<#0092FF>" + config.days(), this.lang.get(Lang.GUI_EDIT_LORE), this.lang.get(Lang.GUI_PH_CONFIG_DAYS_LORE)).build();
        this.inventory.setItem(12, k);

        final ItemStack a = new ItemBuilder(this.amountTradesStack).setLore(this.lang.get(Lang.GUI_VALUE_LORE) + "<#0092FF>" + config.playerHeadsFromServerAmount(), this.lang.get(Lang.GUI_EDIT_LORE)).build();
        this.inventory.setItem(13, a);

        final ItemStack f = new ItemBuilder(this.amountHeadsStack).setLore(this.lang.get(Lang.GUI_VALUE_LORE) + "<#0092FF>" + config.headsPerTrade(), this.lang.get(Lang.GUI_EDIT_LORE)).build();
        this.inventory.setItem(14, f);

        final ItemStack e = new ItemBuilder(this.getMaxUsesStack()).setLore(this.lang.get(Lang.GUI_VALUE_LORE) + "<#0092FF>" + config.maxUses(), this.lang.get(Lang.GUI_EDIT_LORE)).build();
        this.inventory.setItem(15, e);

        final ItemStack b = new ItemBuilder(this.chanceStack).setLore(this.lang.get(Lang.GUI_VALUE_LORE) + "<#0092FF>" + config.playerHeadsFromServerChance(), this.lang.get(Lang.GUI_EDIT_LORE)).build();
        this.inventory.setItem(16, b);

        final ItemStack g = new ItemBuilder(this.customName).setLore(this.lang.get(Lang.GUI_VALUE_LORE) + config.name(), this.lang.get(Lang.GUI_EDIT_LORE)).build();
        this.inventory.setItem(17, g);

        final List<String> resultLore = new ArrayList<>(List.of(
            this.lang.get(Lang.GUI_CONFIG_WG_LIST_LORE),
            "<white>------------"
        ));
        resultLore.addAll(config.lore());
        final ItemStack h = new ItemBuilder(this.loreStack).setLore(resultLore).build();
        this.inventory.setItem(25, h);

        this.inventory.setItem(28, this.getI1());
        this.inventory.setItem(29, this.getPlus());
        this.inventory.setItem(30, this.getI2());
        this.inventory.setItem(31, this.getEquals());
        this.inventory.setItem(32, new ItemBuilder(this.notch).setName(config.name().replace("{PLAYER}", "Notch"))
            .setLore(config.lore()).setAmount(config.headsPerTrade()).build());

        final List<String> blacklistLore = new ArrayList<>(List.of(
            lang.get(Lang.GUI_CONFIG_WG_LIST_LORE),
            ""
        ));
        config.usernameBlacklist().forEach(name -> blacklistLore.add(" <red>-</red> <white>" + name));
        final ItemStack d = new ItemBuilder(this.blacklistStack).setLore(blacklistLore).build();
        this.inventory.setItem(34, d);

        this.fillEmptySlots();

        return this.inventory;
    }

    @Override
    public void onInventoryClick(final InventoryClickEvent event) {
        final @Nullable ItemStack item = event.getCurrentItem();
        final Player p = (Player) event.getWhoClicked();
        final ClickType click = event.getClick();

        if (this.closeButton.isSimilar(item)) {
            p.closeInventory();
        }

        final PlayerHeadConfig config = this.plugin.configManager().playerHeadConfig();

        if (this.enabledStack.isSimilar(item)) {
            config.playerHeadsFromServer(false);
            this.plugin.playerHeads().configChanged();
        } else if (this.disabledStack.isSimilar(item)) {
            config.playerHeadsFromServer(true);
            this.plugin.playerHeads().configChanged();
        }

        if (this.getExperienceEnabled().isSimilar(item)) {
            config.experienceReward(false);
        } else if (this.getExperienceDisabled().isSimilar(item)) {
            config.experienceReward(true);
        }

        if (this.permissionWhitelistStack.isSimilar(item)) {
            config.permissionWhitelist(false);
        } else if (this.noPermissionsWhitelistStack.isSimilar(item)) {
            config.permissionWhitelist(true);
        }

        if (this.getMaxUsesStack().isSimilar(item)) {
            this.maxUsesClick(p, config);
        }

        if (this.chanceStack.isSimilar(item)) {
            this.chanceClick(p, config);
        }

        if (this.amountHeadsStack.isSimilar(item)) {
            this.amountHeadsClick(p, config);
        }

        if (this.amountTradesStack.isSimilar(item)) {
            this.amountTradesClick(p, config);
        }

        if (this.customName.isSimilar(item)) {
            this.customNameClick(p, config);
        }

        if (this.blacklistStack.isSimilar(item)) {
            this.blacklistClick(p, click, config);
        }

        if (this.loreStack.isSimilar(item)) {
            this.loreClick(p, click, config);
        }

        if (this.days.isSimilar(item)) {
            this.daysClick(p, config);
        }

        int rawSlot = event.getRawSlot();
        if (rawSlot == 28) {
            this.setI1(this.updateSlot(event, this.emptyIngredient1()));
        } else if (rawSlot == 30) {
            this.setI2(this.updateSlot(event, this.emptyIngredient2()));
        }

        if (this.getSaveButton().isSimilar(item)) {
            if (!this.getI1().equals(this.emptyIngredient1())) {
                config.ingredientOne(this.getI1());
                config.ingredientTwo(this.getI2().equals(this.emptyIngredient2()) ? null : this.getI2());
                config.save();
                this.plugin.config().load();
            }
        }

        config.save();

        this.getInventory();
    }

    private void maxUsesClick(final Player p, final PlayerHeadConfig config) {
        p.closeInventory();
        new InputConversation()
            .onPromptText(player -> {
                this.plugin.chat().sendParsed(player,
                    this.lang.get(Lang.MESSAGE_SET_MAX_USES_PROMPT)
                        + "<reset>\n" + this.lang.get(Lang.MESSAGE_CURRENT_VALUE) + config.maxUses()
                        + "<reset>\n" + this.lang.get(Lang.MESSAGE_ENTER_NUMBER));
                return "";
            })
            .onValidateInput(this::validateIntGT0)
            .onConfirmText(this::confirmYesNo)
            .onAccepted((player, s) -> {
                config.maxUses(Integer.parseInt(s));
                config.save();
                this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_EDIT_SAVED));
                this.open(player);
            })
            .onDenied(this::editCancelled)
            .start(p);
    }

    private void chanceClick(final Player p, final PlayerHeadConfig config) {
        p.closeInventory();
        new InputConversation()
            .onPromptText(player -> {
                this.plugin.chat().sendParsed(player,
                    this.lang.get(Lang.MESSAGE_SET_CHANCE_PROMPT)
                        + "<reset>\n" + this.lang.get(Lang.MESSAGE_CURRENT_VALUE) + config.playerHeadsFromServerChance()
                        + "<reset>\n" + this.lang.get(Lang.MESSAGE_ENTER_NUMBER));
                return "";
            })
            .onValidateInput(this::validateDouble0T1)
            .onConfirmText(this::confirmYesNo)
            .onAccepted((player, s) -> {
                config.playerHeadsFromServerChance(Double.parseDouble(s));
                config.save();
                this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_EDIT_SAVED));
                this.open(player);
            })
            .onDenied(this::editCancelled)
            .start(p);
    }

    private void amountHeadsClick(final Player p, final PlayerHeadConfig config) {
        p.closeInventory();
        new InputConversation()
            .onPromptText(player -> {
                this.plugin.chat().sendParsed(player,
                    this.lang.get(Lang.MESSAGE_SET_HEADS_AMOUNT_PROMPT)
                        + "<reset>\n" + this.lang.get(Lang.MESSAGE_CURRENT_VALUE) + config.headsPerTrade()
                        + "<reset>\n" + this.lang.get(Lang.MESSAGE_ENTER_NUMBER));
                return "";
            })
            .onValidateInput(this::validateIntGT0)
            .onConfirmText(this::confirmYesNo)
            .onAccepted((player, s) -> {
                config.headsPerTrade(Integer.parseInt(s));
                config.save();
                this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_EDIT_SAVED));
                this.open(player);
            })
            .onDenied(this::editCancelled)
            .start(p);
    }

    private void amountTradesClick(final Player p, final PlayerHeadConfig config) {
        p.closeInventory();
        new InputConversation()
            .onPromptText(player -> {
                this.plugin.chat().sendParsed(player,
                    this.lang.get(Lang.MESSAGE_SET_HEADS_TRADES_AMOUNT_PROMPT)
                        + "<reset>\n" + this.lang.get(Lang.MESSAGE_CURRENT_VALUE) + config.playerHeadsFromServerAmount()
                        + "<reset>\n" + this.lang.get(Lang.MESSAGE_ENTER_NUMBER_OR_RANGE));
                return "";
            })
            .onValidateInput(this::validateIntRange)
            .onConfirmText(this::confirmYesNo)
            .onAccepted((player, s) -> {
                config.playerHeadsFromServerAmount(s);
                config.save();
                this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_EDIT_SAVED));
                this.open(player);
            })
            .onDenied(this::editCancelled)
            .start(p);
    }

    private void customNameClick(final Player p, final PlayerHeadConfig config) {
        p.closeInventory();
        new InputConversation()
            .onPromptText(player -> {
                this.plugin.chat().sendParsed(player,
                    this.lang.get(Lang.MESSAGE_CUSTOM_NAME_PROMPT)
                        + "<reset>\n" + this.lang.get(Lang.MESSAGE_CURRENT_VALUE) + "<reset>" + config.name());
                return "";
            })
            .onValidateInput((pl, s) -> true)
            .onConfirmText(this::confirmYesNo)
            .onAccepted((player, string) -> {
                config.name(string);
                config.save();
                this.open(player);
            })
            .onDenied(this::editCancelled)
            .start(p);
    }

    private void blacklistClick(final Player p, final ClickType click, final PlayerHeadConfig config) {
        if (click.isRightClick()) {
            List<String> l = config.usernameBlacklist();
            if (!(l.size() - 1 < 0)) {
                l.remove(l.size() - 1);
            }
            config.usernameBlacklist(l);
            this.plugin.playerHeads().configChanged();
        } else {
            p.closeInventory();
            new InputConversation()
                .onPromptText(player -> {
                    this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_ADD_BLACKLIST_PLAYER));
                    return "";
                })
                .onValidateInput((player, input) -> {
                    if (input.contains(" ")) {
                        this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_NO_SPACES));
                        return false;
                    }
                    if (TextUtil.containsCaseInsensitive(input, config.usernameBlacklist())) {
                        this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_CREATE_UNIQUE));
                        return false;
                    }
                    return true;
                })
                .onConfirmText(this::confirmYesNo)
                .onAccepted((player, s) -> {
                    List<String> temp = config.usernameBlacklist();
                    temp.add(s);
                    config.usernameBlacklist(temp);
                    config.save();
                    this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_EDIT_SAVED));
                    this.open(player);
                })
                .onDenied(this::editCancelled)
                .start(p);
        }
    }

    private void loreClick(final Player p, final ClickType click, final PlayerHeadConfig config) {
        if (click.isRightClick()) {
            List<String> l = config.lore();
            if (!(l.size() - 1 < 0)) {
                l.remove(l.size() - 1);
            }
            config.lore(l);
        } else {
            p.closeInventory();
            new InputConversation()
                .onPromptText(player -> {
                    this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_ADD_LORE_PROMPT));
                    return "";
                })
                .onValidateInput((player, input) -> true)
                .onConfirmText(this::confirmYesNo)
                .onAccepted((player, s) -> {
                    List<String> temp = config.lore();
                    temp.add(s);
                    config.lore(temp);
                    config.save();
                    this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_EDIT_SAVED));
                    this.open(player);
                })
                .onDenied(this::editCancelled)
                .start(p);
        }
    }

    private void daysClick(final Player p, final PlayerHeadConfig config) {
        p.closeInventory();
        new InputConversation()
            .onPromptText(player -> {
                this.plugin.chat().sendParsed(player,
                    lang.get(Lang.MESSAGE_SET_HEADS_DAYS_PROMPT)
                        + "<reset>\n" + this.lang.get(Lang.MESSAGE_CURRENT_VALUE) + config.days()
                        + "<reset>\n" + this.lang.get(Lang.MESSAGE_ENTER_NUMBER));
                return "";
            })
            .onValidateInput(this::validateIntGTEN1)
            .onConfirmText(this::confirmYesNo)
            .onAccepted((player, s) -> {
                config.days(Integer.parseInt(s));
                config.save();
                this.plugin.playerHeads().configChanged();
                this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_EDIT_SAVED));
                this.open(player);
            })
            .onDenied(this::editCancelled)
            .start(p);
    }

    @Override
    public void onClick(InventoryClickEvent e) {
    }
}
