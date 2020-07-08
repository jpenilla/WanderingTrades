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

public class TradeEditGui extends TradeGui {

    public TradeEditGui(String tradeConfig, String tradeName) {
        super(WanderingTrades.getInstance().getLang().get(Lang.GUI_TRADE_EDIT_TITLE) + tradeName, tradeConfig);
        this.tradeName = tradeName;
        TradeConfig t = WanderingTrades.getInstance().getCfg().getTradeConfigs().get(tradeConfig);
        experienceReward = t.getFile().getBoolean("trades." + tradeName + ".experienceReward");
        maxUses = t.getFile().getInt("trades." + tradeName + ".maxUses");
        if (maxUses == 0) {
            maxUses = 1;
        }
        i1 = TradeConfig.getStack(t.getFile(), "trades." + tradeName + ".ingredients.1");
        if (i1 == null) {
            i1 = ingredient1;
        }
        i2 = TradeConfig.getStack(t.getFile(), "trades." + tradeName + ".ingredients.2");
        if (i2 == null) {
            i2 = ingredient2;
        }
        result = TradeConfig.getStack(t.getFile(), "trades." + tradeName + ".result");
        if (result == null) {
            result = resultStack;
        }
    }

    public @NotNull Inventory getInventory() {
        inventory = super.getInventory();

        inventory.setItem(35, deleteButton);

        ArrayList<String> tradeNameLore = new ArrayList<>();
        tradeNameLore.add(lang.get(Lang.GUI_VALUE_LORE) + "<white>" + tradeName);
        inventory.setItem(10, new ItemBuilder(tradeNameStack).setLore(tradeNameLore).build());

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

        TradeConfig t = WanderingTrades.getInstance().getCfg().getTradeConfigs().get(tradeConfig);

        if (deleteButton.isSimilar(item)) {
            p.closeInventory();
            new AnvilGUI.Builder()
                    .onClose(player -> new TradeListGui(tradeConfig).open(player))
                    .onComplete((player, text) -> {
                        if (text.equals(lang.get(Lang.GUI_ANVIL_CONFIRM_KEY))) {
                            t.deleteTrade(tradeConfig, tradeName);
                            WanderingTrades.getInstance().getCfg().load();
                            return AnvilGUI.Response.close();
                        } else {
                            return AnvilGUI.Response.text(lang.get(Lang.GUI_ANVIL_CONFIRM)
                                    .replace("{KEY}", lang.get(Lang.GUI_ANVIL_CONFIRM_KEY)));
                        }
                    })
                    .text(lang.get(Lang.GUI_ANVIL_CONFIRM)
                            .replace("{KEY}", lang.get(Lang.GUI_ANVIL_CONFIRM_KEY)))
                    .item(new ItemStack(Material.WRITABLE_BOOK))
                    .title(lang.get(Lang.GUI_ANVIL_DELETE_TITLE) + tradeName)
                    .plugin(WanderingTrades.getInstance())
                    .open(p);
        }

        getInventory();
    }
}
