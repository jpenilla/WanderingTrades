package fun.ccmc.wanderingtrades.config;

import com.deanveloper.skullcreator.SkullCreator;
import fun.ccmc.wanderingtrades.WanderingTrades;
import fun.ccmc.wanderingtrades.util.TextUtil;
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
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
        ItemStack is;
        boolean McRPG = false;

        try {
            is = ItemStack.deserialize(config.getConfigurationSection(key + ".itemStack").getValues(true));
        } catch (NullPointerException e) {
            is = null;
        }

        if (is == null) {
            if (config.getString(key + ".material") != null) {
                if (config.getString(key + ".material").contains("head-")) {
                    is = SkullCreator.itemFromBase64(config.getString(key + ".material").replace("head-", ""));
                } else {
                    if (config.getString(key + ".material").toUpperCase().contains("MCRPG") && WanderingTrades.getInstance().getMcRPG() != null) {
                        is = new ItemStack(Material.CHIPPED_ANVIL);
                        ItemMeta im = is.getItemMeta();
                        if (config.getString(key + ".material").toUpperCase().contains("SKILL")) {
                            im.setDisplayName("mcrpg_skill_book_placeholder_");
                        } else {
                            im.setDisplayName("mcrpg_upgrade_book_placeholder_");
                        }
                        McRPG = true;
                        is.setItemMeta(im);
                        is.setAmount(config.getInt(key + ".amount"));
                    } else {
                        if (Material.getMaterial(config.getString(key + ".material").toUpperCase()) != null) {
                            is = new ItemStack(Material.getMaterial(config.getString(key + ".material").toUpperCase()), config.getInt(key + ".amount"));
                        } else {
                            is = new ItemStack(Material.STONE);
                            WanderingTrades.getInstance().getLog().warn(config.getString(key + ".material") + " is not a valid material");
                        }
                    }
                }

                if (!McRPG) {
                    ItemMeta iMeta = is.getItemMeta();

                    String cname = config.getString(key + ".customname");
                    if (cname != null && !cname.equals("NONE")) {
                        iMeta.setDisplayName(TextUtil.colorize(cname));
                    }

                    if (config.getStringList(key + ".lore").size() != 0) {
                        iMeta.setLore(TextUtil.colorize(config.getStringList(key + ".lore")));
                    }

                    config.getStringList(key + ".enchantments").forEach(s -> {
                        if (s.contains(":")) {
                            String[] e = s.split(":");
                            Enchantment ench = EnchantmentWrapper.getByKey(NamespacedKey.minecraft(e[0].toLowerCase()));
                            if (ench != null) {
                                iMeta.addEnchant(ench, Integer.parseInt(e[1]), true);
                            }
                        }
                    });

                    is.setItemMeta(iMeta);
                    is.setAmount(config.getInt(key + ".amount"));
                }
            }
        }
        return is;
    }

    public void deleteTrade(String configName, String tradeName) {
        file.set(parent + "." + tradeName, null);
        save(configName);
    }

    public boolean writeTrade(String configName, String tradeName, int maxUses, boolean experienceReward, ItemStack i1, ItemStack i2, ItemStack result) {
        String child = parent + "." + tradeName;
        if (i1 == null && i2 == null) {
            return false;
        } else {
            file.set(child + ".maxUses", maxUses);
            file.set(child + ".experienceReward", experienceReward);
            file.set(child + ".result.itemStack", result.serialize());
            writeIngredient(configName, tradeName, 1, i1);
            writeIngredient(configName, tradeName, 2, i2);
            save(configName);
            return true;
        }
    }

    public boolean writeIngredient(String configName, String tradeName, int i, ItemStack is) {
        if (file.getConfigurationSection(parent).getKeys(false).contains(tradeName)) {
            String child = parent + "." + tradeName;
            if (is != null) {
                file.set(child + ".ingredients." + i + ".itemStack", is.serialize());
            } else {
                if (i == 2) {
                    file.set(child + ".ingredients.2", null);
                } else {
                    return false;
                }
            }
            return true;
        } else {
            return false;
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
