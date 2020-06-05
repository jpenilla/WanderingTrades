package fun.ccmc.wanderingtrades.gui;

import fun.ccmc.jmplib.ItemBuilder;
import fun.ccmc.wanderingtrades.WanderingTrades;
import fun.ccmc.wanderingtrades.config.Lang;
import fun.ccmc.wanderingtrades.config.PlayerHeadConfig;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;

public class PlayerHeadGui extends TradeGui {
    private final PlayerHeadConfig config = WanderingTrades.getInstance().getCfg().getPlayerHeadConfig();

    private final ItemStack enabledStack = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_PH_CONFIG_ENABLED)).setLore(gui_toggle_lore).build();
    private final ItemStack disabledStack = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_PH_CONFIG_DISABLED)).setLore(gui_toggle_lore).build();
    private final ItemStack amountStack = new ItemBuilder(Material.LIGHT_BLUE_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_PH_CONFIG_AMOUNT)).build();
    private final ItemStack amountHeadsStack = new ItemBuilder(Material.LIGHT_BLUE_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_PH_CONFIG_AMOUNT_HEADS)).build();
    private final ItemStack chanceStack = new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_PH_CONFIG_CHANCE)).build();
    private final ItemStack blacklistStack = new ItemBuilder(Material.PAPER).setName(lang.get(Lang.GUI_PH_CONFIG_BLACKLIST)).build();
    private final ItemStack loreStack = new ItemBuilder(Material.PAPER).setName(lang.get(Lang.GUI_PH_CONFIG_RESULT_LORE)).build();
    private final ItemStack notch = new ItemBuilder("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTBiNTk0MzgwMTNlYTk1MzYyYzZmMTIyNGI3YzViYjZjMjc5MmIwYjljOWNlZmQ2ZDcwODc2N2ZkOTFlYyJ9fX0=")
            .setName(config.getName().replace("{PLAYER}", "Notch"))
            .setLore(config.getLore()).setAmount(config.getPlayerHeadsFromServerAmount())
            .build();
    private final ItemStack customName = new ItemBuilder(Material.PINK_STAINED_GLASS_PANE).setName(lang.get(Lang.GUI_TC_EDIT_CUSTOM_NAME)).build();

    public PlayerHeadGui() {
        super(WanderingTrades.getInstance().getLang().get(Lang.GUI_PH_CONFIG_TITLE), null);
    }

    @Override
    public Inventory getInventory() {
        inventory.clear();
        inventory.setItem(inventory.getSize() - 1, closeButton);

        if(config.isPlayerHeadsFromServer()) {
            inventory.setItem(10, enabledStack);
        } else {
            inventory.setItem(10, disabledStack);
        }

        if(config.isExperienceReward()) {
            inventory.setItem(11, experienceEnabled);
        } else {
            inventory.setItem(11, experienceDisabled);
        }

        ItemStack a = new ItemBuilder(amountStack).setLore(lang.get(Lang.GUI_VALUE_LORE) + "&b" + config.getPlayerHeadsFromServerAmount(), lang.get(Lang.GUI_EDIT_LORE)).build();
        inventory.setItem(12, a);

        ItemStack f = new ItemBuilder(amountHeadsStack).setLore(lang.get(Lang.GUI_VALUE_LORE) + "&b" + config.getAmountOfHeadsPerTrade(), lang.get(Lang.GUI_EDIT_LORE)).build();
        inventory.setItem(13, f);

        ItemStack e = new ItemBuilder(maxUsesStack).setLore(lang.get(Lang.GUI_VALUE_LORE) + "&b" + config.getMaxUses(), lang.get(Lang.GUI_EDIT_LORE)).build();
        inventory.setItem(14, e);

        ItemStack b = new ItemBuilder(chanceStack).setLore(lang.get(Lang.GUI_VALUE_LORE) + "&b" + config.getPlayerHeadsFromServerChance(), lang.get(Lang.GUI_EDIT_LORE)).build();
        inventory.setItem(15, b);

        ItemStack g = new ItemBuilder(customName).setLore(lang.get(Lang.GUI_VALUE_LORE) + config.getName(), lang.get(Lang.GUI_EDIT_LORE)).build();
        inventory.setItem(16, g);

        ArrayList<String> resultLore = new ArrayList<>(Arrays.asList(
                lang.get(Lang.GUI_CONFIG_WG_LIST_LORE),
                "&f------------"
        ));
        resultLore.addAll(config.getLore());
        ItemStack h = new ItemBuilder(loreStack).setLore(resultLore).build();
        inventory.setItem(25, h);

        inventory.setItem(28, i1);
        inventory.setItem(29, plus);
        inventory.setItem(30, i2);
        inventory.setItem(31, equals);
        inventory.setItem(32, notch);

        ArrayList<String> blacklistLore = new ArrayList<>(Arrays.asList(
                lang.get(Lang.GUI_CONFIG_WG_LIST_LORE),
                ""
        ));
        config.getUsernameBlacklist().forEach(name -> blacklistLore.add(" &4- &f" + name));
        ItemStack d = new ItemBuilder(blacklistStack).setLore(blacklistLore).build();
        inventory.setItem(34, d);

        IntStream.range(0, inventory.getSize()).forEach(slot -> {
            if (inventory.getItem(slot) == null) {
                inventory.setItem(slot, filler);
            }
        });

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

        if (closeButton.isSimilar(item)) {
            p.closeInventory();
        }
    }
}
