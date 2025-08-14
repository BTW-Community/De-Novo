package btw.community.denovo.emi;

import btw.block.BTWBlocks;
import btw.block.blocks.AestheticOpaqueEarthBlock;
import btw.community.denovo.block.DNBlocks;
import btw.community.denovo.item.DNItems;
import btw.community.denovo.recipes.LootEntry;
import btw.community.denovo.recipes.SiftingCraftingManager;
import btw.community.denovo.recipes.SiftingRecipe;
import btw.community.denovo.utils.CisternUtils;
import btw.item.BTWItems;
import emi.dev.emi.emi.api.EmiPlugin;
import emi.dev.emi.emi.api.EmiRegistry;
import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.api.recipe.EmiRecipeCategory;
import emi.dev.emi.emi.api.render.EmiTexture;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.recipe.btw.EmiProgressiveRecipe;
import emi.dev.emi.emi.runtime.EmiReloadLog;
import emi.shims.java.net.minecraft.util.SyntheticIdentifier;
import net.minecraft.src.*;

import java.util.ArrayList;
import java.util.function.Supplier;

public class DeNovoEmiPlugin implements EmiPlugin {

    private static final EmiTexture SHIFT_RIGHT_CLICK_TEXTURE = new EmiTexture(new ResourceLocation("denovo", "textures/emi/shift_right_click.png"), 0, 0, 20, 20, 20, 20, 20, 20);


    static {
        DeNovoEmiRecipeCategories.SIEVE = DeNovoEmiPlugin.category("sieve",  EmiStack.of(DNBlocks.sieve));
        DeNovoEmiRecipeCategories.CISTERN = DeNovoEmiPlugin.category("cistern",  EmiStack.of(DNBlocks.cistern));
        DeNovoEmiRecipeCategories.COMPOSTER = DeNovoEmiPlugin.category("composter",  EmiStack.of(DNBlocks.composter));
    }

    @Override
    public void register(EmiRegistry reg) {
        addCategories(reg);

        addInWorldRecipes(reg);
        addProgressiveCraftingRecipes(reg);
        addSiftingRecipes(reg);
    }

    private static void addCategories(EmiRegistry reg) {
        reg.addCategory(DeNovoEmiRecipeCategories.SIEVE);
        reg.addWorkstation(DeNovoEmiRecipeCategories.SIEVE, EmiStack.of(new ItemStack(DNBlocks.sieve, 1, 0)));

        reg.addCategory(DeNovoEmiRecipeCategories.CISTERN);
        reg.addWorkstation(DeNovoEmiRecipeCategories.CISTERN, EmiStack.of(new ItemStack(DNBlocks.cistern, 1, 0)));

        reg.addCategory(DeNovoEmiRecipeCategories.COMPOSTER);
        reg.addWorkstation(DeNovoEmiRecipeCategories.COMPOSTER, EmiStack.of(new ItemStack(DNBlocks.composter, 1, 0)));
    }

    private static void addInWorldRecipes(EmiRegistry reg){
        addRummagingInteractionRecipes(reg);
        addGoldenDungInteractionRecipes(reg);
        addComposterInteractionRecipes(reg);
        addCisternInteractionRecipes(reg);

        addComposterProcessingRecipe(reg, "composter/process_maggot_creation",
                DNBlocks.composter,
                CisternUtils.CONTENTS_COMPOST, CisternUtils.CONTENTS_MAGGOTS,
                CisternUtils.MAGGOT_CREATION_TIME,
                "denovo.emi.compost", "denovo.emi.maggots");

        addComposterProcessingRecipe(reg,"composter/process_clay_water",
                DNBlocks.composter,
                CisternUtils.CONTENTS_CLAY_WATER, CisternUtils.CONTENTS_INFECTED_WATER,
                CisternUtils.CLAY_WATER_CONVERSION_TIME,
                "denovo.emi.water.clay", "denovo.emi.water.infected");

        addComposterProcessingRecipe(reg, "composter/process_infected_water",
                DNBlocks.composter,
                CisternUtils.CONTENTS_INFECTED_WATER, CisternUtils.CONTENTS_RUST_WATER,
                CisternUtils.INFECTED_WATER_CONVERSION_TIME,
                "denovo.emi.water.infected.dirt", "denovo.emi.water.rust");

        //Cistern
        addCisternProcessingRecipe(reg,"cistern/process_clay_water",
                CisternUtils.CONTENTS_CLAY_WATER, CisternUtils.CONTENTS_INFECTED_WATER,
                CisternUtils.CLAY_WATER_CONVERSION_TIME,
                "denovo.emi.water.clay", "denovo.emi.water.infected");

        addCisternProcessingRecipe(reg,"cistern/process_muddy_water",
                CisternUtils.CONTENTS_MUDDY_WATER, CisternUtils.CONTENTS_WATER,
                CisternUtils.MUDDY_WATER_SETTLE_TIME,
                "denovo.emi.water.clay", "denovo.emi.water");

        addCisternProcessingRecipe(reg, "cistern/process_infected_water",
                CisternUtils.CONTENTS_INFECTED_WATER, CisternUtils.CONTENTS_RUST_WATER,
                CisternUtils.INFECTED_WATER_CONVERSION_TIME,
                "denovo.emi.water.infected.dirt", "denovo.emi.water.rust");
    }

