package btw.community.denovo.emi;

import btw.block.BTWBlocks;
import btw.community.denovo.block.DNBlocks;
import btw.community.denovo.emi.custom.EmiGloomStack;
import btw.community.denovo.emi.custom.EmiHeartStack;
import btw.community.denovo.emi.custom.EmiRainStack;
import btw.community.denovo.emi.custom.EmiRightClickStack;
import btw.community.denovo.emi.recipes.EmiCharcoalRecipe;
import btw.community.denovo.emi.recipes.EmiCisternBaseRecipe;
import btw.community.denovo.emi.recipes.EmiSieveRecipe;
import btw.community.denovo.emi.tag.DeNovoTags;
import btw.community.denovo.item.DNItems;
import btw.community.denovo.recipes.LootEntry;
import btw.community.denovo.recipes.SiftingCraftingManager;
import btw.community.denovo.recipes.SiftingRecipe;
import btw.community.denovo.utils.CisternUtils;
import btw.item.BTWItems;
import btw.item.tag.BTWTags;
import emi.dev.emi.emi.api.EmiPlugin;
import emi.dev.emi.emi.api.EmiRegistry;
import emi.dev.emi.emi.api.plugin.BTWPlugin;
import emi.dev.emi.emi.api.recipe.EmiInfoRecipe;
import emi.dev.emi.emi.api.recipe.EmiRecipeCategory;
import emi.dev.emi.emi.api.recipe.EmiWorldInteractionRecipe;
import emi.dev.emi.emi.api.render.EmiTexture;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.recipe.btw.EmiProgressiveRecipe;
import emi.shims.java.net.minecraft.text.Text;
import emi.shims.java.net.minecraft.util.SyntheticIdentifier;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ResourceLocation;

import java.util.List;

public class DeNovoEmiPlugin implements EmiPlugin {

    private static final EmiTexture SHIFT_RIGHT_CLICK_TEXTURE = new EmiTexture(new ResourceLocation("denovo", "textures/emi/shift_right_click.png"), 0, 0, 20, 20, 20, 20, 20, 20);
    private static final EmiTexture USE_RIGHT_CLICK_TEXTURE = new EmiTexture(new ResourceLocation("denovo", "textures/emi/use_right_click.png"), 0, 0, 20, 20, 20, 20, 20, 20);

    //Cistern Damage Values
    private final int EMPTY = CisternUtils.pack(5, 0, CisternUtils.CONTENTS_EMPTY, 0);
    private final int WATER_5 = CisternUtils.pack(5, 0, CisternUtils.CONTENTS_WATER, 0);
    private final int WATER_10 = CisternUtils.pack(10, 0, CisternUtils.CONTENTS_WATER, 0);
    private final int WATER_15 = CisternUtils.pack(15, 0, CisternUtils.CONTENTS_WATER, 0);
    private final int WATER_MUDDY = CisternUtils.pack(15, 0, CisternUtils.CONTENTS_MUDDY_WATER, 0);
    private final int WATER_CLAY_0 = CisternUtils.pack(15, 0, CisternUtils.CONTENTS_CLAY_WATER, 0);
    private final int WATER_CLAY_50 = CisternUtils.pack(15, 0, CisternUtils.CONTENTS_CLAY_WATER, CisternUtils.CLAY_WATER_CONVERSION_TIME/2);
    private final int WATER_INFECTED = CisternUtils.pack(15, 0, CisternUtils.CONTENTS_INFECTED_WATER, 0);
    private final int WATER_INFECTED_DIRT = CisternUtils.pack(15, 16, CisternUtils.CONTENTS_INFECTED_WATER, 0);
    private final int WATER_RUST = CisternUtils.pack(15, 0, CisternUtils.CONTENTS_RUST_WATER, CisternUtils.INFECTED_WATER_CONVERSION_TIME);
    private final int COMPOST_16 = CisternUtils.pack(0, 16, CisternUtils.CONTENTS_COMPOST, 0);
    private final int MAGGOTS_16 = CisternUtils.pack(0, 16, CisternUtils.CONTENTS_MAGGOTS, 0);


