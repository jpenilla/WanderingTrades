package xyz.jpenilla.wanderingtrades.config;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntSupplier;
import java.util.logging.Level;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

public final class PlayerHeadConfig extends DefaultedConfig {
    private static final String HEAD_TRADE_PREFIX = "headTrade.";

    private final File file;
    private final FileConfiguration config;

    private int maxUses = 1;
    private boolean experienceReward;
    private boolean playerHeadsFromServer;
    private boolean permissionWhitelist;
    private double playerHeadsFromServerChance;
    private String playerHeadsFromServerAmount;
    private IntSupplier playerHeadsFromServerAmountFunction;
    private int headsPerTrade;
    private String name;
    private List<String> lore;
    private ItemStack ingredient1;
    private ItemStack ingredient2;
    private List<String> usernameBlacklist;
    private int days;

    private PlayerHeadConfig(final @NonNull WanderingTrades plugin, final @NonNull File file) {
        super(plugin, "playerheads.yml");
        this.file = file;
        this.config = YamlConfiguration.loadConfiguration(file);
        this.config.setDefaults(this.defaultConfig);
        this.reload();
    }

    public void reload() {
        this.load();
        this.save();
    }

    @Override
    protected FileConfiguration config() {
        return this.config;
    }

    public void load() {
        try {
            this.config.load(this.file);
        } catch (final IOException | InvalidConfigurationException ex) {
            throw new RuntimeException("Failed to load config", ex);
        }

        this.playerHeadsFromServer = this.config.getBoolean(Fields.playerHeadsFromServer);
        this.playerHeadsFromServerChance = this.config.getDouble(Fields.playerHeadsFromServerChance);
        this.playerHeadsFromServerAmount = this.config.getString(Fields.playerHeadsFromServerAmount);
        this.playerHeadsFromServerAmountFunction = randAmountFunction(this.playerHeadsFromServerAmount);
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
        this.set(Fields.playerHeadsFromServer, this.playerHeadsFromServer);
        this.set(Fields.playerHeadsFromServerChance, this.playerHeadsFromServerChance);
        this.set(Fields.playerHeadsFromServerAmount, this.playerHeadsFromServerAmount);
        this.set(Fields.days, this.days);
        this.set(Fields.usernameBlacklist, this.usernameBlacklist);
        this.set(Fields.permissionWhitelist, this.permissionWhitelist);
        this.set(HEAD_TRADE_PREFIX + Fields.maxUses, this.maxUses);
        this.set(HEAD_TRADE_PREFIX + Fields.experienceReward, this.experienceReward);
        ItemStackSerialization.writeOrRemove(this.config, HEAD_TRADE_PREFIX + "ingredients.1", this.ingredient1);
        ItemStackSerialization.writeOrRemove(this.config, HEAD_TRADE_PREFIX + "ingredients.2", this.ingredient2);
        this.set(HEAD_TRADE_PREFIX + "head.amount", this.headsPerTrade);
        this.set(HEAD_TRADE_PREFIX + "head.customname", this.name);
        this.set(HEAD_TRADE_PREFIX + "head.lore", this.lore);

        try {
            this.config.save(this.file);
        } catch (final IOException ex) {
            this.plugin.getLogger().log(Level.WARNING, "Failed to save config", ex);
        }
    }

    private static IntSupplier randAmountFunction(final @Nullable String playerHeadsFromServerAmount) {
        Objects.requireNonNull(playerHeadsFromServerAmount, "Missing playerHeadsFromServerAmount config");
        if (playerHeadsFromServerAmount.contains(":")) {
            final String[] intStrings = playerHeadsFromServerAmount.split(":");
            final int i0 = Integer.parseInt(intStrings[0]);
            final int i1 = Integer.parseInt(intStrings[1]);
            return () -> ThreadLocalRandom.current().nextInt(i0, i1);
        } else {
            final int value = Integer.parseInt(playerHeadsFromServerAmount);
            return () -> value;
        }
    }

    public int getRandAmount() {
        return this.playerHeadsFromServerAmountFunction.getAsInt();
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

    public static PlayerHeadConfig load(final WanderingTrades plugin) {
        final File file = new File(plugin.getDataFolder(), "playerheads.yml");
        if (!file.exists()) {
            plugin.saveResource("playerheads.yml", false);
        }
        return new PlayerHeadConfig(plugin, file);
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
