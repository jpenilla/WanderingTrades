package xyz.jpenilla.wanderingtrades.config;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

import java.io.IOException;
import java.util.List;

@FieldNameConstants
public class PlayerHeadConfig {
    private final FileConfiguration config;

    @Getter @Setter
    private int maxUses = 1;
    @Getter @Setter
    private boolean experienceReward;
    @Getter @Setter
    private boolean playerHeadsFromServer;
    @Getter @Setter
    private boolean permissionWhitelist;
    @Getter @Setter
    private double playerHeadsFromServerChance;
    @Getter @Setter
    private int playerHeadsFromServerAmount;
    @Getter @Setter
    private int headsPerTrade;
    @Getter @Setter
    private String name;
    @Getter @Setter
    private List<String> lore;
    @Getter @Setter
    private ItemStack ingredient1;
    @Getter @Setter
    private ItemStack ingredient2;
    @Getter @Setter
    private List<String> usernameBlacklist;
    @Getter @Setter
    private int days;

    private final String prefix = "headTrade.";

    public PlayerHeadConfig(FileConfiguration config) {
        this.config = config;
        load();
    }

    public void load() {
        playerHeadsFromServer = config.getBoolean(Fields.playerHeadsFromServer);
        playerHeadsFromServerChance = config.getDouble(Fields.playerHeadsFromServerChance);
        playerHeadsFromServerAmount = config.getInt(Fields.playerHeadsFromServerAmount);
        days = config.getInt(Fields.days);
        if (config.getInt(prefix + Fields.maxUses) != 0) {
            maxUses = config.getInt(prefix + Fields.maxUses);
        }
        experienceReward = config.getBoolean(prefix + Fields.experienceReward);
        ingredient1 = TradeConfig.getStack(config, prefix + "ingredients.1");
        ingredient2 = TradeConfig.getStack(config, prefix + "ingredients.2");
        headsPerTrade = config.getInt(prefix + "head.amount");
        name = config.getString(prefix + "head.customname");
        lore = config.getStringList(prefix + "head.lore");
        usernameBlacklist = config.getStringList(Fields.usernameBlacklist);
        permissionWhitelist = config.getBoolean(Fields.permissionWhitelist);
    }

    public void save() {
        config.set(Fields.playerHeadsFromServer, playerHeadsFromServer);
        config.set(Fields.playerHeadsFromServerChance, playerHeadsFromServerChance);
        config.set(Fields.playerHeadsFromServerAmount, playerHeadsFromServerAmount);
        config.set(Fields.days, days);
        config.set(Fields.usernameBlacklist, usernameBlacklist);
        config.set(Fields.permissionWhitelist, permissionWhitelist);
        config.set(prefix + Fields.maxUses, maxUses);
        config.set(prefix + Fields.experienceReward, experienceReward);
        config.set(prefix + "ingredients.1.itemStack", ingredient1.serialize());
        if (ingredient2 != null) {
            config.set(prefix + "ingredients.2.itemStack", ingredient2.serialize());
        } else {
            config.set(prefix + "ingredients.2", null);
        }
        config.set(prefix + "head.amount", headsPerTrade);
        config.set(prefix + "head.customname", name);
        config.set(prefix + "head.lore", lore);

        String path = WanderingTrades.getInstance().getDataFolder() + "/playerheads.yml";
        try {
            config.save(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            config.load(path);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        load();
    }
}
