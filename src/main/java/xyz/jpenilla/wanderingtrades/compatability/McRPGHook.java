package xyz.jpenilla.wanderingtrades.compatability;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import us.eunoians.mcrpg.api.util.books.SkillBookFactory;

import java.util.List;
import java.util.stream.Collectors;

public class McRPGHook {

    public List<MerchantRecipe> replacePlaceholders(List<MerchantRecipe> recipes) {
        return recipes.stream().map(recipe -> {
            final List<ItemStack> ingredients = recipe.getIngredients();
            final ItemStack result = recipe.getResult();
            ItemStack fixedResult = result;
            int maxUses = recipe.getMaxUses();
            boolean experienceReward = recipe.hasExperienceReward();
            if (result.getType().equals(Material.CHIPPED_ANVIL)) {
                final ItemMeta meta = result.getItemMeta();
                if (meta.hasDisplayName()) {
                    if (meta.getDisplayName().equals("mcrpg_skill_book_placeholder_")) {
                        fixedResult = SkillBookFactory.generateUnlockBook();
                    } else if (meta.getDisplayName().equals("mcrpg_upgrade_book_placeholder_")) {
                        fixedResult = SkillBookFactory.generateUpgradeBook();
                    }
                }
            }
            if (fixedResult.equals(result)) {
                return recipe;
            }
            final MerchantRecipe fixedRecipe = new MerchantRecipe(fixedResult, 0, maxUses, experienceReward);
            fixedRecipe.setIngredients(ingredients);
            return fixedRecipe;
        }).collect(Collectors.toList());
    }

}
