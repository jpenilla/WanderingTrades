package fun.ccmc.wanderingtrades.gui;

import fun.ccmc.wanderingtrades.WanderingTrades;
import fun.ccmc.wanderingtrades.config.TradeConfig;
import fun.ccmc.wanderingtrades.util.Gui;
import fun.ccmc.wanderingtrades.util.TextUtil;
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
    private final ItemStack enabledEnabled = Gui.buildLore(Material.LIME_STAINED_GLASS_PANE, "Enabled", "Click to toggle");
    private final ItemStack enabledDisabled = Gui.buildLore(Material.RED_STAINED_GLASS_PANE, "Disabled", "Click to toggle");
    private final ItemStack randomizedEnabled = Gui.buildLore(Material.LIME_STAINED_GLASS_PANE, "Randomized", "Click to toggle");
    private final ItemStack randomizedDisabled = Gui.buildLore(Material.RED_STAINED_GLASS_PANE, "Not Randomized", "Click to toggle");
    private final ItemStack invEnabled = Gui.buildLore(Material.LIME_STAINED_GLASS_PANE, "Invincible", "Click to toggle");
    private final ItemStack invDisabled = Gui.buildLore(Material.RED_STAINED_GLASS_PANE, "Not Invincible", "Click to toggle");
    private final ItemStack randAmount = Gui.build(Material.LIGHT_BLUE_STAINED_GLASS_PANE, "Random Amount");
    private final ItemStack chance = Gui.build(Material.PURPLE_STAINED_GLASS_PANE, "Chance");
    private final ItemStack customName = Gui.build(Material.PINK_STAINED_GLASS_PANE, "Custom Name");

    private final String tradeConfig;

    public TradeConfigEditGui(String tradeConfig) {
        super("&e&lEditing Config&7: &f" + tradeConfig, 45);
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
        ArrayList<String> randAmountlore = new ArrayList<>();
        randAmountlore.add("Value&7: &b" + t.getRandomAmount());
        randAmountlore.add("  &7&oClick to edit");
        randAmountMeta.setLore(TextUtil.colorize(randAmountlore));
        randAmount.setItemMeta(randAmountMeta);
        inventory.setItem(16, randAmount);

        ItemMeta chanceMeta = chance.getItemMeta();
        ArrayList<String> chanceLore = new ArrayList<>();
        chanceLore.add("Value&7: &b" + t.getChance());
        chanceLore.add("  &7&oClick to edit");
        chanceMeta.setLore(TextUtil.colorize(chanceLore));
        chance.setItemMeta(chanceMeta);
        inventory.setItem(28, chance);

        ItemMeta customNameMeta = customName.getItemMeta();
        ArrayList<String> customNameLore = new ArrayList<>();
        customNameLore.add("Value&7: &r" + t.getCustomName());
        customNameLore.add("  &7&oClick to edit");
        customNameMeta.setLore(TextUtil.colorize(customNameLore));
        customName.setItemMeta(customNameMeta);
        inventory.setItem(30, customName);

        IntStream.range(0, inventory.getSize()).forEach(slot -> {
            if (inventory.getItem(slot) == null) {
                inventory.setItem(slot, Gui.build(Material.GRAY_STAINED_GLASS_PANE));
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
                                return AnvilGUI.Response.text("Number must be > 0");
                            } else {
                                t.setRandomAmount(i);
                                t.save(tradeConfig);
                            }
                        } catch (NumberFormatException ex) {
                            return AnvilGUI.Response.text("Enter a number");
                        }
                        return AnvilGUI.Response.close();
                    })
                    .text(t.getRandomAmount() + "")
                    .item(new ItemStack(Material.WRITABLE_BOOK))
                    .title("Set Random Amount")
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
                                return AnvilGUI.Response.text("Number must be 0.00-1.00");
                            } else {
                                t.setChance(d);
                                t.save(tradeConfig);
                            }
                        } catch (NumberFormatException ex) {
                            return AnvilGUI.Response.text("Enter a number 0.00-1.00");
                        }
                        return AnvilGUI.Response.close();
                    })
                    .text(t.getChance() + "")
                    .item(new ItemStack(Material.WRITABLE_BOOK))
                    .title("Set Chance 0.00-1.00")
                    .plugin(WanderingTrades.getInstance())
                    .open(p);
        }

        if (customName.isSimilar(item)) {
            p.closeInventory();
            String cN;
            if(t.getCustomName() == null) {
                cN = "Enter name here";
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
                    .title("Enter a Name or 'NONE'")
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
