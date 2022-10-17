package xyz.jpenilla.wanderingtrades.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.incendo.interfaces.core.click.ClickContext;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.pane.ChestPane;
import org.incendo.interfaces.paper.type.ChestInterface;
import xyz.jpenilla.pluginbase.legacy.HeadBuilder;
import xyz.jpenilla.pluginbase.legacy.InputConversation;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Lang;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;

import static xyz.jpenilla.wanderingtrades.gui.PartsFactory.chestItem;

@DefaultQualifier(NonNull.class)
public final class TradeEditInterface extends AbstractTradeInterface {
    private final ItemStack deleteButton = new HeadBuilder(HeadSkins.RED_RECYCLE_BIN_FULL)
        .setName(this.lang.get(Lang.GUI_TRADE_DELETE))
        .setLore(this.lang.get(Lang.GUI_TRADE_DELETE_LORE))
        .build();
    private final String tradeName;

    public TradeEditInterface(
        final WanderingTrades plugin,
        final TradeConfig tradeConfig,
        final String tradeName
    ) {
        super(plugin, tradeConfig);
        this.tradeName = tradeName;
    }

    @Override
    protected ChestInterface buildInterface() {
        final MerchantRecipe trade = this.tradeConfig.getTrade(this.tradeName);

        this.ingredientOne.item(trade.getIngredients().get(0));
        if (trade.getIngredients().size() == 2) {
            this.ingredientTwo.item(trade.getIngredients().get(1));
        }
        this.result.item(trade.getResult());
        this.maxUses = trade.getMaxUses();
        this.experienceReward = trade.hasExperienceReward();

        return ChestInterface.builder()
            .rows(5)
            .title(this.plugin.miniMessage().deserialize(this.lang.get(Lang.GUI_TRADE_EDIT_TITLE) + this.tradeName))
            .addTransform(this.parts.fill())
            .addTransform(this.infoTransform())
            .addTransform(this.tradeNameTransform())
            .addTransform(this.experienceRewardTransform())
            .addTransform(this.maxUsesTransform())
            .addReactiveTransform(this.ingredientOne)
            .addTransform(chestItem(this.parts.plus(), 2, 3))
            .addReactiveTransform(this.ingredientTwo)
            .addTransform(chestItem(this.parts.equals(), 4, 3))
            .addReactiveTransform(this.result)
            .addTransform(this.cancelTransform())
            .addTransform(this.saveTransform())
            .addTransform(chestItem(ItemStackElement.of(this.deleteButton, this::deleteClick), 8, 3))
            .build();
    }

    @Override
    protected String tradeName() {
        return this.tradeName;
    }

    private void deleteClick(final ClickContext<ChestPane, InventoryClickEvent, PlayerViewer> context) {
        context.viewer().player().closeInventory();
        InputConversation.create()
            .onPromptText(player -> {
                this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_DELETE_PROMPT).replace("{TRADE_NAME}", this.tradeName));
                this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_CONFIRM).replace("{KEY}", this.lang.get(Lang.MESSAGE_CONFIRM_KEY)));
                return "";
            })
            .onValidateInput((player, s) -> {
                if (s.equals(this.lang.get(Lang.MESSAGE_CONFIRM_KEY))) {
                    this.tradeConfig.deleteTrade(this.tradeName);
                    this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_EDIT_SAVED));
                    new ListTradesInterface(this.plugin, this.tradeConfig).open(player);
                } else {
                    this.validators.editCancelled(player, s);
                }
                return true;
            })
            .start(context.viewer().player());
    }
}
