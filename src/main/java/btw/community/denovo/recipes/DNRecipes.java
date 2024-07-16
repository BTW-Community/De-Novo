package btw.community.denovo.recipes;

import btw.block.BTWBlocks;
import btw.community.denovo.block.DNBlocks;
import btw.community.denovo.block.blocks.ComposterBlock;
import btw.community.denovo.item.DNItems;
import btw.crafting.recipe.RecipeManager;
import btw.item.BTWItems;
import net.minecraft.src.Block;
import net.minecraft.src.FurnaceRecipes;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

public class DNRecipes {
    public static void addRecipes() {
        addCraftingRecipes();
        addSieveRecipes();
        addMaggotsRecipes();
        addComposterRecipes();
        addCisternRecipes();
    }

    private static void addCraftingRecipes() {
        RecipeManager.addRecipe(new ItemStack(DNBlocks.sieve), new Object[]{
                "TT",
                "SS",
                'T', new ItemStack(Item.silk),
                'S', new ItemStack(Item.stick),
        });

        RecipeManager.addShapelessRecipe(new ItemStack(Item.bowlEmpty),
                new Object[]{
                        new ItemStack(BTWItems.wickerPane),
                        new ItemStack(BTWItems.wickerPane),
                        new ItemStack(Item.clay)
                });
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
        RecipeManager.addRecipe(new ItemStack(DNBlocks.composter), new Object[]{
                "SS",
                "CC",
                'S', new ItemStack(Item.stick),
                'C', new ItemStack(BTWBlocks.unlitCampfire)
        });

        //Compostables
        ComposterBlock.validCompostables.add(new ItemStack(DNItems.rawMaggots));

        ComposterBlock.validCompostables.add(new ItemStack(Item.stick));
        ComposterBlock.validCompostables.add(new ItemStack(BTWItems.sawDust));
        for (int type = 0; type < 5; type++) {
            ComposterBlock.validCompostables.add(new ItemStack(BTWItems.bark, 1, type));
        }

        ComposterBlock.validCompostables.add(new ItemStack(Item.poisonousPotato));
        ComposterBlock.validCompostables.add(new ItemStack(BTWItems.hemp));
        ComposterBlock.validCompostables.add(new ItemStack(BTWItems.hempFibers));

        ComposterBlock.validCompostables.add(new ItemStack(Item.rottenFlesh));
        ComposterBlock.validCompostables.add(new ItemStack(Item.spiderEye));
        ComposterBlock.validCompostables.add(new ItemStack(Item.fermentedSpiderEye));
        ComposterBlock.validCompostables.add(new ItemStack(BTWItems.creeperOysters));
        ComposterBlock.validCompostables.add(new ItemStack(BTWItems.batWing));
        ComposterBlock.validCompostables.add(new ItemStack(BTWItems.mysteriousGland));

        ComposterBlock.validCompostables.add(new ItemStack(Block.deadBush));
        ComposterBlock.validCompostables.add(new ItemStack(Block.plantRed));
        ComposterBlock.validCompostables.add(new ItemStack(Block.plantYellow));
        ComposterBlock.validCompostables.add(new ItemStack(Block.cocoaPlant));
        ComposterBlock.validCompostables.add(new ItemStack(Block.vine));
        ComposterBlock.validCompostables.add(new ItemStack(Block.waterlily));
        ComposterBlock.validCompostables.add(new ItemStack(BTWItems.redMushroom));
        ComposterBlock.validCompostables.add(new ItemStack(BTWItems.brownMushroom));

        for (int type = 0; type < 5; type++) {
            ComposterBlock.validCompostables.add(new ItemStack(Block.wood, 1, type));
        }
        for (int type = 0; type < 4; type++) {
            ComposterBlock.validCompostables.add(new ItemStack(Block.leaves, 1, type));
        }
        for (int type = 0; type < 16; type++) {
            ComposterBlock.validCompostables.add(new ItemStack(Block.sapling, 1, type));
        }
        for (int type = 0; type < 3; type++) {
            ComposterBlock.validCompostables.add(new ItemStack(Block.tallGrass, 1, type));
        }

    }

    private static void addCisternRecipes() {
        //remove old recipe
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.cauldron, 1), new Object[]{"# #", "# #", "###", '#', Item.ingotIron});
        //add new recipe
        RecipeManager.addRecipe(new ItemStack(DNItems.cistern, 1), new Object[]{"# #", "# #", "###", '#', Item.ingotIron});

    }


    private static void addSieveRecipes() {
        // Dirt sifting
        SiftingCraftingManager.addSiftingRecipe(
                new LootEntry[]{
                        new LootEntry(1.0D / 2, 1, new ItemStack(BTWItems.stone)),
                        new LootEntry(1.0D / 5, 1, new ItemStack(Block.sapling)),
                        new LootEntry(1.0D / 10, 1, new ItemStack(BTWItems.sugarCaneRoots))
                },
                new ItemStack(BTWBlocks.aestheticEarth, 1, 7),
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