    static {
        DeNovoEmiRecipeCategories.SIEVE = DeNovoEmiPlugin.category("sieve",  EmiStack.of(DNBlocks.sieve));
        DeNovoEmiRecipeCategories.CISTERN = DeNovoEmiPlugin.category("cistern",  EmiStack.of(DNBlocks.cistern));
        DeNovoEmiRecipeCategories.COMPOSTER = DeNovoEmiPlugin.category("composter",  EmiStack.of(DNBlocks.composter));
        DeNovoEmiRecipeCategories.CHARCOAL = DeNovoEmiPlugin.category("charcoal",  EmiStack.of(DNBlocks.charcoalPile));
    }

    @Override
    public void register(EmiRegistry reg) {
        addInfoRecipes(reg);
        addCategories(reg);

        addWorldInteractionRecipes(reg);
        addCisternBaseWorldInteractionRecipe(reg);
        addComposterOnlyRecipes(reg);
        addCisternBaseRecipes(reg);
        addSiftingRecipes(reg);
        addProgressiveCraftingRecipes(reg);
        addCharcoalProcessingRecipes(reg);
        addLavaWorldInteractionRecipes(reg);
    }



    private void addInfoRecipes(EmiRegistry reg) {
        this.info(reg, DNItems.woodSickle, "emi.denovo.sickle.info");
        this.info(reg, DNItems.flintHammer, "emi.denovo.flint_hammer.info");
        this.info(reg, DNBlocks.composter, WATER_15, "emi.denovo.composter_water.info");
    }


    private void addCategories(EmiRegistry reg) {
        reg.addCategory(DeNovoEmiRecipeCategories.SIEVE);
        reg.addWorkstation(DeNovoEmiRecipeCategories.SIEVE, EmiStack.of(new ItemStack(DNBlocks.sieve, 1, 0)));

        reg.addCategory(DeNovoEmiRecipeCategories.CISTERN);
        reg.addWorkstation(DeNovoEmiRecipeCategories.CISTERN, EmiStack.of(new ItemStack(DNBlocks.cistern, 1, 0)));

        reg.addCategory(DeNovoEmiRecipeCategories.COMPOSTER);
        reg.addWorkstation(DeNovoEmiRecipeCategories.COMPOSTER, EmiStack.of(new ItemStack(DNBlocks.composter, 1, 0)));

        reg.addCategory(DeNovoEmiRecipeCategories.CHARCOAL);
    }

