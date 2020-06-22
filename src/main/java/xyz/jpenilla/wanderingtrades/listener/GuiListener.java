package xyz.jpenilla.wanderingtrades.listener;

import xyz.jpenilla.wanderingtrades.gui.GuiHolder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class GuiListener implements Listener {

    @EventHandler
    private void onTest() {

    }

    @EventHandler
    private void onInventoryClickEvent(InventoryClickEvent event) {
        if (event.getRawSlot() < 0) {
            return;
        }
        Inventory inventory = event.getView().getTopInventory();
        InventoryHolder holder = inventory.getHolder();
        if (holder instanceof GuiHolder) {
            ((GuiHolder) holder).onInventoryClick(event);
        }

    }

    @EventHandler
    private void onInventoryDragEvent(InventoryDragEvent event) {
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder();
        if (holder instanceof GuiHolder) {
            for (int slot : event.getRawSlots()) {
                if (event.getInventorySlots().contains(slot)) {
                    event.setCancelled(true);
                }
            }
            ((GuiHolder) holder).onInventoryDrag(event);
        }
    }

    @EventHandler
    private void onInventoryOpenEvent(InventoryOpenEvent event) {
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder();
        if (holder instanceof GuiHolder) {
            ((GuiHolder) holder).onInventoryOpen(event);
        }
    }

    @EventHandler
    private void onInventoryCloseEvent(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder();
        if (holder instanceof GuiHolder) {
            ((GuiHolder) holder).onInventoryClose(event);
        }
    }
}