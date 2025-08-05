package btw.community.denovo.recipes;

import btw.block.BTWBlocks;
import btw.community.denovo.block.DNBlocks;
import btw.community.denovo.item.DNItems;
import btw.community.denovo.item.items.RustWaterBowlItem;
import btw.community.denovo.utils.CisternUtils;
import btw.crafting.recipe.RecipeManager;
import btw.item.BTWItems;
import net.minecraft.src.Block;
import net.minecraft.src.FurnaceRecipes;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

public class DNRecipes {
    public static void addRecipes() {
        addBowlsRecipes();
        addSieveRecipes();
        addMaggotsRecipes();
        addComposterRecipes();
        addCisternRecipes();
        addSickleRecipes();
        addHoeRecipes();
        addHammerRecipes();
        addCharcoalRecipes();
    }

    private static void addCharcoalRecipes() {
        RecipeManager.addShapelessRecipe(new ItemStack(Item.coal,1, 1), new Object[]{
                new ItemStack(DNItems.charcoalDust),
                new ItemStack(DNItems.charcoalDust),
                new ItemStack(DNItems.charcoalDust),
                new ItemStack(DNItems.charcoalDust)
        });
    }

    private static void addHammerRecipes() {
        RecipeManager.addShapelessRecipe(new ItemStack( DNItems.flintHammer ), new Object[] {
                new ItemStack(Item.silk),
                new ItemStack(Item.stick),
                new ItemStack(Item.flint),
                new ItemStack(Item.flint),
        });
    }

    private static void addHoeRecipes() {
        RecipeManager.addShapelessRecipe(new ItemStack( Item.hoeStone ), new Object[] {
                new ItemStack(Item.silk),
                new ItemStack(Item.stick),
                new ItemStack(BTWItems.sharpStone),
        });
    }

    private static void addSickleRecipes() {
        RecipeManager.addShapelessRecipe(new ItemStack(DNItems.woodSickle), new Object[]{
                new ItemStack(Item.silk),
                new ItemStack(Item.stick),
                new ItemStack(BTWItems.pointyStick),
                new ItemStack(BTWItems.pointyStick)
        });
    }

    private static void addBowlsRecipes() {

        RecipeManager.addShapelessRecipe(new ItemStack(Item.bowlEmpty), new Object[]{
                new ItemStack(BTWItems.wickerPane),
                new ItemStack(BTWItems.wickerPane),
                new ItemStack(Item.clay)
        });
    }

    private static void addMaggotsRecipes() {
        //Check FishingRodBaitingRecipeMixin for baiting fishing rod

        //Maggots direct to string
        /*
        RecipeManager.addShapelessRecipe(new ItemStack(Item.silk),
                new Object[]{
                        new ItemStack(DNItems.rawMaggots)
                });
         */

        //Maggots progressively to String
        RecipeManager.addShapelessRecipe(new ItemStack(DNItems.maggotsSilkExtraction, 1, RustWaterBowlItem.TIME_TO_CRAFT), new Object[] {
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

        //
        RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.ironOrePile), new Object[] {
                new ItemStack(DNItems.ironDust),
                new ItemStack(DNItems.ironDust),
                new ItemStack(DNItems.ironDust),
                new ItemStack(DNItems.ironDust)
        });

        // Valid Dirts
        CisternUtils.validDirt.add(new ItemStack(BTWItems.dirtPile));
        CisternUtils.validDirt.add(new ItemStack(BTWItems.gravelPile));

        //Compostables
        //CisternUtils.validCompostables.add(new ItemStack(DNItems.rawMaggots));

        CisternUtils.validCompostables.add(new ItemStack(Item.stick));
        CisternUtils.validCompostables.add(new ItemStack(BTWItems.sawDust));
        for (int type = 0; type < 5; type++) {
            CisternUtils.validCompostables.add(new ItemStack(BTWItems.bark, 1, type));
        }

        CisternUtils.validCompostables.add(new ItemStack(Item.poisonousPotato));
        CisternUtils.validCompostables.add(new ItemStack(Item.reed));
        CisternUtils.validCompostables.add(new ItemStack(BTWItems.sugarCaneRoots));
        CisternUtils.validCompostables.add(new ItemStack(BTWItems.hemp));
        CisternUtils.validCompostables.add(new ItemStack(BTWItems.hempFibers));

        CisternUtils.validCompostables.add(new ItemStack(Item.rottenFlesh));
        CisternUtils.validCompostables.add(new ItemStack(Item.spiderEye));
        CisternUtils.validCompostables.add(new ItemStack(Item.fermentedSpiderEye));
        CisternUtils.validCompostables.add(new ItemStack(BTWItems.creeperOysters));
        CisternUtils.validCompostables.add(new ItemStack(BTWItems.batWing));
        CisternUtils.validCompostables.add(new ItemStack(BTWItems.mysteriousGland));

        CisternUtils.validCompostables.add(new ItemStack(Block.deadBush));
        CisternUtils.validCompostables.add(new ItemStack(Block.plantRed));
        CisternUtils.validCompostables.add(new ItemStack(Block.plantYellow));
        CisternUtils.validCompostables.add(new ItemStack(Block.cocoaPlant));
        CisternUtils.validCompostables.add(new ItemStack(Block.vine));
        CisternUtils.validCompostables.add(new ItemStack(Block.waterlily));
        CisternUtils.validCompostables.add(new ItemStack(BTWItems.redMushroom));
        CisternUtils.validCompostables.add(new ItemStack(BTWItems.brownMushroom));

        for (int type = 0; type < 5; type++) {
            CisternUtils.validCompostables.add(new ItemStack(Block.wood, 1, type));
        }
        for (int type = 0; type < 4; type++) {
            CisternUtils.validCompostables.add(new ItemStack(Block.leaves, 1, type));
        }
        for (int type = 0; type < 16; type++) {
            CisternUtils.validCompostables.add(new ItemStack(Block.sapling, 1, type));
        }
        for (int type = 0; type < 3; type++) {
            CisternUtils.validCompostables.add(new ItemStack(Block.tallGrass, 1, type));
        }
    }

