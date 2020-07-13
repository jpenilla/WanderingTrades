package xyz.jpenilla.wanderingtrades.gui;

import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.jpenilla.jmplib.ItemBuilder;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Lang;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;

import java.util.ArrayList;
import java.util.stream.IntStream;

public class TradeCreateGui extends TradeGui {

    public TradeCreateGui(String tradeConfig) {
        super(WanderingTrades.getInstance().getLang().get(Lang.GUI_TRADE_CREATE_TITLE), tradeConfig);
    }

    public @NotNull Inventory getInventory() {
        inventory = super.getInventory();

        ArrayList<String> tradeNameLore = new ArrayList<>();
        tradeNameLore.add(lang.get(Lang.GUI_VALUE_LORE) + "<white>" + getTradeName());
        tradeNameLore.add(lang.get(Lang.GUI_EDIT_LORE));
        inventory.setItem(10, new ItemBuilder(getTradeNameStack()).setLore(tradeNameLore).build());

        IntStream.range(0, inventory.getSize()).forEach(slot -> {
            if (inventory.getItem(slot) == null) {
                inventory.setItem(slot, filler);
            }
        });

        return inventory;
    }

    public void onClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        Player p = (Player) event.getWhoClicked();

        TradeConfig t = WanderingTrades.getInstance().getCfg().getTradeConfigs().get(getTradeConfig());

        if (getTradeNameStack().isSimilar(item)) {
            p.closeInventory();
            new AnvilGUI.Builder()
                    .onClose(this::reOpen)
                    .onComplete((player, text) -> {
                        if (!text.contains(" ")) {
                            if (!t.getFile().getConfigurationSection("trades").contains(text)) {
                                setTradeName(text);
                                return AnvilGUI.Response.close();
                            } else {
                                return AnvilGUI.Response.text(lang.get(Lang.MESSAGE_CREATE_UNIQUE));
                            }
                        } else {
                            return AnvilGUI.Response.text(lang.get(Lang.MESSAGE_NO_SPACES));
                        }
                    })
                    .text(lang.get(Lang.GUI_ANVIL_TYPE_HERE))
                    .item(new ItemStack(Material.WRITABLE_BOOK))
                    .title(lang.get(Lang.MESSAGE_CREATE_CONFIG_PROMPT))
                    .plugin(WanderingTrades.getInstance())
                    .open(p);
        }
    }
}
