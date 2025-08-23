package xyz.jpenilla.wanderingtrades.gui;

import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
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
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Messages;
import xyz.jpenilla.wanderingtrades.config.PlayerHeadConfig;
import xyz.jpenilla.wanderingtrades.gui.transform.SlotTransform;
import xyz.jpenilla.wanderingtrades.util.BooleanConsumer;
import xyz.jpenilla.wanderingtrades.util.Components;
import xyz.jpenilla.wanderingtrades.util.HeadBuilder;
import xyz.jpenilla.wanderingtrades.util.ItemBuilder;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static xyz.jpenilla.wanderingtrades.gui.PartsFactory.chestItem;
import static xyz.jpenilla.wanderingtrades.gui.PartsFactory.toggle;

@DefaultQualifier(NonNull.class)
public final class PlayerHeadConfigInterface extends BaseInterface {
    private final ItemStack permissionWhitelistStack = ItemBuilder.create(Material.LIME_STAINED_GLASS_PANE)
        .customName(Messages.GUI_PH_CONFIG_PWL_ENABLED)
        .lore(this.parts.toggleLore(), Messages.GUI_PH_CONFIG_PWL_LORE)
        .build();
    private final ItemStack noPermissionsWhitelistStack = ItemBuilder.create(Material.RED_STAINED_GLASS_PANE)
        .customName(Messages.GUI_PH_CONFIG_PWL_DISABLED)
        .lore(this.parts.toggleLore(), Messages.GUI_PH_CONFIG_PWL_LORE)
        .build();

    public PlayerHeadConfigInterface(final WanderingTrades plugin) {
        super(plugin);
    }