    private static void addCisternRecipes() {
        //remove old recipe
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.cauldron, 1), new Object[]{"# #", "# #", "###", '#', Item.ingotIron});
        //add new recipe
        RecipeManager.addRecipe(new ItemStack(DNBlocks.cistern, 1), new Object[]{"# #", "# #", "###", '#', Item.ingotIron});

    }

    private static void addSieveRecipes() {
        // Crafting
        RecipeManager.addShapelessRecipe(new ItemStack(DNItems.mesh), new Object[]{
                new ItemStack(Item.silk),
                new ItemStack(Item.silk),
                new ItemStack(Item.silk),
                new ItemStack(Item.silk),
        });

        RecipeManager.addRecipe(new ItemStack(DNBlocks.sieve), new Object[]{
                "TT",
                "SS",
                'T', new ItemStack(Item.silk),
                'S', new ItemStack(Item.stick),
        });

        // Dirt sifting
        SiftingCraftingManager.addSiftingRecipe(
                new LootEntry[]{
                        new LootEntry(1.0D / 2, 1, new ItemStack(BTWItems.stone)),
                        new LootEntry(1.0D / 20, 1, new ItemStack(BTWBlocks.oakSapling)),
                        new LootEntry(1.0D / 20, 1, new ItemStack(BTWBlocks.spruceSapling)),
                        new LootEntry(1.0D / 20, 1, new ItemStack(BTWBlocks.birchSapling)),
                        new LootEntry(1.0D / 20, 1, new ItemStack(BTWBlocks.jungleSapling)),
                        new LootEntry(1.0D / 10, 1, new ItemStack(BTWItems.sugarCaneRoots))
                },
                new ItemStack(BTWItems.dirtPile),
                new ItemStack(DNItems.mesh)
        );

        SiftingCraftingManager.addSiftingRecipe(
                new LootEntry[]{
                        new LootEntry(1.0D / 2, 4, new ItemStack(BTWItems.stone)),
                        new LootEntry(1.0D / 20, 4, new ItemStack(BTWBlocks.oakSapling)),
                        new LootEntry(1.0D / 20, 4, new ItemStack(BTWBlocks.spruceSapling)),
                        new LootEntry(1.0D / 20, 4, new ItemStack(BTWBlocks.birchSapling)),
                        new LootEntry(1.0D / 20, 4, new ItemStack(BTWBlocks.jungleSapling)),
                        new LootEntry(1.0D / 10, 4, new ItemStack(BTWItems.sugarCaneRoots))
                },
                new ItemStack(BTWBlocks.looseDirtSlab),
                new ItemStack(DNItems.mesh)
        );

        SiftingCraftingManager.addSiftingRecipe(
                new LootEntry[]{
                        new LootEntry(1.0D / 2, 8, new ItemStack(BTWItems.stone)),
                        new LootEntry(1.0D / 20, 8, new ItemStack(BTWBlocks.oakSapling)),
                        new LootEntry(1.0D / 20, 8, new ItemStack(BTWBlocks.spruceSapling)),
                        new LootEntry(1.0D / 20, 8, new ItemStack(BTWBlocks.birchSapling)),
                        new LootEntry(1.0D / 20, 8, new ItemStack(BTWBlocks.jungleSapling)),
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

        SiftingCraftingManager.addSiftingRecipe(
                new LootEntry[]{
                        new LootEntry(1.0D / 2, 4, new ItemStack(BTWItems.stone)),
                        new LootEntry(1.0D / 2, 4, new ItemStack(BTWItems.gravelPile))
                },
                new ItemStack(BTWBlocks.looseCobblestoneSlab),
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
                        new LootEntry(1.0D / 4, 1, new ItemStack(Item.flint))
                },
                new ItemStack(BTWBlocks.sandAndGravelSlab, 1, 0), //0 is gravel, 1 is sand
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
                        new LootEntry(1.0D / 3, 8, new ItemStack(Item.pumpkinSeeds)),
                        new LootEntry(1.0D / 3, 8, new ItemStack(Item.melonSeeds)),
                        new LootEntry(1.0D / 3, 8, new ItemStack(Item.seeds)),
                },
                new ItemStack(BTWBlocks.aestheticEarth, 1, 7),
                new ItemStack(DNItems.mesh)
        );

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
