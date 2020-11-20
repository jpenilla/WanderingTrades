package xyz.jpenilla.wanderingtrades.config;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import xyz.jpenilla.jmplib.HeadBuilder;
import xyz.jpenilla.jmplib.ItemBuilder;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

@FieldNameConstants
public class TradeConfig {
    private final WanderingTrades plugin;
    @Getter
    private final FileConfiguration file;
    @Getter @Setter
    private boolean randomized;
    @Getter @Setter
    private boolean enabled;
    @Getter @Setter
    private String randomAmount;
    private List<MerchantRecipe> allTrades;
    @Getter @Setter
    private double chance;
    @Getter @Setter
    private boolean invincible;
    @Getter @Setter
    private String customName;

    private static final String TRADES = "trades";

    public TradeConfig(WanderingTrades instance, FileConfiguration config) {
        plugin = instance;
        file = config;
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
    }

    public void save(String configName) {
        file.set(Fields.enabled, enabled);
        file.set(Fields.randomized, randomized);
        file.set(Fields.invincible, invincible);
        file.set(Fields.randomAmount, randomAmount);
        file.set(Fields.chance, chance);
        file.set(Fields.customName, customName);

        String path = plugin.getDataFolder() + "/trades/" + configName + ".yml";
        try {
            file.save(path);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, String.format("Failed to save trade config: '%s'", path), e);
        }

        load();
    }

    public static @Nullable ItemStack getStack(FileConfiguration config, String key) {
        ItemBuilder itemBuilder = null;

        if (WanderingTrades.getInstance().isPaperServer() && WanderingTrades.getInstance().getMajorMinecraftVersion() > 14) {
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

        if (materialString != null && materialString.toUpperCase(Locale.ENGLISH).contains("MCRPG") && WanderingTrades.getInstance().getMcRPG() != null) {
            itemBuilder = new ItemBuilder(Material.CHIPPED_ANVIL).setAmount(amount);
            if (materialString.toUpperCase().contains("SKILL")) {
                itemBuilder.setName("mcrpg_skill_book_placeholder_");
            } else {
                itemBuilder.setName("mcrpg_upgrade_book_placeholder_");
            }
            return itemBuilder.build();
        }

        if (itemBuilder == null && materialString != null) {

            if (materialString.startsWith("head-")) {
                itemBuilder = new HeadBuilder(materialString.substring(5));
            } else {
                Material material = Material.getMaterial(materialString.toUpperCase());

                if (material != null) {
                    itemBuilder = new ItemBuilder(material).setAmount(amount);
                } else {
                    itemBuilder = new ItemBuilder(Material.STONE);
                    WanderingTrades.getInstance().getLogger().log(Level.WARNING, String.format("'%s' is not a valid material!", materialString));
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

            for (final String enchantString : config.getStringList(key + ".enchantments")) {
                final Enchantment enchantment;
                final int level;
                if (enchantString.contains(":")) {
                    final String[] args = enchantString.toLowerCase(Locale.ENGLISH).split(":");
                    if (args.length == 2) {
                        enchantment = Enchantment.getByKey(NamespacedKey.minecraft(args[0]));
                        level = Integer.parseInt(args[1]);
                    } else if (args.length == 3) {
                        // noinspection deprecation
                        enchantment = Enchantment.getByKey(new NamespacedKey(args[0], args[1]));
                        level = Integer.parseInt(args[2]);
                    } else {
                        enchantment = null;
                        level = 0;
                    }
                } else {
                    enchantment = null;
                    level = 0;
                }
                if (enchantment != null) {
                    itemBuilder.addEnchant(enchantment, level);
                } else {
                    WanderingTrades.getInstance().getLogger().log(Level.WARNING, String.format("'%s' is not a valid enchantment!", materialString));
                }
            }
        }

        if (itemBuilder != null) {
            return itemBuilder.build();
        } else {
            return null;
        }
    }

    public void deleteTrade(String configName, String tradeName) {
        file.set(TRADES + "." + tradeName, null);
        save(configName);
    }

    public void writeTrade(String configName, String tradeName, int maxUses, boolean experienceReward, ItemStack i1, ItemStack i2, ItemStack result) {
        String child = TRADES + "." + tradeName;
        if (i1 != null) {
            file.set(child + ".maxUses", maxUses);
            file.set(child + ".experienceReward", experienceReward);
            if (plugin.isPaperServer() && plugin.getMajorMinecraftVersion() > 14) {
                file.set(child + ".result.itemStackAsBytes", result.serializeAsBytes());
            } else {
                file.set(child + ".result.itemStack", result.serialize());
            }
            writeIngredient(tradeName, 1, i1);
            writeIngredient(tradeName, 2, i2);
            save(configName);
        }
    }

    public @NonNull ConfigurationSection getTradeSection() {
        ConfigurationSection section = this.file.getConfigurationSection(TRADES);
        if (section == null) {
            section = this.file.createSection("");
        }
        return section;
    }

    public void writeIngredient(String tradeName, int i, ItemStack is) {
        if (this.getTradeSection().getKeys(false).contains(tradeName)) {
            String child = TRADES + "." + tradeName;
            if (is != null) {
                if (plugin.isPaperServer() && plugin.getMajorMinecraftVersion() > 14) {
                    file.set(child + ".ingredients." + i + ".itemStackAsBytes", is.serializeAsBytes());
                } else {
                    file.set(child + ".ingredients." + i + ".itemStack", is.serialize());
                }
            } else if (i == 2) {
                file.set(child + ".ingredients.2", null);
            }
        }
    }

    private List<MerchantRecipe> readTrades() {
        List<MerchantRecipe> tradeList = new ArrayList<>();
        this.getTradeSection().getKeys(false).forEach(key -> {
            String prefix = TRADES + "." + key + ".";

            int maxUses = 1;
            if (file.getInt(prefix + "maxUses") != 0) {
                maxUses = file.getInt(prefix + "maxUses");
            }

            ItemStack result = getStack(file, prefix + "result");
            if (result != null) {
                MerchantRecipe recipe = new MerchantRecipe(result, 0, maxUses, file.getBoolean(prefix + "experienceReward"));

                int ingredientNumber = 1;
                while (ingredientNumber < 3) {
                    ItemStack ingredient = getStack(file, prefix + "ingredients." + ingredientNumber);
                    if (ingredient != null) {
                        recipe.addIngredient(ingredient);
                    }
                    ingredientNumber++;
                }

                tradeList.add(recipe);
            } else {
                plugin.getLogger().log(Level.WARNING, String.format("Failed to read trade: '%s', missing/invalid result item", prefix));
            }
        });
        return tradeList;
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
        if (plugin.getMcRPG() != null) {
            return plugin.getMcRPG().replacePlaceholders(h);
        } else {
            return h;
        }
    }

    public int getRandAmount() {
        if (randomAmount.contains(":")) {
            String[] ints = randomAmount.split(":");
            return ThreadLocalRandom.current().nextInt(Integer.parseInt(ints[0]), Integer.parseInt(ints[1]));
        } else {
            return Integer.parseInt(randomAmount);
        }
    }
}