    private static void addRummagingInteractionRecipes(EmiRegistry reg) {
        reg.addRecipe(EmiRummagingWorldInteractionRecipe.builder().id(
                        new ResourceLocation("denovo", "/world/block_interaction/denovo/rummaging_dead_bush"))
                .leftInput(EmiStack.EMPTY)
                .renderPlusOverlay(SHIFT_RIGHT_CLICK_TEXTURE)
                .rightInput(EmiStack.of(Block.grass), false)
                .output(EmiStack.of(new ItemStack(Block.deadBush, 1, 3)))
                .setArrowToolTip("denovo.emi.hunger.rummaging")
                .setRenderBack(true, false,false)
                .supportsRecipeTree(true).build());
    }

    private static void addGoldenDungInteractionRecipes(EmiRegistry reg) {

        ArrayList validBlocks = new ArrayList();
        validBlocks.add(EmiStack.of(Block.bedrock));
        validBlocks.add(EmiStack.of(Block.grass));
//        validBlocks.add(EmiStack.of(BTWBlocks.looseSparseGrass)); //sparse
        validBlocks.add(EmiStack.of(Block.dirt));
        validBlocks.add(EmiStack.of(BTWBlocks.looseDirt));
        validBlocks.add(EmiStack.of(BTWBlocks.planterWithSoil));
        validBlocks.add(EmiStack.of(new ItemStack(BTWBlocks.aestheticEarth, 1, AestheticOpaqueEarthBlock.SUBTYPE_BLIGHT_LEVEL_0)));
        validBlocks.add(EmiStack.of(new ItemStack(BTWBlocks.aestheticEarth, 1, AestheticOpaqueEarthBlock.SUBTYPE_BLIGHT_LEVEL_1)));
        validBlocks.add(EmiStack.of(new ItemStack(BTWBlocks.aestheticEarth, 1, AestheticOpaqueEarthBlock.SUBTYPE_BLIGHT_LEVEL_2)));
        validBlocks.add(EmiStack.of(new ItemStack(BTWBlocks.aestheticEarth, 1, AestheticOpaqueEarthBlock.SUBTYPE_BLIGHT_ROOTS_LEVEL_2)));
        validBlocks.add(EmiStack.of(new ItemStack(BTWBlocks.aestheticEarth, 1, AestheticOpaqueEarthBlock.SUBTYPE_BLIGHT_LEVEL_3)));
        validBlocks.add(EmiStack.of(new ItemStack(BTWBlocks.aestheticEarth, 1, AestheticOpaqueEarthBlock.SUBTYPE_BLIGHT_ROOTS_LEVEL_3)));

        reg.addRecipe(EmiGoldenDungWorldInteractionRecipe.builder().id(
                        new ResourceLocation("denovo", "/world/block_interaction/denovo/golden_dung"))
                .leftInput(EmiStack.of(BTWItems.goldenDung))
                .rightInput(EmiIngredient.of(validBlocks.stream().toList()), false)
                .output(EmiIngredient.of(validBlocks.stream().toList()))
                .setRenderBack(true, false,false)
                .supportsRecipeTree(true).build());
    }

    private static void addCisternProcessingRecipe(EmiRegistry reg, String id, int inputType, int outputType, int processingTime, String inputString, String outputString) {
        addRecipeSafe(reg, () -> new EmiCisternRecipe(new ResourceLocation("denovo", id),
                new ItemStack(DNBlocks.cistern, 1, CisternUtils.pack(15, 0, inputType, 0)),
                EmiStack.of(new ItemStack(DNBlocks.cistern, 1, CisternUtils.pack(15, 0, outputType, 0))),
                processingTime / 20, // converted to s
                inputString, outputString
        ));
    }

