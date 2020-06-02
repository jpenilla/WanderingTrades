package fun.ccmc.wanderingtrades.gui;

import fun.ccmc.jmplib.Gui;
import fun.ccmc.jmplib.TextUtil;
import fun.ccmc.wanderingtrades.WanderingTrades;
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
import java.util.Arrays;
import java.util.stream.IntStream;

public class TradeEditGui extends GuiHolder {
    private final String tradeConfig;
    private final ItemStack cancelButton = Gui.buildHeadLore("&4Cancel", "&7&o  Click to cancel and go back",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTZjNjBkYTQxNGJmMDM3MTU5YzhiZThkMDlhOGVjYjkxOWJmODlhMWEyMTUwMWI1YjJlYTc1OTYzOTE4YjdiIn19fQ==");
    private final ItemStack saveButton = Gui.buildHeadLore("&aSave", "&7&o  Click to save and go back",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGE5OTM0MmUyYzczYTlmMzgyMjYyOGU3OTY0ODgyMzRmMjU4NDQ2ZjVhMmQ0ZDU5ZGRlNGFhODdkYjk4In19fQ==");
    private final ItemStack info = Gui.buildHead("&b&lInfo",
            new ArrayList<>(Arrays.asList(
                    " &b- &fIn order to save your edits,",
                    "   &fyou must have a result item,",
                    "   &fand at least one ingredient."
            )), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTY0MzlkMmUzMDZiMjI1NTE2YWE5YTZkMDA3YTdlNzVlZGQyZDUwMTVkMTEzYjQyZjQ0YmU2MmE1MTdlNTc0ZiJ9fX0=");
    private final ItemStack plus = Gui.buildHead("&e+",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzIzMzJiNzcwYTQ4NzQ2OTg4NjI4NTVkYTViM2ZlNDdmMTlhYjI5MWRmNzY2YjYwODNiNWY5YTBjM2M2ODQ3ZSJ9fX0=");
    private final ItemStack equals = Gui.buildHead("&e=",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzkyNzY2ZmVhNmMwNTc1MGU0MGRjODNjZDdlOTNhYjM0ODQ2ZDQ0MDkyMDk1MWRhMjYzNTk4MzZlY2YwOGY0YiJ9fX0=");
    private final ItemStack deleteButton = Gui.buildHeadLore("&4Delete", " &b- &fClick to delete this trade",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzY5NzY0NjE1ZGQ5Y2EwNTk5YmQ5ODg1ZjIyMmFhNWVhNWI0NzZiZDFiOTNlOTYyODUzNjZkMWQ0YzEifX19");
    private final ItemStack experienceEnabled = Gui.buildLore(Material.LIME_STAINED_GLASS_PANE, "Experience Reward", "Click to toggle");
    private final ItemStack experienceDisabled = Gui.buildLore(Material.RED_STAINED_GLASS_PANE, "No Experience Reward", "Click to toggle");
    private final ItemStack maxUsesStack = Gui.build(Material.LIGHT_BLUE_STAINED_GLASS_PANE, "Max Uses");
    private final ItemStack tradeNameStack = Gui.build(Material.PINK_STAINED_GLASS_PANE, "Trade Name");
    private final ItemStack ingredient1 = Gui.build(Material.STRUCTURE_VOID, "&aIngredient One",
            new ArrayList<>(Arrays.asList(
                    " &b- &7Click with an item to set",
                    " &b- &7Click without item to reset",
                    " &b- &7Required"
            )));
    private final ItemStack ingredient2 = Gui.build(Material.STRUCTURE_VOID, "&aIngredient Two",
            new ArrayList<>(Arrays.asList(
                    " &b- &7Click with an item to set",
                    " &b- &7Click without item to reset",
                    " &b- &7Optional"
            )));
    private final ItemStack resultStack = Gui.build(Material.STRUCTURE_VOID, "&aResult",
            new ArrayList<>(Arrays.asList(
                    " &b- &7Click with an item to set",
                    " &b- &7Click without item to reset",
                    " &b- &7Required"
            )));

    private final String tradeName;
    private int maxUses;
    private boolean experienceReward;
    private ItemStack i1;
    private ItemStack i2;
    private ItemStack result;

    public TradeEditGui(String tradeConfig, String tradeName) {
        super("&e&lEditing Trade&7: &f" + tradeName, 45);
        this.tradeName = tradeName;
        this.tradeConfig = tradeConfig;
        TradeConfig t = WanderingTrades.getInstance().getCfg().getTradeConfigs().get(tradeConfig);
        experienceReward = t.getFile().getBoolean("trades." + tradeName + ".experienceReward");
        maxUses = t.getFile().getInt("trades." + tradeName + ".maxUses");
        if (maxUses == 0) {
            maxUses = 1;
        }
        i1 = TradeConfig.getStack(t.getFile(), "trades." + tradeName + ".ingredients.1");
        if (i1 == null) {
            i1 = ingredient1;
        }
        i2 = TradeConfig.getStack(t.getFile(), "trades." + tradeName + ".ingredients.2");
        if (i2 == null) {
            i2 = ingredient2;
        }
        result = TradeConfig.getStack(t.getFile(), "trades." + tradeName + ".result");
        if (result == null) {
            result = resultStack;
        }
    }

    public Inventory getInventory() {
        inventory.clear();

        inventory.setItem(inventory.getSize() - 1, cancelButton);
        inventory.setItem(inventory.getSize() - 2, saveButton);

        inventory.setItem(8, info);

        TradeConfig t = WanderingTrades.getInstance().getCfg().getTradeConfigs().get(tradeConfig);

        inventory.setItem(28, i1);
        inventory.setItem(29, plus);
        inventory.setItem(30, i2);
        inventory.setItem(31, equals);
        inventory.setItem(32, result);

        inventory.setItem(35, deleteButton);

        ItemMeta tradeNameStackItemMeta = tradeNameStack.getItemMeta();
        ArrayList<String> tradeNameLore = new ArrayList<>();
        tradeNameLore.add("Value&7: &r" + tradeName);
        tradeNameStackItemMeta.setLore(TextUtil.colorize(tradeNameLore));
        tradeNameStack.setItemMeta(tradeNameStackItemMeta);
        inventory.setItem(10, tradeNameStack);

        if (experienceReward) {
            inventory.setItem(12, experienceEnabled);
        } else {
            inventory.setItem(12, experienceDisabled);
        }

        ItemMeta maxUsesMeta = maxUsesStack.getItemMeta();
        ArrayList<String> maxUsesLore = new ArrayList<>();
        maxUsesLore.add("Value&7: &b" + maxUses);
        maxUsesLore.add("  &7&oClick to edit");
        maxUsesMeta.setLore(TextUtil.colorize(maxUsesLore));
        maxUsesStack.setItemMeta(maxUsesMeta);
        inventory.setItem(14, maxUsesStack);

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

        if (deleteButton.isSimilar(item)) {
            p.closeInventory();
            new AnvilGUI.Builder()
                    .onClose(player -> {
                        new TradeListGui(tradeConfig).open(player);
                    })
                    .onComplete((player, text) -> {
                        if (text.equals("yes")) {
                            t.deleteTrade(tradeConfig, tradeName);
                            WanderingTrades.getInstance().getCfg().load();
                            return AnvilGUI.Response.close();
                        } else {
                            return AnvilGUI.Response.text("Type 'yes' to confirm");
                        }
                    })
                    .text("Type 'yes' to confirm")
                    .item(new ItemStack(Material.WRITABLE_BOOK))
                    .title("Deleting trade: " + tradeName)
                    .plugin(WanderingTrades.getInstance())
                    .open(p);
        }

        getInventory();
    }

    private void reOpen(Player player) {
        Bukkit.getServer().getScheduler().runTaskLater(WanderingTrades.getInstance(), () -> player.openInventory(getInventory()), 1L);
    }

    private ItemStack updateSlot(InventoryClickEvent event, ItemStack def) {
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
}
