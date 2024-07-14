package btw.community.denovo.recipes;

import btw.block.BTWBlocks;
import btw.community.denovo.block.DNBlocks;
import btw.community.denovo.item.DNItems;
import btw.crafting.recipe.RecipeManager;
import btw.item.BTWItems;
import net.minecraft.src.Block;
import net.minecraft.src.CraftingManager;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

public class DNRecipes {
    public static void addRecipes() {
        addCraftingRecipes();
        addSieveRecipes();
    }

    private static void addCraftingRecipes() {
        RecipeManager.addRecipe(new ItemStack(DNBlocks.sieve), new Object[]{
                "TT",
                "SS",
                'T', new ItemStack(Item.silk),
                'S', new ItemStack(Item.stick),
        });
    }

    private static void addSieveRecipes() {
        // Dirt sifting
        SiftingCraftingManager.addSiftingRecipe(
                new LootEntry[]{
                        new LootEntry(1.0D / 2, 1, new ItemStack(BTWItems.stone)),
                        new LootEntry(1.0D / 5, 1, new ItemStack(Block.sapling)),
                        new LootEntry(1.0D / 10, 1, new ItemStack(BTWItems.sugarCaneRoots))
                },
                new ItemStack(BTWItems.dirtPile),
                new ItemStack(DNItems.mesh)
        );

        SiftingCraftingManager.addSiftingRecipe(
                new LootEntry[]{
                        new LootEntry(1.0D / 2, 8, new ItemStack(BTWItems.stone)),
                        new LootEntry(1.0D / 5, 8, new ItemStack(Block.sapling)),
                        new LootEntry(1.0D / 10, 8, new ItemStack(BTWItems.sugarCaneRoots))
                },
                new ItemStack(BTWBlocks.looseDirt),
                new ItemStack(DNItems.mesh)
        );

        // Cobble sifting
        SiftingCraftingManager.addSiftingRecipe(
                new LootEntry[]{
                        new LootEntry(1.0D / 2, 8, new ItemStack(BTWItems.stone)),
                        new LootEntry(1.0D / 2, 8, new ItemStack(BTWItems.gravelPile))
                },
                new ItemStack(BTWBlocks.looseCobblestone),
                new ItemStack(DNItems.mesh)
        );

        // Gravel sifting
        SiftingCraftingManager.addSiftingRecipe(
                new LootEntry[]{
                        new LootEntry(1.0D / 8, 1, new ItemStack(Item.flint))
                },
                new ItemStack(BTWItems.gravelPile),
                new ItemStack(DNItems.mesh)
        );

        SiftingCraftingManager.addSiftingRecipe(
                new LootEntry[]{
                        new LootEntry(1.0D, 1, new ItemStack(Item.flint))
                },
                new ItemStack(Block.gravel),
                new ItemStack(DNItems.mesh)
        );

        SiftingCraftingManager.addSiftingRecipe(
                new LootEntry[]{
                        new LootEntry(1.0D, 1, new ItemStack(BTWItems.sandPile)),
                        new LootEntry(1.0D / 8, 1, new ItemStack(Item.flint))
                },
                new ItemStack(BTWItems.gravelPile),
                new ItemStack(BTWBlocks.wickerPane)
        );

        // Dung sifting
        SiftingCraftingManager.addSiftingRecipe(
                new LootEntry[]{
                        new LootEntry(1.0D / 3, 1, new ItemStack(Item.pumpkinSeeds)),
                        new LootEntry(1.0D / 3, 1, new ItemStack(Item.melonSeeds)),
                        new LootEntry(1.0D / 3, 1, new ItemStack(Item.seeds)),
                },
                new ItemStack(BTWItems.dung),
                new ItemStack(DNItems.mesh)
        );
    }
}