    private static void addComposterProcessingRecipe(EmiRegistry reg, String id, Block block, int inputType, int outputType, int processingTime, String inputString, String outputString) {
        if (inputType == CisternUtils.CONTENTS_COMPOST){
            addRecipeSafe(reg, () -> new EmiComposterRecipe(new ResourceLocation("denovo", id),
                    new ItemStack(block, 1, CisternUtils.pack(0, 16, inputType, 0)),
                    EmiStack.of( new ItemStack(block, 1, CisternUtils.pack(0, 16, outputType, 0))),
                    processingTime / 20, // converted to s
                    inputString, outputString
            ));
        }
        else if (inputType == CisternUtils.CONTENTS_INFECTED_WATER){
            addRecipeSafe(reg, () -> new EmiComposterRecipe(new ResourceLocation("denovo", id),
                    new ItemStack(DNBlocks.composter, 1, CisternUtils.pack(15, 16, inputType, 0)),
                    EmiStack.of( new ItemStack(DNBlocks.composter, 1, CisternUtils.pack(0, 16, outputType, CisternUtils.INFECTED_WATER_CONVERSION_TIME))),
                    processingTime / 20, // converted to s
                    inputString, outputString
            ));
        }
        else {
            addRecipeSafe(reg, () -> new EmiComposterRecipe(new ResourceLocation("denovo", id),
                    new ItemStack(DNBlocks.composter, 1, CisternUtils.pack(15, 0, inputType, 0)),
                    EmiStack.of( new ItemStack(DNBlocks.composter, 1, CisternUtils.pack(15, 0, outputType, 0))),
                    processingTime / 20, // converted to s
                    inputString, outputString
            ));
        }


    }

    private static void addCisternInteractionRecipes(EmiRegistry reg) {
        //Rain filling
        int[] liquids = {5, 9, 12, 15 };
        reg.addRecipe(EmiCustomWorldInteractionRecipe.builder().id(
                        new ResourceLocation("denovo", "/world/block_interaction/denovo/cistern_rain_filling"))
                .leftInput(new EmiRainStack())
                .rightInput(EmiStack.of(DNBlocks.cistern), false)
                .output(EmiStack.of(new ItemStack(DNBlocks.cistern, 1, CisternUtils.pack(0, 0, CisternUtils.CONTENTS_WATER, 0))))
                .animateOutputContents(liquids, null)
                .setArrowToolTip("denovo.emi.cistern.rain")
                .setRenderBack(false, false,false)
                .supportsRecipeTree(true).build());

        //Filling with water
        ArrayList waterContainers = new ArrayList();
        waterContainers.add(EmiStack.of(DNItems.waterBowl));
        waterContainers.add(EmiStack.of(new ItemStack(Item.potion, 1, 0)));
        liquids = new int[]{5, 9, 12, 15 };
        reg.addRecipe(EmiCustomWorldInteractionRecipe.builder().id(
                        new ResourceLocation("denovo", "/world/block_interaction/denovo/cistern_water_filling"))
                .leftInput(EmiIngredient.of(waterContainers.stream().toList()))
                .rightInput(EmiStack.of(DNBlocks.cistern), false)
                .output(EmiStack.of(new ItemStack(DNBlocks.cistern, 1, CisternUtils.pack(15, 0, CisternUtils.CONTENTS_WATER, 0))))
                .animateOutputContents(liquids, null)
                .setRenderBack(true, false,false)
                .supportsRecipeTree(true).build());

        //bucket
        liquids = new int[]{5, 15 };
        reg.addRecipe(EmiCustomWorldInteractionRecipe.builder().id(
                        new ResourceLocation("denovo", "/world/block_interaction/denovo/cistern_bucket_filling"))
                .leftInput(EmiStack.of(Item.bucketWater))
                .rightInput(EmiStack.of(DNBlocks.cistern), false)
                .output(EmiStack.of(new ItemStack(DNBlocks.cistern, 1, CisternUtils.pack(15, 0, CisternUtils.CONTENTS_WATER, 0))))
                .animateOutputContents(liquids, null)
                .setRenderBack(true, false,false)
                .supportsRecipeTree(true).build());

        //clay
        reg.addRecipe(EmiCustomWorldInteractionRecipe.builder().id(
                        new ResourceLocation("denovo", "/world/block_interaction/denovo/cistern_clay_water"))
                .leftInput(EmiStack.of(Item.clay))
                .rightInput(EmiStack.of(new ItemStack(DNBlocks.cistern, 1, CisternUtils.pack(15, 0, CisternUtils.CONTENTS_WATER,0))), false)
                .output(EmiStack.of(new ItemStack(DNBlocks.cistern, 1, CisternUtils.pack(15, 0, CisternUtils.CONTENTS_CLAY_WATER, 0))))
                .setRenderBack(true, false,false)
                .supportsRecipeTree(true).build());

        //dirt
        reg.addRecipe(EmiCustomWorldInteractionRecipe.builder().id(
                        new ResourceLocation("denovo", "/world/block_interaction/denovo/cistern_muddy_water"))
                .leftInput(EmiStack.of(BTWItems.dirtPile))
                .rightInput(EmiStack.of(new ItemStack(DNBlocks.cistern, 1, CisternUtils.pack(15, 0, CisternUtils.CONTENTS_WATER,0))), false)
                .output(EmiStack.of(new ItemStack(DNBlocks.cistern, 1, CisternUtils.pack(15, 0, CisternUtils.CONTENTS_MUDDY_WATER, 0))))
                .setRenderBack(true, false,false)
                .supportsRecipeTree(true).build());

        //dirt
        int[] solids = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
        reg.addRecipe(EmiCustomWorldInteractionRecipe.builder().id(
                        new ResourceLocation("denovo", "/world/block_interaction/denovo/cistern_infected_water"))
                .leftInput(EmiStack.of(BTWItems.dirtPile))
                .rightInput(EmiStack.of(new ItemStack(DNBlocks.cistern, 1, CisternUtils.pack(15, 0, CisternUtils.CONTENTS_INFECTED_WATER,0))), false)
                .output(EmiStack.of(new ItemStack(DNBlocks.cistern, 1, CisternUtils.pack(15, 0, CisternUtils.CONTENTS_INFECTED_WATER, 0))))
                .animateOutputContents(null, solids)
                .setRenderBack(true, false,false)
                .supportsRecipeTree(true).build());

        //rust
        reg.addRecipe(EmiCustomWorldInteractionRecipe.builder().id(
                        new ResourceLocation("denovo", "/world/block_interaction/denovo/cistern_rust_water"))
                .leftInput(EmiStack.of(Item.bowlEmpty))
                .rightInput(EmiStack.of(new ItemStack(DNBlocks.cistern, 1, CisternUtils.pack(15, 0, CisternUtils.CONTENTS_RUST_WATER,0))), false)
                .output(EmiStack.of(DNItems.rustWaterBowl))
                .setRenderBack(true, false,false)
                .supportsRecipeTree(true).build());

    }

