package xyz.jpenilla.wanderingtrades.gui;

import java.util.List;
import java.util.Map;
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
import xyz.jpenilla.pluginbase.legacy.itembuilder.HeadBuilder;
import xyz.jpenilla.pluginbase.legacy.itembuilder.ItemBuilder;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Lang;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;

@DefaultQualifier(NonNull.class)
public final class ListTradesInterface extends BaseInterface {
    private final TradeConfig tradeConfig;

    public ListTradesInterface(
        final WanderingTrades plugin,
        final TradeConfig tradeConfig
    ) {
        super(plugin);
        this.tradeConfig = tradeConfig;
    }

    @Override
    protected ChestInterface buildInterface() {
        final int rows = 6;
        return ChestInterface.builder()
            .rows(rows)
            .title(this.plugin.miniMessage().deserialize(this.lang.get(Lang.GUI_TRADE_LIST_TITLE) + this.tradeConfig.configName()))
            .addTransform(this.parts.fillBottomRow())
            .addTransform((pane, view) -> pane.element(this.newTradeElement(), 4, pane.rows() - 1))
            .addTransform((pane, view) -> pane.element(this.settingsElement(), 7, pane.rows() - 1))
            .addTransform(this.parts.backButton(context -> new ListTradeConfigsInterface(this.plugin).replaceActiveScreen(context)))
            .addReactiveTransform(this.parts.paginationTransform(rows, this::listElements))
            .build();
    }

    private ItemStackElement<ChestPane> settingsElement() {
        final ItemStack stack = new HeadBuilder(HeadSkins.WRENCH_ON_IRON)
            .miniMessageContext()
            .customName(this.lang.get(Lang.GUI_TRADE_LIST_EDIT_CONFIG))
            .lore(this.lang.get(Lang.GUI_TRADE_LIST_EDIT_CONFIG_LORE))
            .exitAndBuild();
        return ItemStackElement.of(stack, this::settingsClick);
    }

    private void settingsClick(final ClickContext<ChestPane, InventoryClickEvent, PlayerViewer> context) {
        new TradeConfigSettingsInterface(this.plugin, this.tradeConfig).replaceActiveScreen(context);
    }

    private ItemStackElement<ChestPane> newTradeElement() {
        final ItemStack stack = new HeadBuilder(HeadSkins.GREEN_PLUS)
            .miniMessageContext()
            .customName(this.lang.get(Lang.GUI_TRADE_LIST_NEW_TRADE))
            .lore(this.lang.get(Lang.GUI_TRADE_LIST_NEW_TRADE_LORE))
            .exitAndBuild();
        return ItemStackElement.of(stack, this::newTradeClick);
    }

    private void newTradeClick(final ClickContext<ChestPane, InventoryClickEvent, PlayerViewer> context) {
        new TradeCreateInterface(this.plugin, this.tradeConfig).replaceActiveScreen(context);
    }

    private List<ItemStackElement<ChestPane>> listElements() {
        return this.tradeConfig.tradesByName().entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(entry -> this.tradeEntry(entry.getKey(), entry.getValue()))
            .toList();
    }

    private ItemStackElement<ChestPane> tradeEntry(final String tradeName, final MerchantRecipe recipe) {
        final ItemStack stack = ItemBuilder.create(recipe.getResult())
            .clearEnchants()
            .clearLore()
            .miniMessageContext()
            .customName(tradeName)
            .exitAndBuild();
        return ItemStackElement.of(stack, context -> this.tradeEntryClick(tradeName, context));
    }

    private void tradeEntryClick(
        final String tradeName,
        final ClickContext<ChestPane, InventoryClickEvent, PlayerViewer> context
    ) {
        new TradeEditInterface(this.plugin, this.tradeConfig, tradeName).replaceActiveScreen(context);
    }
}
