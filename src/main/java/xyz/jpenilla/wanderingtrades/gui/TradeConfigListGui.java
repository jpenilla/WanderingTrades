package xyz.jpenilla.wanderingtrades.gui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.jpenilla.pluginbase.legacy.InputConversation;
import xyz.jpenilla.pluginbase.legacy.ItemBuilder;
import xyz.jpenilla.pluginbase.legacy.TextUtil;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Lang;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;

public class TradeConfigListGui extends PaginatedGui {
    private final ItemStack newConfig = new ItemBuilder(Material.WRITABLE_BOOK).setName(lang.get(Lang.GUI_TC_LIST_ADD_CONFIG)).setLore(lang.get(Lang.GUI_TC_LIST_ADD_CONFIG_LORE)).build();
    private final List<String> configNames = new ArrayList<>();

    public TradeConfigListGui() {
        super(WanderingTrades.instance().langConfig().get(Lang.GUI_TC_LIST_TITLE), 36);
        Arrays.stream(WanderingTrades.instance().config().tradeConfigs().keySet().toArray()).forEach(completion -> configNames.add((String) completion));
    }

    public List<ItemStack> getListItems() {
        return Arrays.stream(WanderingTrades.instance().config().tradeConfigs().keySet().toArray(new String[0]))
            .sorted()
            .map(configName -> WanderingTrades.instance().config().tradeConfigs().get(configName))
            .map(tradeConfig -> {
                final Set<String> tradeKeys = Objects.requireNonNull(tradeConfig.fileConfiguration().getConfigurationSection("trades")).getKeys(false);
                final List<String> finalLores = new ArrayList<>();
                tradeKeys.stream()
                    .sorted()
                    .limit(10)
                    .forEach(key -> finalLores.add("<gray>  " + key));
                if (finalLores.size() == 10) {
                    finalLores.add(WanderingTrades.instance().langConfig().get(Lang.GUI_TC_LIST_AND_MORE).replace("{VALUE}", String.valueOf(tradeKeys.size() - 10)));
                }
                return new ItemBuilder(Material.PAPER).setName(tradeConfig.configName()).setLore(finalLores).build();
            })
            .collect(Collectors.toList());
    }

    public Inventory getInv(Inventory i) {
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
            new InputConversation()
                .onPromptText(player -> {
                    WanderingTrades.instance().chat().sendParsed(player, lang.get(Lang.MESSAGE_CREATE_CONFIG_PROMPT));
                    return "";
                })
                .onValidateInput((player, input) -> {
                    if (input.contains(" ")) {
                        WanderingTrades.instance().chat().sendParsed(player, lang.get(Lang.MESSAGE_NO_SPACES));
                        return false;
                    }
                    if (TextUtil.containsCaseInsensitive(input, configNames)) {
                        WanderingTrades.instance().chat().sendParsed(player, lang.get(Lang.MESSAGE_CREATE_UNIQUE));
                        return false;
                    }
                    return true;
                })
                .onConfirmText(this::onConfirmYesNo)
                .onAccepted((player, s) -> {
                    try {
                        Files.copy(
                            Objects.requireNonNull(WanderingTrades.instance().getResource("trades/blank.yml")),
                            new File(String.format("%s/trades/%s.yml", WanderingTrades.instance().getDataFolder(), s)).toPath()
                        );

                        WanderingTrades.instance().config().load();
                        WanderingTrades.instance().chat().sendParsed(player, lang.get(Lang.MESSAGE_CREATE_CONFIG_SUCCESS));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        WanderingTrades.instance().chat().sendParsed(player, "<red>Error");
                    }
                    reOpen(p);
                })
                .onDenied((player, s) -> {
                    WanderingTrades.instance().chat().sendParsed(player, lang.get(Lang.MESSAGE_CREATE_CONFIG_CANCEL));
                    reOpen(player);
                })
                .start(p);
            return;
        }
        if (i != null) {
            final ItemMeta meta = i.getItemMeta();
            if (meta.hasDisplayName()) {
                final String displayName = meta.getDisplayName();
                if (TextUtil.containsCaseInsensitive(displayName, configNames)) {
                    p.closeInventory();
                    final TradeConfig tradeConfig = Objects.requireNonNull(WanderingTrades.instance().config().tradeConfigs().get(displayName));
                    new TradeListGui(tradeConfig).open(p);
                }
            }
        }
    }

    @Override
    public void reOpen(Player p) {
        new TradeConfigListGui().open(p);
    }
}
