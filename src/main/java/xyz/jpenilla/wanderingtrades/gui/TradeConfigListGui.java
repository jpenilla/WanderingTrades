package xyz.jpenilla.wanderingtrades.gui;

import org.apache.commons.io.FileUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.jpenilla.jmplib.InputConversation;
import xyz.jpenilla.jmplib.ItemBuilder;
import xyz.jpenilla.jmplib.TextUtil;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Lang;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class TradeConfigListGui extends PaginatedGui {
    private final ItemStack newConfig = new ItemBuilder(Material.WRITABLE_BOOK).setName(lang.get(Lang.GUI_TC_LIST_ADD_CONFIG)).setLore(lang.get(Lang.GUI_TC_LIST_ADD_CONFIG_LORE)).build();
    private final ArrayList<String> configNames = new ArrayList<>();

    public TradeConfigListGui() {
        super(WanderingTrades.getInstance().getLang().get(Lang.GUI_TC_LIST_TITLE), 36);
        Arrays.stream(WanderingTrades.getInstance().getCfg().getTradeConfigs().keySet().toArray()).forEach(completion -> configNames.add((String) completion));
    }

    public List<ItemStack> getListItems() {
        ArrayList<ItemStack> items = new ArrayList<>();
        ArrayList<String> configs = new ArrayList<>();
        Arrays.stream(WanderingTrades.getInstance().getCfg().getTradeConfigs().keySet().toArray()).forEach(completion -> configs.add((String) completion));
        for (String config : configs) {
            TradeConfig t = WanderingTrades.getInstance().getCfg().getTradeConfigs().get(config);
            ArrayList<String> lore = new ArrayList<>();
            t.getFile().getConfigurationSection("trades").getKeys(false).forEach(key -> lore.add("<gray><italic>  " + key));
            String[] lores = new String[lore.size()];
            for (int j = 0; j < lore.size(); j++) {
                lores[j] = lore.get(j);
            }
            ArrayList<String> finalLores = new ArrayList<>();
            for (int x = 0; x < 10; ++x) {
                try {
                    finalLores.add("<gray>" + lores[x]);
                } catch (ArrayIndexOutOfBoundsException ignored) {
                }
            }
            if (lores.length > 10) {
                finalLores.add(WanderingTrades.getInstance().getLang().get(Lang.GUI_TC_LIST_AND_MORE).replace("{VALUE}", String.valueOf(lores.length - 10)));
            }
            items.add(new ItemBuilder(Material.PAPER).setName(config).setLore(finalLores).build());
        }
        return items;
    }

    public Inventory getInventory() {
        Inventory i = super.getInventory();
        i.setItem(i.getSize() - 5, newConfig);
        i.setItem(inventory.getSize() - 1, closeButton);
        IntStream.range(i.getSize() - 9, i.getSize() - 1).forEach(s -> {
            if (inventory.getItem(s) == null) {
                inventory.setItem(s, filler);
            }
        });
        return i;
    }

    public void onClick(Player p, ItemStack i) {
        if (closeButton.isSimilar(i)) {
            p.closeInventory();
            return;
        }
        if (newConfig.isSimilar(i)) {
            p.closeInventory();
            new InputConversation(WanderingTrades.getInstance().getConversationFactory())
                    .onPromptText(player -> {
                        WanderingTrades.getInstance().getChat().sendPlaceholders(player, lang.get(Lang.MESSAGE_CREATE_CONFIG_PROMPT));
                        return "";
                    })
                    .onValidateInput((player, input) -> {
                        if (input.contains(" ")) {
                            WanderingTrades.getInstance().getChat().sendPlaceholders(player, lang.get(Lang.MESSAGE_NO_SPACES));
                            return false;
                        }
                        if (TextUtil.containsCaseInsensitive(input, configNames)) {
                            WanderingTrades.getInstance().getChat().sendPlaceholders(player, lang.get(Lang.MESSAGE_CREATE_UNIQUE));
                            return false;
                        }
                        return true;
                    })
                    .onConfirmText(this::onConfirmYesNo)
                    .onAccepted((player, s) -> {
                        try {
                            FileUtils.copyToFile(WanderingTrades.getInstance().getResource("trades/blank.yml"), new File(WanderingTrades.getInstance().getDataFolder() + "/trades/" + s + ".yml"));
                            WanderingTrades.getInstance().getCfg().load();
                            WanderingTrades.getInstance().getChat().sendPlaceholders(player, lang.get(Lang.MESSAGE_CREATE_CONFIG_SUCCESS));
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            WanderingTrades.getInstance().getChat().sendPlaceholders(player, "<red>Error");
                        }
                        reOpen(p);
                    })
                    .onDenied((player, s) -> {
                        WanderingTrades.getInstance().getChat().sendPlaceholders(player, lang.get(Lang.MESSAGE_CREATE_CONFIG_CANCEL));
                        reOpen(player);
                    })
                    .start(p);
            return;
        }
        if (i != null) {
            //try {
            if (TextUtil.containsCaseInsensitive(i.getItemMeta().getDisplayName(), configNames)) {
                p.closeInventory();
                new TradeListGui(i.getItemMeta().getDisplayName()).open(p);
            }
        } //catch (NullPointerException ignored) {}
    }

    @Override
    public void reOpen(Player p) {
        new TradeConfigListGui().open(p);
    }
}
