package btw.community.denovo.mixins;

import btw.block.blocks.OreChunkBlock;
import btw.block.blocks.OreChunkBlockIron;
import btw.community.denovo.block.DNBlocks;
import btw.community.denovo.block.tileentities.LavaCobbleTileEntity;
import btw.item.BTWItems;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(OreChunkBlockIron.class)
public class OreChunkIronBlockMixin extends OreChunkBlock {

    protected OreChunkIronBlockMixin(int iBlockID) {
        super(iBlockID);
    }

    @Override
    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer player, int iFacing, float fXClick, float fYClick, float fZClick) {
        ItemStack heldStack = player.getHeldItem();
        if (heldStack != null && heldStack.itemID == BTWItems.stone.itemID){
            int oldMeta = world.getBlockMetadata(i,j,k);
            world.setBlockToAir(i,j,k);
            world.setBlockAndMetadataWithNotify(i,j,k, DNBlocks.lavaCobble.blockID, oldMeta);
            LavaCobbleTileEntity lavaCobble = (LavaCobbleTileEntity) world.getBlockTileEntity(i,j,k);
            if (lavaCobble != null){
                lavaCobble.setCobbleCounter(1);
                world.markTileEntityChunkModified(i,j,k, lavaCobble);
                if (!player.capabilities.isCreativeMode) --heldStack.stackSize;
                return true;
            }
        }

        return super.onBlockActivated(world,i,j,k,player, iFacing, fXClick,fYClick,fZClick);
    }
}
