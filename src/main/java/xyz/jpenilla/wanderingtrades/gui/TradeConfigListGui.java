package xyz.jpenilla.wanderingtrades.gui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import xyz.jpenilla.pluginbase.legacy.InputConversation;
import xyz.jpenilla.pluginbase.legacy.ItemBuilder;
import xyz.jpenilla.pluginbase.legacy.TextUtil;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Lang;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;

@DefaultQualifier(NonNull.class)
public final class TradeConfigListGui extends PaginatedGui {
    private final ItemStack newConfig = new ItemBuilder(Material.WRITABLE_BOOK)
        .setName(this.lang.get(Lang.GUI_TC_LIST_ADD_CONFIG))
        .setLore(this.lang.get(Lang.GUI_TC_LIST_ADD_CONFIG_LORE))
        .build();
    private final List<String> configNames;

    public TradeConfigListGui(final WanderingTrades plugin) {
        super(plugin, plugin.langConfig().get(Lang.GUI_TC_LIST_TITLE), 36);
        this.configNames = List.copyOf(plugin.configManager().tradeConfigs().keySet());
    }

    @Override
    public List<ItemStack> getListItems() {
        return this.plugin.configManager().tradeConfigs().entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(Map.Entry::getValue)
            .map(tradeConfig -> {
                final Set<String> tradeKeys = new HashSet<>(tradeConfig.tradesByName().keySet());
                final List<String> finalLores = new ArrayList<>();
                tradeKeys.stream()
                    .sorted()
                    .limit(10)
                    .forEach(key -> finalLores.add("<gray>  " + key));
                if (finalLores.size() == 10) {
                    finalLores.add(this.plugin.langConfig().get(Lang.GUI_TC_LIST_AND_MORE).replace("{VALUE}", String.valueOf(tradeKeys.size() - 10)));
                }
                return new ItemBuilder(Material.PAPER).setName(tradeConfig.configName()).setLore(finalLores).build();
            })
            .toList();
    }

    @Override
    public Inventory getInventory(final Inventory inventory) {
        inventory.setItem(inventory.getSize() - 5, this.newConfig);
        inventory.setItem(this.inventory.getSize() - 1, this.closeButton);
        IntStream.range(inventory.getSize() - 9, inventory.getSize() - 1).forEach(s -> {
            if (this.inventory.getItem(s) == null) {
                this.inventory.setItem(s, this.filler);
            }
        });
        return inventory;
    }

    @Override
    public void onClick(final Player player, final @Nullable ItemStack stack) {
        if (this.closeButton.isSimilar(stack)) {
            player.closeInventory();
            return;
        }
        if (this.newConfig.isSimilar(stack)) {
            this.newConfigClick(player);
            return;
        }
        if (stack != null) {
            final ItemMeta meta = stack.getItemMeta();
            if (meta.hasDisplayName()) {
                final String displayName = meta.getDisplayName();
                if (TextUtil.containsCaseInsensitive(displayName, this.configNames)) {
                    player.closeInventory();
                    final TradeConfig tradeConfig = Objects.requireNonNull(this.plugin.configManager().tradeConfigs().get(displayName));
                    new TradeListGui(this.plugin, tradeConfig).open(player);
                }
            }
        }
    }

    private void newConfigClick(final Player p) {
        p.closeInventory();
        new InputConversation()
            .onPromptText(player -> {
                this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_CREATE_CONFIG_PROMPT));
                return "";
            })
            .onValidateInput((player, input) -> {
                if (input.contains(" ")) {
                    this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_NO_SPACES));
                    return false;
                }
                if (TextUtil.containsCaseInsensitive(input, this.configNames)) {
                    this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_CREATE_UNIQUE));
                    return false;
                }
                return true;
            })
            .onConfirmText(this::confirmYesNo)
            .onAccepted((player, s) -> {
                try {
                    Files.copy(
                        Objects.requireNonNull(this.plugin.getResource("trades/blank.yml")),
                        new File(String.format("%s/trades/%s.yml", this.plugin.getDataFolder(), s)).toPath()
                    );

                    this.plugin.config().load();
                    this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_CREATE_CONFIG_SUCCESS));
                } catch (IOException ex) {
                    ex.printStackTrace();
                    this.plugin.chat().sendParsed(player, "<red>Error");
                }
                this.reOpen(p);
            })
            .onDenied((player, s) -> {
                this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_CREATE_CONFIG_CANCEL));
                this.reOpen(player);
            })
            .start(p);
    }

    @Override
    public void reOpen(final Player player) {
        new TradeConfigListGui(this.plugin).open(player);
    }
}