    @Override
    protected ChestInterface buildInterface() {
        final SlotTransform ingredientOne = new SlotTransform(
            ItemBuilder.create(Material.STRUCTURE_VOID)
                .customName(Messages.GUI_TRADE_INGREDIENT_1)
                .lore(Messages.GUI_TRADE_REQUIRED_LORE.asComponents())
                .build(),
            1,
            3
        );
        ingredientOne.item(this.playerHeadConfig().ingredientOne());

        final SlotTransform ingredientTwo = new SlotTransform(
            ItemBuilder.create(Material.STRUCTURE_VOID)
                .customName(Messages.GUI_TRADE_INGREDIENT_2)
                .lore(Messages.GUI_TRADE_OPTIONAL_LORE.asComponents())
                .build(),
            3,
            3
        );
        ingredientTwo.item(this.playerHeadConfig().ingredientTwo());

        return ChestInterface.builder()
            .rows(5)
            .title(Messages.GUI_PH_CONFIG_TITLE.asComponent())
            .addTransform(this.parts.fill())
            .addTransform(this.parts.toggle(
                Messages.GUI_PH_CONFIG_ENABLED,
                Messages.GUI_PH_CONFIG_DISABLED,
                this.playerHeadConfig()::playerHeadsFromServer,
                this.saveConfig(this.playerHeadConfig()::playerHeadsFromServer),
                0,
                1
            ))
            .addTransform(toggle(
                on -> on ? this.permissionWhitelistStack : this.noPermissionsWhitelistStack,
                this.playerHeadConfig()::permissionWhitelist,
                this.saveConfig(this.playerHeadConfig()::permissionWhitelist),
                1,
                1
            ))
            .addTransform(this.parts.toggle(
                Messages.GUI_TRADE_EXP_REWARD,
                Messages.GUI_TRADE_NO_EXP_REWARD,
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
            ItemBuilder.create(Material.LIGHT_BLUE_STAINED_GLASS_PANE)
                .customName(Messages.GUI_PH_CONFIG_DAYS)
                .lore(
                    this.parts.valueLore(this.playerHeadConfig().days(), TextColor.color(0x0092FF)),
                    Messages.GUI_EDIT_LORE,
                    Messages.GUI_PH_CONFIG_DAYS_LORE
                )
                .build(),
            this::daysClick
        );
    }

    private ItemStackElement<ChestPane> amountTradesElement() {
        return ItemStackElement.of(
            ItemBuilder.create(Material.LIGHT_BLUE_STAINED_GLASS_PANE)
                .customName(Messages.GUI_PH_CONFIG_AMOUNT)
                .lore(
                    this.parts.valueLore(this.playerHeadConfig().playerHeadsFromServerAmount(), TextColor.color(0x0092FF)),
                    Messages.GUI_EDIT_LORE
                )
                .build(),
            this::amountTradesClick
        );
    }

    private ItemStackElement<ChestPane> amountHeadsElement() {
        return ItemStackElement.of(
            ItemBuilder.create(Material.LIGHT_BLUE_STAINED_GLASS_PANE)
                .customName(Messages.GUI_PH_CONFIG_AMOUNT_HEADS)
                .lore(
                    this.parts.valueLore(this.playerHeadConfig().headsPerTrade(), TextColor.color(0x0092FF)),
                    Messages.GUI_EDIT_LORE
                )
                .build(),
            this::amountHeadsClick
        );
    }

    public ItemStackElement<ChestPane> headChanceElement() {
        return ItemStackElement.of(
            ItemBuilder.create(Material.PURPLE_STAINED_GLASS_PANE)
                .customName(Messages.GUI_PH_CONFIG_CHANCE)
                .lore(
                    this.parts.valueLore(this.playerHeadConfig().playerHeadsFromServerChance(), TextColor.color(0x0092FF)),
                    Messages.GUI_EDIT_LORE
                )
                .build(),
            this::chanceClick
        );
    }

    public ItemStackElement<ChestPane> customNameElement() {
        return ItemStackElement.of(
            ItemBuilder.create(Material.PINK_STAINED_GLASS_PANE)
                .customName(Messages.GUI_TC_EDIT_CUSTOM_NAME)
                .lore(
                    this.parts.valueLore(miniMessage().deserialize(this.playerHeadConfig().name()).colorIfAbsent(NamedTextColor.YELLOW)),
                    Messages.GUI_EDIT_LORE
                )
                .build(),
            this::customNameClick
        );
    }

    private ItemStackElement<ChestPane> blacklistElement() {
        final List<ComponentLike> blacklistLore = new ArrayList<>(List.of(
            Messages.GUI_CONFIG_WG_LIST_LORE,
            Component.empty()
        ));
        this.playerHeadConfig().usernameBlacklist().forEach(name -> blacklistLore.add(
            miniMessage().deserialize(" <red>-</red> <white>" + name)
        ));
        return ItemStackElement.of(
            ItemBuilder.create(Material.PAPER)
                .customName(Messages.GUI_PH_CONFIG_BLACKLIST)
                .lore(blacklistLore)
                .build(),
            this::blacklistClick
        );
    }

    private ItemStackElement<ChestPane> loreElement() {
        final List<ComponentLike> resultLore = new ArrayList<>(List.of(
            Messages.GUI_CONFIG_WG_LIST_LORE,
            Component.text("------------", NamedTextColor.WHITE)
        ));
        resultLore.addAll(this.playerHeadConfig().lore().stream().map(miniMessage()::deserialize).toList());
        return ItemStackElement.of(
            ItemBuilder.create(Material.PAPER)
                .customName(Messages.GUI_PH_CONFIG_RESULT_LORE)
                .lore(resultLore)
                .build(),
            this::loreClick
        );
    }

    private ItemStackElement<ChestPane> saveElement(
        final SlotTransform ingredientOne,
        final SlotTransform ingredientTwo
    ) {
        return ItemStackElement.of(
            ItemBuilder.create(this.parts.saveTradeButton())
                .lore(Messages.GUI_PH_CONFIG_SAVE_LORE)
                .build(),
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
                player.sendMessage(Messages.MESSAGE_SET_HEADS_DAYS_PROMPT);
                player.sendMessage(Messages.MESSAGE_CURRENT_VALUE.withPlaceholders(Components.valuePlaceholder(this.playerHeadConfig().days())));
                player.sendMessage(Messages.MESSAGE_ENTER_NUMBER);
                return "";
            })
            .onValidateInput(this.validators::validateIntGTEN1)
            .onConfirmText(this.validators::confirmYesNo)
            .onAccepted((player, s) -> {
                this.playerHeadConfig().days(Integer.parseInt(s));
                this.playerHeadConfig().save();
                this.plugin.playerHeads().configChanged();
                player.sendMessage(Messages.MESSAGE_EDIT_SAVED);
                this.open(player);
            })
            .onDenied(this.validators::editCancelled)
            .start(context.viewer().player());
    }

    private void amountTradesClick(final ClickContext<ChestPane, InventoryClickEvent, PlayerViewer> context) {
        context.viewer().player().closeInventory();
        InputConversation.create()
            .onPromptText(player -> {
                player.sendMessage(Messages.MESSAGE_SET_HEADS_TRADES_AMOUNT_PROMPT);
                player.sendMessage(Messages.MESSAGE_CURRENT_VALUE.withPlaceholders(Components.valuePlaceholder(this.playerHeadConfig().playerHeadsFromServerAmount())));
                player.sendMessage(Messages.MESSAGE_ENTER_NUMBER_OR_RANGE);
                return "";
            })
            .onValidateInput(this.validators::validateIntRange)
            .onConfirmText(this.validators::confirmYesNo)
            .onAccepted((player, s) -> {
                this.playerHeadConfig().playerHeadsFromServerAmount(s);
                this.playerHeadConfig().save();
                player.sendMessage(Messages.MESSAGE_EDIT_SAVED);
                this.open(player);
            })
            .onDenied(this.validators::editCancelled)
            .start(context.viewer().player());
    }

