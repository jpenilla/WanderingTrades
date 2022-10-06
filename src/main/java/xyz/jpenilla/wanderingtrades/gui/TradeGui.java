package xyz.jpenilla.wanderingtrades.gui;

import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.jpenilla.pluginbase.legacy.HeadBuilder;
import xyz.jpenilla.pluginbase.legacy.InputConversation;
import xyz.jpenilla.pluginbase.legacy.ItemBuilder;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Lang;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;

public abstract class TradeGui extends GuiHolder {

    private final ItemStack cancelButton = new HeadBuilder("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTZjNjBkYTQxNGJmMDM3MTU5YzhiZThkMDlhOGVjYjkxOWJmODlhMWEyMTUwMWI1YjJlYTc1OTYzOTE4YjdiIn19fQ==")
        .setName(lang.get(Lang.GUI_TRADE_CANCEL)).setLore(lang.get(Lang.GUI_TRADE_CANCEL_LORE)).build();
    private final ItemStack saveButton = new HeadBuilder("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGE5OTM0MmUyYzczYTlmMzgyMjYyOGU3OTY0ODgyMzRmMjU4NDQ2ZjVhMmQ0ZDU5ZGRlNGFhODdkYjk4In19fQ==")
        .setName(lang.get(Lang.GUI_TRADE_SAVE)).setLore(lang.get(Lang.GUI_TRADE_SAVE_LORE)).build();
    private final ItemStack info = new HeadBuilder("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTY0MzlkMmUzMDZiMjI1NTE2YWE5YTZkMDA3YTdlNzVlZGQyZDUwMTVkMTEzYjQyZjQ0YmU2MmE1MTdlNTc0ZiJ9fX0=")
        .setName(lang.get(Lang.GUI_TRADE_INFO)).setLore(lang.getList(Lang.GUI_TRADE_INFO_LORE)).build();
    private final ItemStack plus = new HeadBuilder("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzIzMzJiNzcwYTQ4NzQ2OTg4NjI4NTVkYTViM2ZlNDdmMTlhYjI5MWRmNzY2YjYwODNiNWY5YTBjM2M2ODQ3ZSJ9fX0=")
        .setName("<color:blue>+").build();
    private final ItemStack equals = new HeadBuilder("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzkyNzY2ZmVhNmMwNTc1MGU0MGRjODNjZDdlOTNhYjM0ODQ2ZDQ0MDkyMDk1MWRhMjYzNTk4MzZlY2YwOGY0YiJ9fX0=")
        .setName("<color:yellow>=").build();
    private final ItemStack deleteButton = new HeadBuilder("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzY5NzY0NjE1ZGQ5Y2EwNTk5YmQ5ODg1ZjIyMmFhNWVhNWI0NzZiZDFiOTNlOTYyODUzNjZkMWQ0YzEifX19")
        .setName(lang.get(Lang.GUI_TRADE_DELETE)).setLore(lang.get(Lang.GUI_TRADE_DELETE_LORE)).build();
    private final ItemStack experienceEnabled = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_TRADE_EXP_REWARD)).setLore(gui_toggle_lore).build();
    private final ItemStack experienceDisabled = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_TRADE_NO_EXP_REWARD)).setLore(gui_toggle_lore).build();
    private final ItemStack maxUsesStack = new ItemBuilder(Material.LIGHT_BLUE_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_TRADE_MAX_USES)).build();
    private final ItemStack tradeNameStack = new ItemBuilder(Material.PINK_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_TRADE_TRADE_NAME)).build();
    private final ItemStack ingredient1 = new ItemBuilder(Material.STRUCTURE_VOID).setName(lang.get(Lang.GUI_TRADE_INGREDIENT_1)).setLore(lang.getList(Lang.GUI_TRADE_REQUIRED_LORE)).build();
    private final ItemStack ingredient2 = new ItemBuilder(Material.STRUCTURE_VOID).setName(lang.get(Lang.GUI_TRADE_INGREDIENT_2)).setLore(lang.getList(Lang.GUI_TRADE_OPTIONAL_LORE)).build();
    private final ItemStack resultStack = new ItemBuilder(Material.STRUCTURE_VOID).setName(lang.get(Lang.GUI_TRADE_RESULT)).setLore(lang.getList(Lang.GUI_TRADE_REQUIRED_LORE)).build();

    private String tradeName = null;
    private int maxUses = 1;
    private boolean experienceReward = true;
    private ItemStack i1 = ingredient1;
    private ItemStack i2 = ingredient2;
    private ItemStack result = resultStack;

    protected final TradeConfig tradeConfig;

    public TradeGui(String name, TradeConfig tradeConfig) {
        super(name, 45);
        this.tradeConfig = tradeConfig;
    }

    @Override
    public @NonNull Inventory getInventory() {
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

        ArrayList<String> maxUsesLore = new ArrayList<>();
        maxUsesLore.add(lang.get(Lang.GUI_VALUE_LORE) + "<color:#0092FF>" + maxUses);
        maxUsesLore.add(lang.get(Lang.GUI_EDIT_LORE));
        inventory.setItem(14, new ItemBuilder(maxUsesStack).setLore(maxUsesLore).build());

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
            new InputConversation()
                .onPromptText(player -> {
                    WanderingTrades.instance().chat().sendParsed(player,
                        lang.get(Lang.MESSAGE_SET_MAX_USES_PROMPT)
                            + "<reset>\n" + lang.get(Lang.MESSAGE_CURRENT_VALUE) + maxUses
                            + "<reset>\n" + lang.get(Lang.MESSAGE_ENTER_NUMBER));
                    return "";
                })
                .onValidateInput(this::onValidateIntGT0)
                .onConfirmText(this::onConfirmYesNo)
                .onAccepted((player, s) -> {
                    maxUses = Integer.parseInt(s);
                    open(player);
                })
                .onDenied(this::onEditCancelled)
                .start(p);
        }

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
                tradeConfig.writeTrade(tradeName, maxUses, experienceReward, i1, ingred2, result);
                WanderingTrades.instance().config().load();
                p.closeInventory();
                new TradeListGui(tradeConfig).open(p);
            }
        }

        onClick(event);

        getInventory();
    }

    public abstract void onClick(InventoryClickEvent e);

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
        player.openInventory(getInventory());
    }

    public ItemStack getCancelButton() {
        return this.cancelButton;
    }

    public ItemStack getSaveButton() {
        return this.saveButton;
    }

    public ItemStack getInfo() {
        return this.info;
    }

    public ItemStack getPlus() {
        return this.plus;
    }

    public ItemStack getEquals() {
        return this.equals;
    }

    public ItemStack getDeleteButton() {
        return this.deleteButton;
    }

    public ItemStack getExperienceEnabled() {
        return this.experienceEnabled;
    }

    public ItemStack getExperienceDisabled() {
        return this.experienceDisabled;
    }

    public ItemStack getMaxUsesStack() {
        return this.maxUsesStack;
    }

    public ItemStack getTradeNameStack() {
        return this.tradeNameStack;
    }

    public ItemStack getIngredient1() {
        return this.ingredient1;
    }

    public ItemStack getIngredient2() {
        return this.ingredient2;
    }

    public ItemStack getResultStack() {
        return this.resultStack;
    }

    public String getTradeName() {
        return this.tradeName;
    }

    public int getMaxUses() {
        return this.maxUses;
    }

    public boolean isExperienceReward() {
        return this.experienceReward;
    }

    public ItemStack getI1() {
        return this.i1;
    }

    public ItemStack getI2() {
        return this.i2;
    }

    public ItemStack getResult() {
        return this.result;
    }

    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }

    public void setMaxUses(int maxUses) {
        this.maxUses = maxUses;
    }

    public void setExperienceReward(boolean experienceReward) {
        this.experienceReward = experienceReward;
    }

    public void setI1(ItemStack i1) {
        this.i1 = i1;
    }

    public void setI2(ItemStack i2) {
        this.i2 = i2;
    }

    public void setResult(ItemStack result) {
        this.result = result;
    }
}
