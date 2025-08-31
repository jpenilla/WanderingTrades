package xyz.jpenilla.wanderingtrades.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.incendo.interfaces.core.click.ClickContext;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.pane.ChestPane;
import org.incendo.interfaces.paper.type.ChestInterface;
import org.jspecify.annotations.NullMarked;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Messages;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;
import xyz.jpenilla.wanderingtrades.util.Components;
import xyz.jpenilla.wanderingtrades.util.HeadBuilder;
import xyz.jpenilla.wanderingtrades.util.InputConversation;

import static xyz.jpenilla.wanderingtrades.gui.PartsFactory.chestItem;

@NullMarked
public final class TradeEditInterface extends AbstractTradeInterface {
    private final ItemStack deleteButton = new HeadBuilder(HeadSkins.RED_RECYCLE_BIN_FULL)
        .customName(Messages.GUI_TRADE_DELETE)
        .lore(Messages.GUI_TRADE_DELETE_LORE)
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
            .title(Component.textOfChildren(Messages.GUI_TRADE_EDIT_TITLE, Component.text(this.tradeName)))
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
                player.sendMessage(
                    Messages.MESSAGE_DELETE_PROMPT.withPlaceholders(Components.placeholder("name", this.tradeName))
                );
                player.sendMessage(
                    Messages.MESSAGE_CONFIRM.withPlaceholders(Components.placeholder("key", Messages.MESSAGE_CONFIRM_KEY.message()))
                );
                return "";
            })
            .onValidateInput((player, s) -> {
                if (s.equals(Messages.MESSAGE_CONFIRM_KEY.message())) {
                    this.tradeConfig.deleteTrade(this.tradeName);
                    player.sendMessage(Messages.MESSAGE_EDIT_SAVED);
                    new ListTradesInterface(this.plugin, this.tradeConfig).open(player);
                } else {
                    this.validators.editCancelled(player, s);
                }
                return true;
            })
            .start(context.viewer().player());
    }
}
