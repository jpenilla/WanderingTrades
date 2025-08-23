package xyz.jpenilla.wanderingtrades.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.incendo.interfaces.core.click.ClickContext;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.pane.ChestPane;
import org.incendo.interfaces.paper.type.ChestInterface;
import xyz.jpenilla.pluginbase.legacy.InputConversation;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Messages;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;

import static xyz.jpenilla.wanderingtrades.gui.PartsFactory.chestItem;

@DefaultQualifier(NonNull.class)
public final class TradeCreateInterface extends AbstractTradeInterface {
    private @Nullable String tradeName;

    public TradeCreateInterface(
        final WanderingTrades plugin,
        final TradeConfig tradeConfig
    ) {
        super(plugin, tradeConfig);
    }

    @Override
    protected ChestInterface buildInterface() {
        this.maxUses = 1;
        this.experienceReward = true;

        return ChestInterface.builder()
            .rows(5)
            .title(Messages.GUI_TRADE_CREATE_TITLE.asComponent())
            .addTransform(this.parts.fill())
            .addTransform(this.infoTransform())
            .addTransform(this.tradeNameTransform(this::tradeNameClick))
            .addTransform(this.experienceRewardTransform())
            .addTransform(this.maxUsesTransform())
            .addReactiveTransform(this.ingredientOne)
            .addTransform(chestItem(this.parts.plus(), 2, 3))
            .addReactiveTransform(this.ingredientTwo)
            .addTransform(chestItem(this.parts.equals(), 4, 3))
            .addReactiveTransform(this.result)
            .addTransform(this.cancelTransform())
            .addTransform(this.saveTransform())
            .build();
    }

    @Override
    protected @Nullable String tradeName() {
        return this.tradeName;
    }

    private void tradeNameClick(final ClickContext<ChestPane, InventoryClickEvent, PlayerViewer> context) {
        context.viewer().player().closeInventory();
        InputConversation.create()
            .onPromptText(player -> {
                player.sendMessage(Messages.MESSAGE_CREATE_TRADE_PROMPT);
                return "";
            })
            .onValidateInput((player, input) -> {
                if (input.contains(" ")) {
                    player.sendMessage(Messages.MESSAGE_NO_SPACES);
                    return false;
                }
                if (this.tradeConfig.tradesByName().containsKey(input)) {
                    player.sendMessage(Messages.MESSAGE_CREATE_UNIQUE);
                    return false;
                }
                return true;
            })
            .onConfirmText(this.validators::confirmYesNo)
            .onAccepted((player, s) -> {
                this.tradeName = s;
                this.open(player);
            })
            .onDenied(this.validators::editCancelled)
            .start(context.viewer().player());
    }
}