    private void addWorldInteractionRecipes(EmiRegistry reg) {

        //Player shitting themselves
        reg.addRecipe(EmiWorldInteractionRecipe.builder().id(new ResourceLocation("denovo", "/world/block_interaction/denovo/gloom_dung"))
                .leftInput(new EmiGloomStack())
                .rightInput(new EmiHeartStack(), false, sw -> {
                    sw.appendTooltip(Text.translatable("emi.world_interaction.denovo.gloom_dung"));
                    return sw;
                })
                .output(EmiStack.of(new ItemStack(BTWItems.dung, 1, 0)).setChance(0.5F))
                .supportsRecipeTree(true)
                .build());

        //Composter Gloom Mushrooms
        reg.addRecipe(EmiWorldInteractionRecipe.builder().id(new ResourceLocation("denovo", "/world/block_interaction/denovo/composter_mushroom_spread"))
                .leftInput(new EmiGloomStack())
                .rightInput(EmiIngredient.of(List.of(
                        EmiStack.of(new ItemStack(DNBlocks.composter, 1, COMPOST_16)),
                        EmiStack.of(new ItemStack(DNBlocks.composter, 1, MAGGOTS_16))
                )), false)
                .output(EmiStack.of(BTWItems.brownMushroom), sw -> {
                    sw.appendTooltip(Text.translatable("emi.world_interaction.denovo.composter_mushroom"));
                    return sw;
                })
                .supportsRecipeTree(true)
                .build());


        //Golden Dung
        reg.addRecipe(EmiWorldInteractionRecipe.builder().id(new ResourceLocation("denovo", "/world/block_interaction/denovo/golden_dung"))
                .leftInput(EmiStack.of(new ItemStack(BTWItems.goldenDung, 1, 0)))
                .rightInput(EmiIngredient.of(DeNovoTags.validGoldenDungBushBlocks), false)
                .output(EmiStack.of(new ItemStack(Block.deadBush, 1, 0)))
                .supportsRecipeTree(true)
                .build());

        //Rummaging
        reg.addRecipe(EmiWorldInteractionRecipe.builder().id(new ResourceLocation("denovo", "/world/block_interaction/denovo/rummaging_dead_bush"))
                .leftInput(new EmiRightClickStack(), sw -> {
                    sw.appendTooltip(Text.translatable("emi.world_interaction.denovo.right_click"));
                    return sw;
                })
                .rightInput(EmiStack.of(Block.grass), false, sw -> {
                    sw.appendTooltip(Text.translatable("emi.world_interaction.denovo.hold_shift"));
                    return sw;
                })
                .output(EmiStack.of(new ItemStack(Block.deadBush, 1, 3)), sw -> {
                    sw.appendTooltip(Text.translatable("emi.world_interaction.denovo.rummaging_cost"));
                    return sw;
                })
                .build());

        //Placing sticks
        reg.addRecipe(EmiWorldInteractionRecipe.builder().id(new ResourceLocation("denovo", "/world/block_interaction/denovo/placing_sticks"))
                .leftInput(EmiStack.of(Item.stick), sw -> {
                    sw.appendTooltip(Text.translatable("emi.world_interaction.denovo.hold_shift"));
                    return sw;
                })
                .rightInput(EmiIngredient.of(
                        List.of(EmiStack.EMPTY,
                                EmiStack.of(new ItemStack(DNBlocks.placedSticks, 1, 0)),
                                EmiStack.of(new ItemStack(DNBlocks.placedSticks, 1, 1)),
                                EmiStack.of(new ItemStack(DNBlocks.placedSticks, 1, 2)),
                                EmiStack.of(new ItemStack(DNBlocks.placedSticks, 1, 3)),
                                EmiStack.of(new ItemStack(DNBlocks.placedSticks, 1, 4)),
                                EmiStack.of(new ItemStack(DNBlocks.placedSticks, 1, 5)),
                                EmiStack.of(new ItemStack(DNBlocks.placedSticks, 1, 6)),
                                EmiStack.of(new ItemStack(DNBlocks.placedSticks, 1, 7)),
                                EmiStack.of(new ItemStack(DNBlocks.placedSticks, 1, 8)),
                                EmiStack.of(new ItemStack(DNBlocks.placedSticks, 1, 9)),
                                EmiStack.of(new ItemStack(DNBlocks.placedSticks, 1, 10)),
                                EmiStack.of(new ItemStack(DNBlocks.placedSticks, 1, 11)),
                                EmiStack.of(new ItemStack(DNBlocks.placedSticks, 1, 12)),
                                EmiStack.of(new ItemStack(DNBlocks.placedSticks, 1, 13)),
                                EmiStack.of(new ItemStack(DNBlocks.placedSticks, 1, 14))
                        )
                ), false)
                .output(EmiStack.of(new ItemStack(DNBlocks.placedSticks,1,15)))
                .supportsRecipeTree(true)
                .build());

        //Water Source bowl
        reg.addRecipe(EmiWorldInteractionRecipe.builder().id(new ResourceLocation("denovo", "/world/block_interaction/denovo/water_bowl_from_source"))
                .leftInput(EmiStack.of(new ItemStack(Item.bowlEmpty, 1, 0)))
                .rightInput(EmiStack.of(Block.waterStill), false)
                .output(EmiStack.of(new ItemStack(DNItems.waterBowl, 1, 0)))
                .supportsRecipeTree(true)
                .build());

        //Water Source bucket
        reg.addRecipe(EmiWorldInteractionRecipe.builder().id(new ResourceLocation("denovo", "/world/block_interaction/denovo/water_bucket_from_source"))
                .leftInput(EmiStack.of(new ItemStack(Item.bucketEmpty, 1, 0)))
                .rightInput(EmiStack.of(Block.waterStill), false)
                .output(EmiStack.of(new ItemStack(Item.bucketWater, 1, 0)))
                .supportsRecipeTree(true)
                .build());

        //Lava to Obsidian bowl
        reg.addRecipe(EmiWorldInteractionRecipe.builder().id(new ResourceLocation("denovo", "/world/block_interaction/denovo/water_bowl_from_source"))
                .leftInput(EmiStack.of(new ItemStack(DNItems.waterBowl, 1, 0)))
                .rightInput(EmiStack.of(Block.lavaStill), false)
                .output(EmiStack.of(new ItemStack(Block.obsidian, 1, 0)))
                .supportsRecipeTree(true)
                .build());

        //Lava to Obsidian bucket
        reg.addRecipe(EmiWorldInteractionRecipe.builder().id(new ResourceLocation("denovo", "/world/block_interaction/denovo/water_bucket_from_source"))
                .leftInput(EmiStack.of(new ItemStack(Item.bucketWater, 1, 0)))
                .rightInput(EmiStack.of(Block.lavaStill), false)
                .output(EmiStack.of(new ItemStack(Block.obsidian, 1, 0)))
                .supportsRecipeTree(true)
                .build());


        //Adding Water composter bottle
        reg.addRecipe(EmiWorldInteractionRecipe.builder().id(new ResourceLocation("denovo", "/world/block_interaction/denovo/composter_adding_water_bottle"))
                .leftInput(EmiStack.of(new ItemStack(Item.potion, 1, 0)))
                .rightInput(EmiStack.of(new ItemStack(DNBlocks.composter, 1, EMPTY)), false)
                .output(EmiStack.of(new ItemStack(DNBlocks.composter, 1, WATER_15)))
                .supportsRecipeTree(true)
                .build());

        //Removing Water composter bottle
        reg.addRecipe(EmiWorldInteractionRecipe.builder().id(new ResourceLocation("denovo", "/world/block_interaction/denovo/composter_removing_water_bottle"))
                .leftInput(EmiStack.of(new ItemStack(Item.glassBottle)))
                .rightInput(EmiStack.of(new ItemStack(DNBlocks.composter, 1, WATER_15)), false)
                .output(EmiStack.of(new ItemStack(Item.potion, 1, 0)))
                .supportsRecipeTree(true)
                .build());

        //Adding Water composter bowl
        reg.addRecipe(EmiWorldInteractionRecipe.builder().id(new ResourceLocation("denovo", "/world/block_interaction/denovo/composter_adding_water_bowl"))
                .leftInput(EmiStack.of(new ItemStack(DNItems.waterBowl, 1, 0)))
                .rightInput(EmiStack.of(new ItemStack(DNBlocks.composter, 1, EMPTY)), false)
                .output(EmiStack.of(new ItemStack(DNBlocks.composter, 1, WATER_15)))
                .supportsRecipeTree(true)
                .build());

        //Removing Water composter bowl
        reg.addRecipe(EmiWorldInteractionRecipe.builder().id(new ResourceLocation("denovo", "/world/block_interaction/denovo/composter_removing_water_bowl"))
                .leftInput(EmiStack.of(new ItemStack(Item.bowlEmpty, 1, 0)))
                .rightInput(EmiStack.of(new ItemStack(DNBlocks.composter, 1, WATER_15)), false)
                .output(EmiStack.of(new ItemStack(DNItems.waterBowl, 1, 0)))
                .supportsRecipeTree(true)
                .build());

        //Adding Water cistern bottle
        reg.addRecipe(EmiWorldInteractionRecipe.builder().id(new ResourceLocation("denovo", "/world/block_interaction/denovo/cistern_adding_water_bottle"))
                .leftInput(EmiStack.of(new ItemStack(Item.potion, 1, 0)))
                .rightInput(EmiIngredient.of(List.of(
                        EmiStack.of(new ItemStack(DNBlocks.cistern, 1, EMPTY)),
                        EmiStack.of(new ItemStack(DNBlocks.cistern, 1, WATER_5)),
                        EmiStack.of(new ItemStack(DNBlocks.cistern, 1, WATER_10))
                )), false)
                .output(EmiStack.of(new ItemStack(DNBlocks.cistern, 1, WATER_15)))
                .supportsRecipeTree(true)
                .build());

        //Removing Water cistern bottle
        reg.addRecipe(EmiWorldInteractionRecipe.builder().id(new ResourceLocation("denovo", "/world/block_interaction/denovo/cistern_removing_water_bottle"))
                .leftInput(EmiStack.of(new ItemStack(Item.glassBottle)))
                .rightInput(EmiStack.of(new ItemStack(DNBlocks.cistern, 1, WATER_15)), false)
                .output(EmiStack.of(new ItemStack(Item.potion, 1, 0)))
                .supportsRecipeTree(true)
                .build());

        //Adding Water cistern bowl
        reg.addRecipe(EmiWorldInteractionRecipe.builder().id(new ResourceLocation("denovo", "/world/block_interaction/denovo/cistern_adding_water_bowl"))
                .leftInput(EmiStack.of(new ItemStack(DNItems.waterBowl, 1, 0)))
                .rightInput(EmiIngredient.of(List.of(
                        EmiStack.of(new ItemStack(DNBlocks.cistern, 1, EMPTY)),
                        EmiStack.of(new ItemStack(DNBlocks.cistern, 1, WATER_5)),
                        EmiStack.of(new ItemStack(DNBlocks.cistern, 1, WATER_10))
                )), false)
                .output(EmiStack.of(new ItemStack(DNBlocks.cistern, 1, WATER_15)))
                .supportsRecipeTree(true)
                .build());

        //Removing Water cistern bowl
        reg.addRecipe(EmiWorldInteractionRecipe.builder().id(new ResourceLocation("denovo", "/world/block_interaction/denovo/cistern_removing_water_bowl"))
                .leftInput(EmiStack.of(new ItemStack(Item.bowlEmpty, 1, 0)))
                .rightInput(EmiIngredient.of(List.of(
                        EmiStack.of(new ItemStack(DNBlocks.cistern, 1, WATER_5)),
                        EmiStack.of(new ItemStack(DNBlocks.cistern, 1, WATER_10)),
                        EmiStack.of(new ItemStack(DNBlocks.cistern, 1, WATER_15))
                )), false)
                .output(EmiStack.of(new ItemStack(DNItems.waterBowl, 1, 0)))
                .supportsRecipeTree(true)
                .build());

        //Adding Water cistern bucket
        reg.addRecipe(EmiWorldInteractionRecipe.builder().id(new ResourceLocation("denovo", "/world/block_interaction/denovo/cistern_adding_water_bucket"))
                .leftInput(EmiStack.of(new ItemStack(Item.bucketWater, 1, 0)))
                .rightInput(EmiStack.of(new ItemStack(DNBlocks.cistern, 1, EMPTY)), false)
                .output(EmiStack.of(new ItemStack(DNBlocks.cistern, 1, WATER_15)))
                .supportsRecipeTree(true)
                .build());

        //Removing Water cistern bucket
        reg.addRecipe(EmiWorldInteractionRecipe.builder().id(new ResourceLocation("denovo", "/world/block_interaction/denovo/cistern_removing_water_bucket"))
                .leftInput(EmiStack.of(new ItemStack(Item.bucketEmpty, 1, 0)))
                .rightInput(EmiStack.of(new ItemStack(DNBlocks.cistern, 1, WATER_15)), false)
                .output(EmiStack.of(new ItemStack(Item.bucketWater, 1, 0)))
                .supportsRecipeTree(true)
                .build());
    }