    private static void addComposterInteractionRecipes(EmiRegistry reg) {
        //Rain
        int[] liquids = {5, 9, 12, 15 };
        reg.addRecipe(EmiCustomWorldInteractionRecipe.builder().id(
                        new ResourceLocation("denovo", "/world/block_interaction/denovo/composter_rain_filling"))
                .leftInput(new EmiRainStack())
                .rightInput(EmiStack.of(DNBlocks.composter), false)
                .output(EmiStack.of(new ItemStack(DNBlocks.composter, 1, CisternUtils.pack(0, 0, CisternUtils.CONTENTS_WATER, 0))))
                .animateOutputContents(liquids, null)
                .setArrowToolTip("denovo.emi.arrow.rain")
                .setRenderBack(false, false,false)
                .supportsRecipeTree(true).build());

        //Filling with water
        ArrayList waterContainers = new ArrayList();
        waterContainers.add(EmiStack.of(DNItems.waterBowl));
        waterContainers.add(EmiStack.of(new ItemStack(Item.potion, 1, 0)));
        reg.addRecipe(EmiCustomWorldInteractionRecipe.builder().id(
                new ResourceLocation("denovo", "/world/block_interaction/denovo/composter_water_filling"))
                .leftInput(EmiIngredient.of(waterContainers.stream().toList()))
                .rightInput(EmiStack.of(DNBlocks.composter), false)
                .output(EmiStack.of(new ItemStack(DNBlocks.composter, 1, CisternUtils.pack(15, 0, CisternUtils.CONTENTS_WATER, 0))))
                .animateOutputContents(liquids, null)
                .setRenderBack(true, false,false)
                .supportsRecipeTree(true).build());

        //Composting
        ArrayList compostables = new ArrayList();
        for (ItemStack item : CisternUtils.validCompostables) { compostables.add(EmiStack.of(item)); }
        int[] solids = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };

