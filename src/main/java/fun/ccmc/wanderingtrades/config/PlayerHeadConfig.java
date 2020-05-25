package fun.ccmc.wanderingtrades.config;

import com.deanveloper.skullcreator.SkullCreator;
import fun.ccmc.wanderingtrades.WanderingTrades;
import fun.ccmc.wanderingtrades.util.TextFormatting;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class PlayerHeadConfig {

    private final WanderingTrades plugin;

    @Getter private int maxUses = 1;
    @Getter private final boolean experienceReward;
    @Getter private final boolean playerHeadsFromServer;
    @Getter private final double playerHeadsFromServerChance;
    @Getter private final int playerHeadsFromServerAmount;
    @Getter private final int amountOfHeadsPerTrade;
    @Getter private final String name;
    @Getter private final ArrayList<String> lore;
    @Getter private final ItemStack ingredient1;
    @Getter private final ItemStack ingredient2;

    public PlayerHeadConfig(WanderingTrades instance, FileConfiguration config) {
        plugin = instance;
        playerHeadsFromServer = config.getBoolean("playerHeadsFromServer");
        playerHeadsFromServerChance = config.getDouble("playerHeadsFromServerChance");
        playerHeadsFromServerAmount = config.getInt("playerHeadsFromServerAmount");
        String prefix = "headTrade.";
        if (config.getInt(prefix + "maxUses") != 0) {
            maxUses = config.getInt(prefix + "maxUses");
        }
        experienceReward = config.getBoolean(prefix + "experienceReward");
        ingredient1 = getStack(config, prefix + "ingredients.1");
        ingredient2 = getStack(config, prefix + "ingredients.2");
        amountOfHeadsPerTrade = config.getInt(prefix + "head.amount");
        name = TextFormatting.colorize(config.getString(prefix + "head.customname"));
        lore = TextFormatting.colorize(config.getStringList(prefix + "head.lore"));
    }

    private ItemStack getStack(FileConfiguration config, String key) {
        ItemStack is = null;

        if(config.getString(key + ".material") != null) {
            if(config.getString(key + ".material").contains("head-")) {
                is = SkullCreator.withBase64(new ItemStack(Material.PLAYER_HEAD, config.getInt(key + ".amount")), config.getString(key + ".material").replace("head-", ""));
            } else {
                if(Material.getMaterial(config.getString(key + ".material").toUpperCase()) != null) {
                    is = new ItemStack(Material.getMaterial(config.getString(key + ".material").toUpperCase()), config.getInt(key + ".amount"));
                } else {
                    is = new ItemStack(Material.STONE);
                    plugin.getLog().warn(config.getString(key + ".material") + " is not a valid material");
                }
            }

            ItemMeta iMeta = is.getItemMeta();

            String cname = config.getString(key + ".customname");
            if(cname != null && !cname.equals("NONE")) {
                iMeta.setDisplayName(TextFormatting.colorize(cname));
            }

            if(config.getStringList(key + ".lore").size() != 0) {
                iMeta.setLore(TextFormatting.colorize(config.getStringList(key + ".lore")));
            }

            config.getStringList(key + ".enchantments").forEach(s -> {
                if(s.contains(":")) {
                    String[] e = s.split(":");
                    Enchantment ench = EnchantmentWrapper.getByKey(NamespacedKey.minecraft(e[0].toLowerCase()));
                    if(ench != null) {
                        iMeta.addEnchant(ench, Integer.parseInt(e[1]), true);
                    }
                }
            });

            is.setItemMeta(iMeta);
        }
        return is;
    }
}
