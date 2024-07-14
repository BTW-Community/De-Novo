package btw.community.denovo.mixins;

import btw.block.blocks.DailyGrowthCropsBlock;
import btw.block.blocks.SaplingBlock;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SaplingBlock.class)
public abstract class SaplingBlockMixin extends DailyGrowthCropsBlock {
    public SaplingBlockMixin(int id, String name, String textureBase) {
        super(id);
    }

    private float getReduceGrowthChance() {
        return 0.5F;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int iFacing, float fXClick, float fYClick, float fZClick) {
        if (player.getCurrentEquippedItem() != null)
            return super.onBlockActivated(world, x, y, z, player, iFacing, fXClick, fYClick, fZClick);

        if (getGrowthLevel(world.getBlockMetadata(x, y, z)) > 2) {
            dropItemsIndividually(world, x, y, z, Item.stick.itemID, 1, 0, 1);

            //reduce Growth Level
            if (world.rand.nextFloat() <= getReduceGrowthChance()) {
                reduceGrowthLevel(world, x, y, z);
            }

            return true;
        }

        return false;
    }

    private void reduceGrowthLevel(World world, int x, int y, int z) {
        int newGrowthLevel = getGrowthLevel(world, x, y, z) - 2;
        setGrowthLevel(world, x, y, z, newGrowthLevel);
        setHasGrownToday(world, x, y, z, false);
    }
}
