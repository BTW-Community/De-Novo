package btw.community.denovo.recipes;

import btw.block.BTWBlocks;
import btw.block.blocks.FurnaceBlock;
import btw.community.denovo.block.DNBlocks;
import btw.community.denovo.block.blocks.ComposterBlock;
import btw.community.denovo.item.DNItems;
import btw.crafting.recipe.RecipeManager;
import btw.item.BTWItems;
import net.minecraft.src.*;

public class DNRecipes {
    public static void addRecipes() {
        addSieveRecipes();
        addMaggotsRecipes();
        addComposterRecipes();
    }

    private static void addMaggotsRecipes() {
        //Check FishingRodBaitingRecipeMixin for baiting fishing rod

        //maggots to string
        RecipeManager.addShapelessRecipe(new ItemStack(Item.silk),
            new Object[]{
                new ItemStack(DNItems.rawMaggots)
            });

        FurnaceRecipes.smelting().addSmelting(DNItems.rawMaggots.itemID, new ItemStack(DNItems.cookedMaggots), 0);
        RecipeManager.addCampfireRecipe(DNItems.rawMaggots.itemID, new ItemStack(DNItems.cookedMaggots));
    }

    private static void addComposterRecipes() {
        //Composter
        RecipeManager.addRecipe(new ItemStack(DNBlocks.composter), new Object[] {
                "SS",
                "CC",
                'S', new ItemStack(Item.stick),
                'C', new ItemStack(BTWBlocks.unlitCampfire)
        });

        //Compostables
        ComposterBlock.validCompostables.add(new ItemStack(Item.stick));
        ComposterBlock.validCompostables.add(new ItemStack(BTWItems.sawDust));

        ComposterBlock.validCompostables.add(new ItemStack(Item.rottenFlesh));
        ComposterBlock.validCompostables.add(new ItemStack(Item.spiderEye));
        ComposterBlock.validCompostables.add(new ItemStack(Item.fermentedSpiderEye));
        ComposterBlock.validCompostables.add(new ItemStack(BTWItems.creeperOysters));
        ComposterBlock.validCompostables.add(new ItemStack(BTWItems.batWing));
        ComposterBlock.validCompostables.add(new ItemStack(BTWItems.mysteriousGland));

        ComposterBlock.validCompostables.add(new ItemStack(Block.deadBush));
        ComposterBlock.validCompostables.add(new ItemStack(Block.plantRed));
        ComposterBlock.validCompostables.add(new ItemStack(Block.plantYellow));
        ComposterBlock.validCompostables.add(new ItemStack(BTWItems.redMushroom));
        ComposterBlock.validCompostables.add(new ItemStack(BTWItems.brownMushroom));

        ComposterBlock.validCompostables.add(new ItemStack(DNItems.rawMaggots));

        for (int type=0; type < 5; type++){
            ComposterBlock.validCompostables.add(new ItemStack(Block.wood, 1, type));
        }
        for (int type=0; type < 4; type++){
            ComposterBlock.validCompostables.add(new ItemStack(Block.leaves, 1, type));
        }
        for (int type=0; type < 16; type++){
            ComposterBlock.validCompostables.add(new ItemStack(Block.sapling, 1, type));
        }
        for (int type=0; type < 3; type++){
            ComposterBlock.validCompostables.add(new ItemStack(Block.tallGrass, 1, type));
        }

    }

    private static void addSieveRecipes() {
        SiftingCraftingManager.addSiftingRecipe(
                new LootEntry[]{
                        new LootEntry(0.5D, new ItemStack(BTWItems.stone)),
                        new LootEntry(0.2D, new ItemStack(Block.sapling)),
                        new LootEntry(0.1D, new ItemStack(BTWItems.sugarCaneRoots))
                },
                new ItemStack(BTWBlocks.aestheticEarth, 1, 7),
                new ItemStack(DNItems.mesh)
        );
    }
}
