package xyz.jpenilla.wanderingtrades.config;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class PlayerHeadConfig {
    private static final String HEAD_TRADE_PREFIX = "headTrade.";

    private final File file;
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

    private PlayerHeadConfig(final @NonNull File file) {
        this.file = file;
        this.config = YamlConfiguration.loadConfiguration(file);
        this.load();
    }

    public void load() {
        this.playerHeadsFromServer = this.config.getBoolean(Fields.playerHeadsFromServer);
        this.playerHeadsFromServerChance = this.config.getDouble(Fields.playerHeadsFromServerChance);
        this.playerHeadsFromServerAmount = this.config.getString(Fields.playerHeadsFromServerAmount);
        this.days = this.config.getInt(Fields.days);
        if (this.config.getInt(HEAD_TRADE_PREFIX + Fields.maxUses) != 0) {
            this.maxUses = this.config.getInt(HEAD_TRADE_PREFIX + Fields.maxUses);
        }
        this.experienceReward = this.config.getBoolean(HEAD_TRADE_PREFIX + Fields.experienceReward);
        this.ingredient1 = ItemStackSerialization.read(this.config, HEAD_TRADE_PREFIX + "ingredients.1");
        this.ingredient2 = ItemStackSerialization.read(this.config, HEAD_TRADE_PREFIX + "ingredients.2");
        this.headsPerTrade = this.config.getInt(HEAD_TRADE_PREFIX + "head.amount");
        this.name = this.config.getString(HEAD_TRADE_PREFIX + "head.customname");
        this.lore = this.config.getStringList(HEAD_TRADE_PREFIX + "head.lore");
        this.usernameBlacklist = this.config.getStringList(Fields.usernameBlacklist);
        this.permissionWhitelist = this.config.getBoolean(Fields.permissionWhitelist);
    }

    public void save() {
        this.config.set(Fields.playerHeadsFromServer, this.playerHeadsFromServer);
        this.config.set(Fields.playerHeadsFromServerChance, this.playerHeadsFromServerChance);
        this.config.set(Fields.playerHeadsFromServerAmount, this.playerHeadsFromServerAmount);
        this.config.set(Fields.days, this.days);
        this.config.set(Fields.usernameBlacklist, this.usernameBlacklist);
        this.config.set(Fields.permissionWhitelist, this.permissionWhitelist);
        this.config.set(HEAD_TRADE_PREFIX + Fields.maxUses, this.maxUses);
        this.config.set(HEAD_TRADE_PREFIX + Fields.experienceReward, this.experienceReward);
        ItemStackSerialization.writeOrRemove(this.config, HEAD_TRADE_PREFIX + "ingredients.1", this.ingredient1);
        ItemStackSerialization.writeOrRemove(this.config, HEAD_TRADE_PREFIX + "ingredients.2", this.ingredient2);
        this.config.set(HEAD_TRADE_PREFIX + "head.amount", this.headsPerTrade);
        this.config.set(HEAD_TRADE_PREFIX + "head.customname", this.name);
        this.config.set(HEAD_TRADE_PREFIX + "head.lore", this.lore);

        try {
            this.config.save(this.file);
        } catch (final IOException e) {
            e.printStackTrace();
        }

        try {
            this.config.load(this.file);
        } catch (final IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        this.load();
    }

    public int getRandAmount() {
        if (this.playerHeadsFromServerAmount.contains(":")) {
            String[] ints = this.playerHeadsFromServerAmount.split(":");
            return ThreadLocalRandom.current().nextInt(Integer.parseInt(ints[0]), Integer.parseInt(ints[1]));
        } else {
            return Integer.parseInt(this.playerHeadsFromServerAmount);
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

    public static PlayerHeadConfig load(final File file) {
        return new PlayerHeadConfig(file);
    }

    private static final class Fields {
        public static final String maxUses = "maxUses";
        public static final String experienceReward = "experienceReward";
        public static final String playerHeadsFromServer = "playerHeadsFromServer";
        public static final String permissionWhitelist = "permissionWhitelist";
        public static final String playerHeadsFromServerChance = "playerHeadsFromServerChance";
        public static final String playerHeadsFromServerAmount = "playerHeadsFromServerAmount";
        public static final String usernameBlacklist = "usernameBlacklist";
        public static final String days = "days";
    }
}