        reg.addRecipe(EmiCustomWorldInteractionRecipe.builder().id(
                        new ResourceLocation("denovo", "/world/block_interaction/denovo/composter_composting"))
                .leftInput(EmiIngredient.of(compostables.stream().toList()))
                .rightInput(EmiStack.of(DNBlocks.composter), false)
                .output(EmiStack.of(new ItemStack(DNBlocks.composter, 1, CisternUtils.pack(0, 16, CisternUtils.CONTENTS_INFECTED_WATER, 0))))
                .animateOutputContents(null, solids)
                .setArrowToolTip("denovo.emi.composter.composting")
                .setRenderBack(true, false,false)
                .supportsRecipeTree(true).build());

        //Removing Dirt
        reg.addRecipe(EmiCustomWorldInteractionRecipe.builder().id(
                        new ResourceLocation("denovo", "/world/block_interaction/denovo/composter_dirt"))
                .leftInput(EmiStack.EMPTY)
                .rightInput(EmiStack.of(new ItemStack(DNBlocks.composter, 1, CisternUtils.pack(0, 16, CisternUtils.CONTENTS_COMPOST, 0))), false)
                .output(EmiStack.of(BTWItems.dirtPile))
                .setRenderBack(true, false,true)
                .supportsRecipeTree(true).build());

        //Removing Maggots
        reg.addRecipe(EmiCustomWorldInteractionRecipe.builder().id(
                        new ResourceLocation("denovo", "/world/block_interaction/denovo/composter_maggots"))
                .leftInput(EmiStack.EMPTY)
                .rightInput(EmiStack.of(new ItemStack(DNBlocks.composter, 1, CisternUtils.pack(0, 16, CisternUtils.CONTENTS_MAGGOTS, 0))), false)
                .output(EmiStack.of(DNItems.rawMaggots))
                .setRenderBack(true, false,true)
                .supportsRecipeTree(true).build());

        reg.addRecipe(EmiCustomWorldInteractionRecipe.builder().id(
                        new ResourceLocation("denovo", "/world/block_interaction/denovo/composter_maggots_dirt"))
                .leftInput(EmiStack.EMPTY)
                .rightInput(EmiStack.of(new ItemStack(DNBlocks.composter, 1, CisternUtils.pack(0, 16, CisternUtils.CONTENTS_MAGGOTS, 0))), false)
                .output(EmiStack.of(BTWItems.dirtPile))
                .setRenderBack(true, false,true)
                .supportsRecipeTree(true).build());
    }

    private static void addProgressiveCraftingRecipes(EmiRegistry reg) {
        // Progressive Crafting
        addRecipeSafe(reg, () -> new EmiProgressiveRecipe(new ResourceLocation("denovo", "maggots_silk_extraction"),
                new ItemStack(DNItems.maggotsSilkExtraction), new ItemStack(Item.silk)));
        addRecipeSafe(reg, () -> new EmiProgressiveRecipe(new ResourceLocation("denovo", "rust_water_bowl"),
                new ItemStack(DNItems.rustWaterBowl), new ItemStack(DNItems.ironDust)));
    }

    private static void addSiftingRecipes(EmiRegistry reg) {
        // Sieve
        for (SiftingRecipe siftingRecipe : SiftingCraftingManager.getRecipes()){
            LootEntry[] output = siftingRecipe.getLootTable();
            ItemStack filterUsed = siftingRecipe.getFilterUsed();
            ItemStack input = siftingRecipe.getInput();
            boolean containsSouls = siftingRecipe.getContainsSouls();
            addRecipeSafe(reg, () -> new EmiSieveRecipe(new SyntheticIdentifier((SiftingRecipe)siftingRecipe),
                (LootEntry[]) output, (ItemStack) input, (ItemStack)filterUsed, false));

        }
    }

    public static EmiRecipeCategory category(String id, EmiStack icon) {
        return new EmiRecipeCategory(new ResourceLocation("denovo", id), icon, new EmiTexture(new ResourceLocation("denovo", "textures/simple_icons/" + id + ".png"), 0, 0, 16, 16, 16, 16, 16, 16));
    }

    private static void addRecipeSafe(EmiRegistry registry, Supplier<EmiRecipe> supplier) {
        try {
            registry.addRecipe(supplier.get());
        } catch (Throwable e) {
            EmiReloadLog.warn("Exception when parsing EMI/DeNovo recipe (no ID available)");
            EmiReloadLog.error(e);
        }
    }

    private static void addRecipeSafe(EmiRegistry registry, Supplier<EmiRecipe> supplier, IRecipe recipe) {
        try {
            registry.addRecipe(supplier.get());
        } catch (Throwable e) {
            EmiReloadLog.warn("Exception when parsing DeNovo recipe " + recipe);
            EmiReloadLog.error(e);
        }
    }
}