    private void amountHeadsClick(final ClickContext<ChestPane, InventoryClickEvent, PlayerViewer> context) {
        context.viewer().player().closeInventory();
        InputConversation.create()
            .onPromptText(player -> {
                player.sendMessage(Messages.MESSAGE_SET_HEADS_AMOUNT_PROMPT);
                player.sendMessage(Messages.MESSAGE_CURRENT_VALUE.withPlaceholders(Components.valuePlaceholder(this.playerHeadConfig().headsPerTrade())));
                player.sendMessage(Messages.MESSAGE_ENTER_NUMBER);
                return "";
            })
            .onValidateInput(this.validators::validateIntGT0)
            .onConfirmText(this.validators::confirmYesNo)
            .onAccepted((player, s) -> {
                this.playerHeadConfig().headsPerTrade(Integer.parseInt(s));
                this.playerHeadConfig().save();
                this.plugin.playerHeads().configChanged();
                player.sendMessage(Messages.MESSAGE_EDIT_SAVED);
                this.open(player);
            })
            .onDenied(this.validators::editCancelled)
            .start(context.viewer().player());
    }

    private void maxUsesClick(final ClickContext<ChestPane, InventoryClickEvent, PlayerViewer> context) {
        context.viewer().player().closeInventory();
        InputConversation.create()
            .onPromptText(player -> {
                player.sendMessage(Messages.MESSAGE_SET_MAX_USES_PROMPT);
                player.sendMessage(Messages.MESSAGE_CURRENT_VALUE.withPlaceholders(Components.valuePlaceholder(this.playerHeadConfig().maxUses())));
                player.sendMessage(Messages.MESSAGE_ENTER_NUMBER);
                return "";
            })
            .onValidateInput(this.validators::validateIntGT0)
            .onConfirmText(this.validators::confirmYesNo)
            .onAccepted((player, s) -> {
                this.playerHeadConfig().maxUses(Integer.parseInt(s));
                this.playerHeadConfig().save();
                this.plugin.playerHeads().configChanged();
                player.sendMessage(Messages.MESSAGE_EDIT_SAVED);
                this.open(player);
            })
            .onDenied(this.validators::editCancelled)
            .start(context.viewer().player());
    }

    private void chanceClick(final ClickContext<ChestPane, InventoryClickEvent, PlayerViewer> context) {
        context.viewer().player().closeInventory();
        InputConversation.create()
            .onPromptText(player -> {
                player.sendMessage(Messages.MESSAGE_SET_CHANCE_PROMPT);
                player.sendMessage(Messages.MESSAGE_CURRENT_VALUE.withPlaceholders(Components.valuePlaceholder(this.playerHeadConfig().playerHeadsFromServerChance())));
                player.sendMessage(Messages.MESSAGE_ENTER_NUMBER);
                return "";
            })
            .onValidateInput(this.validators::validateDouble0T1)
            .onConfirmText(this.validators::confirmYesNo)
            .onAccepted((player, s) -> {
                this.playerHeadConfig().playerHeadsFromServerChance(Double.parseDouble(s));
                this.playerHeadConfig().save();
                player.sendMessage(Messages.MESSAGE_EDIT_SAVED);
                this.open(player);
            })
            .onDenied(this.validators::editCancelled)
            .start(context.viewer().player());
    }

    private void customNameClick(final ClickContext<ChestPane, InventoryClickEvent, PlayerViewer> context) {
        context.viewer().player().closeInventory();
        InputConversation.create()
            .onPromptText(player -> {
                player.sendMessage(Messages.MESSAGE_CUSTOM_NAME_PROMPT);
                player.sendMessage(Messages.MESSAGE_CURRENT_VALUE.withPlaceholders(
                    Components.valuePlaceholder(miniMessage().deserialize(this.playerHeadConfig().name()))
                ));
                return "";
            })
            .onValidateInput((pl, s) -> true)
            .onConfirmText(this.validators.confirmYesNo(s -> miniMessage().deserialize(s).colorIfAbsent(NamedTextColor.YELLOW)))
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
                    player.sendMessage(Messages.MESSAGE_ADD_BLACKLIST_PLAYER);
                    return "";
                })
                .onValidateInput((player, input) -> {
                    if (input.contains(" ")) {
                        player.sendMessage(Messages.MESSAGE_NO_SPACES);
                        return false;
                    }
                    if (TextUtil.containsCaseInsensitive(input, this.playerHeadConfig().usernameBlacklist())) {
                        player.sendMessage(Messages.MESSAGE_CREATE_UNIQUE);
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
                    player.sendMessage(Messages.MESSAGE_EDIT_SAVED);
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
                    player.sendMessage(Messages.MESSAGE_ADD_LORE_PROMPT);
                    return "";
                })
                .onValidateInput((player, input) -> true)
                .onConfirmText(this.validators.confirmYesNo(s -> miniMessage().deserialize(s).colorIfAbsent(NamedTextColor.DARK_PURPLE)))
                .onAccepted((player, s) -> {
                    List<String> temp = this.playerHeadConfig().lore();
                    temp.add(s);
                    this.playerHeadConfig().lore(temp);
                    this.playerHeadConfig().save();
                    this.plugin.playerHeads().configChanged();
                    player.sendMessage(Messages.MESSAGE_EDIT_SAVED);
                    this.open(player);
                })
                .onDenied(this.validators::editCancelled)
                .start(context.viewer().player());
        }
    }
}
