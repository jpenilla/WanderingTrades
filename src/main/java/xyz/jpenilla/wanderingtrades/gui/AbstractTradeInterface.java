package xyz.jpenilla.wanderingtrades.gui;

import java.util.ArrayList;
import java.util.List;
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
import xyz.jpenilla.pluginbase.legacy.HeadBuilder;
import xyz.jpenilla.pluginbase.legacy.InputConversation;
import xyz.jpenilla.pluginbase.legacy.ItemBuilder;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Lang;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;
import xyz.jpenilla.wanderingtrades.gui.transform.SlotTransform;

import static xyz.jpenilla.wanderingtrades.gui.PartsFactory.chestItem;

@DefaultQualifier(NonNull.class)
public abstract class AbstractTradeInterface extends BaseInterface {
    private final ItemStack info = new HeadBuilder(HeadSkins.INFO)
        .setName(this.lang.get(Lang.GUI_TRADE_INFO))
        .setLore(this.lang.getList(Lang.GUI_TRADE_INFO_LORE))
        .build();
    private final ItemStack cancelButton = new HeadBuilder(HeadSkins.RED_X_ON_BLACK)
        .setName(this.lang.get(Lang.GUI_TRADE_CANCEL))
        .setLore(this.lang.get(Lang.GUI_TRADE_CANCEL_LORE))
        .build();
    private final ItemStack saveButton = new HeadBuilder(HeadSkins.GREEN_CHECK_ON_BLACK)
        .setName(this.lang.get(Lang.GUI_TRADE_SAVE))
        .setLore(this.lang.get(Lang.GUI_TRADE_SAVE_LORE))
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

        this.ingredientOne = new SlotTransform(
            new ItemBuilder(Material.STRUCTURE_VOID)
                .setName(this.lang.get(Lang.GUI_TRADE_INGREDIENT_1))
                .setLore(this.lang.getList(Lang.GUI_TRADE_REQUIRED_LORE))
                .build(),
            1,
            3
        );
        this.ingredientTwo = new SlotTransform(
            new ItemBuilder(Material.STRUCTURE_VOID)
                .setName(this.lang.get(Lang.GUI_TRADE_INGREDIENT_2))
                .setLore(this.lang.getList(Lang.GUI_TRADE_REQUIRED_LORE))
                .build(),
            3,
            3
        );
        this.result = new SlotTransform(
            new ItemBuilder(Material.STRUCTURE_VOID)
                .setName(this.lang.get(Lang.GUI_TRADE_RESULT))
                .setLore(this.lang.getList(Lang.GUI_TRADE_REQUIRED_LORE))
                .build(),
            5,
            3
        );
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
                this.plugin.chat().sendParsed(player,
                    this.lang.get(Lang.MESSAGE_SET_MAX_USES_PROMPT)
                        + "<reset>\n" + this.lang.get(Lang.MESSAGE_CURRENT_VALUE) + this.maxUses
                        + "<reset>\n" + this.lang.get(Lang.MESSAGE_ENTER_NUMBER));
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
        return chestItem(this::experienceRewardElement, 3, 1);
    }

    private ItemStackElement<ChestPane> experienceRewardElement() {
        return this.parts.toggle(
            this.lang.get(Lang.GUI_TRADE_EXP_REWARD),
            this.lang.get(Lang.GUI_TRADE_NO_EXP_REWARD),
            () -> this.experienceReward,
            value -> this.experienceReward = value
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
        return chestItem(
            () -> ItemStackElement.of(this.tradeNameStack(canEdit), clickHandler),
            1,
            1
        );
    }

    private ItemStack tradeNameStack(final boolean canEdit) {
        final List<String> lore = new ArrayList<>();
        lore.add(this.tradeNameValueLore());
        if (canEdit) {
            lore.add(this.lang.get(Lang.GUI_EDIT_LORE));
        }
        return new ItemBuilder(Material.PINK_STAINED_GLASS_PANE)
            .setName(this.lang.get(Lang.GUI_TRADE_TRADE_NAME))
            .setLore(lore)
            .build();
    }

    private String tradeNameValueLore() {
        final @Nullable String tradeName = this.tradeName();
        final String displayName = tradeName == null ? "<gray>________" : "<white>" + tradeName;
        return this.lang.get(Lang.GUI_VALUE_LORE) + displayName;
    }

    protected Transform<ChestPane, PlayerViewer> saveTransform() {
        return (pane, view) -> pane.element(ItemStackElement.of(this.saveButton, this::saveClick), 7, pane.rows() - 1);
    }

    private void saveClick(final ClickContext<ChestPane, InventoryClickEvent, PlayerViewer> context) {
        final @Nullable String tradeName = this.tradeName();
        if (tradeName != null && this.result.item() != null && this.ingredientOne.item() != null) {
            this.tradeConfig.addTrade(
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
