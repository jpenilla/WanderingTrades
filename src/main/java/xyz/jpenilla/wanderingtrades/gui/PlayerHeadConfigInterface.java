package xyz.jpenilla.wanderingtrades.gui;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.incendo.interfaces.core.click.ClickContext;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.pane.ChestPane;
import org.incendo.interfaces.paper.type.ChestInterface;
import xyz.jpenilla.pluginbase.legacy.InputConversation;
import xyz.jpenilla.pluginbase.legacy.TextUtil;
import xyz.jpenilla.pluginbase.legacy.itembuilder.HeadBuilder;
import xyz.jpenilla.pluginbase.legacy.itembuilder.ItemBuilder;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Lang;
import xyz.jpenilla.wanderingtrades.config.PlayerHeadConfig;
import xyz.jpenilla.wanderingtrades.gui.transform.SlotTransform;
import xyz.jpenilla.wanderingtrades.util.BooleanConsumer;

import static xyz.jpenilla.wanderingtrades.gui.PartsFactory.chestItem;
import static xyz.jpenilla.wanderingtrades.gui.PartsFactory.toggle;

@DefaultQualifier(NonNull.class)
public final class PlayerHeadConfigInterface extends BaseInterface {
    private final ItemStack permissionWhitelistStack = ItemBuilder.create(Material.LIME_STAINED_GLASS_PANE).miniMessageContext()
        .customName(this.lang.get(Lang.GUI_PH_CONFIG_PWL_ENABLED))
        .lore(this.parts.toggleLore(), this.lang.get(Lang.GUI_PH_CONFIG_PWL_LORE))
        .exitAndBuild();
    private final ItemStack noPermissionsWhitelistStack = ItemBuilder.create(Material.RED_STAINED_GLASS_PANE).miniMessageContext()
        .customName(this.lang.get(Lang.GUI_PH_CONFIG_PWL_DISABLED))
        .lore(this.parts.toggleLore(), this.lang.get(Lang.GUI_PH_CONFIG_PWL_LORE))
        .exitAndBuild();

    public PlayerHeadConfigInterface(final WanderingTrades plugin) {
        super(plugin);
    }

    @Override
    protected ChestInterface buildInterface() {
        final SlotTransform ingredientOne = new SlotTransform(
            ItemBuilder.create(Material.STRUCTURE_VOID).miniMessageContext()
                .customName(this.lang.get(Lang.GUI_TRADE_INGREDIENT_1))
                .lore(this.lang.getList(Lang.GUI_TRADE_REQUIRED_LORE))
                .exitAndBuild(),
            1,
            3
        );
        ingredientOne.item(this.playerHeadConfig().ingredientOne());

        final SlotTransform ingredientTwo = new SlotTransform(
            ItemBuilder.create(Material.STRUCTURE_VOID).miniMessageContext()
                .customName(this.lang.get(Lang.GUI_TRADE_INGREDIENT_2))
                .lore(this.lang.getList(Lang.GUI_TRADE_REQUIRED_LORE))
                .exitAndBuild(),
            3,
            3
        );
        ingredientTwo.item(this.playerHeadConfig().ingredientTwo());

        return ChestInterface.builder()
            .rows(5)
            .title(this.plugin.miniMessage().deserialize(this.plugin.langConfig().get(Lang.GUI_PH_CONFIG_TITLE)))
            .addTransform(this.parts.fill())
            .addTransform(this.parts.toggle(
                this.lang.get(Lang.GUI_PH_CONFIG_ENABLED),
                this.lang.get(Lang.GUI_PH_CONFIG_DISABLED),
                this.playerHeadConfig()::playerHeadsFromServer,
                this.saveConfig(this.playerHeadConfig()::playerHeadsFromServer),
                0,
                1
            ))
            .addTransform(chestItem(
                () -> toggle(
                    on -> on ? this.permissionWhitelistStack : this.noPermissionsWhitelistStack,
                    this.playerHeadConfig()::permissionWhitelist,
                    this.saveConfig(this.playerHeadConfig()::permissionWhitelist)
                ),
                1,
                1
            ))
            .addTransform(this.parts.toggle(
                this.lang.get(Lang.GUI_TRADE_EXP_REWARD),
                this.lang.get(Lang.GUI_TRADE_NO_EXP_REWARD),
                this.playerHeadConfig()::experienceReward,
                this.saveConfig(this.playerHeadConfig()::experienceReward),
                2,
                1
            ))
            .addTransform(chestItem(this::daysElement, 3, 1))
            .addTransform(chestItem(this::amountTradesElement, 4, 1))
            .addTransform(chestItem(this::amountHeadsElement, 5, 1))
            .addTransform(chestItem(() -> this.parts.maxUsesElement(this.playerHeadConfig().maxUses(), this::maxUsesClick), 6, 1))
            .addTransform(chestItem(this::headChanceElement, 7, 1))
            .addTransform(chestItem(this::customNameElement, 8, 1))
            .addTransform(chestItem(this::loreElement, 7, 2))
            .addTransform(chestItem(this::blacklistElement, 7, 3))
            .addReactiveTransform(ingredientOne)
            .addTransform(chestItem(this.parts.plus(), 2, 3))
            .addReactiveTransform(ingredientTwo)
            .addTransform(chestItem(this.parts.equals(), 4, 3))
            .addTransform(chestItem(this::resultHeadElement, 5, 3))
            .addTransform((pane, view) -> pane.element(this.saveElement(ingredientOne, ingredientTwo), 0, pane.rows() - 1))
            .addTransform(this.parts.closeButton())
            .build();
    }

