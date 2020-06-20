package xyz.jpenilla.wanderingtrades.gui;

import xyz.jpenilla.jmplib.ItemBuilder;
import xyz.jpenilla.jmplib.TextUtil;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Lang;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.stream.IntStream;

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

    private final String tradeConfig;

    public TradeConfigEditGui(String tradeConfig) {
        super(WanderingTrades.getInstance().getLang().get(Lang.GUI_TC_EDIT_TITLE) + tradeConfig, 45);
        this.tradeConfig = tradeConfig;
    }

    public Inventory getInventory() {
        inventory.clear();

        inventory.setItem(inventory.getSize() - 1, backButton);

        TradeConfig t = WanderingTrades.getInstance().getCfg().getTradeConfigs().get(tradeConfig);

        ItemStack enabled;
        if (t.isEnabled()) {
            enabled = enabledEnabled;
        } else {
            enabled = enabledDisabled;
        }
        ItemStack randomized;
        if (t.isRandomized()) {
            randomized = randomizedEnabled;
        } else {
            randomized = randomizedDisabled;
        }
        ItemStack inv;
        if (t.isInvincible()) {
            inv = invEnabled;
        } else {
            inv = invDisabled;
        }
        inventory.setItem(10, enabled);
        inventory.setItem(12, randomized);
        inventory.setItem(14, inv);

        ItemMeta randAmountMeta = randAmount.getItemMeta();
        ArrayList<String> randAmountLore = new ArrayList<>();
        randAmountLore.add(lang.get(Lang.GUI_VALUE_LORE) + "&b" + t.getRandomAmount());
        randAmountLore.add(lang.get(Lang.GUI_EDIT_LORE));
        randAmountMeta.setLore(TextUtil.colorize(randAmountLore));
        randAmount.setItemMeta(randAmountMeta);
        inventory.setItem(16, randAmount);

        ItemMeta chanceMeta = chance.getItemMeta();
        ArrayList<String> chanceLore = new ArrayList<>();
        chanceLore.add(lang.get(Lang.GUI_VALUE_LORE) + "&b" + t.getChance());
        chanceLore.add(lang.get(Lang.GUI_EDIT_LORE));
        chanceMeta.setLore(TextUtil.colorize(chanceLore));
        chance.setItemMeta(chanceMeta);
        inventory.setItem(28, chance);

        ItemMeta customNameMeta = customName.getItemMeta();
        ArrayList<String> customNameLore = new ArrayList<>();
        customNameLore.add(lang.get(Lang.GUI_VALUE_LORE) + t.getCustomName());
        customNameLore.add(lang.get(Lang.GUI_EDIT_LORE));
        customNameMeta.setLore(TextUtil.colorize(customNameLore));
        customName.setItemMeta(customNameMeta);
        inventory.setItem(30, customName);

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

        TradeConfig t = WanderingTrades.getInstance().getCfg().getTradeConfigs().get(tradeConfig);

        if (enabledEnabled.isSimilar(item)) {
            t.setEnabled(false);
        }
        if (enabledDisabled.isSimilar(item)) {
            t.setEnabled(true);
        }

        if (randomizedEnabled.isSimilar(item)) {
            t.setRandomized(false);
        }
        if (randomizedDisabled.isSimilar(item)) {
            t.setRandomized(true);
        }

        if (invEnabled.isSimilar(item)) {
            t.setInvincible(false);
        }
        if (invDisabled.isSimilar(item)) {
            t.setInvincible(true);
        }

        if (randAmount.isSimilar(item)) {
            p.closeInventory();
            new AnvilGUI.Builder()
                    .onClose(this::reOpen)
                    .onComplete((player, text) -> {
                        try {
                            int i = Integer.parseInt(text);
                            if (i < 1) {
                                return AnvilGUI.Response.text(lang.get(Lang.GUI_ANVIL_NUMBER_GT_0));
                            } else {
                                t.setRandomAmount(i);
                                t.save(tradeConfig);
                            }
                        } catch (NumberFormatException ex) {
                            return AnvilGUI.Response.text(lang.get(Lang.GUI_ANVIL_ENTER_NUMBER));
                        }
                        return AnvilGUI.Response.close();
                    })
                    .text(String.valueOf(t.getRandomAmount()))
                    .item(new ItemStack(Material.WRITABLE_BOOK))
                    .title(lang.get(Lang.GUI_ANVIL_SET_RAND_AMOUNT_TITLE))
                    .plugin(WanderingTrades.getInstance())
                    .open(p);
        }

        if (chance.isSimilar(item)) {
            p.closeInventory();
            new AnvilGUI.Builder()
                    .onClose(this::reOpen)
                    .onComplete((player, text) -> {
                        try {
                            double d = Double.parseDouble(text);
                            if (d < 0 || d > 1) {
                                return AnvilGUI.Response.text(lang.get(Lang.GUI_ANVIL_NUMBER_0T1));
                            } else {
                                t.setChance(d);
                                t.save(tradeConfig);
                            }
                        } catch (NumberFormatException ex) {
                            return AnvilGUI.Response.text(lang.get(Lang.GUI_ANVIL_ENTER_NUMBER));
                        }
                        return AnvilGUI.Response.close();
                    })
                    .text(String.valueOf(t.getChance()))
                    .item(new ItemStack(Material.WRITABLE_BOOK))
                    .title(lang.get(Lang.GUI_ANVIL_SET_CHANCE_TITLE))
                    .plugin(WanderingTrades.getInstance())
                    .open(p);
        }

        if (customName.isSimilar(item)) {
            p.closeInventory();
            String cN;
            if(t.getCustomName() == null) {
                cN = lang.get(Lang.GUI_ANVIL_TYPE_HERE);
            } else {
                cN = t.getCustomName();
            }
            new AnvilGUI.Builder()
                    .onClose(this::reOpen)
                    .onComplete((player, text) -> {
                        t.setCustomName(text);
                        t.save(tradeConfig);
                        return AnvilGUI.Response.close();
                    })
                    .text(cN)
                    .item(new ItemStack(Material.WRITABLE_BOOK))
                    .title(lang.get(Lang.GUI_ANVIL_CREATE_TITLE_OR_NONE))
                    .plugin(WanderingTrades.getInstance())
                    .open(p);
        }

        t.save(tradeConfig);

        getInventory();
    }

    private void reOpen(Player player) {
        Bukkit.getServer().getScheduler().runTaskLater(WanderingTrades.getInstance(), () -> new TradeConfigEditGui(tradeConfig).open(player), 1L);
    }
}
