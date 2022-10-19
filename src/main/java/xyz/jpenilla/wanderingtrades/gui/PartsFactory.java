package xyz.jpenilla.wanderingtrades.gui;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.incendo.interfaces.core.click.ClickContext;
import org.incendo.interfaces.core.click.ClickHandler;
import org.incendo.interfaces.core.transform.Transform;
import org.incendo.interfaces.core.transform.types.PaginatedTransform;
import org.incendo.interfaces.core.util.Vector2;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.pane.ChestPane;
import org.incendo.interfaces.paper.transform.PaperTransform;
import xyz.jpenilla.pluginbase.legacy.itembuilder.HeadBuilder;
import xyz.jpenilla.pluginbase.legacy.itembuilder.ItemBuilder;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Lang;
import xyz.jpenilla.wanderingtrades.config.LangConfig;
import xyz.jpenilla.wanderingtrades.util.BooleanConsumer;

import static org.incendo.interfaces.paper.transform.PaperTransform.chestFill;

@DefaultQualifier(NonNull.class)
public final class PartsFactory {
    private final LangConfig lang;
    private final ItemStack plus = new HeadBuilder(HeadSkins.LIGHT_BLUE_PLUS)
        .customName(Component.text('+', NamedTextColor.YELLOW))
        .build();
    private final ItemStack equals = new HeadBuilder(HeadSkins.LIGHT_BLUE_EQUALS)
        .customName(Component.text('=', NamedTextColor.YELLOW))
        .build();
    private final ItemStack filler = ItemBuilder.create(Material.GRAY_STAINED_GLASS_PANE)
        .customName(Component.text(' '))
        .build();
    private final ItemStack saveTradeButton;
    private final ItemStack backButton;
    private final ItemStack closeButton;
    private final ItemStack nextPage;
    private final ItemStack previousPage;
    private final String toggleLore;

    PartsFactory(final WanderingTrades plugin) {
        this.lang = plugin.langConfig();
        this.toggleLore = this.lang.get(Lang.GUI_TOGGLE_LORE);
        this.backButton = new HeadBuilder(HeadSkins.REDSTONE_BACKWARDS_ARROW).miniMessageContext()
            .customName(this.lang.get(Lang.GUI_BACK))
            .lore(this.lang.get(Lang.GUI_BACK_LORE))
            .exitAndBuild();
        this.closeButton = ItemBuilder.create(Material.BARRIER).miniMessageContext()
            .customName(this.lang.get(Lang.GUI_CLOSE))
            .lore(this.lang.get(Lang.GUI_CLOSE_LORE))
            .exitAndBuild();
        this.saveTradeButton = new HeadBuilder(HeadSkins.GREEN_CHECK_ON_BLACK).miniMessageContext()
            .customName(this.lang.get(Lang.GUI_TRADE_SAVE))
            .lore(this.lang.get(Lang.GUI_TRADE_SAVE_LORE))
            .exitAndBuild();
        this.nextPage = ItemBuilder.create(Material.ARROW).miniMessageContext()
            .customName(this.lang.get(Lang.GUI_PAGED_NEXT))
            .lore(this.lang.get(Lang.GUI_PAGED_NEXT_LORE))
            .exitAndBuild();
        this.previousPage = ItemBuilder.create(Material.FEATHER).miniMessageContext()
            .customName(this.lang.get(Lang.GUI_PAGED_LAST))
            .lore(this.lang.get(Lang.GUI_PAGED_LAST_LORE))
            .exitAndBuild();
    }

    public ItemStackElement<ChestPane> maxUsesElement(
        final int value,
        final ClickHandler<ChestPane, InventoryClickEvent, PlayerViewer, ClickContext<ChestPane, InventoryClickEvent, PlayerViewer>> clickHandler
    ) {
        final ItemStack stack = ItemBuilder.create(Material.LIGHT_BLUE_STAINED_GLASS_PANE).miniMessageContext()
            .customName(this.lang.get(Lang.GUI_TRADE_MAX_USES))
            .lore(
                this.lang.get(Lang.GUI_VALUE_LORE) + "<#0092FF>" + value,
                this.lang.get(Lang.GUI_EDIT_LORE)
            )
            .exitAndBuild();
        return ItemStackElement.of(stack, clickHandler);
    }

    public ItemStackElement<ChestPane> plus() {
        return ItemStackElement.of(this.plus);
    }

    public ItemStackElement<ChestPane> equals() {
        return ItemStackElement.of(this.equals);
    }

