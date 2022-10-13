package xyz.jpenilla.wanderingtrades.gui;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import xyz.jpenilla.pluginbase.legacy.InputConversation;
import xyz.jpenilla.pluginbase.legacy.ItemBuilder;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Lang;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;

@DefaultQualifier(NonNull.class)
public final class TradeEditGui extends TradeGui {
    public TradeEditGui(
        final WanderingTrades plugin,
        final TradeConfig tradeConfig,
        final String tradeName
    ) {
        super(
            plugin,
            plugin.langConfig().get(Lang.GUI_TRADE_EDIT_TITLE) + tradeName,
            tradeConfig
        );
        final MerchantRecipe trade = tradeConfig.getTrade(tradeName);
        this.setTradeName(tradeName);
        this.setExperienceReward(trade.hasExperienceReward());
        this.setMaxUses(trade.getMaxUses());
        if (this.getMaxUses() == 0) {
            this.setMaxUses(1);
        }
        final List<ItemStack> ingredients = trade.getIngredients();
        this.setI1(ingredients.get(0));
        if (this.getI1() == null) {
            this.setI1(this.emptyIngredient1());
        }
        if (ingredients.size() == 2) {
            this.setI2(ingredients.get(1));
        }
        if (this.getI2() == null) {
            this.setI2(this.emptyIngredient2());
        }
        this.setResult(trade.getResult());
        if (this.getResult() == null) {
            this.setResult(this.emptyResult());
        }
    }

    @Override
    public Inventory getInventory() {
        super.getInventory();

        this.inventory.setItem(35, this.getDeleteButton());

        final List<String> tradeNameLore = new ArrayList<>();
        tradeNameLore.add(this.lang.get(Lang.GUI_VALUE_LORE) + "<white>" + this.getTradeName());
        this.inventory.setItem(10, new ItemBuilder(this.getTradeNameStack()).setLore(tradeNameLore).build());

        this.fillEmptySlots();

        return this.inventory;
    }

    @Override
    public void onClick(final InventoryClickEvent event) {
        final @Nullable ItemStack item = event.getCurrentItem();
        final Player player = (Player) event.getWhoClicked();

        if (this.getDeleteButton().isSimilar(item)) {
            this.deleteClick(player);
        }

        this.getInventory();
    }

    private void deleteClick(final Player p) {
        p.closeInventory();
        new InputConversation()
            .onPromptText((player -> {
                this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_DELETE_PROMPT).replace("{TRADE_NAME}", this.getTradeName()));
                this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_CONFIRM).replace("{KEY}", this.lang.get(Lang.MESSAGE_CONFIRM_KEY)));
                return "";
            }))
            .onValidateInput(((player, s) -> {
                if (s.equals(this.lang.get(Lang.MESSAGE_CONFIRM_KEY))) {
                    this.tradeConfig.deleteTrade(this.getTradeName());
                    this.plugin.config().load();
                    this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_EDIT_SAVED));
                    new TradeListGui(this.plugin, this.tradeConfig).open(player);
                } else {
                    this.editCancelled(player, s);
                }
                return true;
            }))
            .start(p);
    }
}
