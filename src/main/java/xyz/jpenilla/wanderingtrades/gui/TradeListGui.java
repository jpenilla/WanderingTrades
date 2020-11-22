package xyz.jpenilla.wanderingtrades.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.jpenilla.jmplib.HeadBuilder;
import xyz.jpenilla.jmplib.ItemBuilder;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Lang;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class TradeListGui extends PaginatedGui {
    private final ItemStack editButton = new ItemBuilder(Material.CHEST).setName(lang.get(Lang.GUI_TRADE_LIST_EDIT_CONFIG)).setLore(lang.get(Lang.GUI_TRADE_LIST_EDIT_CONFIG_LORE)).build();
    private final ItemStack newTradeStack = new HeadBuilder("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA1NmJjMTI0NGZjZmY5OTM0NGYxMmFiYTQyYWMyM2ZlZTZlZjZlMzM1MWQyN2QyNzNjMTU3MjUzMWYifX19")
            .setName(lang.get(Lang.GUI_TRADE_LIST_NEW_TRADE)).setLore(lang.get(Lang.GUI_TRADE_LIST_NEW_TRADE_LORE)).build();
    private final TradeConfig tradeConfig;

    public TradeListGui(TradeConfig tradeConfig) {
        super(WanderingTrades.getInstance().getLang().get(Lang.GUI_TRADE_LIST_TITLE) + tradeConfig.getConfigName(), 54);
        this.tradeConfig = tradeConfig;
    }

    public Inventory getInv(Inventory i) {
        i.setItem(inventory.getSize() - 1, backButton);
        i.setItem(inventory.getSize() - 2, editButton);
        i.setItem(inventory.getSize() - 5, newTradeStack);
        IntStream.range(i.getSize() - 9, i.getSize() - 1).forEach(s -> {
            if (inventory.getItem(s) == null) {
                inventory.setItem(s, filler);
            }
        });
        return i;
    }

    public void onClick(Player p, ItemStack i) {
        if (backButton.isSimilar(i)) {
            p.closeInventory();
            new TradeConfigListGui().open(p);
        } else if (editButton.isSimilar(i)) {
            p.closeInventory();
            new TradeConfigEditGui(tradeConfig).open(p);
        } else if (newTradeStack.isSimilar(i)) {
            p.closeInventory();
            new TradeCreateGui(tradeConfig).open(p);
        } else if (getListItems().contains(i)) {
            p.closeInventory();
            new TradeEditGui(tradeConfig, i.getItemMeta().getDisplayName()).open(p);
        }
    }

    public List<ItemStack> getListItems() {
        List<ItemStack> trades = new ArrayList<>();
        tradeConfig.getTradeSection().getKeys(false).stream().sorted().forEach(key -> {
            ItemStack itemStack = TradeConfig.getStack(tradeConfig.getFile(), "trades." + key + ".result");
            if (itemStack != null) {
                final ItemBuilder builder = new ItemBuilder(itemStack);
                builder.setName(key);
                builder.clearEnchants();
                builder.clearLore();
                trades.add(builder.build());
            }
        });
        return trades;
    }

    @Override
    public void reOpen(Player p) {
        new TradeListGui(tradeConfig).open(p);
    }
}
