package xyz.jpenilla.wanderingtrades.gui;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.stream.IntStream;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.jpenilla.jmplib.HeadBuilder;
import xyz.jpenilla.jmplib.InputConversation;
import xyz.jpenilla.jmplib.ItemBuilder;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Lang;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;

public class TradeConfigEditGui extends GuiHolder {
    private final ItemStack enabledEnabled = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_TC_EDIT_ENABLED)).setLore(gui_toggle_lore).build();
    private final ItemStack enabledDisabled = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_TC_EDIT_DISABLED)).setLore(gui_toggle_lore).build();
    private final ItemStack randomizedEnabled = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_TC_EDIT_RANDOMIZED)).setLore(gui_toggle_lore).build();
    private final ItemStack randomizedDisabled = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_TC_EDIT_NOT_RANDOMIZED)).setLore(gui_toggle_lore).build();
    private final ItemStack invEnabled = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_TC_EDIT_INVINCIBLE)).setLore(gui_toggle_lore).build();
    private final ItemStack invDisabled = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_TC_EDIT_NOT_INVINCIBLE)).setLore(gui_toggle_lore).build();
    private final ItemStack randAmount = new ItemBuilder(Material.LIGHT_BLUE_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_TC_EDIT_RANDOM_AMOUNT)).build();
    private final ItemStack chance = new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_TC_EDIT_CHANCE)).build();
    private final ItemStack customName = new ItemBuilder(Material.PINK_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_TC_EDIT_CUSTOM_NAME)).build();
    private final ItemStack deleteButton = new HeadBuilder("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzY5NzY0NjE1ZGQ5Y2EwNTk5YmQ5ODg1ZjIyMmFhNWVhNWI0NzZiZDFiOTNlOTYyODUzNjZkMWQ0YzEifX19")
        .setName(lang.get(Lang.GUI_TRADE_DELETE)).setLore(lang.get(Lang.GUI_CONFIG_DELETE_LORE)).build();

    private final TradeConfig tradeConfig;

    public TradeConfigEditGui(TradeConfig tradeConfig) {
        super(WanderingTrades.instance().langConfig().get(Lang.GUI_TC_EDIT_TITLE) + tradeConfig.configName(), 45);
        this.tradeConfig = tradeConfig;
    }

    public @NonNull Inventory getInventory() {
        inventory.clear();

        inventory.setItem(inventory.getSize() - 1, backButton);

        ItemStack enabled;
        if (tradeConfig.enabled()) {
            enabled = enabledEnabled;
        } else {
            enabled = enabledDisabled;
        }
        ItemStack randomized;
        if (tradeConfig.randomized()) {
            randomized = randomizedEnabled;
        } else {
            randomized = randomizedDisabled;
        }
        ItemStack inv;
        if (tradeConfig.invincible()) {
            inv = invEnabled;
        } else {
            inv = invDisabled;
        }
        inventory.setItem(10, enabled);
        inventory.setItem(12, randomized);
        inventory.setItem(14, inv);

        ArrayList<String> randAmountLore = new ArrayList<>();
        randAmountLore.add(lang.get(Lang.GUI_VALUE_LORE) + "<color:#0092FF>" + tradeConfig.randomAmount());
        randAmountLore.add(lang.get(Lang.GUI_EDIT_LORE));
        inventory.setItem(16, new ItemBuilder(randAmount).setLore(randAmountLore).build());

        ArrayList<String> chanceLore = new ArrayList<>();
        chanceLore.add(lang.get(Lang.GUI_VALUE_LORE) + "<color:#0092FF>" + tradeConfig.chance());
        chanceLore.add(lang.get(Lang.GUI_EDIT_LORE));
        inventory.setItem(28, new ItemBuilder(chance).setLore(chanceLore).build());

        ArrayList<String> customNameLore = new ArrayList<>();
        customNameLore.add(lang.get(Lang.GUI_VALUE_LORE) + "<white>" + tradeConfig.customName());
        customNameLore.add(lang.get(Lang.GUI_EDIT_LORE));
        inventory.setItem(30, new ItemBuilder(customName).setLore(customNameLore).build());

        inventory.setItem(inventory.getSize() - 11, deleteButton);

        IntStream.range(0, inventory.getSize()).forEach(slot -> {
            if (inventory.getItem(slot) == null) {
                inventory.setItem(slot, filler);
            }
        });

        return inventory;
    }

    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        Player p = (Player) event.getWhoClicked();
        ClickType click = event.getClick();
        if (event.getSlot() != event.getRawSlot()) {
            if (click.isKeyboardClick() || click.isShiftClick()) {
                event.setCancelled(true);
            }
            return;
        }
        event.setCancelled(true);

        if (backButton.isSimilar(item)) {
            p.closeInventory();
            new TradeListGui(tradeConfig).open(p);
        }

        if (enabledEnabled.isSimilar(item)) {
            tradeConfig.enabled(false);
        }
        if (enabledDisabled.isSimilar(item)) {
            tradeConfig.enabled(true);
        }

        if (randomizedEnabled.isSimilar(item)) {
            tradeConfig.randomized(false);
        }
        if (randomizedDisabled.isSimilar(item)) {
            tradeConfig.randomized(true);
        }

        if (invEnabled.isSimilar(item)) {
            tradeConfig.invincible(false);
        }
        if (invDisabled.isSimilar(item)) {
            tradeConfig.invincible(true);
        }

        if (randAmount.isSimilar(item)) {
            p.closeInventory();
            new InputConversation()
                .onPromptText(player -> {
                    WanderingTrades.instance().chat().sendParsed(player,
                        lang.get(Lang.MESSAGE_SET_RAND_AMOUNT_PROMPT)
                            + "<reset>\n" + lang.get(Lang.MESSAGE_CURRENT_VALUE) + tradeConfig.randomAmount()
                            + "<reset>\n" + lang.get(Lang.MESSAGE_ENTER_NUMBER_OR_RANGE));
                    return "";
                })
                .onValidateInput(TradeConfigEditGui::validateIntRange)
                .onConfirmText(this::onConfirmYesNo)
                .onAccepted((player, s) -> {
                    tradeConfig.randomAmount(s);
                    tradeConfig.save();
                    WanderingTrades.instance().chat().sendParsed(player, lang.get(Lang.MESSAGE_EDIT_SAVED));
                    open(player);
                })
                .onDenied(this::onEditCancelled)
                .start(p);
        }

        if (chance.isSimilar(item)) {
            p.closeInventory();
            new InputConversation()
                .onPromptText(player -> {
                    WanderingTrades.instance().chat().sendParsed(player,
                        lang.get(Lang.MESSAGE_SET_CHANCE_PROMPT)
                            + "<reset>\n" + lang.get(Lang.MESSAGE_CURRENT_VALUE) + tradeConfig.chance()
                            + "<reset>\n" + lang.get(Lang.MESSAGE_ENTER_NUMBER));
                    return "";
                })
                .onValidateInput(this::onValidateDouble0T1)
                .onConfirmText(this::onConfirmYesNo)
                .onAccepted((player, s) -> {
                    tradeConfig.chance(Double.parseDouble(s));
                    tradeConfig.save();
                    WanderingTrades.instance().chat().sendParsed(player, lang.get(Lang.MESSAGE_EDIT_SAVED));
                    open(player);
                })
                .onDenied(this::onEditCancelled)
                .start(p);
        }

        if (customName.isSimilar(item)) {
            p.closeInventory();
            new InputConversation()
                .onPromptText(player -> {
                    WanderingTrades.instance().chat().sendParsed(player,
                        lang.get(Lang.MESSAGE_CREATE_TITLE_OR_NONE_PROMPT)
                            + "<reset>\n" + lang.get(Lang.MESSAGE_CURRENT_VALUE) + "<reset>" + tradeConfig.customName());
                    return "";
                })
                .onValidateInput((pl, s) -> true)
                .onConfirmText(this::onConfirmYesNo)
                .onAccepted((player, string) -> {
                    tradeConfig.customName(string);
                    tradeConfig.save();
                    open(player);
                })
                .onDenied(this::onEditCancelled)
                .start(p);
        }

        if (deleteButton.isSimilar(item)) {
            p.closeInventory();
            new InputConversation()
                .onPromptText((player -> {
                    WanderingTrades.instance().chat().sendParsed(player, lang.get(Lang.MESSAGE_DELETE_PROMPT).replace("{TRADE_NAME}", tradeConfig.configName()));
                    WanderingTrades.instance().chat().sendParsed(player, lang.get(Lang.MESSAGE_CONFIRM).replace("{KEY}", lang.get(Lang.MESSAGE_CONFIRM_KEY)));
                    return "";
                }))
                .onValidateInput(((player, s) -> {
                    if (s.equals(lang.get(Lang.MESSAGE_CONFIRM_KEY))) {
                        final Path tcFile = WanderingTrades.instance().dataPath().resolve("/trades/" + tradeConfig + ".yml");
                        try {
                            Files.delete(tcFile);
                        } catch (Exception e) {
                            WanderingTrades.instance().getLogger().log(Level.WARNING, "File delete failed", e);
                        }
                        WanderingTrades.instance().config().load();
                        WanderingTrades.instance().chat().sendParsed(player, lang.get(Lang.MESSAGE_EDIT_SAVED));
                        new TradeConfigListGui().open(player);
                    } else {
                        onEditCancelled(player, s);
                    }
                    return true;
                }))
                .start(p);
        }

        tradeConfig.save();

        getInventory();
    }

    public void reOpen(Player player) {
        Bukkit.getServer().getScheduler().runTaskLater(WanderingTrades.instance(), () -> new TradeConfigEditGui(tradeConfig).open(player), 1L);
    }

    public static boolean validateIntRange(Player p, String s) {
        if (s.contains(":")) {
            try {
                String[] split = s.split(":");
                if (validateInt(null, split[0]) && validateInt(null, split[1])) {
                    return true;
                } else {
                    WanderingTrades.instance().chat().sendParsed(p, WanderingTrades.instance().langConfig().get(Lang.MESSAGE_ENTER_NUMBER_OR_RANGE));
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        } else {
            return validateInt(p, s);
        }
    }

    public static boolean validateInt(Player player, String input) {
        try {
            int i = Integer.parseInt(input);
            if (i < 0) {
                WanderingTrades.instance().chat().sendParsed(player, WanderingTrades.instance().langConfig().get(Lang.MESSAGE_NUMBER_GTE_0));
                return false;
            }
        } catch (NumberFormatException ex) {
            WanderingTrades.instance().chat().sendParsed(player, WanderingTrades.instance().langConfig().get(Lang.MESSAGE_ENTER_NUMBER_OR_RANGE));
            return false;
        }
        return true;
    }
}