    private void addCisternBaseWorldInteractionRecipe(EmiRegistry reg) {
        String[] blockName = new String[] {"composter", "cistern"};
        Block[] blocks = new Block[]{ DNBlocks.composter, DNBlocks.cistern};

        for (int i = 0; i < 2; i++) {
            //Rain Filling
            reg.addRecipe(EmiWorldInteractionRecipe.builder().id(new ResourceLocation("denovo", "/world/block_interaction/denovo/" + blockName[i] + "_water_rain"))
                    .leftInput(new EmiRainStack())
                    .rightInput(EmiStack.of(new ItemStack(blocks[i], 1, EMPTY)), false)
                    .output(EmiStack.of(new ItemStack(blocks[i], 1, WATER_15)))
                    .supportsRecipeTree(true)
                    .build());

            //Morning Filling
            reg.addRecipe(EmiWorldInteractionRecipe.builder().id(new ResourceLocation("denovo", "/world/block_interaction/denovo/" + blockName[i] + "_water_morning"))
                    .leftInput(EmiStack.of(new ItemStack(blocks[i], 1, EMPTY)))
                    .rightInput(EmiStack.of(Item.pocketSundial), false, sw -> {
                        sw.appendTooltip(Text.translatable("emi.world_interaction.denovo.morning"));
                        return sw;
                    })
                    .output(EmiStack.of(new ItemStack(blocks[i], 1, WATER_10)))
                    .supportsRecipeTree(true)
                    .build());

            //Adding Dirt for Clay
            reg.addRecipe(EmiWorldInteractionRecipe.builder().id(new ResourceLocation("denovo", "/world/block_interaction/denovo/" + blockName[i] + "_dirt_pile"))
                    .leftInput(EmiStack.of(BTWItems.dirtPile))
                    .rightInput(EmiStack.of(new ItemStack(blocks[i], 1, WATER_15)), false)
                    .output(EmiStack.of(new ItemStack(blocks[i], 1, WATER_MUDDY)))
                    .supportsRecipeTree(true)
                    .build());

            //Adding Clay for Infected
            reg.addRecipe(EmiWorldInteractionRecipe.builder().id(new ResourceLocation("denovo", "/world/block_interaction/denovo/" + blockName[i] + "_clay"))
                    .leftInput(EmiStack.of(Item.clay))
                    .rightInput(EmiStack.of(new ItemStack(blocks[i], 1, WATER_15)), false)
                    .output(EmiStack.of(new ItemStack(blocks[i], 1, WATER_CLAY_0)))
                    .supportsRecipeTree(true)
                    .build());

            reg.addRecipe(EmiWorldInteractionRecipe.builder().id(new ResourceLocation("denovo", "/world/block_interaction/denovo/" + blockName[i] + "_infected"))
                    .leftInput(EmiStack.of(BTWItems.dirtPile), sw -> {
                        sw.appendTooltip(Text.translatable("emi.world_interaction.denovo.composter_filling"));
                        return sw;
                    })
                    .rightInput(EmiStack.of(new ItemStack(blocks[i], 1, WATER_INFECTED)), false)
                    .output(EmiStack.of(new ItemStack(blocks[i], 1, WATER_INFECTED_DIRT)))
                    .supportsRecipeTree(true)
                    .build());

            reg.addRecipe(EmiWorldInteractionRecipe.builder().id(new ResourceLocation("denovo", "/world/block_interaction/denovo/" + blockName[i] + "_rust"))
                    .leftInput(EmiStack.of(Item.bowlEmpty))
                    .rightInput(EmiStack.of(new ItemStack(blocks[i], 1, WATER_RUST)), false)
                    .output(EmiStack.of(DNItems.rustWaterBowl))
                    .supportsRecipeTree(true)
                    .build());
        }
    }

