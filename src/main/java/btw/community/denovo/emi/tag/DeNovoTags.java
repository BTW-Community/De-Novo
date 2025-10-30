package btw.community.denovo.emi.tag;

import btw.block.BTWBlocks;
import btw.community.denovo.utils.CisternUtils;
import btw.item.BTWItems;
import btw.item.tag.BTWTags;
import btw.item.tag.Tag;
import btw.item.tag.TagInstance;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.registry.EmiTags;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ResourceLocation;

public class DeNovoTags {
    public static void init() {
        //call class to init tags
    }
    public static final Tag compostables = Tag.of(DeNovoTags.loc("compostables"))
            .add(new ItemStack(Item.stick))
            .add(new ItemStack(BTWItems.sawDust))
            .add(BTWTags.barks)

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

            .add(new ItemStack(Block.deadBush))
            .add(new ItemStack(Block.plantRed))
            .add(new ItemStack(Block.plantYellow))
            .add(new ItemStack(Block.cocoaPlant))
            .add(new ItemStack(Block.vine))
            .add(new ItemStack(Block.waterlily))
            .add(new ItemStack(BTWItems.redMushroom))
            .add(new ItemStack(BTWItems.brownMushroom))

            .add(BTWTags.logs)
            .addFromAndUntilDamage(0, 3, Block.leaves)

            .add(new ItemStack(BTWBlocks.oakSapling, 1, 0))
            .add(new ItemStack(BTWBlocks.oakSapling, 1, 7))
            .add(new ItemStack(BTWBlocks.birchSapling, 1, 0))
            .add(new ItemStack(BTWBlocks.birchSapling, 1, 7))
            .add(new ItemStack(BTWBlocks.spruceSapling, 1, 0))
            .add(new ItemStack(BTWBlocks.spruceSapling, 1, 7))
            .add(new ItemStack(BTWBlocks.jungleSapling, 1, 0))
            .add(new ItemStack(BTWBlocks.jungleSapling, 1, 7))

            .addFromAndUntilDamage(0, 2, Block.tallGrass);

    private static ResourceLocation loc(String id) {
        return new ResourceLocation("denovo", id);
    }
}
