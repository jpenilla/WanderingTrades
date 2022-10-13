package xyz.jpenilla.wanderingtrades.gui;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import xyz.jpenilla.pluginbase.legacy.HeadBuilder;
import xyz.jpenilla.pluginbase.legacy.ItemBuilder;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Lang;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;

@DefaultQualifier(NonNull.class)
public final class TradeListGui extends PaginatedGui {
    private final ItemStack editButton = new ItemBuilder(Material.CHEST)
        .setName(this.lang.get(Lang.GUI_TRADE_LIST_EDIT_CONFIG))
        .setLore(this.lang.get(Lang.GUI_TRADE_LIST_EDIT_CONFIG_LORE))
        .build();
    private final ItemStack newTradeStack = new HeadBuilder("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA1NmJjMTI0NGZjZmY5OTM0NGYxMmFiYTQyYWMyM2ZlZTZlZjZlMzM1MWQyN2QyNzNjMTU3MjUzMWYifX19")
        .setName(this.lang.get(Lang.GUI_TRADE_LIST_NEW_TRADE))
        .setLore(this.lang.get(Lang.GUI_TRADE_LIST_NEW_TRADE_LORE))
        .build();
    private final TradeConfig tradeConfig;

    public TradeListGui(final WanderingTrades plugin, final TradeConfig tradeConfig) {
        super(plugin, plugin.langConfig().get(Lang.GUI_TRADE_LIST_TITLE) + tradeConfig.configName(), 54);
        this.tradeConfig = tradeConfig;
    }

    @Override
    public Inventory getInventory(final Inventory inventory) {
        inventory.setItem(this.inventory.getSize() - 1, this.backButton);
        inventory.setItem(this.inventory.getSize() - 2, this.editButton);
        inventory.setItem(this.inventory.getSize() - 5, this.newTradeStack);
        IntStream.range(inventory.getSize() - 9, inventory.getSize() - 1).forEach(s -> {
            if (this.inventory.getItem(s) == null) {
                this.inventory.setItem(s, this.filler);
            }
        });
        return inventory;
    }

    @Override
    public void onClick(final Player player, final @Nullable ItemStack stack) {
        if (this.backButton.isSimilar(stack)) {
            player.closeInventory();
            new TradeConfigListGui(this.plugin).open(player);
        } else if (this.editButton.isSimilar(stack)) {
            player.closeInventory();
            new TradeConfigEditGui(this.plugin, this.tradeConfig).open(player);
        } else if (this.newTradeStack.isSimilar(stack)) {
            player.closeInventory();
            new TradeCreateGui(this.plugin, this.tradeConfig).open(player);
        } else if (this.getListItems().contains(stack)) {
            player.closeInventory();
            new TradeEditGui(this.plugin, this.tradeConfig, stack.getItemMeta().getDisplayName()).open(player);
        }
    }

    @Override
    public List<ItemStack> getListItems() {
        return this.tradeConfig.tradesByName().entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(entry -> {
                final ItemBuilder builder = new ItemBuilder(entry.getValue().getResult());
                builder.setName(entry.getKey());
                builder.clearEnchants();
                builder.clearLore();
                return builder.build();
            })
            .toList();
    }

    @Override
    public void reOpen(final Player player) {
        new TradeListGui(this.plugin, this.tradeConfig).open(player);
    }
}
