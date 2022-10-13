package xyz.jpenilla.wanderingtrades.gui;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import xyz.jpenilla.pluginbase.legacy.HeadBuilder;
import xyz.jpenilla.pluginbase.legacy.InputConversation;
import xyz.jpenilla.pluginbase.legacy.ItemBuilder;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Lang;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;

public abstract class TradeGui extends BaseGui {
    private final ItemStack cancelButton = new HeadBuilder("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTZjNjBkYTQxNGJmMDM3MTU5YzhiZThkMDlhOGVjYjkxOWJmODlhMWEyMTUwMWI1YjJlYTc1OTYzOTE4YjdiIn19fQ==")
        .setName(this.lang.get(Lang.GUI_TRADE_CANCEL))
        .setLore(this.lang.get(Lang.GUI_TRADE_CANCEL_LORE))
        .build();
    private final ItemStack saveButton = new HeadBuilder("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGE5OTM0MmUyYzczYTlmMzgyMjYyOGU3OTY0ODgyMzRmMjU4NDQ2ZjVhMmQ0ZDU5ZGRlNGFhODdkYjk4In19fQ==")
        .setName(this.lang.get(Lang.GUI_TRADE_SAVE))
        .setLore(this.lang.get(Lang.GUI_TRADE_SAVE_LORE))
        .build();
    private final ItemStack info = new HeadBuilder("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTY0MzlkMmUzMDZiMjI1NTE2YWE5YTZkMDA3YTdlNzVlZGQyZDUwMTVkMTEzYjQyZjQ0YmU2MmE1MTdlNTc0ZiJ9fX0=")
        .setName(this.lang.get(Lang.GUI_TRADE_INFO))
        .setLore(this.lang.getList(Lang.GUI_TRADE_INFO_LORE))
        .build();
    private final ItemStack plus = new HeadBuilder("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzIzMzJiNzcwYTQ4NzQ2OTg4NjI4NTVkYTViM2ZlNDdmMTlhYjI5MWRmNzY2YjYwODNiNWY5YTBjM2M2ODQ3ZSJ9fX0=")
        .setName("<blue>+")
        .build();
    private final ItemStack equals = new HeadBuilder("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzkyNzY2ZmVhNmMwNTc1MGU0MGRjODNjZDdlOTNhYjM0ODQ2ZDQ0MDkyMDk1MWRhMjYzNTk4MzZlY2YwOGY0YiJ9fX0=")
        .setName("<yellow>=")
        .build();
    private final ItemStack deleteButton = new HeadBuilder("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzY5NzY0NjE1ZGQ5Y2EwNTk5YmQ5ODg1ZjIyMmFhNWVhNWI0NzZiZDFiOTNlOTYyODUzNjZkMWQ0YzEifX19")
        .setName(this.lang.get(Lang.GUI_TRADE_DELETE))
        .setLore(this.lang.get(Lang.GUI_TRADE_DELETE_LORE))
        .build();
    private final ItemStack experienceEnabled = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
        .setName(this.lang.get(Lang.GUI_TRADE_EXP_REWARD))
        .setLore(this.toggleLore)
        .build();
    private final ItemStack experienceDisabled = new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
        .setName(this.lang.get(Lang.GUI_TRADE_NO_EXP_REWARD))
        .setLore(this.toggleLore)
        .build();
    private final ItemStack maxUsesStack = new ItemBuilder(Material.LIGHT_BLUE_STAINED_GLASS_PANE)
        .setName(this.lang.get(Lang.GUI_TRADE_MAX_USES))
        .build();
    private final ItemStack tradeNameStack = new ItemBuilder(Material.PINK_STAINED_GLASS_PANE)
        .setName(this.lang.get(Lang.GUI_TRADE_TRADE_NAME))
        .build();
    private final ItemStack emptyIngredient1 = new ItemBuilder(Material.STRUCTURE_VOID)
        .setName(this.lang.get(Lang.GUI_TRADE_INGREDIENT_1))
        .setLore(this.lang.getList(Lang.GUI_TRADE_REQUIRED_LORE))
        .build();
    private final ItemStack emptyIngredient2 = new ItemBuilder(Material.STRUCTURE_VOID)
        .setName(this.lang.get(Lang.GUI_TRADE_INGREDIENT_2))
        .setLore(this.lang.getList(Lang.GUI_TRADE_OPTIONAL_LORE))
        .build();
    private final ItemStack emptyResult = new ItemBuilder(Material.STRUCTURE_VOID)
        .setName(this.lang.get(Lang.GUI_TRADE_RESULT))
        .setLore(this.lang.getList(Lang.GUI_TRADE_REQUIRED_LORE))
        .build();
    protected final TradeConfig tradeConfig;
    private String tradeName = null;
    private int maxUses = 1;
    private boolean experienceReward = true;
    private ItemStack i1 = this.emptyIngredient1;
    private ItemStack i2 = this.emptyIngredient2;
    private ItemStack result = this.emptyResult;

    public TradeGui(final WanderingTrades plugin, final String name, final TradeConfig tradeConfig) {
        super(plugin, name, 45);
        this.tradeConfig = tradeConfig;
    }

