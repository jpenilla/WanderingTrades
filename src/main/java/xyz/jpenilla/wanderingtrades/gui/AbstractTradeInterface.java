package xyz.jpenilla.wanderingtrades.gui;

import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.incendo.interfaces.core.click.ClickContext;
import org.incendo.interfaces.core.click.ClickHandler;
import org.incendo.interfaces.core.transform.Transform;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.pane.ChestPane;
import xyz.jpenilla.pluginbase.legacy.InputConversation;
import xyz.jpenilla.pluginbase.legacy.itembuilder.HeadBuilder;
import xyz.jpenilla.pluginbase.legacy.itembuilder.ItemBuilder;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Messages;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;
import xyz.jpenilla.wanderingtrades.gui.transform.SlotTransform;
import xyz.jpenilla.wanderingtrades.util.Components;

import static xyz.jpenilla.wanderingtrades.gui.PartsFactory.chestItem;

@DefaultQualifier(NonNull.class)
public abstract class AbstractTradeInterface extends BaseInterface {
    private final ItemStack info = new HeadBuilder(HeadSkins.INFO)
        .customName(Messages.GUI_TRADE_INFO)
        .lore(Messages.GUI_TRADE_INFO_LORE.asComponents())
        .build();
    private final ItemStack cancelButton = new HeadBuilder(HeadSkins.RED_X_ON_BLACK)
        .customName(Messages.GUI_TRADE_CANCEL)
        .lore(Messages.GUI_TRADE_CANCEL_LORE)
        .build();
    private final ItemStack saveButton = new HeadBuilder(HeadSkins.GREEN_CHECK_ON_BLACK)
        .customName(Messages.GUI_TRADE_SAVE)
        .lore(Messages.GUI_TRADE_SAVE_LORE)
        .build();
    protected final TradeConfig tradeConfig;
    protected final SlotTransform ingredientOne;
    protected final SlotTransform ingredientTwo;
    protected final SlotTransform result;
    protected int maxUses;
    protected boolean experienceReward;

    protected AbstractTradeInterface(final WanderingTrades plugin, final TradeConfig tradeConfig) {
        super(plugin);
        this.tradeConfig = tradeConfig;

        final ItemStack emptySlotOne = ItemBuilder.create(Material.STRUCTURE_VOID)
            .customName(Messages.GUI_TRADE_INGREDIENT_1)
            .lore(Messages.GUI_TRADE_REQUIRED_LORE.asComponents())
            .build();
        final ItemStack emptySlotTwo = ItemBuilder.create(Material.STRUCTURE_VOID)
            .customName(Messages.GUI_TRADE_INGREDIENT_2)
            .lore(Messages.GUI_TRADE_OPTIONAL_LORE.asComponents())
            .build();
        final ItemStack emptyResult = ItemBuilder.create(Material.STRUCTURE_VOID)
            .customName(Messages.GUI_TRADE_RESULT)
            .lore(Messages.GUI_TRADE_REQUIRED_LORE.asComponents())
            .build();
        this.ingredientOne = new SlotTransform(emptySlotOne, 1, 3);
        this.ingredientTwo = new SlotTransform(emptySlotTwo, 3, 3);
        this.result = new SlotTransform(emptyResult, 5, 3);
    }

    protected abstract @Nullable String tradeName();

    protected Transform<ChestPane, PlayerViewer> infoTransform() {
        return chestItem(ItemStackElement.of(this.info), 8, 0);
    }

    protected Transform<ChestPane, PlayerViewer> maxUsesTransform() {
        return chestItem(() -> this.parts.maxUsesElement(this.maxUses, this::maxUsesClick), 5, 1);
    }

    protected void maxUsesClick(final ClickContext<ChestPane, InventoryClickEvent, PlayerViewer> context) {
        context.viewer().player().closeInventory();
        InputConversation.create()
            .onPromptText(player -> {
                this.plugin.chat().send(player, Messages.MESSAGE_SET_MAX_USES_PROMPT);
                this.plugin.chat().send(player, Messages.MESSAGE_CURRENT_VALUE.withPlaceholders(Components.valuePlaceholder(this.maxUses)));
                this.plugin.chat().send(player, Messages.MESSAGE_ENTER_NUMBER);
                return "";
            })
            .onValidateInput(this.validators::validateIntGT0)
            .onConfirmText(this.validators::confirmYesNo)
            .onAccepted((player, s) -> {
                this.maxUses = Integer.parseInt(s);
                this.open(player);
            })
            .onDenied(this.validators::editCancelled)
            .start(context.viewer().player());
    }

    protected Transform<ChestPane, PlayerViewer> experienceRewardTransform() {
        return this.parts.toggle(
            Messages.GUI_TRADE_EXP_REWARD,
            Messages.GUI_TRADE_NO_EXP_REWARD,
            () -> this.experienceReward,
            value -> this.experienceReward = value,
            3,
            1
        );
    }

    protected Transform<ChestPane, PlayerViewer> cancelTransform() {
        final ItemStackElement<ChestPane> element = ItemStackElement.of(
            this.cancelButton,
            context -> new ListTradesInterface(this.plugin, this.tradeConfig).replaceActiveScreen(context)
        );
        return (pane, view) -> pane.element(element, 8, pane.rows() - 1);
    }

    protected Transform<ChestPane, PlayerViewer> tradeNameTransform(
        final ClickHandler<ChestPane, InventoryClickEvent, PlayerViewer, ClickContext<ChestPane, InventoryClickEvent, PlayerViewer>> clickHandler
    ) {
        return this.tradeNameTransform(true, clickHandler);
    }

    protected Transform<ChestPane, PlayerViewer> tradeNameTransform() {
        return this.tradeNameTransform(false, ClickHandler.dummy());
    }

    private Transform<ChestPane, PlayerViewer> tradeNameTransform(
        final boolean canEdit,
        final ClickHandler<ChestPane, InventoryClickEvent, PlayerViewer, ClickContext<ChestPane, InventoryClickEvent, PlayerViewer>> clickHandler
    ) {
        return chestItem(() -> ItemStackElement.of(this.tradeNameStack(canEdit), clickHandler), 1, 1);
    }

    private ItemStack tradeNameStack(final boolean canEdit) {
        final List<ComponentLike> lore = new ArrayList<>();
        lore.add(this.tradeNameValueLore());
        if (canEdit) {
            lore.add(Messages.GUI_EDIT_LORE);
        }
        return ItemBuilder.create(Material.PINK_STAINED_GLASS_PANE)
            .customName(Messages.GUI_TRADE_TRADE_NAME)
            .lore(lore)
            .build();
    }

    private Component tradeNameValueLore() {
        final @Nullable String tradeName = this.tradeName();
        final String displayName = tradeName == null ? "<gray>________" : "<white>" + tradeName;
        return this.parts.valueLore(this.plugin.miniMessage().deserialize(displayName));
    }

    protected Transform<ChestPane, PlayerViewer> saveTransform() {
        return (pane, view) -> pane.element(ItemStackElement.of(this.saveButton, this::saveClick), 7, pane.rows() - 1);
    }

    private void saveClick(final ClickContext<ChestPane, InventoryClickEvent, PlayerViewer> context) {
        final @Nullable String tradeName = this.tradeName();
        if (tradeName != null && this.result.item() != null && this.ingredientOne.item() != null) {
            this.tradeConfig.setTrade(
                tradeName,
                this.maxUses,
                this.experienceReward,
                this.ingredientOne.item(),
                this.ingredientTwo.item(),
                this.result.item()
            );
            new ListTradesInterface(this.plugin, this.tradeConfig).replaceActiveScreen(context);
        }
    }
}