    private void addComposterOnlyRecipes(EmiRegistry reg) {

        //Maggot Creation
        BTWPlugin.addRecipeSafe(reg, () -> new EmiCisternBaseRecipe(
                new ResourceLocation("denovo", "composter/maggot_creation"), DeNovoEmiRecipeCategories.COMPOSTER,
                new ItemStack(DNBlocks.composter, 1, COMPOST_16),
                List.of(
                        EmiStack.of(new ItemStack(DNBlocks.composter, 1, MAGGOTS_16))
                ),
                CisternUtils.MAGGOT_CREATION_TIME));

        //Adding Compost
        reg.addRecipe(EmiWorldInteractionRecipe.builder().id(new ResourceLocation("denovo", "/world/block_interaction/denovo/compost_adding"))
                .leftInput(EmiIngredient.of(DeNovoTags.compostables), sw -> {
                    sw.appendTooltip(Text.translatable("emi.world_interaction.denovo.composter_filling"));
                    return sw;
                })
                .rightInput(EmiStack.of(new ItemStack(DNBlocks.composter, 1, EMPTY)), false)
                .output(EmiStack.of(new ItemStack(DNBlocks.composter, 1, COMPOST_16)))
                .supportsRecipeTree(true)
                .build());

        //Remove Maggots
        reg.addRecipe(EmiWorldInteractionRecipe.builder().id(new ResourceLocation("denovo", "/world/block_interaction/denovo/composter_maggots_removal"))
                .leftInput(new EmiRightClickStack(), sw -> {
                    sw.appendTooltip(Text.translatable("emi.world_interaction.denovo.right_click"));
                    return sw;
                })
                .rightInput(EmiStack.of(new ItemStack(DNBlocks.composter, 1, MAGGOTS_16)), false)
                .output(EmiStack.of(DNItems.rawMaggots))
                .supportsRecipeTree(true)
                .build());

        //Remove Dirt
        reg.addRecipe(EmiWorldInteractionRecipe.builder().id(new ResourceLocation("denovo", "/world/block_interaction/denovo/composter_dirt_removal"))
                .leftInput(new EmiRightClickStack(), sw -> {
                    sw.appendTooltip(Text.translatable("emi.world_interaction.denovo.right_click"));
                    return sw;
                })
                .rightInput(EmiStack.of(new ItemStack(DNBlocks.composter, 1, COMPOST_16)), false)
                .output(EmiStack.of(BTWBlocks.looseDirt))
                .supportsRecipeTree(true)
                .build());
    }


