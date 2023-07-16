package xyz.jpenilla.wanderingtrades.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.logging.Level;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

public final class TradeConfig {
    private static final String TRADES = "trades";

    private final WanderingTrades plugin;
    private final File file;
    private final String configName;
    private final FileConfiguration config;
    private boolean randomized;
    private boolean enabled;
    private String randomAmount;
    private volatile Map<String, MerchantRecipe> allTrades;
    private double chance;
    private boolean invincible;
    private String customName;
    private boolean disableHeroOfTheVillageGifts;

    private TradeConfig(
        final WanderingTrades plugin,
        final File file
    ) {
        this.plugin = plugin;
        this.file = file;
        this.configName = file.getName().split("\\.")[0];
        this.config = YamlConfiguration.loadConfiguration(file);
        this.load();
    }

    public void load() {
        this.readTrades();
        this.randomized = this.config.getBoolean(Fields.randomized);
        this.randomAmount = this.config.getString(Fields.randomAmount);
        this.enabled = this.config.getBoolean(Fields.enabled);
        this.chance = this.config.getDouble(Fields.chance);
        this.invincible = this.config.getBoolean(Fields.invincible, false);
        this.customName = this.config.getString(Fields.customName, null);
        this.disableHeroOfTheVillageGifts = this.config.getBoolean(Fields.disableHeroOfTheVillageGifts, false);
    }

    public void save() {
        this.config.set(Fields.enabled, this.enabled);
        this.config.set(Fields.randomized, this.randomized);
        this.config.set(Fields.invincible, this.invincible);
        this.config.set(Fields.randomAmount, this.randomAmount);
        this.config.set(Fields.chance, this.chance);
        this.config.set(Fields.customName, this.customName);
        this.config.set(Fields.disableHeroOfTheVillageGifts, this.disableHeroOfTheVillageGifts);

        try {
            this.config.save(this.file);
        } catch (final IOException ex) {
            this.plugin.getLogger().log(Level.WARNING, String.format("Failed to save trade config: '%s'", this.file), ex);
        }

        this.load();
    }

    public void deleteTrade(final String tradeName) {
        this.config.set(TRADES + "." + tradeName, null);
        this.modifyTrades(map -> map.remove(tradeName));
        this.save();
    }

    private void writeStack(final String path, final @Nullable ItemStack itemStack) {
        ItemStackSerialization.writeOrRemove(this.config, path, itemStack);
    }

    public void setTrade(
        final String tradeName,
        final int maxUses,
        final boolean experienceReward,
        final @Nullable ItemStack i1,
        final @Nullable ItemStack i2,
        final ItemStack result
    ) {
        if (i1 == null) {
            return;
        }

        final String child = TRADES + "." + tradeName;

        this.config.set(child + ".maxUses", maxUses);
        this.config.set(child + ".experienceReward", experienceReward);
        this.writeStack(child + ".result", result);
        this.writeIngredient(tradeName, 1, i1);
        this.writeIngredient(tradeName, 2, i2);
        this.save();

        final @Nullable MerchantRecipe recipe = this.readTrade(tradeName);
        if (recipe != null) {
            this.modifyTrades(map -> map.put(tradeName, recipe));
        }
    }

    private void modifyTrades(final Consumer<Map<String, MerchantRecipe>> op) {
        final Map<String, MerchantRecipe> map = new TreeMap<>(this.allTrades);
        op.accept(map);
        this.allTrades = Collections.unmodifiableMap(new LinkedHashMap<>(map));
    }

    private @NonNull ConfigurationSection getTradeSection() {
        ConfigurationSection section = this.config.getConfigurationSection(TRADES);
        if (section == null) {
            section = this.config.createSection(TRADES);
        }
        return section;
    }

    public void writeIngredient(String tradeName, int i, ItemStack is) {
        if (this.getTradeSection().getKeys(false).contains(tradeName)) {
            final String child = TRADES + "." + tradeName;
            if (is != null) {
                this.writeStack(child + ".ingredients." + i, is);
            } else if (i == 2) {
                this.writeStack(child + ".ingredients.2", null);
            }
        }
    }

    private void readTrades() {
        final Map<String, MerchantRecipe> tradeMap = new TreeMap<>();

        for (final String key : this.getTradeSection().getKeys(false)) {
            final @Nullable MerchantRecipe recipe = this.readTrade(key);
            if (recipe != null) {
                tradeMap.put(key, recipe);
            }
        }

        this.allTrades = Collections.unmodifiableMap(new LinkedHashMap<>(tradeMap));
    }

