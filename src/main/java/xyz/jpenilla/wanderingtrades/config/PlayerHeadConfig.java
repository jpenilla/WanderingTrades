package xyz.jpenilla.wanderingtrades.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PlayerHeadConfig {
    private final FileConfiguration config;

    private int maxUses = 1;
    private boolean experienceReward;
    private boolean playerHeadsFromServer;
    private boolean permissionWhitelist;
    private double playerHeadsFromServerChance;
    private String playerHeadsFromServerAmount;
    private int headsPerTrade;
    private String name;
    private List<String> lore;
    private ItemStack ingredient1;
    private ItemStack ingredient2;
    private List<String> usernameBlacklist;
    private int days;

    private final String prefix = "headTrade.";

    public PlayerHeadConfig(FileConfiguration config) {
        this.config = config;
        load();
    }

    public void load() {
        playerHeadsFromServer = config.getBoolean(Fields.playerHeadsFromServer);
        playerHeadsFromServerChance = config.getDouble(Fields.playerHeadsFromServerChance);
        playerHeadsFromServerAmount = config.getString(Fields.playerHeadsFromServerAmount);
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

        String path = WanderingTrades.instance().getDataFolder() + "/playerheads.yml";
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

    public int getRandAmount() {
        if (playerHeadsFromServerAmount.contains(":")) {
            String[] ints = playerHeadsFromServerAmount.split(":");
            return ThreadLocalRandom.current().nextInt(Integer.parseInt(ints[0]), Integer.parseInt(ints[1]));
        } else {
            return Integer.parseInt(playerHeadsFromServerAmount);
        }
    }

    public int maxUses() {
        return this.maxUses;
    }

    public boolean experienceReward() {
        return this.experienceReward;
    }

    public boolean playerHeadsFromServer() {
        return this.playerHeadsFromServer;
    }

    public boolean permissionWhitelist() {
        return this.permissionWhitelist;
    }

    public double playerHeadsFromServerChance() {
        return this.playerHeadsFromServerChance;
    }

    public String playerHeadsFromServerAmount() {
        return this.playerHeadsFromServerAmount;
    }

    public int headsPerTrade() {
        return this.headsPerTrade;
    }

    public String name() {
        return this.name;
    }

    public List<String> lore() {
        return this.lore;
    }

    public ItemStack ingredientOne() {
        return this.ingredient1;
    }

    public ItemStack ingredientTwo() {
        return this.ingredient2;
    }

    public List<String> usernameBlacklist() {
        return this.usernameBlacklist;
    }

    public int days() {
        return this.days;
    }

    public void maxUses(int maxUses) {
        this.maxUses = maxUses;
    }

    public void experienceReward(boolean experienceReward) {
        this.experienceReward = experienceReward;
    }

    public void playerHeadsFromServer(boolean playerHeadsFromServer) {
        this.playerHeadsFromServer = playerHeadsFromServer;
    }

    public void permissionWhitelist(boolean permissionWhitelist) {
        this.permissionWhitelist = permissionWhitelist;
    }

    public void playerHeadsFromServerChance(double playerHeadsFromServerChance) {
        this.playerHeadsFromServerChance = playerHeadsFromServerChance;
    }

    public void playerHeadsFromServerAmount(String playerHeadsFromServerAmount) {
        this.playerHeadsFromServerAmount = playerHeadsFromServerAmount;
    }

    public void headsPerTrade(int headsPerTrade) {
        this.headsPerTrade = headsPerTrade;
    }

    public void name(String name) {
        this.name = name;
    }

    public void lore(List<String> lore) {
        this.lore = lore;
    }

    public void ingredientOne(ItemStack ingredient1) {
        this.ingredient1 = ingredient1;
    }

    public void ingredientTwo(ItemStack ingredient2) {
        this.ingredient2 = ingredient2;
    }

    public void usernameBlacklist(List<String> usernameBlacklist) {
        this.usernameBlacklist = usernameBlacklist;
    }

    public void days(int days) {
        this.days = days;
    }

    public static final class Fields {
        public static final String config = "config";
        public static final String maxUses = "maxUses";
        public static final String experienceReward = "experienceReward";
        public static final String playerHeadsFromServer = "playerHeadsFromServer";
        public static final String permissionWhitelist = "permissionWhitelist";
        public static final String playerHeadsFromServerChance = "playerHeadsFromServerChance";
        public static final String playerHeadsFromServerAmount = "playerHeadsFromServerAmount";
        public static final String headsPerTrade = "headsPerTrade";
        public static final String name = "name";
        public static final String lore = "lore";
        public static final String ingredient1 = "ingredient1";
        public static final String ingredient2 = "ingredient2";
        public static final String usernameBlacklist = "usernameBlacklist";
        public static final String days = "days";
        public static final String prefix = "prefix";
    }
}