    private PlayerHeadConfig playerHeadConfig() {
        return this.plugin.configManager().playerHeadConfig();
    }

    private BooleanConsumer saveConfig(final BooleanConsumer setter) {
        return value -> {
            setter.accept(value);
            this.playerHeadConfig().save();
            this.plugin.playerHeads().configChanged();
        };
    }

    private ItemStackElement<ChestPane> daysElement() {
        return ItemStackElement.of(
            ItemBuilder.create(Material.LIGHT_BLUE_STAINED_GLASS_PANE).miniMessageContext()
                .customName(this.lang.get(Lang.GUI_PH_CONFIG_DAYS))
                .lore(
                    this.lang.get(Lang.GUI_VALUE_LORE) + "<#0092FF>" + this.playerHeadConfig().days(),
                    this.lang.get(Lang.GUI_EDIT_LORE),
                    this.lang.get(Lang.GUI_PH_CONFIG_DAYS_LORE)
                )
                .exitAndBuild(),
            this::daysClick
        );
    }

    private ItemStackElement<ChestPane> amountTradesElement() {
        return ItemStackElement.of(
            ItemBuilder.create(Material.LIGHT_BLUE_STAINED_GLASS_PANE).miniMessageContext()
                .customName(this.lang.get(Lang.GUI_PH_CONFIG_AMOUNT))
                .lore(
                    this.lang.get(Lang.GUI_VALUE_LORE) + "<#0092FF>" + this.playerHeadConfig().playerHeadsFromServerAmount(),
                    this.lang.get(Lang.GUI_EDIT_LORE)
                )
                .exitAndBuild(),
            this::amountTradesClick
        );
    }

    private ItemStackElement<ChestPane> amountHeadsElement() {
        return ItemStackElement.of(
            ItemBuilder.create(Material.LIGHT_BLUE_STAINED_GLASS_PANE).miniMessageContext()
                .customName(this.lang.get(Lang.GUI_PH_CONFIG_AMOUNT_HEADS))
                .lore(
                    this.lang.get(Lang.GUI_VALUE_LORE) + "<#0092FF>" + this.playerHeadConfig().headsPerTrade(),
                    this.lang.get(Lang.GUI_EDIT_LORE)
                )
                .exitAndBuild(),
            this::amountHeadsClick
        );
    }

    public ItemStackElement<ChestPane> headChanceElement() {
        return ItemStackElement.of(
            ItemBuilder.create(Material.PURPLE_STAINED_GLASS_PANE).miniMessageContext()
                .customName(this.lang.get(Lang.GUI_PH_CONFIG_CHANCE))
                .lore(
                    this.lang.get(Lang.GUI_VALUE_LORE) + "<#0092FF>" + this.playerHeadConfig().playerHeadsFromServerChance(),
                    this.lang.get(Lang.GUI_EDIT_LORE)
                )
                .exitAndBuild(),
            this::chanceClick
        );
    }