    private void addCisternBaseRecipes(EmiRegistry reg) {
        String[] blockName = new String[] {"composter", "cistern"};
        Block[] blocks = new Block[]{ DNBlocks.composter, DNBlocks.cistern};
        EmiRecipeCategory[] categories = new EmiRecipeCategory[]{ DeNovoEmiRecipeCategories.COMPOSTER, DeNovoEmiRecipeCategories.CISTERN};

        for (int i = 0; i < 2; i++) {
            //Clay Creation
            int index = i;
            BTWPlugin.addRecipeSafe(reg, () -> new EmiCisternBaseRecipe(
                    new ResourceLocation("denovo", blockName[index] + "/clay_creation"), categories[index],
                    new ItemStack(blocks[index], 1, WATER_MUDDY),
                    List.of(
                            EmiStack.of(new ItemStack(blocks[index], 1, WATER_15)),
                            EmiStack.of(Item.clay)
                    ),
                    CisternUtils.MUDDY_WATER_SETTLE_TIME));

            //Iron
            BTWPlugin.addRecipeSafe(reg, () -> new EmiCisternBaseRecipe(
                    new ResourceLocation("denovo", blockName[index] + "/infected_creation_part1"), categories[index],
                    new ItemStack(blocks[index], 1, WATER_CLAY_0),
                    List.of(
                            EmiStack.of(new ItemStack(blocks[index], 1, WATER_15)).setChance(0.9F),
                            EmiStack.of(new ItemStack(blocks[index], 1, WATER_CLAY_50)).setChance(0.1F)
                    ),
                    CisternUtils.CLAY_WATER_CONVERSION_TIME/2));

            BTWPlugin.addRecipeSafe(reg, () -> new EmiCisternBaseRecipe(
                    new ResourceLocation("denovo", blockName[index] + "/infected_creation_part2"), categories[index],
                    new ItemStack(blocks[index], 1, WATER_CLAY_50),
                    List.of(
                            EmiStack.of(new ItemStack(blocks[index], 1, WATER_INFECTED))
                    ),
                    CisternUtils.CLAY_WATER_CONVERSION_TIME/2));

            BTWPlugin.addRecipeSafe(reg, () -> new EmiCisternBaseRecipe(
                    new ResourceLocation("denovo", blockName[index] + "/rust_creation"), categories[index],
                    new ItemStack(blocks[index], 1, WATER_INFECTED_DIRT),
                    List.of(
                            EmiStack.of(new ItemStack(blocks[index], 1, WATER_RUST))
                    ),
                    CisternUtils.INFECTED_WATER_CONVERSION_TIME));
        }
    }

