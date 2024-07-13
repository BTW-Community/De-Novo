package btw.community.denovo.recipes;

import btw.block.BTWBlocks;
import btw.community.denovo.item.DNItems;
import btw.item.BTWItems;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;

public class DNRecipes {
    public static void addRecipes() {
        SiftingCraftingManager.addSiftingRecipe(
                new LootEntry[]{
                        new LootEntry(0.5D, 1, new ItemStack(BTWItems.stone)),
                        new LootEntry(0.2D, 1, new ItemStack(Block.sapling)),
                        new LootEntry(0.1D, 1, new ItemStack(BTWItems.sugarCaneRoots))
                },
                new ItemStack(BTWItems.dirtPile),
                new ItemStack(DNItems.mesh)
        );

        SiftingCraftingManager.addSiftingRecipe(
                new LootEntry[]{
                        new LootEntry(0.5D, 8, new ItemStack(BTWItems.stone)),
                        new LootEntry(0.2D, 8, new ItemStack(Block.sapling)),
                        new LootEntry(0.1D, 8, new ItemStack(BTWItems.sugarCaneRoots))
                },
                new ItemStack(BTWBlocks.looseDirt),
                new ItemStack(DNItems.mesh)
        );
    }
}
