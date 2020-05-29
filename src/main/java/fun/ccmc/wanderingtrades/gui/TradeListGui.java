package fun.ccmc.wanderingtrades.gui;

import fun.ccmc.wanderingtrades.WanderingTrades;
import fun.ccmc.wanderingtrades.config.TradeConfig;
import fun.ccmc.wanderingtrades.util.Gui;
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
    private final ItemStack editButton = Gui.buildLore(Material.CHEST, "&aEdit Config Settings", "&7&o  Click to edit settings for this config");
    private final ItemStack plus = Gui.buildHeadLore("New Trade", "  &7Click to create new trade", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA1NmJjMTI0NGZjZmY5OTM0NGYxMmFiYTQyYWMyM2ZlZTZlZjZlMzM1MWQyN2QyNzNjMTU3MjUzMWYifX19");
    private final String tradeConfig;

    public TradeListGui(String tradeConfig) {
        super("&e&lConfig&7: &f" + tradeConfig, 54, getTradeStacks(tradeConfig));
        this.tradeConfig = tradeConfig;
        Arrays.stream(WanderingTrades.getInstance().getCfg().getTradeConfigs().keySet().toArray()).forEach(completion -> configNames.add((String) completion));
    }

    public Inventory getInventory() {
        Inventory i = super.getInventory();
        i.setItem(inventory.getSize() - 1, backButton);
        i.setItem(inventory.getSize() - 2, editButton);
        i.setItem(inventory.getSize() - 5, plus);
        IntStream.range(i.getSize() - 9, i.getSize() - 1).forEach(s -> {
            if (inventory.getItem(s) == null) {
                inventory.setItem(s, Gui.build(Material.GRAY_STAINED_GLASS_PANE));
            }
        });
        return i;
    }

    public void onClick(Player p, ItemStack i) {
        if (backButton.isSimilar(i)) {
            p.closeInventory();
            new ConfigListGui().open(p);
        } else if (editButton.isSimilar(i)) {
            p.closeInventory();
            new EditConfigGui(tradeConfig).open(p);
        }
    }

    private static ArrayList<ItemStack> getTradeStacks(String configName) {
        ArrayList<ItemStack> trades = new ArrayList<>();
        TradeConfig tc = WanderingTrades.getInstance().getCfg().getTradeConfigs().get(configName);
        tc.getFile().getConfigurationSection("trades").getKeys(false).forEach(key -> {
            ItemStack s = TradeConfig.getStack(tc.getFile(), "trades." + key + ".result");
            ItemMeta m = s.getItemMeta();
            m.setDisplayName(key);
            s.setItemMeta(m);
            trades.add(s);
        });
        return trades;
    }
}
