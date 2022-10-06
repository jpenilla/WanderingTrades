package xyz.jpenilla.wanderingtrades.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import xyz.jpenilla.jmplib.HeadBuilder;
import xyz.jpenilla.jmplib.ItemBuilder;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

import static io.papermc.lib.PaperLib.isPaper;

public class TradeConfig {
    private final WanderingTrades plugin;
    private final FileConfiguration file;
    private final String configName;
    private boolean randomized;
    private boolean enabled;
    private String randomAmount;
    private List<MerchantRecipe> allTrades;
    private double chance;
    private boolean invincible;
    private String customName;
    private boolean disableHeroOfTheVillageGifts;

    private static final String TRADES = "trades";

    public TradeConfig(WanderingTrades instance, String configName, FileConfiguration config) {
        this.plugin = instance;
        this.configName = configName;
        this.file = config;
        load();
    }

    public void load() {
        allTrades = readTrades();
        randomized = file.getBoolean(Fields.randomized);
        randomAmount = file.getString(Fields.randomAmount);
        enabled = file.getBoolean(Fields.enabled);
        chance = file.getDouble(Fields.chance);
        invincible = file.getBoolean(Fields.invincible, false);
        customName = file.getString(Fields.customName, null);
        disableHeroOfTheVillageGifts = file.getBoolean(Fields.disableHeroOfTheVillageGifts, false);
    }

    public void save() {
        file.set(Fields.enabled, enabled);
        file.set(Fields.randomized, randomized);
        file.set(Fields.invincible, invincible);
        file.set(Fields.randomAmount, randomAmount);
        file.set(Fields.chance, chance);
        file.set(Fields.customName, customName);
        file.set(Fields.disableHeroOfTheVillageGifts, disableHeroOfTheVillageGifts);

        final String path = String.format("%s/trades/%s.yml", plugin.getDataFolder(), this.configName);
        try {
            file.save(path);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, String.format("Failed to save trade config: '%s'", path), e);
        }