    private @Nullable MerchantRecipe readTrade(final String key) {
        final String prefix = TRADES + "." + key + ".";

        int maxUses = 1;
        if (this.config.getInt(prefix + "maxUses") != 0) {
            maxUses = this.config.getInt(prefix + "maxUses");
        }

        final ItemStack result = ItemStackSerialization.read(this.config, prefix + "result");
        if (result != null) {
            final MerchantRecipe recipe = new MerchantRecipe(
                result,
                0,
                maxUses,
                this.config.getBoolean(prefix + "experienceReward")
            );

            for (int i = 1; i < 3; i++) {
                final ItemStack ingredient = ItemStackSerialization.read(this.config, prefix + "ingredients." + i);
                if (ingredient != null) {
                    recipe.addIngredient(ingredient);
                }
            }

            return recipe;
        } else {
            this.plugin.getLogger().log(Level.WARNING, String.format("Failed to read trade: '%s', missing/invalid result item", prefix));
        }
        return null;
    }

    private List<MerchantRecipe> pickTrades(final Collection<MerchantRecipe> trades, final int amount) {
        final List<MerchantRecipe> list = new ArrayList<>(trades);
        Collections.shuffle(list);
        if (list.size() < amount) {
            final List<MerchantRecipe> listCopy = new ArrayList<>(trades);
            while (list.size() < amount) {
                Collections.shuffle(listCopy);
                list.addAll(listCopy);
            }
        }
        return list.subList(0, amount);
    }

    public MerchantRecipe getTrade(final String name) {
        return Objects.requireNonNull(this.allTrades.get(name));
    }

    public Map<String, MerchantRecipe> tradesByName() {
        return this.allTrades;
    }

    public @Nullable List<MerchantRecipe> tryGetTrades(final CommandSender sender) {
        try {
            return this.getTrades(true);
        } catch (final IllegalStateException ex) {
            this.plugin.chat().send(sender, Messages.COMMAND_EXCEPTION_MALFORMED_CONFIG);
            return null;
        }
    }

    public List<MerchantRecipe> getTrades(boolean bypassDisabled) {
        final List<MerchantRecipe> trades = new ArrayList<>();
        if (this.enabled || bypassDisabled) {
            final Map<String, MerchantRecipe> allTrades = this.allTrades;
            if (this.randomized) {
                trades.addAll(this.pickTrades(allTrades.values(), this.getRandAmount()));
            } else {
                trades.addAll(allTrades.values());
            }
        }
        return trades;
    }

    public int getRandAmount() {
        if (this.randomAmount.contains(":")) {
            final String[] ints = this.randomAmount.split(":");
            return ThreadLocalRandom.current().nextInt(
                Integer.parseInt(ints[0]),
                Integer.parseInt(ints[1])
            );
        }
        return Integer.parseInt(this.randomAmount);
    }

    public String configName() {
        return this.configName;
    }

    public boolean randomized() {
        return this.randomized;
    }

    public boolean enabled() {
        return this.enabled;
    }

    public String randomAmount() {
        return this.randomAmount;
    }

    public double chance() {
        return this.chance;
    }

    public boolean invincible() {
        return this.invincible;
    }

    public String customName() {
        return this.customName;
    }

    public boolean disableHeroOfTheVillageGifts() {
        return this.disableHeroOfTheVillageGifts;
    }

    public void randomized(boolean randomized) {
        this.randomized = randomized;
    }

    public void enabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void randomAmount(String randomAmount) {
        this.randomAmount = randomAmount;
    }

    public void chance(double chance) {
        this.chance = chance;
    }

    public void invincible(boolean invincible) {
        this.invincible = invincible;
    }

    public void customName(String customName) {
        this.customName = customName;
    }

    public void disableHeroOfTheVillageGifts(boolean disableHeroOfTheVillageGifts) {
        this.disableHeroOfTheVillageGifts = disableHeroOfTheVillageGifts;
    }

    public static TradeConfig load(final WanderingTrades plugin, final File file) {
        return new TradeConfig(plugin, file);
    }

    public static final class Fields {
        public static final String plugin = "plugin";
        public static final String file = "file";
        public static final String randomized = "randomized";
        public static final String enabled = "enabled";
        public static final String randomAmount = "randomAmount";
        public static final String chance = "chance";
        public static final String invincible = "invincible";
        public static final String customName = "customName";
        public static final String disableHeroOfTheVillageGifts = "disableHeroOfTheVillageGifts";
    }
}
