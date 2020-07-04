package xyz.jpenilla.wanderingtrades.config;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import xyz.jpenilla.jmplib.HeadBuilder;
import xyz.jpenilla.jmplib.ItemBuilder;
import xyz.jpenilla.wanderingtrades.WanderingTrades;

import java.io.IOException;
import java.util.*;

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
    private int randomAmount;
    private List<MerchantRecipe> allTrades;
    @Getter @Setter
    private double chance;
    @Getter @Setter
    private boolean invincible;
    @Getter @Setter
    private String customName;

    private final String parent = "trades";

    public TradeConfig(WanderingTrades instance, FileConfiguration config) {
        plugin = instance;
        file = config;
        load();
    }

    public void load() {
        allTrades = readTrades(file);
        randomized = file.getBoolean(Fields.randomized);
        randomAmount = file.getInt(Fields.randomAmount);
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
            plugin.getLog().warn(e.getMessage());
        }

        load();
    }

    public static ItemStack getStack(FileConfiguration config, String key) {
        ItemBuilder itemBuilder;
        boolean McRPG = false;

        try {
            itemBuilder = new ItemBuilder(ItemStack.deserialize(config.getConfigurationSection(key + ".itemStack").getValues(true)));
        } catch (NullPointerException e) {
            itemBuilder = null;
        }

        if (itemBuilder == null && config.getString(key + ".material") != null) {
            if (config.getString(key + ".material").contains("head-")) {
                itemBuilder = new HeadBuilder(config.getString(key + ".material").replace("head-", ""));
            } else {
                if (config.getString(key + ".material").toUpperCase().contains("MCRPG") && WanderingTrades.getInstance().getMcRPG() != null) {
                    itemBuilder = new ItemBuilder(Material.CHIPPED_ANVIL).setAmount(config.getInt(key + ".amount"));
                    if (config.getString(key + ".material").toUpperCase().contains("SKILL")) {
                        itemBuilder.setName("mcrpg_skill_book_placeholder_");
                    } else {
                        itemBuilder.setName("mcrpg_upgrade_book_placeholder_");
                    }
                    McRPG = true;
                } else {
                    String matName;

                    try {
                        matName = Objects.requireNonNull(config.getString(key + ".material")).toUpperCase();
                    } catch (NullPointerException e) {
                        matName = Material.STONE.toString();
                        WanderingTrades.getInstance().getLog().warn(config.getString(key + ".material") + " is not a valid material");
                    }

                    Material m = Material.getMaterial(matName);

                    if (m != null) {
                        itemBuilder = new ItemBuilder(m).setAmount(config.getInt(key + ".amount"));
                    } else {
                        itemBuilder = new ItemBuilder(Material.STONE);
                        WanderingTrades.getInstance().getLog().warn(config.getString(key + ".material") + " is not a valid material");
                    }
                }
            }

            if (!McRPG) {
                String cname = config.getString(key + ".customname");
                if (cname != null && !cname.equals("NONE")) {
                    itemBuilder.setName(cname);
                }
                if (config.getStringList(key + ".lore").size() != 0) {
                    itemBuilder.setLore(config.getStringList(key + ".lore"));
                }
                itemBuilder.setAmount(config.getInt(key + ".amount"));

                for (String s : config.getStringList(key + ".enchantments")) {
                    if (s.contains(":")) {
                        String[] e = s.split(":");
                        Enchantment ench = EnchantmentWrapper.getByKey(NamespacedKey.minecraft(e[0].toLowerCase()));
                        if (ench != null) {
                            itemBuilder.addEnchant(ench, Integer.parseInt(e[1]));
                        }
                    }
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
        file.set(parent + "." + tradeName, null);
        save(configName);
    }

    public void writeTrade(String configName, String tradeName, int maxUses, boolean experienceReward, ItemStack i1, ItemStack i2, ItemStack result) {
        String child = parent + "." + tradeName;
        if (i1 != null) {
            file.set(child + ".maxUses", maxUses);
            file.set(child + ".experienceReward", experienceReward);
            file.set(child + ".result.itemStack", result.serialize());
            writeIngredient(tradeName, 1, i1);
            writeIngredient(tradeName, 2, i2);
            save(configName);
        }
    }

    public void writeIngredient(String tradeName, int i, ItemStack is) {
        if (file.getConfigurationSection(parent).getKeys(false).contains(tradeName)) {
            String child = parent + "." + tradeName;
            if (is != null) {
                file.set(child + ".ingredients." + i + ".itemStack", is.serialize());
            } else if (i == 2) {
                file.set(child + ".ingredients.2", null);
            }
        }
    }

    private ArrayList<MerchantRecipe> readTrades(FileConfiguration config) {
        ArrayList<MerchantRecipe> tradeList = new ArrayList<>();
        config.getConfigurationSection(parent).getKeys(false).forEach(key -> {
            String prefix = parent + "." + key + ".";

            int maxUses = 1;
            if (config.getInt(prefix + "maxUses") != 0) {
                maxUses = config.getInt(prefix + "maxUses");
            }

            ItemStack result = getStack(config, prefix + "result");
            MerchantRecipe recipe = new MerchantRecipe(result, 0, maxUses, config.getBoolean(prefix + "experienceReward"));

            int ingredientNumber = 1;
            while (ingredientNumber < 3) {
                ItemStack ingredient = getStack(config, prefix + "ingredients." + ingredientNumber);
                if (ingredient != null) {
                    recipe.addIngredient(ingredient);
                }
                ingredientNumber++;
            }

            tradeList.add(recipe);
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

    public ArrayList<MerchantRecipe> getTrades(boolean bypassDisabled) {
        ArrayList<MerchantRecipe> h = new ArrayList<>();
        if (enabled || bypassDisabled) {
            if (randomized) {
                h.addAll(pickTrades(allTrades, randomAmount));
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
}
