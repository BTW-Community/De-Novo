package btw.community.denovo.recipes;

import btw.AddonHandler;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;

import java.util.ArrayList;

public class SiftingCraftingManager {
    private static final ArrayList<SiftingRecipe> recipes = new ArrayList<SiftingRecipe>();

    public static void addSiftingRecipe(LootEntry[] lootTable, ItemStack input, ItemStack filterUsed) {
        if (input.stackSize != 1) {
            AddonHandler.logWarning("Cannot add hopper filtering recipe with input stack size > 1 for input " + input.getUnlocalizedName());
        }

        input.stackSize = 1;
        filterUsed.stackSize = 1;

        recipes.add(new SiftingRecipe(lootTable, input, filterUsed, false));
    }

    public static void addSoulSiftingRecipe(LootEntry[] lootTable, ItemStack input) {
        if (input.stackSize != 1) {
            AddonHandler.logWarning("Cannot add hopper soul filtering recipe with input stack size > 1 for input " + input.getUnlocalizedName());
        }

        input.stackSize = 1;

        recipes.add(new SiftingRecipe(lootTable, input, new ItemStack(Block.slowSand), true));
    }

    public static SiftingRecipe getRecipe(ItemStack input, ItemStack filterUsed) {
        for (SiftingRecipe recipe : recipes) {
            if (recipe.matchesInputs(input, filterUsed)) {
                return recipe;
            }
        }

        return null;
    }
}
