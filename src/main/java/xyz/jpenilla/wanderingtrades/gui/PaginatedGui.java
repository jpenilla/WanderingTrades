package xyz.jpenilla.wanderingtrades.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.jpenilla.jmplib.ItemBuilder;
import xyz.jpenilla.wanderingtrades.config.Lang;

import java.util.List;

public class PaginatedGui extends GuiHolder {
    private final ItemStack nextPage = new ItemBuilder(Material.ARROW).setName(lang.get(Lang.GUI_PAGED_NEXT)).setLore(lang.get(Lang.GUI_PAGED_NEXT_LORE)).build();
    private final ItemStack previousPage = new ItemBuilder(Material.FEATHER).setName(lang.get(Lang.GUI_PAGED_LAST)).setLore(lang.get(Lang.GUI_PAGED_LAST_LORE)).build();

    protected int page = 0;
    protected final List<ItemStack> items;

    public PaginatedGui(String name, int size, List<ItemStack> itemsToDisplay) {
        super(name, size);
        this.items = itemsToDisplay;
    }

    public Inventory getInventory() {
        inventory.clear();

        int maxPages = (int) Math.ceil(items.size() / (double) (inventory.getSize() - 9));
        page = Math.min(maxPages, page);

        if (page > 0) {
            inventory.setItem(inventory.getSize() - 9, previousPage);
        }
        if (page < maxPages - 1) {
            inventory.setItem(inventory.getSize() - 8, nextPage);
        }

        int startIndex = page * (inventory.getSize() - 9);
        for (int i = 0; i < inventory.getSize() - 9; i++) {
            if (i + startIndex >= items.size()) {
                break;
            }
            inventory.setItem(i, items.get(startIndex + i));
        }

        return inventory;
    }

    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        Player p = (Player) event.getWhoClicked();
        ClickType click = event.getClick();
        if (event.getSlot() != event.getRawSlot()) {
            if (click.isKeyboardClick() || click.isShiftClick()) {
                event.setCancelled(true);
            }
            return;
        }
        event.setCancelled(true);
        if (nextPage.isSimilar(item)) {
            page++;
        } else if (previousPage.isSimilar(item)) {
            page--;
        } else {
            onClick(p, item);
        }
        getInventory();
    }

    public void onClick(Player p, ItemStack i) {
    }

}
