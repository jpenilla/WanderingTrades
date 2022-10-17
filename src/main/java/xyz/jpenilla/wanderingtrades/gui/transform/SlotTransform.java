package xyz.jpenilla.wanderingtrades.gui.transform;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.incendo.interfaces.core.transform.InterfaceProperty;
import org.incendo.interfaces.core.transform.ReactiveTransform;
import org.incendo.interfaces.core.view.InterfaceView;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.element.ItemStackElement;
import org.incendo.interfaces.paper.pane.ChestPane;

@DefaultQualifier(NonNull.class)
public final class SlotTransform implements ReactiveTransform<ChestPane, PlayerViewer, InterfaceProperty<?>> {
    private final ItemStack emptyItem;
    private final int x;
    private final int y;
    private final InterfaceProperty<@Nullable ItemStack> item;

    public SlotTransform(
        final ItemStack emptyItem,
        final int x,
        final int y
    ) {
        this.emptyItem = emptyItem;
        this.x = x;
        this.y = y;
        this.item = InterfaceProperty.of(null);
    }

    public void item(final @Nullable ItemStack stack) {
        this.item.set(stack);
    }

    public @Nullable ItemStack item() {
        return this.item.get();
    }

    @Override
    public InterfaceProperty<?>[] properties() {
        return new InterfaceProperty<?>[]{this.item};
    }

    @Override
    public ChestPane apply(final ChestPane pane, final InterfaceView<ChestPane, PlayerViewer> view) {
        final @Nullable ItemStack item = this.item.get();
        final ItemStackElement<ChestPane> element = item == null
            ? this.emptyElement()
            : this.presentElement(item);
        return pane.element(element, this.x, this.y);
    }

    private ItemStackElement<ChestPane> emptyElement() {
        return ItemStackElement.of(this.emptyItem, context -> {
            final @Nullable ItemStack cursor = context.cause().getCursor();
            if (cursor != null && cursor.getType() != Material.AIR) {
                this.item.set(cursor);
                context.viewer().player().setItemOnCursor(null);
            }
        });
    }

    private ItemStackElement<ChestPane> presentElement(final ItemStack item) {
        return ItemStackElement.of(item, context -> {
            final @Nullable ItemStack cursor = context.cause().getCursor();
            this.item.set(cursor == null || cursor.getType() == Material.AIR ? null : cursor);
            context.viewer().player().setItemOnCursor(item);
        });
    }
}
