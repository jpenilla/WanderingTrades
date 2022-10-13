package xyz.jpenilla.wanderingtrades.gui;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import xyz.jpenilla.pluginbase.legacy.ItemBuilder;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Lang;

@DefaultQualifier(NonNull.class)
public abstract class PaginatedGui extends BaseGui {
    private final ItemStack nextPage = new ItemBuilder(Material.ARROW)
        .setName(this.lang.get(Lang.GUI_PAGED_NEXT))
        .setLore(this.lang.get(Lang.GUI_PAGED_NEXT_LORE))
        .build();
    private final ItemStack previousPage = new ItemBuilder(Material.FEATHER)
        .setName(this.lang.get(Lang.GUI_PAGED_LAST))
        .setLore(this.lang.get(Lang.GUI_PAGED_LAST_LORE))
        .build();

    private int page = 0;

    protected PaginatedGui(final WanderingTrades plugin, final String name, final int size) {
        super(plugin, name, size);
    }

    @Override
    public final Inventory getInventory() {
        this.inventory.clear();

        final List<ItemStack> items = this.getListItems();

        int maxPages = (int) Math.ceil(items.size() / (double) (this.inventory.getSize() - 9));
        this.page = Math.min(maxPages, this.page);

        if (this.page > 0) {
            this.inventory.setItem(this.inventory.getSize() - 9, this.previousPage);
        }
        if (this.page < maxPages - 1) {
            this.inventory.setItem(this.inventory.getSize() - 8, this.nextPage);
        }

        int startIndex = this.page * (this.inventory.getSize() - 9);
        for (int i = 0; i < this.inventory.getSize() - 9; i++) {
            if (i + startIndex >= items.size()) {
                break;
            }
            this.inventory.setItem(i, items.get(startIndex + i));
        }

        return this.getInventory(this.inventory);
    }

    protected abstract Inventory getInventory(Inventory inventory);

    protected abstract List<ItemStack> getListItems();

    @Override
    public final void onInventoryClick(InventoryClickEvent event) {
        final @Nullable ItemStack item = event.getCurrentItem();
        final Player player = (Player) event.getWhoClicked();
        if (this.nextPage.isSimilar(item)) {
            this.page++;
        } else if (this.previousPage.isSimilar(item)) {
            this.page--;
        } else {
            this.onClick(player, item);
        }
        this.getInventory();
    }

    protected abstract void onClick(Player player, @Nullable ItemStack stack);

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
