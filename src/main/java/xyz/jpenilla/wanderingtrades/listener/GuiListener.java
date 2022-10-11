package xyz.jpenilla.wanderingtrades.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import xyz.jpenilla.wanderingtrades.gui.BaseGui;

@DefaultQualifier(NonNull.class)
public final class GuiListener implements Listener {
    @EventHandler
    private void onInventoryClickEvent(final InventoryClickEvent event) {
        if (event.getRawSlot() < 0) {
            return;
        }
        if (event.getView().getTopInventory().getHolder() instanceof BaseGui gui) {
            gui.handleClick(event);
        }
    }

    @EventHandler
    private void onInventoryDragEvent(final InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof BaseGui holder) {
            for (int slot : event.getRawSlots()) {
                if (event.getInventorySlots().contains(slot)) {
                    event.setCancelled(true);
                }
            }
            holder.onInventoryDrag(event);
        }
    }

    @EventHandler
    private void onInventoryOpenEvent(final InventoryOpenEvent event) {
        if (event.getInventory().getHolder() instanceof BaseGui holder) {
            holder.onInventoryOpen(event);
        }
    }

    @EventHandler
    private void onInventoryCloseEvent(final InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof BaseGui holder) {
            holder.onInventoryClose(event);
        }
    }
}
