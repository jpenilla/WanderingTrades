package xyz.jpenilla.wanderingtrades.gui;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;
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
import xyz.jpenilla.pluginbase.legacy.HeadBuilder;
import xyz.jpenilla.pluginbase.legacy.ItemBuilder;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Lang;
import xyz.jpenilla.wanderingtrades.config.LangConfig;
import xyz.jpenilla.wanderingtrades.util.BooleanConsumer;

import static org.incendo.interfaces.paper.transform.PaperTransform.chestFill;

@DefaultQualifier(NonNull.class)
public final class PartsFactory {
    private final LangConfig lang;
    private final ItemStack plus = new HeadBuilder(HeadSkins.LIGHT_BLUE_PLUS)
        .setName("<yellow>+")
        .build();
    private final ItemStack equals = new HeadBuilder(HeadSkins.LIGHT_BLUE_EQUALS)
        .setName("<yellow>=")
        .build();
    private final ItemStack filler = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").build();
    private final ItemStack saveTradeButton;
    private final ItemStack backButton;
    private final ItemStack closeButton;
    private final ItemStack nextPage;
    private final ItemStack previousPage;
    private final String toggleLore;

    PartsFactory(final WanderingTrades plugin) {
        this.lang = plugin.langConfig();
        this.toggleLore = this.lang.get(Lang.GUI_TOGGLE_LORE);
        this.backButton = new HeadBuilder(HeadSkins.REDSTONE_BACKWARDS_ARROW)
            .setName(this.lang.get(Lang.GUI_BACK))
            .setLore(this.lang.get(Lang.GUI_BACK_LORE))
            .build();
        this.closeButton = new ItemBuilder(Material.BARRIER)
            .setName(this.lang.get(Lang.GUI_CLOSE))
            .setLore(this.lang.get(Lang.GUI_CLOSE_LORE))
            .build();
        this.saveTradeButton = new HeadBuilder(HeadSkins.GREEN_CHECK_ON_BLACK)
            .setName(this.lang.get(Lang.GUI_TRADE_SAVE))
            .setLore(this.lang.get(Lang.GUI_TRADE_SAVE_LORE))
            .build();
        this.nextPage = new ItemBuilder(Material.ARROW)
            .setName(this.lang.get(Lang.GUI_PAGED_NEXT))
            .setLore(this.lang.get(Lang.GUI_PAGED_NEXT_LORE))
            .build();
        this.previousPage = new ItemBuilder(Material.FEATHER)
            .setName(this.lang.get(Lang.GUI_PAGED_LAST))
            .setLore(this.lang.get(Lang.GUI_PAGED_LAST_LORE))
            .build();
    }

    public ItemStackElement<ChestPane> maxUsesElement(
        final int value,
        final ClickHandler<ChestPane, InventoryClickEvent, PlayerViewer, ClickContext<ChestPane, InventoryClickEvent, PlayerViewer>> clickHandler
    ) {
        return ItemStackElement.of(
            new ItemBuilder(Material.LIGHT_BLUE_STAINED_GLASS_PANE)
                .setName(this.lang.get(Lang.GUI_TRADE_MAX_USES))
                .setLore(
                    this.lang.get(Lang.GUI_VALUE_LORE) + "<#0092FF>" + value,
                    this.lang.get(Lang.GUI_EDIT_LORE)
                )
                .build(),
            clickHandler
        );
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
        return toggle(
            on -> new ItemBuilder(on ? onMaterial : offMaterial)
                .setName(on ? onName : offName)
                .setLore(this.toggleLore)
                .build(),
            getter,
            setter
        );
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
