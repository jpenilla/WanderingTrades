package fun.ccmc.wanderingtrades.config;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

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
    private double playerHeadsFromServerChance;
    @Getter @Setter
    private int playerHeadsFromServerAmount;
    @Getter @Setter
    private int amountOfHeadsPerTrade;
    @Getter @Setter
    private String name;
    @Getter @Setter
    private List<String> lore;
    @Getter
    private ItemStack ingredient1;
    @Getter
    private ItemStack ingredient2;
    @Getter
    private List<String> usernameBlacklist;

    private final String prefix = "headTrade.";

    public PlayerHeadConfig(FileConfiguration config) {
        this.config = config;
        load();
    }

    public void load() {
        playerHeadsFromServer = config.getBoolean(Fields.playerHeadsFromServer);
        playerHeadsFromServerChance = config.getDouble(Fields.playerHeadsFromServerChance);
        playerHeadsFromServerAmount = config.getInt(Fields.playerHeadsFromServerAmount);
        if (config.getInt(prefix + Fields.maxUses) != 0) {
            maxUses = config.getInt(prefix + Fields.maxUses);
        }
        experienceReward = config.getBoolean(prefix + Fields.experienceReward);
        ingredient1 = TradeConfig.getStack(config, prefix + "ingredients.1");
        ingredient2 = TradeConfig.getStack(config, prefix + "ingredients.2");
        amountOfHeadsPerTrade = config.getInt(prefix + "head.amount");
        name = config.getString(prefix + "head.customname");
        lore = config.getStringList(prefix + "head.lore");
        usernameBlacklist = config.getStringList(Fields.usernameBlacklist);
    }

    public void save() {
        config.set(Fields.playerHeadsFromServer, playerHeadsFromServer);
        config.set(Fields.playerHeadsFromServerChance, playerHeadsFromServerChance);
        config.set(Fields.playerHeadsFromServerAmount, playerHeadsFromServerAmount);
        config.set(Fields.usernameBlacklist, usernameBlacklist);
        config.set(prefix + Fields.maxUses, maxUses);
        config.set(prefix + Fields.experienceReward, experienceReward);
        config.set(prefix + "ingredients.1", ingredient1.serialize());
        if(ingredient2 != null) {
            config.set(prefix + "ingredients.2", ingredient2.serialize());
        } else {
            config.set(prefix + "ingredients.2", null);
        }
        config.set(prefix + "head.amount", amountOfHeadsPerTrade);
        config.set(prefix + "head.customname", name);
        config.set(prefix + "head.lore", lore);
    }
}