        load();
    }

    public static @Nullable ItemStack getStack(FileConfiguration config, String key) {
        ItemBuilder itemBuilder = null;

        if (isPaper()) {
            final byte[] stack = (byte[]) config.get(key + ".itemStackAsBytes");
            if (stack != null) {
                itemBuilder = new ItemBuilder(ItemStack.deserializeBytes(stack));
            }
        }

        if (itemBuilder == null) {
            final ConfigurationSection configSection = config.getConfigurationSection(key + ".itemStack");
            if (configSection != null) {
                itemBuilder = new ItemBuilder(ItemStack.deserialize(configSection.getValues(true)));
            }
        }

        final String materialString = config.getString(key + ".material");
        final int amount = config.getInt(key + ".amount", 1);

        if (itemBuilder == null && materialString != null) {

            if (materialString.startsWith("head-")) {
                itemBuilder = new HeadBuilder(materialString.substring(5));
            } else {
                Material material = Material.getMaterial(materialString.toUpperCase());

                if (material != null) {
                    itemBuilder = new ItemBuilder(material).setAmount(amount);
                } else {
                    itemBuilder = new ItemBuilder(Material.STONE);
                    WanderingTrades.instance().getLogger().log(Level.WARNING, String.format("'%s' is not a valid material!", materialString));
                }
            }

            final String customName = config.getString(key + ".customname");
            if (customName != null && !customName.equals("NONE") && !customName.isEmpty()) {
                itemBuilder.setName(customName);
            }
            final List<String> lores = config.getStringList(key + ".lore");
            if (lores.size() != 0) {
                itemBuilder.setLore(lores);
            }
            itemBuilder.setAmount(amount);

            applyEnchants(itemBuilder, config.getStringList(key + ".enchantments"));
        }

        if (itemBuilder != null) {
            return itemBuilder.build();
        } else {
            return null;
        }
    }

    private static void applyEnchants(final ItemBuilder itemBuilder, final List<String> enchantStrings) {
        final Material material = itemBuilder.build().getType();
        for (final String enchantString : enchantStrings) {
            final EnchantWithLevel enchantment = readEnchantString(enchantString);

            if (enchantment == null) {
                WanderingTrades.instance().getLogger().log(Level.WARNING, String.format("'%s' is not a valid enchantment!", enchantString));
                continue;
            }

            if (material == Material.ENCHANTED_BOOK) {
                itemBuilder.<EnchantmentStorageMeta>editMeta(meta -> meta.addStoredEnchant(enchantment.enchantment, enchantment.level, true));
            } else {
                itemBuilder.addEnchant(enchantment.enchantment, enchantment.level);
            }
        }
    }

    private record EnchantWithLevel(Enchantment enchantment, int level) {
    }

    private static @Nullable EnchantWithLevel readEnchantString(final String enchantString) {
        if (enchantString.contains(":")) {
            final String[] args = enchantString.toLowerCase(Locale.ENGLISH).split(":");
            if (args.length == 1) {
                return new EnchantWithLevel(Enchantment.getByKey(NamespacedKey.minecraft(args[0])), 1);
            } else if (args.length == 2) {
                return new EnchantWithLevel(Enchantment.getByKey(NamespacedKey.minecraft(args[0])), Integer.parseInt(args[1]));
            } else if (args.length == 3) {
                return new EnchantWithLevel(Enchantment.getByKey(new NamespacedKey(args[0], args[1])), Integer.parseInt(args[2]));
            }
            return null;
        }
        return null;
    }

    public void deleteTrade(String tradeName) {
        this.file.set(TRADES + "." + tradeName, null);
        this.save();
    }

    private void writeStack(final String path, final @Nullable ItemStack itemStack) {
        if (itemStack == null) {
            this.file.set(path, null);
            return;
        }
        if (isPaper()) {
            this.file.set(path + ".itemStackAsBytes", itemStack.serializeAsBytes());
        } else {
            this.file.set(path + ".itemStack", itemStack.serialize());
        }
    }

    public void writeTrade(String tradeName, int maxUses, boolean experienceReward, ItemStack i1, ItemStack i2, ItemStack result) {
        final String child = TRADES + "." + tradeName;
        if (i1 != null) {
            this.file.set(child + ".maxUses", maxUses);
            this.file.set(child + ".experienceReward", experienceReward);
            this.writeStack(child + ".result", result);
            this.writeIngredient(tradeName, 1, i1);
            this.writeIngredient(tradeName, 2, i2);
            this.save();
        }
    }

    public @NonNull ConfigurationSection getTradeSection() {
        ConfigurationSection section = this.file.getConfigurationSection(TRADES);
        if (section == null) {
            section = this.file.createSection(TRADES);
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

    private List<MerchantRecipe> readTrades() {
        final List<MerchantRecipe> results = new ArrayList<>();
        for (final String key : this.getTradeSection().getKeys(false)) {
            final String prefix = TRADES + "." + key + ".";

            int maxUses = 1;
            if (file.getInt(prefix + "maxUses") != 0) {
                maxUses = file.getInt(prefix + "maxUses");
            }

            ItemStack result = getStack(file, prefix + "result");
            if (result != null) {
                MerchantRecipe recipe = new MerchantRecipe(result, 0, maxUses, file.getBoolean(prefix + "experienceReward"));

                for (int i = 1; i < 3; i++) {
                    ItemStack ingredient = getStack(file, prefix + "ingredients." + i);
                    if (ingredient != null) {
                        recipe.addIngredient(ingredient);
                    }
                }

                results.add(recipe);
            } else {
                plugin.getLogger().log(Level.WARNING, String.format("Failed to read trade: '%s', missing/invalid result item", prefix));
            }
        }
        return results;
    }

    private List<MerchantRecipe> pickTrades(List<MerchantRecipe> lst, int amount) {
        List<MerchantRecipe> copy = new LinkedList<>(lst);
        Collections.shuffle(copy);
        List<MerchantRecipe> f = new LinkedList<>(lst);
        int i = 0;
        while (i < amount) {
            Collections.shuffle(f);
            copy.addAll(f);
            i++;
        }
        return copy.subList(0, amount);
    }

    public List<MerchantRecipe> getTrades(boolean bypassDisabled) {
        List<MerchantRecipe> h = new ArrayList<>();
        if (enabled || bypassDisabled) {
            if (randomized) {
                h.addAll(pickTrades(allTrades, getRandAmount()));
            } else {
                h.addAll(allTrades);
            }
        }
        return h;
    }

    public int getRandAmount() {
        if (randomAmount.contains(":")) {
            String[] ints = randomAmount.split(":");
            return ThreadLocalRandom.current().nextInt(Integer.parseInt(ints[0]), Integer.parseInt(ints[1]));
        }
        return Integer.parseInt(randomAmount);
    }

    public FileConfiguration fileConfiguration() {
        return this.file;
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

    public void setDisableHeroOfTheVillageGifts(boolean disableHeroOfTheVillageGifts) {
        this.disableHeroOfTheVillageGifts = disableHeroOfTheVillageGifts;
    }

    public static final class Fields {
        public static final String plugin = "plugin";
        public static final String file = "file";
        public static final String configName = "configName";
        public static final String randomized = "randomized";
        public static final String enabled = "enabled";
        public static final String randomAmount = "randomAmount";
        public static final String allTrades = "allTrades";
        public static final String chance = "chance";
        public static final String invincible = "invincible";
        public static final String customName = "customName";
        public static final String disableHeroOfTheVillageGifts = "disableHeroOfTheVillageGifts";
    }
}
