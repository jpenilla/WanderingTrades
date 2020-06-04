package fun.ccmc.wanderingtrades.gui;

import fun.ccmc.jmplib.ItemBuilder;
import fun.ccmc.wanderingtrades.WanderingTrades;
import fun.ccmc.wanderingtrades.config.Lang;
import fun.ccmc.wanderingtrades.config.TradeConfig;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;

public class TradeListGui extends PaginatedGui {
    private final ArrayList<String> configNames = new ArrayList<>();
    private final ItemStack editButton = new ItemBuilder(Material.CHEST).setName(lang.get(Lang.GUI_TRADE_LIST_EDIT_CONFIG)).setLore(lang.get(Lang.GUI_TRADE_LIST_EDIT_CONFIG_LORE)).build();
    private final ItemStack newTradeStack = new ItemBuilder("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA1NmJjMTI0NGZjZmY5OTM0NGYxMmFiYTQyYWMyM2ZlZTZlZjZlMzM1MWQyN2QyNzNjMTU3MjUzMWYifX19")
            .setName(lang.get(Lang.GUI_TRADE_LIST_NEW_TRADE)).setLore(lang.get(Lang.GUI_TRADE_LIST_NEW_TRADE_LORE)).build();
    private final String tradeConfig;

    public TradeListGui(String tradeConfig) {
        super(WanderingTrades.getInstance().getLang().get(Lang.GUI_TRADE_LIST_TITLE) + tradeConfig, 54, getTradeStacks(tradeConfig));
        this.tradeConfig = tradeConfig;
        Arrays.stream(WanderingTrades.getInstance().getCfg().getTradeConfigs().keySet().toArray()).forEach(completion -> configNames.add((String) completion));
    }

    public Inventory getInventory() {
        Inventory i = super.getInventory();
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
        } else if (getTradeStacks(tradeConfig).contains(i)) {
            p.closeInventory();
            new TradeEditGui(tradeConfig, i.getItemMeta().getDisplayName()).open(p);
        }
    }

    private static ArrayList<ItemStack> getTradeStacks(String configName) {
        ArrayList<ItemStack> trades = new ArrayList<>();
        TradeConfig tc = WanderingTrades.getInstance().getCfg().getTradeConfigs().get(configName);
        tc.getFile().getConfigurationSection("trades").getKeys(false).forEach(key -> {
            ItemStack s = TradeConfig.getStack(tc.getFile(), "trades." + key + ".result");
            ItemMeta m = s.getItemMeta();
            m.setDisplayName(key);
            m.getEnchants().keySet().forEach(m::removeEnchant);
            s.setItemMeta(m);
            trades.add(s);
        });
        return trades;
    }
}
