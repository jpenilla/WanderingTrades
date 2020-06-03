package fun.ccmc.wanderingtrades.gui;

import fun.ccmc.jmplib.Gui;
import fun.ccmc.jmplib.TextUtil;
import fun.ccmc.wanderingtrades.WanderingTrades;
import fun.ccmc.wanderingtrades.config.Lang;
import fun.ccmc.wanderingtrades.config.TradeConfig;
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

public class TradeGui extends GuiHolder {

    public final ItemStack cancelButton = Gui.buildHeadLore(lang.get(Lang.GUI_TRADE_CANCEL), lang.get(Lang.GUI_TRADE_CANCEL_LORE),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTZjNjBkYTQxNGJmMDM3MTU5YzhiZThkMDlhOGVjYjkxOWJmODlhMWEyMTUwMWI1YjJlYTc1OTYzOTE4YjdiIn19fQ==");
    public final ItemStack saveButton = Gui.buildHeadLore(lang.get(Lang.GUI_TRADE_SAVE), lang.get(Lang.GUI_TRADE_SAVE_LORE),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGE5OTM0MmUyYzczYTlmMzgyMjYyOGU3OTY0ODgyMzRmMjU4NDQ2ZjVhMmQ0ZDU5ZGRlNGFhODdkYjk4In19fQ==");
    public final ItemStack info = Gui.buildHead(lang.get(Lang.GUI_TRADE_INFO),
            lang.getList(Lang.GUI_TRADE_INFO_LORE),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTY0MzlkMmUzMDZiMjI1NTE2YWE5YTZkMDA3YTdlNzVlZGQyZDUwMTVkMTEzYjQyZjQ0YmU2MmE1MTdlNTc0ZiJ9fX0=");
    public final ItemStack plus = Gui.buildHead("&e+",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzIzMzJiNzcwYTQ4NzQ2OTg4NjI4NTVkYTViM2ZlNDdmMTlhYjI5MWRmNzY2YjYwODNiNWY5YTBjM2M2ODQ3ZSJ9fX0=");
    public final ItemStack equals = Gui.buildHead("&e=",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzkyNzY2ZmVhNmMwNTc1MGU0MGRjODNjZDdlOTNhYjM0ODQ2ZDQ0MDkyMDk1MWRhMjYzNTk4MzZlY2YwOGY0YiJ9fX0=");
    public final ItemStack deleteButton = Gui.buildHeadLore(lang.get(Lang.GUI_TRADE_DELETE), lang.get(Lang.GUI_TRADE_DELETE_LORE),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzY5NzY0NjE1ZGQ5Y2EwNTk5YmQ5ODg1ZjIyMmFhNWVhNWI0NzZiZDFiOTNlOTYyODUzNjZkMWQ0YzEifX19");
    public final ItemStack experienceEnabled = Gui.buildLore(Material.LIME_STAINED_GLASS_PANE, lang.get(Lang.GUI_TRADE_EXP_REWARD), gui_toggle_lore);
    public final ItemStack experienceDisabled = Gui.buildLore(Material.RED_STAINED_GLASS_PANE, lang.get(Lang.GUI_TRADE_NO_EXP_REWARD), gui_toggle_lore);
    public final ItemStack maxUsesStack = Gui.build(Material.LIGHT_BLUE_STAINED_GLASS_PANE, lang.get(Lang.GUI_TRADE_MAX_USES));
    public final ItemStack tradeNameStack = Gui.build(Material.PINK_STAINED_GLASS_PANE, lang.get(Lang.GUI_TRADE_TRADE_NAME));
    public final ItemStack ingredient1 = Gui.build(Material.STRUCTURE_VOID, lang.get(Lang.GUI_TRADE_INGREDIENT_1),
            lang.getList(Lang.GUI_TRADE_REQUIRED_LORE));
    public final ItemStack ingredient2 = Gui.build(Material.STRUCTURE_VOID, lang.get(Lang.GUI_TRADE_INGREDIENT_2),
            lang.getList(Lang.GUI_TRADE_OPTIONAL_LORE));
    public final ItemStack resultStack = Gui.build(Material.STRUCTURE_VOID, lang.get(Lang.GUI_TRADE_RESULT),
            lang.getList(Lang.GUI_TRADE_REQUIRED_LORE));

    public final String tradeConfig;
    public String tradeName = null;
    public int maxUses = 1;
    public boolean experienceReward = true;
    public ItemStack i1 = ingredient1;
    public ItemStack i2 = ingredient2;
    public ItemStack result = resultStack;

    public TradeGui(String name, String tradeConfig) {
        super(name, 45);
        this.tradeConfig = tradeConfig;
    }

    @Override
    public Inventory getInventory() {
        inventory.clear();

        inventory.setItem(inventory.getSize() - 1, cancelButton);
        inventory.setItem(inventory.getSize() - 2, saveButton);

        inventory.setItem(8, info);

        inventory.setItem(28, i1);
        inventory.setItem(29, plus);
        inventory.setItem(30, i2);
        inventory.setItem(31, equals);
        inventory.setItem(32, result);

        if (experienceReward) {
            inventory.setItem(12, experienceEnabled);
        } else {
            inventory.setItem(12, experienceDisabled);
        }

        ItemMeta maxUsesMeta = maxUsesStack.getItemMeta();
        ArrayList<String> maxUsesLore = new ArrayList<>();
        maxUsesLore.add(lang.get(Lang.GUI_VALUE_LORE) + "&b" + maxUses);
        maxUsesLore.add(lang.get(Lang.GUI_EDIT_LORE));
        maxUsesMeta.setLore(TextUtil.colorize(maxUsesLore));
        maxUsesStack.setItemMeta(maxUsesMeta);
        inventory.setItem(14, maxUsesStack);

        return inventory;
    }

    @Override
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

        if (cancelButton.isSimilar(item)) {
            p.closeInventory();
            new TradeListGui(tradeConfig).open(p);
        }

        if (experienceEnabled.isSimilar(item)) {
            experienceReward = false;
        }
        if (experienceDisabled.isSimilar(item)) {
            experienceReward = true;
        }

        if (maxUsesStack.isSimilar(item)) {
            p.closeInventory();
            new AnvilGUI.Builder()
                    .onClose(this::reOpen)
                    .onComplete((player, text) -> {
                        try {
                            int i = Integer.parseInt(text);
                            if (i < 1) {
                                return AnvilGUI.Response.text("Number must be > 0");
                            } else {
                                maxUses = i;
                            }
                        } catch (NumberFormatException ex) {
                            return AnvilGUI.Response.text("Enter a number");
                        }
                        return AnvilGUI.Response.close();
                    })
                    .text(maxUses + "")
                    .item(new ItemStack(Material.WRITABLE_BOOK))
                    .title("Set Max Uses")
                    .plugin(WanderingTrades.getInstance())
                    .open(p);
        }

        TradeConfig t = WanderingTrades.getInstance().getCfg().getTradeConfigs().get(tradeConfig);

        int rS = event.getRawSlot();
        if (rS == 28) {
            i1 = updateSlot(event, ingredient1);
        } else if (rS == 30) {
            i2 = updateSlot(event, ingredient2);
        } else if (rS == 32) {
            result = updateSlot(event, resultStack);
        }

        if (saveButton.isSimilar(item)) {
            if (tradeName != null && !result.equals(resultStack) && !i1.equals(ingredient1)) {
                ItemStack ingred2 = null;
                if (!i2.equals(ingredient2)) {
                    ingred2 = i2;
                }
                t.writeTrade(tradeConfig, tradeName, maxUses, experienceReward, i1, ingred2, result);
                WanderingTrades.getInstance().getCfg().load();
                p.closeInventory();
                new TradeListGui(tradeConfig).open(p);
            }
        }

        onClick(event);

        getInventory();
    }

    public void onClick(InventoryClickEvent e) {
    }

    public ItemStack updateSlot(InventoryClickEvent event, ItemStack def) {
        ItemStack cursor = event.getCursor();
        if (cursor != null) {
            if (!Material.AIR.equals(cursor.getType())) {
                event.getView().setCursor(null);
                return cursor;
            } else {
                if (!def.isSimilar(event.getCurrentItem())) {
                    event.getView().setCursor(event.getCurrentItem());
                }
                return def;
            }
        } else {
            return def;
        }
    }

    public void reOpen(Player player) {
        Bukkit.getServer().getScheduler().runTaskLater(WanderingTrades.getInstance(), () -> player.openInventory(getInventory()), 1L);
    }
}
