package xyz.jpenilla.wanderingtrades.util;

import io.papermc.lib.PaperLib;
import java.lang.reflect.Method;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import xyz.jpenilla.pluginbase.legacy.MiniMessageUtil;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

@DefaultQualifier(NonNull.class)
public final class Inventories {
    private static @MonotonicNonNull Method PAPER_CREATE_INVENTORY;

    private Inventories() {
    }

    public static Inventory createInventory(final InventoryHolder inventoryHolder, final int size, final String miniMessageName) {
        if (PaperLib.isPaper()) {
            return paperCreateInventory(inventoryHolder, size, miniMessageName);
        }
        return legacyCreateInventory(inventoryHolder, size, miniMessageName);
    }

    @SuppressWarnings("deprecation")
    private static Inventory legacyCreateInventory(final InventoryHolder inventoryHolder, final int size, final String miniMessageName) {
        return Bukkit.getServer().createInventory(
            inventoryHolder,
            size,
            MiniMessageUtil.miniMessageToLegacy(miniMessageName)
        );
    }

    private static Inventory paperCreateInventory(final InventoryHolder inventoryHolder, final int size, final String miniMessageName) {
        try {
            return (Inventory) paperCreateInventoryMethod().invoke(
                Bukkit.getServer(),
                inventoryHolder,
                size,
                Components.toNative(
                    WanderingTrades.instance().miniMessage().deserialize(miniMessageName)
                )
            );
        } catch (final ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static Method paperCreateInventoryMethod() {
        if (PAPER_CREATE_INVENTORY == null) {
            try {
                PAPER_CREATE_INVENTORY = Server.class.getMethod(
                    "createInventory",
                    InventoryHolder.class,
                    int.class,
                    Components.nativeAdventureComponentClass()
                );
            } catch (final ReflectiveOperationException ex) {
                throw new RuntimeException(ex);
            }
        }
        return PAPER_CREATE_INVENTORY;
    }
}
