package fun.ccmc.wanderingtrades.gui;

import fun.ccmc.wanderingtrades.WanderingTrades;
import fun.ccmc.wanderingtrades.config.TradeConfig;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;

public class ConfigListGui extends GuiBuilder {
    public ConfigListGui() {
        super("&e&lTrade Configs", 36);
    }

    public void addItems() {
        ArrayList<String> configs = new ArrayList<>();
        Arrays.stream(WanderingTrades.getInstance().getCfg().getTradeConfigs().keySet().toArray()).forEach(completion -> configs.add((String) completion));
        int i = 0;
        for(String config : configs) {
            TradeConfig t = WanderingTrades.getInstance().getCfg().getTradeConfigs().get(config);
            ArrayList<String> lore = new ArrayList<>();
            t.getFile().getConfigurationSection("trades").getKeys(false).forEach(key -> {
                lore.add("&7&o  " + key);
            });
            getGui().setItem(i, build(Material.PAPER, "&a" + config, lore));
            i++;
        }
        getGui().setItem(35, build(Material.BARRIER, "&4Close"));
    }
}