    public ItemStack saveTradeButton() {
        return this.saveTradeButton;
    }

    public String toggleLore() {
        return this.toggleLore;
    }

    public Transform<ChestPane, PlayerViewer> backButton(
        final ClickHandler<ChestPane, InventoryClickEvent, PlayerViewer, ClickContext<ChestPane, InventoryClickEvent, PlayerViewer>> clickHandler
    ) {
        return (pane, view) -> pane.element(
            ItemStackElement.of(this.backButton, clickHandler),
            8,
            pane.rows() - 1
        );
    }

    public Transform<ChestPane, PlayerViewer> closeButton() {
        return (pane, view) -> pane.element(
            ItemStackElement.of(
                this.closeButton,
                context -> context.viewer().player().closeInventory()
            ),
            8,
            pane.rows() - 1
        );
    }

    public Transform<ChestPane, PlayerViewer> fill() {
        return chestFill(ItemStackElement.of(this.filler));
    }

    public Transform<ChestPane, PlayerViewer> fillBottomRow() {
        return (pane, view) -> {
            for (int x = 0; x < 9; x++) {
                pane = pane.element(ItemStackElement.of(this.filler), x, pane.rows() - 1);
            }
            return pane;
        };
    }

    public PaginatedTransform<ItemStackElement<ChestPane>, ChestPane, PlayerViewer> paginationTransform(
        final int rows,
        final Supplier<List<ItemStackElement<ChestPane>>> list
    ) {
        final PaginatedTransform<ItemStackElement<ChestPane>, ChestPane, PlayerViewer> pagination = new PaginatedTransform<>(
            Vector2.at(0, 0),
            Vector2.at(8, rows - 2),
            list
        );
        pagination.backwardElement(
            Vector2.at(0, rows - 1),
            transform -> ItemStackElement.of(this.previousPage, context -> transform.previousPage())
        );
        pagination.forwardElement(
            Vector2.at(1, rows - 1),
            transform -> ItemStackElement.of(this.nextPage, context -> transform.nextPage())
        );
        return pagination;
    }

    public Transform<ChestPane, PlayerViewer> toggle(
        final String onName,
        final String offName,
        final BooleanSupplier getter,
        final BooleanConsumer setter,
        final int x,
        final int y
    ) {
        return (pane, view) -> pane.element(this.toggle(onName, offName, getter, setter), x, y);
    }

    public Transform<ChestPane, PlayerViewer> toggle(
        final String onName,
        final Material onMaterial,
        final String offName,
        final Material offMaterial,
        final BooleanSupplier getter,
        final BooleanConsumer setter,
        final int x,
        final int y
    ) {
        return (pane, view) -> pane.element(this.toggle(onName, onMaterial, offName, offMaterial, getter, setter), x, y);
    }

    public ItemStackElement<ChestPane> toggle(
        final String onName,
        final String offName,
        final BooleanSupplier getter,
        final BooleanConsumer setter
    ) {
        return this.toggle(onName, Material.LIME_STAINED_GLASS_PANE, offName, Material.RED_STAINED_GLASS_PANE, getter, setter);
    }

    public ItemStackElement<ChestPane> toggle(
        final String onName,
        final Material onMaterial,
        final String offName,
        final Material offMaterial,
        final BooleanSupplier getter,
        final BooleanConsumer setter
    ) {
        final Function<Boolean, ItemStack> item = on ->
            ItemBuilder.create(on ? onMaterial : offMaterial)
                .miniMessageContext()
                .customName(on ? onName : offName)
                .lore(this.toggleLore)
                .exitAndBuild();
        return toggle(item, getter, setter);
    }

    public static ItemStackElement<ChestPane> toggle(
        final Function<Boolean, ItemStack> stack,
        final BooleanSupplier getter,
        final BooleanConsumer setter
    ) {
        return ItemStackElement.of(
            stack.apply(getter.getAsBoolean()),
            context -> {
                setter.accept(!getter.getAsBoolean());
                context.view().update();
            }
        );
    }

    // Only use for static elements!
    public static Transform<ChestPane, PlayerViewer> chestItem(
        final ItemStackElement<ChestPane> element,
        final int x,
        final int y
    ) {
        return PaperTransform.chestItem(() -> element, x, y);
    }

    public static Transform<ChestPane, PlayerViewer> chestItem(
        final Supplier<ItemStackElement<ChestPane>> element,
        final int x,
        final int y
    ) {
        return PaperTransform.chestItem(element, x, y);
    }
}
