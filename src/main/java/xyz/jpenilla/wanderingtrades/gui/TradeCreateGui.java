package xyz.jpenilla.wanderingtrades.gui;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import xyz.jpenilla.pluginbase.legacy.InputConversation;
import xyz.jpenilla.pluginbase.legacy.ItemBuilder;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Lang;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;

@DefaultQualifier(NonNull.class)
public final class TradeCreateGui extends TradeGui {
    public TradeCreateGui(final WanderingTrades plugin, final TradeConfig tradeConfig) {
        super(
            plugin,
            plugin.langConfig().get(Lang.GUI_TRADE_CREATE_TITLE),
            tradeConfig
        );
    }

    @Override
    public Inventory getInventory() {
        super.getInventory();

        final List<String> tradeNameLore = new ArrayList<>();
        tradeNameLore.add(this.tradeNameValueLore());
        tradeNameLore.add(this.lang.get(Lang.GUI_EDIT_LORE));
        this.inventory.setItem(10, new ItemBuilder(this.getTradeNameStack()).setLore(tradeNameLore).build());

        this.fillEmptySlots();

        return this.inventory;
    }

    private String tradeNameValueLore() {
        final @Nullable String tradeName = this.getTradeName();
        final String displayName = tradeName == null ? "<gray>________" : "<white>" + tradeName;
        return this.lang.get(Lang.GUI_VALUE_LORE) + displayName;
    }

    @Override
    public void onClick(final InventoryClickEvent event) {
        final @Nullable ItemStack item = event.getCurrentItem();
        final Player player = (Player) event.getWhoClicked();

        if (this.getTradeNameStack().isSimilar(item)) {
            player.closeInventory();
            this.tradeNameClick(player);
        }
    }

    private void tradeNameClick(final Player p) {
        new InputConversation()
            .onPromptText(player -> {
                this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_CREATE_TRADE_PROMPT));
                return "";
            })
            .onValidateInput((player, input) -> {
                if (input.contains(" ")) {
                    this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_NO_SPACES));
                    return false;
                }
                if (this.tradeConfig.tradesByName().containsKey(input)) {
                    this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_CREATE_UNIQUE));
                    return false;
                }
                return true;
            })
            .onConfirmText(this::confirmYesNo)
            .onAccepted((player, s) -> {
                this.setTradeName(s);
                this.open(player);
            })
            .onDenied(this::editCancelled)
            .start(p);
    }
}
