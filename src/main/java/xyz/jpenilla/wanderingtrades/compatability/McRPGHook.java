package xyz.jpenilla.wanderingtrades.compatability;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import us.eunoians.mcrpg.api.util.books.SkillBookFactory;

import java.util.ArrayList;
import java.util.List;

public class McRPGHook {
    public ArrayList<MerchantRecipe> replacePlaceholders(List<MerchantRecipe> recipes) {
        ArrayList<MerchantRecipe> fixed = new ArrayList<>();
        recipes.forEach(recipe -> {
            List<ItemStack> ingredients = recipe.getIngredients();
            ItemStack result = recipe.getResult();
            ItemStack fixedResult = result;
            int maxUses = recipe.getMaxUses();
            boolean experienceReward = recipe.hasExperienceReward();
            if (result.getType().equals(Material.CHIPPED_ANVIL)) {
                ItemMeta meta = result.getItemMeta();
                if (meta.getDisplayName().equals("mcrpg_skill_book_placeholder_")) {
                    fixedResult = SkillBookFactory.generateUnlockBook();
                }
                if (meta.getDisplayName().equals("mcrpg_upgrade_book_placeholder_")) {
                    fixedResult = SkillBookFactory.generateUpgradeBook();
                }
            }
            MerchantRecipe r = new MerchantRecipe(fixedResult, 0, maxUses, experienceReward);
            r.setIngredients(ingredients);
            fixed.add(r);
        });
        return fixed;
    }
}