    public ItemStackElement<ChestPane> customNameElement() {
        return ItemStackElement.of(
            ItemBuilder.create(Material.PINK_STAINED_GLASS_PANE).miniMessageContext()
                .customName(this.lang.get(Lang.GUI_TC_EDIT_CUSTOM_NAME))
                .lore(
                    this.lang.get(Lang.GUI_VALUE_LORE) + "<yellow>" + this.playerHeadConfig().name(),
                    this.lang.get(Lang.GUI_EDIT_LORE)
                )
                .exitAndBuild(),
            this::customNameClick
        );
    }

    private ItemStackElement<ChestPane> blacklistElement() {
        final List<String> blacklistLore = new ArrayList<>(List.of(
            this.lang.get(Lang.GUI_CONFIG_WG_LIST_LORE),
            ""
        ));
        this.playerHeadConfig().usernameBlacklist().forEach(name -> blacklistLore.add(" <red>-</red> <white>" + name));
        return ItemStackElement.of(
            ItemBuilder.create(Material.PAPER).miniMessageContext()
                .customName(this.lang.get(Lang.GUI_PH_CONFIG_BLACKLIST))
                .lore(blacklistLore)
                .exitAndBuild(),
            this::blacklistClick
        );
    }

    private ItemStackElement<ChestPane> loreElement() {
        final List<String> resultLore = new ArrayList<>(List.of(
            this.lang.get(Lang.GUI_CONFIG_WG_LIST_LORE),
            "<white>------------"
        ));
        resultLore.addAll(this.playerHeadConfig().lore());
        return ItemStackElement.of(
            ItemBuilder.create(Material.PAPER).miniMessageContext()
                .customName(this.lang.get(Lang.GUI_PH_CONFIG_RESULT_LORE))
                .lore(resultLore)
                .exitAndBuild(),
            this::loreClick
        );
    }

    private ItemStackElement<ChestPane> saveElement(
        final SlotTransform ingredientOne,
        final SlotTransform ingredientTwo
    ) {
        return ItemStackElement.of(
            ItemBuilder.create(this.parts.saveTradeButton()).miniMessageContext()
                .lore(this.lang.get(Lang.GUI_PH_CONFIG_SAVE_LORE))
                .exitAndBuild(),
            context -> {
                if (ingredientOne.item() != null) {
                    final PlayerHeadConfig config = this.playerHeadConfig();
                    config.ingredientOne(ingredientOne.item());
                    config.ingredientTwo(ingredientTwo.item());
                    config.save();
                    this.plugin.playerHeads().configChanged();
                }
            }
        );
    }

    private ItemStackElement<ChestPane> resultHeadElement() {
        return ItemStackElement.of(
            new HeadBuilder(HeadSkins.NOTCH)
                .stackSize(this.playerHeadConfig().headsPerTrade())
                .miniMessageContext()
                .customName(this.playerHeadConfig().name().replace("{PLAYER}", "Notch"))
                .lore(this.playerHeadConfig().lore())
                .exitAndBuild()
        );
    }

