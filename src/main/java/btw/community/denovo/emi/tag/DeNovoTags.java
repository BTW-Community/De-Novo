package btw.community.denovo.emi.tag;

import btw.block.BTWBlocks;
import btw.community.denovo.item.DNItems;
import btw.item.BTWItems;
import btw.item.tag.BTWTags;
import btw.item.tag.Tag;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ResourceLocation;

public class DeNovoTags {

    public static void init() {
        //call class to init tags
    }

    public static Tag snowFilling = Tag.of(DeNovoTags.loc("snow_filling"),
            Item.snowball);

    public static Tag ironBacteriaStarters = Tag.of(DeNovoTags.loc("iron_bacteria_starters"),
            BTWItems.dirtPile,
            BTWItems.gravelPile);

    public static Tag muddyWaterStarters = Tag.of(DeNovoTags.loc("muddy_water_starters"),
            BTWItems.dirtPile);

    public static Tag clayWaterStarters = Tag.of(DeNovoTags.loc("clay_water_starters"),
            Item.clay);

    public static final Tag sickle = Tag.of(DeNovoTags.loc("sickle"),
                    DNItems.woodSickle,
                    DNItems.stoneSickle);

    public static final Tag rich_compostables = Tag.of(DeNovoTags.loc("rich_compostables"))
            .add(BTWTags.barks)
            .addFromAndUntilDamage(0, 3, Block.leaves);

    public static final Tag compostables = Tag.of(DeNovoTags.loc("compostables"))

            .add(new ItemStack(Item.stick))
            .add(new ItemStack(BTWItems.sawDust))

            .add(new ItemStack(Item.poisonousPotato))
            .add(new ItemStack(Item.reed))
            .add(new ItemStack(BTWItems.sugarCaneRoots))
            .add(new ItemStack(BTWItems.hemp))
            .add(new ItemStack(BTWItems.hempFibers))

            .add(new ItemStack(Item.rottenFlesh))
            .add(new ItemStack(Item.spiderEye))
            .add(new ItemStack(Item.fermentedSpiderEye))
            .add(new ItemStack(BTWItems.creeperOysters))
            .add(new ItemStack(BTWItems.batWing))
            .add(new ItemStack(BTWItems.mysteriousGland))
            .add(new ItemStack(DNItems.rawMaggots))

            .add(new ItemStack(Block.deadBush))
            .add(new ItemStack(Block.plantRed))
            .add(new ItemStack(Block.plantYellow))
            .add(new ItemStack(BTWItems.cocoaBeans))
            .add(new ItemStack(Block.vine))
            .add(new ItemStack(Block.waterlily))
            .add(new ItemStack(BTWItems.redMushroom))
            .add(new ItemStack(BTWItems.brownMushroom))
            .add(new ItemStack(Block.cactus))

            .add(new ItemStack(BTWBlocks.oakSapling, 1, 0))
            .add(new ItemStack(BTWBlocks.oakSapling, 1, 7))
            .add(new ItemStack(BTWBlocks.birchSapling, 1, 0))
            .add(new ItemStack(BTWBlocks.birchSapling, 1, 7))
            .add(new ItemStack(BTWBlocks.spruceSapling, 1, 0))
            .add(new ItemStack(BTWBlocks.spruceSapling, 1, 7))
            .add(new ItemStack(BTWBlocks.jungleSapling, 1, 0))
            .add(new ItemStack(BTWBlocks.jungleSapling, 1, 7))

            .addFromAndUntilDamage(0, 2, Block.tallGrass);

    public static final Tag validGoldenDungBushBlocks = Tag.of(DeNovoTags.loc("validGoldenDungBushBlocks"),
            Block.bedrock,
            BTWBlocks.looseSparseGrass,
            Block.grass,
            Block.dirt,
            BTWBlocks.looseDirt,
            BTWBlocks.planterWithSoil)
            .addFromAndUntilDamage(0, 6, BTWBlocks.aestheticEarth);

    public static ResourceLocation loc(String id) {
        return new ResourceLocation("denovo", id);
    }
}
