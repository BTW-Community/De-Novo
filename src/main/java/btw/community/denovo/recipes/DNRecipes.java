package btw.community.denovo.recipes;

import btw.community.denovo.item.DNItems;
import btw.item.BTWItems;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;

public class DNRecipes {
    public static void addRecipes() {
        SiftingCraftingManager.addSiftingRecipe(
                new LootEntry[]{
                        new LootEntry(0.5D, new ItemStack(BTWItems.stone)),
                        new LootEntry(0.2D, new ItemStack(Block.sapling)),
                        new LootEntry(0.1D, new ItemStack(BTWItems.sugarCaneRoots))
                },
                new ItemStack(BTWItems.dirtPile),
                new ItemStack(DNItems.mesh)
        );
    }
}