    @Override
    public @NonNull Inventory getInventory() {
        this.inventory.clear();

        this.inventory.setItem(this.inventory.getSize() - 1, this.cancelButton);
        this.inventory.setItem(this.inventory.getSize() - 2, this.saveButton);

        this.inventory.setItem(8, this.info);

        this.inventory.setItem(28, this.i1);
        this.inventory.setItem(29, this.plus);
        this.inventory.setItem(30, this.i2);
        this.inventory.setItem(31, this.equals);
        this.inventory.setItem(32, this.result);

        this.inventory.setItem(12, this.experienceReward ? this.experienceEnabled : this.experienceDisabled);

        final List<String> maxUsesLore = new ArrayList<>();
        maxUsesLore.add(this.lang.get(Lang.GUI_VALUE_LORE) + "<#0092FF>" + this.maxUses);
        maxUsesLore.add(this.lang.get(Lang.GUI_EDIT_LORE));
        this.inventory.setItem(14, new ItemBuilder(this.maxUsesStack).setLore(maxUsesLore).build());

        return this.inventory;
    }

    @Override
    public void onInventoryClick(final InventoryClickEvent event) {
        final @Nullable ItemStack item = event.getCurrentItem();
        final Player p = (Player) event.getWhoClicked();

        if (this.cancelButton.isSimilar(item)) {
            p.closeInventory();
            new TradeListGui(this.plugin, this.tradeConfig).open(p);
            return;
        }

        if (this.experienceEnabled.isSimilar(item)) {
            this.experienceReward = false;
        } else if (this.experienceDisabled.isSimilar(item)) {
            this.experienceReward = true;
        }

        if (this.maxUsesStack.isSimilar(item)) {
            this.maxUsesClick(p);
        }

        int rS = event.getRawSlot();
        if (rS == 28) {
            this.i1 = this.updateSlot(event, this.emptyIngredient1);
        } else if (rS == 30) {
            this.i2 = this.updateSlot(event, this.emptyIngredient2);
        } else if (rS == 32) {
            this.result = this.updateSlot(event, this.emptyResult);
        }

        if (this.saveButton.isSimilar(item)) {
            this.saveClick(p);
        }

        this.onClick(event);

        this.getInventory();
    }

    private void maxUsesClick(final Player p) {
        p.closeInventory();
        new InputConversation()
            .onPromptText(player -> {
                this.plugin.chat().sendParsed(player,
                    this.lang.get(Lang.MESSAGE_SET_MAX_USES_PROMPT)
                        + "<reset>\n" + this.lang.get(Lang.MESSAGE_CURRENT_VALUE) + this.maxUses
                        + "<reset>\n" + this.lang.get(Lang.MESSAGE_ENTER_NUMBER));
                return "";
            })
            .onValidateInput(this::validateIntGT0)
            .onConfirmText(this::confirmYesNo)
            .onAccepted((player, s) -> {
                this.maxUses = Integer.parseInt(s);
                this.open(player);
            })
            .onDenied(this::editCancelled)
            .start(p);
    }

    private void saveClick(final Player player) {
        if (this.tradeName != null && !this.result.equals(this.emptyResult) && !this.i1.equals(this.emptyIngredient1)) {
            @Nullable ItemStack ingred2 = null;
            if (!this.i2.equals(this.emptyIngredient2)) {
                ingred2 = this.i2;
            }
            this.tradeConfig.writeTrade(this.tradeName, this.maxUses, this.experienceReward, this.i1, ingred2, this.result);
            this.plugin.config().load();
            player.closeInventory();
            new TradeListGui(this.plugin, this.tradeConfig).open(player);
        }
    }

    public abstract void onClick(InventoryClickEvent e);

    public ItemStack updateSlot(final InventoryClickEvent event, final ItemStack def) {
        @Nullable ItemStack cursor = event.getCursor();
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

    @Override
    public void reOpen(final Player player) {
        player.openInventory(this.getInventory());
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

    public ItemStack emptyIngredient1() {
        return this.emptyIngredient1;
    }

    public ItemStack emptyIngredient2() {
        return this.emptyIngredient2;
    }

    public ItemStack emptyResult() {
        return this.emptyResult;
    }

    public String getTradeName() {
        return this.tradeName;
    }

    public void setTradeName(final String tradeName) {
        this.tradeName = tradeName;
    }

    public int getMaxUses() {
        return this.maxUses;
    }

    public void setMaxUses(final int maxUses) {
        this.maxUses = maxUses;
    }

    public boolean isExperienceReward() {
        return this.experienceReward;
    }

    public void setExperienceReward(final boolean experienceReward) {
        this.experienceReward = experienceReward;
    }

    public ItemStack getI1() {
        return this.i1;
    }

    public void setI1(final ItemStack i1) {
        this.i1 = i1;
    }

    public ItemStack getI2() {
        return this.i2;
    }

    public void setI2(final ItemStack i2) {
        this.i2 = i2;
    }

    public ItemStack getResult() {
        return this.result;
    }

    public void setResult(final ItemStack result) {
        this.result = result;
    }
}
