package fun.ccmc.wanderingtrades.gui;

import fun.ccmc.wanderingtrades.WanderingTrades;
import fun.ccmc.wanderingtrades.config.TradeConfig;
import fun.ccmc.wanderingtrades.util.Chat;
import fun.ccmc.wanderingtrades.util.TextUtil;
import net.wesjd.anvilgui.AnvilGUI;
import org.apache.commons.io.FileUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ConfigListGui extends PaginatedGui {
    private final ItemStack newConfig = GuiManager.buildSingleLore(Material.WRITABLE_BOOK, "&aAdd config", "&7&o  Click to add a new config");
    private final ArrayList<String> configNames = new ArrayList<>();

    public ConfigListGui() {
        super("&a&lTrade Configs", 54, getConfigStacks());
        Arrays.stream(WanderingTrades.getInstance().getCfg().getTradeConfigs().keySet().toArray()).forEach(completion -> configNames.add((String) completion));
    }

    private static ArrayList<ItemStack> getConfigStacks() {
        ArrayList<ItemStack> items = new ArrayList<>();
        ArrayList<String> configs = new ArrayList<>();
        Arrays.stream(WanderingTrades.getInstance().getCfg().getTradeConfigs().keySet().toArray()).forEach(completion -> configs.add((String) completion));
        int i = 0;
        for (String config : configs) {
            TradeConfig t = WanderingTrades.getInstance().getCfg().getTradeConfigs().get(config);
            ArrayList<String> lore = new ArrayList<>();
            t.getFile().getConfigurationSection("trades").getKeys(false).forEach(key -> lore.add("&7&o  " + key));
            String[] lores = new String[lore.size()];
            for (int j = 0; j < lore.size(); j++) {
                lores[j] = lore.get(j);
            }
            ArrayList<String> finalLores = new ArrayList<>();
            for (int x = 0; x < 10; ++x) {
                try {
                    finalLores.add("&7" + lores[x]);
                } catch (ArrayIndexOutOfBoundsException ignored) {
                }
            }
            if (lores.length > 10) {
                finalLores.add("  &7&o...and " + (lores.length - 10) + " more");
            }
            items.add(GuiManager.build(Material.PAPER, "" + config, finalLores));
            i++;
        }
        return items;
    }

    public Inventory getInventory() {
        Inventory i = super.getInventory();
        i.setItem(i.getSize() - 5, newConfig);
        i.setItem(inventory.getSize() - 1, closeButton);
        return i;
    }

    public void onClick(Player p, ItemStack i) {
        if (closeButton.isSimilar(i)) {
            p.closeInventory();
        } else if (newConfig.isSimilar(i)) {
            p.closeInventory();
            new AnvilGUI.Builder()
                    .onComplete((player, text) -> {
                        if (!TextUtil.containsCaseInsensitive(text, configNames)) {
                            if (!text.contains(" ")) {
                                try {
                                    FileUtils.copyToFile(WanderingTrades.getInstance().getResource("trades/blank.yml"), new File(WanderingTrades.getInstance().getDataFolder() + "/trades/" + text + ".yml"));
                                    WanderingTrades.getInstance().getCfg().reload();
                                    Chat.sendCenteredMessage(p, "&aSuccessfully created new config and reloaded");
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                    Chat.sendCenteredMessage(p, "&4Error");
                                }
                            } else {
                                return AnvilGUI.Response.text("No spaces");
                            }
                            return AnvilGUI.Response.close();
                        } else {
                            return AnvilGUI.Response.text("Name already taken");
                        }
                    })
                    .text("Type here")
                    .item(new ItemStack(Material.WRITTEN_BOOK))
                    .title("Name the config")
                    .plugin(WanderingTrades.getInstance())
                    .open(p);
        }
        if(i != null) {
        //try {
            if (TextUtil.containsCaseInsensitive(i.getItemMeta().getDisplayName(), configNames)) {
                p.closeInventory();
                WanderingTrades.getInstance().getGuiMgr().openTradeListGui(p, i.getItemMeta().getDisplayName());
            }
        } //catch (NullPointerException ignored) {}
    }
}