    private void addProgressiveCraftingRecipes(EmiRegistry reg) {
        // Progressive Crafting
        BTWPlugin.addRecipeSafe(reg, () -> new EmiProgressiveRecipe(new ResourceLocation("denovo", "maggots_silk_extraction"),
                new ItemStack(DNItems.maggotsSilkExtraction), new ItemStack(Item.silk)));
        BTWPlugin.addRecipeSafe(reg, () -> new EmiProgressiveRecipe(new ResourceLocation("denovo", "rust_water_bowl"),
                new ItemStack(DNItems.rustWaterBowl), new ItemStack(DNItems.ironDust)));
    }

    private void addSiftingRecipes(EmiRegistry reg) {
        // Sieve
        for (SiftingRecipe siftingRecipe : SiftingCraftingManager.getRecipes()){
            LootEntry[] output = siftingRecipe.getLootTable();
            ItemStack filterUsed = siftingRecipe.getFilterUsed();
            ItemStack input = siftingRecipe.getInput();
            boolean containsSouls = siftingRecipe.getContainsSouls();
            BTWPlugin.addRecipeSafe(reg, () -> new EmiSieveRecipe(new SyntheticIdentifier((SiftingRecipe)siftingRecipe),
                    (LootEntry[]) output, (ItemStack) input, (ItemStack)filterUsed, containsSouls));

        }
    }