    private void daysClick(final ClickContext<ChestPane, InventoryClickEvent, PlayerViewer> context) {
        context.viewer().player().closeInventory();
        InputConversation.create()
            .onPromptText(player -> {
                this.plugin.chat().sendParsed(player,
                    this.lang.get(Lang.MESSAGE_SET_HEADS_DAYS_PROMPT)
                        + "<reset>\n" + this.lang.get(Lang.MESSAGE_CURRENT_VALUE) + this.playerHeadConfig().days()
                        + "<reset>\n" + this.lang.get(Lang.MESSAGE_ENTER_NUMBER));
                return "";
            })
            .onValidateInput(this.validators::validateIntGTEN1)
            .onConfirmText(this.validators::confirmYesNo)
            .onAccepted((player, s) -> {
                this.playerHeadConfig().days(Integer.parseInt(s));
                this.playerHeadConfig().save();
                this.plugin.playerHeads().configChanged();
                this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_EDIT_SAVED));
                this.open(player);
            })
            .onDenied(this.validators::editCancelled)
            .start(context.viewer().player());
    }

    private void amountTradesClick(final ClickContext<ChestPane, InventoryClickEvent, PlayerViewer> context) {
        context.viewer().player().closeInventory();
        InputConversation.create()
            .onPromptText(player -> {
                this.plugin.chat().sendParsed(player,
                    this.lang.get(Lang.MESSAGE_SET_HEADS_TRADES_AMOUNT_PROMPT)
                        + "<reset>\n" + this.lang.get(Lang.MESSAGE_CURRENT_VALUE) + this.playerHeadConfig().playerHeadsFromServerAmount()
                        + "<reset>\n" + this.lang.get(Lang.MESSAGE_ENTER_NUMBER_OR_RANGE));
                return "";
            })
            .onValidateInput(this.validators::validateIntRange)
            .onConfirmText(this.validators::confirmYesNo)
            .onAccepted((player, s) -> {
                this.playerHeadConfig().playerHeadsFromServerAmount(s);
                this.playerHeadConfig().save();
                this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_EDIT_SAVED));
                this.open(player);
            })
            .onDenied(this.validators::editCancelled)
            .start(context.viewer().player());
    }

    private void amountHeadsClick(final ClickContext<ChestPane, InventoryClickEvent, PlayerViewer> context) {
        context.viewer().player().closeInventory();
        InputConversation.create()
            .onPromptText(player -> {
                this.plugin.chat().sendParsed(player,
                    this.lang.get(Lang.MESSAGE_SET_HEADS_AMOUNT_PROMPT)
                        + "<reset>\n" + this.lang.get(Lang.MESSAGE_CURRENT_VALUE) + this.playerHeadConfig().headsPerTrade()
                        + "<reset>\n" + this.lang.get(Lang.MESSAGE_ENTER_NUMBER));
                return "";
            })
            .onValidateInput(this.validators::validateIntGT0)
            .onConfirmText(this.validators::confirmYesNo)
            .onAccepted((player, s) -> {
                this.playerHeadConfig().headsPerTrade(Integer.parseInt(s));
                this.playerHeadConfig().save();
                this.plugin.playerHeads().configChanged();
                this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_EDIT_SAVED));
                this.open(player);
            })
            .onDenied(this.validators::editCancelled)
            .start(context.viewer().player());
    }

    private void maxUsesClick(final ClickContext<ChestPane, InventoryClickEvent, PlayerViewer> context) {
        context.viewer().player().closeInventory();
        InputConversation.create()
            .onPromptText(player -> {
                this.plugin.chat().sendParsed(player,
                    this.lang.get(Lang.MESSAGE_SET_MAX_USES_PROMPT)
                        + "<reset>\n" + this.lang.get(Lang.MESSAGE_CURRENT_VALUE) + this.playerHeadConfig().maxUses()
                        + "<reset>\n" + this.lang.get(Lang.MESSAGE_ENTER_NUMBER));
                return "";
            })
            .onValidateInput(this.validators::validateIntGT0)
            .onConfirmText(this.validators::confirmYesNo)
            .onAccepted((player, s) -> {
                this.playerHeadConfig().maxUses(Integer.parseInt(s));
                this.playerHeadConfig().save();
                this.plugin.playerHeads().configChanged();
                this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_EDIT_SAVED));
                this.open(player);
            })
            .onDenied(this.validators::editCancelled)
            .start(context.viewer().player());
    }

    private void chanceClick(final ClickContext<ChestPane, InventoryClickEvent, PlayerViewer> context) {
        context.viewer().player().closeInventory();
        InputConversation.create()
            .onPromptText(player -> {
                this.plugin.chat().sendParsed(player,
                    this.lang.get(Lang.MESSAGE_SET_CHANCE_PROMPT)
                        + "<reset>\n" + this.lang.get(Lang.MESSAGE_CURRENT_VALUE) + this.playerHeadConfig().playerHeadsFromServerChance()
                        + "<reset>\n" + this.lang.get(Lang.MESSAGE_ENTER_NUMBER));
                return "";
            })
            .onValidateInput(this.validators::validateDouble0T1)
            .onConfirmText(this.validators::confirmYesNo)
            .onAccepted((player, s) -> {
                this.playerHeadConfig().playerHeadsFromServerChance(Double.parseDouble(s));
                this.playerHeadConfig().save();
                this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_EDIT_SAVED));
                this.open(player);
            })
            .onDenied(this.validators::editCancelled)
            .start(context.viewer().player());
    }

    private void customNameClick(final ClickContext<ChestPane, InventoryClickEvent, PlayerViewer> context) {
        context.viewer().player().closeInventory();
        InputConversation.create()
            .onPromptText(player -> {
                this.plugin.chat().sendParsed(player,
                    this.lang.get(Lang.MESSAGE_CUSTOM_NAME_PROMPT)
                        + "<reset>\n" + this.lang.get(Lang.MESSAGE_CURRENT_VALUE) + "<reset>" + this.playerHeadConfig().name());
                return "";
            })
            .onValidateInput((pl, s) -> true)
            .onConfirmText(this.validators::confirmYesNo)
            .onAccepted((player, string) -> {
                this.playerHeadConfig().name(string);
                this.playerHeadConfig().save();
                this.plugin.playerHeads().configChanged();
                this.open(player);
            })
            .onDenied(this.validators::editCancelled)
            .start(context.viewer().player());
    }

    private void blacklistClick(final ClickContext<ChestPane, InventoryClickEvent, PlayerViewer> context) {
        if (context.cause().getClick().isRightClick()) {
            // Possible improvement: have user type name of element to remove instead of removing from tail
            List<String> l = this.playerHeadConfig().usernameBlacklist();
            if (!(l.size() - 1 < 0)) {
                l.remove(l.size() - 1);
            }
            this.playerHeadConfig().usernameBlacklist(l);
            this.plugin.playerHeads().configChanged();
            context.view().update();
        } else {
            context.viewer().player().closeInventory();
            InputConversation.create()
                .onPromptText(player -> {
                    this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_ADD_BLACKLIST_PLAYER));
                    return "";
                })
                .onValidateInput((player, input) -> {
                    if (input.contains(" ")) {
                        this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_NO_SPACES));
                        return false;
                    }
                    if (TextUtil.containsCaseInsensitive(input, this.playerHeadConfig().usernameBlacklist())) {
                        this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_CREATE_UNIQUE));
                        return false;
                    }
                    return true;
                })
                .onConfirmText(this.validators::confirmYesNo)
                .onAccepted((player, s) -> {
                    List<String> temp = this.playerHeadConfig().usernameBlacklist();
                    temp.add(s);
                    this.playerHeadConfig().usernameBlacklist(temp);
                    this.playerHeadConfig().save();
                    this.plugin.playerHeads().configChanged();
                    this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_EDIT_SAVED));
                    this.open(player);
                })
                .onDenied(this.validators::editCancelled)
                .start(context.viewer().player());
        }
    }

    private void loreClick(final ClickContext<ChestPane, InventoryClickEvent, PlayerViewer> context) {
        if (context.cause().getClick().isRightClick()) {
            List<String> l = this.playerHeadConfig().lore();
            if (!(l.size() - 1 < 0)) {
                l.remove(l.size() - 1);
            }
            this.playerHeadConfig().lore(l);
            context.view().update();
        } else {
            context.viewer().player().closeInventory();
            InputConversation.create()
                .onPromptText(player -> {
                    this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_ADD_LORE_PROMPT));
                    return "";
                })
                .onValidateInput((player, input) -> true)
                .onConfirmText(this.validators::confirmYesNo)
                .onAccepted((player, s) -> {
                    List<String> temp = this.playerHeadConfig().lore();
                    temp.add(s);
                    this.playerHeadConfig().lore(temp);
                    this.playerHeadConfig().save();
                    this.plugin.playerHeads().configChanged();
                    this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_EDIT_SAVED));
                    this.open(player);
                })
                .onDenied(this.validators::editCancelled)
                .start(context.viewer().player());
        }
    }
}
