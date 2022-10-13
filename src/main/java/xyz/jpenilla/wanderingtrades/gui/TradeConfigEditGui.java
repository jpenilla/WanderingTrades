package xyz.jpenilla.wanderingtrades.gui;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import xyz.jpenilla.pluginbase.legacy.HeadBuilder;
import xyz.jpenilla.pluginbase.legacy.InputConversation;
import xyz.jpenilla.pluginbase.legacy.ItemBuilder;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Lang;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;
import xyz.jpenilla.wanderingtrades.util.Logging;

@DefaultQualifier(NonNull.class)
public final class TradeConfigEditGui extends BaseGui {
    private final ItemStack enabledEnabled = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
        .setName(this.lang.get(Lang.GUI_TC_EDIT_ENABLED))
        .setLore(this.toggleLore)
        .build();
    private final ItemStack enabledDisabled = new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
        .setName(this.lang.get(Lang.GUI_TC_EDIT_DISABLED))
        .setLore(this.toggleLore)
        .build();
    private final ItemStack randomizedEnabled = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
        .setName(this.lang.get(Lang.GUI_TC_EDIT_RANDOMIZED))
        .setLore(this.toggleLore)
        .build();
    private final ItemStack randomizedDisabled = new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
        .setName(this.lang.get(Lang.GUI_TC_EDIT_NOT_RANDOMIZED))
        .setLore(this.toggleLore)
        .build();
    private final ItemStack invEnabled = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
        .setName(this.lang.get(Lang.GUI_TC_EDIT_INVINCIBLE))
        .setLore(this.toggleLore)
        .build();
    private final ItemStack invDisabled = new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
        .setName(this.lang.get(Lang.GUI_TC_EDIT_NOT_INVINCIBLE))
        .setLore(this.toggleLore)
        .build();
    private final ItemStack randAmount = new ItemBuilder(Material.LIGHT_BLUE_STAINED_GLASS_PANE)
        .setName(this.lang.get(Lang.GUI_TC_EDIT_RANDOM_AMOUNT))
        .build();
    private final ItemStack chance = new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE)
        .setName(this.lang.get(Lang.GUI_TC_EDIT_CHANCE))
        .build();
    private final ItemStack customName = new ItemBuilder(Material.PINK_STAINED_GLASS_PANE)
        .setName(this.lang.get(Lang.GUI_TC_EDIT_CUSTOM_NAME))
        .build();
    private final ItemStack deleteButton = new HeadBuilder("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzY5NzY0NjE1ZGQ5Y2EwNTk5YmQ5ODg1ZjIyMmFhNWVhNWI0NzZiZDFiOTNlOTYyODUzNjZkMWQ0YzEifX19")
        .setName(this.lang.get(Lang.GUI_TRADE_DELETE))
        .setLore(this.lang.get(Lang.GUI_CONFIG_DELETE_LORE))
        .build();
    private final TradeConfig tradeConfig;

    public TradeConfigEditGui(final WanderingTrades plugin, TradeConfig tradeConfig) {
        super(plugin, plugin.langConfig().get(Lang.GUI_TC_EDIT_TITLE) + tradeConfig.configName(), 45);
        this.tradeConfig = tradeConfig;
    }

    @Override
    public Inventory getInventory() {
        this.inventory.clear();

        this.inventory.setItem(this.inventory.getSize() - 1, this.backButton);

        this.inventory.setItem(10, this.tradeConfig.enabled() ? this.enabledEnabled : this.enabledDisabled);
        this.inventory.setItem(12, this.tradeConfig.randomized() ? this.randomizedEnabled : this.randomizedDisabled);
        this.inventory.setItem(14, this.tradeConfig.invincible() ? this.invEnabled : this.invDisabled);

        final List<String> randAmountLore = new ArrayList<>();
        randAmountLore.add(this.lang.get(Lang.GUI_VALUE_LORE) + "<#0092FF>" + this.tradeConfig.randomAmount());
        randAmountLore.add(this.lang.get(Lang.GUI_EDIT_LORE));
        this.inventory.setItem(16, new ItemBuilder(this.randAmount).setLore(randAmountLore).build());

        final List<String> chanceLore = new ArrayList<>();
        chanceLore.add(this.lang.get(Lang.GUI_VALUE_LORE) + "<#0092FF>" + this.tradeConfig.chance());
        chanceLore.add(this.lang.get(Lang.GUI_EDIT_LORE));
        this.inventory.setItem(28, new ItemBuilder(this.chance).setLore(chanceLore).build());

        final List<String> customNameLore = new ArrayList<>();
        customNameLore.add(this.lang.get(Lang.GUI_VALUE_LORE) + "<white>" + this.tradeConfig.customName());
        customNameLore.add(this.lang.get(Lang.GUI_EDIT_LORE));
        this.inventory.setItem(30, new ItemBuilder(this.customName).setLore(customNameLore).build());

        this.inventory.setItem(this.inventory.getSize() - 11, this.deleteButton);

        this.fillEmptySlots();

        return this.inventory;
    }

    @Override
    public void onInventoryClick(final InventoryClickEvent event) {
        final @Nullable ItemStack item = event.getCurrentItem();
        final Player p = (Player) event.getWhoClicked();

        if (this.backButton.isSimilar(item)) {
            p.closeInventory();
            new TradeListGui(this.plugin, this.tradeConfig).open(p);
        }

        if (this.enabledEnabled.isSimilar(item)) {
            this.tradeConfig.enabled(false);
        } else if (this.enabledDisabled.isSimilar(item)) {
            this.tradeConfig.enabled(true);
        }

        if (this.randomizedEnabled.isSimilar(item)) {
            this.tradeConfig.randomized(false);
        } else if (this.randomizedDisabled.isSimilar(item)) {
            this.tradeConfig.randomized(true);
        }

        if (this.invEnabled.isSimilar(item)) {
            this.tradeConfig.invincible(false);
        } else if (this.invDisabled.isSimilar(item)) {
            this.tradeConfig.invincible(true);
        }

        if (this.randAmount.isSimilar(item)) {
            this.randAmountClick(p);
        }

        if (this.chance.isSimilar(item)) {
            this.chanceClick(p);
        }

        if (this.customName.isSimilar(item)) {
            this.customNameClick(p);
        }

        if (this.deleteButton.isSimilar(item)) {
            this.deleteClick(p);
        }

        this.tradeConfig.save();

        this.getInventory();
    }

    private void randAmountClick(final Player p) {
        p.closeInventory();
        new InputConversation()
            .onPromptText(player -> {
                this.plugin.chat().sendParsed(player,
                    this.lang.get(Lang.MESSAGE_SET_RAND_AMOUNT_PROMPT)
                        + "<reset>\n" + this.lang.get(Lang.MESSAGE_CURRENT_VALUE) + this.tradeConfig.randomAmount()
                        + "<reset>\n" + this.lang.get(Lang.MESSAGE_ENTER_NUMBER_OR_RANGE));
                return "";
            })
            .onValidateInput(this::validateIntRange)
            .onConfirmText(this::confirmYesNo)
            .onAccepted((player, s) -> {
                this.tradeConfig.randomAmount(s);
                this.tradeConfig.save();
                this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_EDIT_SAVED));
                this.open(player);
            })
            .onDenied(this::editCancelled)
            .start(p);
    }

    private void chanceClick(final Player p) {
        p.closeInventory();
        new InputConversation()
            .onPromptText(player -> {
                this.plugin.chat().sendParsed(player,
                    this.lang.get(Lang.MESSAGE_SET_CHANCE_PROMPT)
                        + "<reset>\n" + this.lang.get(Lang.MESSAGE_CURRENT_VALUE) + this.tradeConfig.chance()
                        + "<reset>\n" + this.lang.get(Lang.MESSAGE_ENTER_NUMBER));
                return "";
            })
            .onValidateInput(this::validateDouble0T1)
            .onConfirmText(this::confirmYesNo)
            .onAccepted((player, s) -> {
                this.tradeConfig.chance(Double.parseDouble(s));
                this.tradeConfig.save();
                this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_EDIT_SAVED));
                this.open(player);
            })
            .onDenied(this::editCancelled)
            .start(p);
    }

    private void customNameClick(final Player p) {
        p.closeInventory();
        new InputConversation()
            .onPromptText(player -> {
                this.plugin.chat().sendParsed(player,
                    this.lang.get(Lang.MESSAGE_CREATE_TITLE_OR_NONE_PROMPT)
                        + "<reset>\n" + this.lang.get(Lang.MESSAGE_CURRENT_VALUE) + "<reset>" + this.tradeConfig.customName());
                return "";
            })
            .onValidateInput((pl, s) -> true)
            .onConfirmText(this::confirmYesNo)
            .onAccepted((player, string) -> {
                this.tradeConfig.customName(string);
                this.tradeConfig.save();
                this.open(player);
            })
            .onDenied(this::editCancelled)
            .start(p);
    }

    private void deleteClick(final Player p) {
        p.closeInventory();
        new InputConversation()
            .onPromptText((player -> {
                this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_DELETE_PROMPT).replace("{TRADE_NAME}", this.tradeConfig.configName()));
                this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_CONFIRM).replace("{KEY}", this.lang.get(Lang.MESSAGE_CONFIRM_KEY)));
                return "";
            }))
            .onValidateInput(((player, s) -> {
                if (s.equals(this.lang.get(Lang.MESSAGE_CONFIRM_KEY))) {
                    final Path tcFile = this.plugin.dataPath().resolve("trades/" + this.tradeConfig.configName() + ".yml");
                    try {
                        Files.delete(tcFile);
                    } catch (Exception e) {
                        Logging.logger().warn("File delete failed", e);
                    }
                    this.plugin.config().load();
                    this.plugin.chat().sendParsed(player, this.lang.get(Lang.MESSAGE_EDIT_SAVED));
                    new TradeConfigListGui(this.plugin).open(player);
                } else {
                    this.editCancelled(player, s);
                }
                return true;
            }))
            .start(p);
    }

    @Override
    public void reOpen(final Player player) {
        Bukkit.getServer().getScheduler().runTaskLater(this.plugin, () -> new TradeConfigEditGui(this.plugin, this.tradeConfig).open(player), 1L);
    }
}