    private void addCharcoalProcessingRecipes(EmiRegistry reg) {
        BTWPlugin.addRecipeSafe(reg, () -> new EmiCharcoalRecipe(new ResourceLocation("denovo", "placed_sticks"),
                new ItemStack(DNBlocks.placedSticks, 1, 15), new ItemStack(DNBlocks.smolderingPlacedSticks))
                .setArrowToolTip("denovo.emi.placed_to_smoldering"));
        BTWPlugin.addRecipeSafe(reg, () -> new EmiCharcoalRecipe(new ResourceLocation("denovo", "smoldering_sticks"),
                new ItemStack(DNBlocks.smolderingPlacedSticks), new ItemStack(DNBlocks.charcoalPile, 1, 7)));
        BTWPlugin.addRecipeSafe(reg, () -> new EmiCharcoalRecipe(new ResourceLocation("denovo", "charcoal_pile"),
                new ItemStack(DNBlocks.charcoalPile, 1, 7), new ItemStack(DNItems.charcoalDust, 1)));
    }

    private void addLavaWorldInteractionRecipes(EmiRegistry reg) {
        int cobbleCount = 8;
        int strawCount = 0;
        int state = 0;
        int fullCobble = CisternUtils.pack(cobbleCount, strawCount, state, 0);

        strawCount = 4;
        int fullStraw = CisternUtils.pack(cobbleCount, strawCount, state, 0);

        state = 1;
        int fullLava = CisternUtils.pack(cobbleCount, strawCount, state, 0);

        //Iron chunk and rocks
        reg.addRecipe(EmiWorldInteractionRecipe.builder().id(new ResourceLocation("denovo", "/world/block_interaction/denovo/lava_creation"))
                .leftInput(EmiStack.of(BTWItems.stone))
                .rightInput(EmiStack.of(BTWItems.ironOreChunk), false)
                .output(EmiStack.of(new ItemStack(DNBlocks.lavaCobble, 1, fullCobble)))
                .supportsRecipeTree(true)
                .build());

        //straw
        reg.addRecipe(EmiWorldInteractionRecipe.builder().id(new ResourceLocation("denovo", "/world/block_interaction/denovo/lava_creation"))
                .leftInput(EmiStack.of(BTWItems.straw))
                .rightInput(EmiStack.of(new ItemStack(DNBlocks.lavaCobble, 1, fullCobble)), false)
                .output(EmiStack.of(new ItemStack(DNBlocks.lavaCobble, 1, fullStraw)))
                .supportsRecipeTree(true)
                .build());

        //cooking
        reg.addRecipe(EmiWorldInteractionRecipe.builder().id(new ResourceLocation("denovo", "/world/block_interaction/denovo/lava_creation"))
                .leftInput(EmiStack.of(new ItemStack(DNBlocks.lavaCobble, 1, fullStraw)), sw -> {
                    sw.appendTooltip(Text.translatable("emi.world_interaction.denovo.lava_cobble"));
                    return sw;
                })
                .rightInput(EmiStack.of(DNBlocks.smolderingPlacedSticks), false)
                .output(EmiStack.of(new ItemStack(DNBlocks.lavaCobble, 1, fullLava)))
                .supportsRecipeTree(true)
                .build());

        //lava
        reg.addRecipe(EmiWorldInteractionRecipe.builder().id(new ResourceLocation("denovo", "/world/block_interaction/denovo/lava_creation"))
                .leftInput(EmiStack.of(new ItemStack(DNBlocks.lavaCobble, 1, fullLava)), sw -> {
                    sw.appendTooltip(Text.translatable("emi.world_interaction.denovo.lava_cobble"));
                    return sw;
                })
                .rightInput(EmiStack.of(Item.pocketSundial), false, sw -> {
                    sw.appendTooltip(Text.translatable("emi.world_interaction.denovo.lava_cobble_time"));
                    return sw;
                })
                .output(EmiStack.of(Block.lavaStill))
                .supportsRecipeTree(true)
                .build());
    }

    public static EmiRecipeCategory category(String id, EmiStack icon) {
        return new EmiRecipeCategory(new ResourceLocation("denovo", id), icon, new EmiTexture(new ResourceLocation("denovo", "textures/simple_icons/" + id + ".png"), 0, 0, 16, 16, 16, 16, 16, 16));
    }

    private void info(EmiRegistry registry, Item item, String info) {
        registry.addRecipe(new EmiInfoRecipe(List.of(EmiStack.of(item)), List.of(Text.translatable(info)), null));
    }

    private void info(EmiRegistry registry, Block block, int itemDamage, String info) {
        registry.addRecipe(new EmiInfoRecipe(List.of(EmiStack.of(new ItemStack(block.blockID, 1, itemDamage))), List.of(Text.translatable(info)), null));
    }
}
