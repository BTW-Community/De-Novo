package btw.community.denovo.mixins;

import btw.block.BTWBlocks;
import btw.community.denovo.block.DNBlocks;
import btw.item.items.ShaftItem;
import btw.world.util.BlockPos;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ShaftItem.class)
public abstract class ShaftItemMixin extends ItemReed {

    public ShaftItemMixin(int par1) {
        super(par1, 0);
    }

    @Override
    public int getBlockIDToPlace(int iItemDamage, int iFacing, float fClickX, float fClickY, float fClickZ) {
        return DNBlocks.placedSticks.blockID;
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ )
    {
        int iNewBlockID = getBlockIDToPlace(itemStack.getItemDamage(), iFacing, fClickX, fClickY, fClickZ);

        //Sock: Added
        if (player.isSneaking() || player.isUsingSpecialKey())
        {
            iNewBlockID = DNBlocks.placedSticks.blockID;
        }
        else {
            iNewBlockID = BTWBlocks.placedShaft.blockID;
        }
        //END Sock

        if ( itemStack.stackSize == 0 ||
                ( player != null && !player.canPlayerEdit( i, j, k, iFacing, itemStack ) ) ||
                ( j == 255 && Block.blocksList[iNewBlockID].blockMaterial.isSolid() ) )
        {
            return false;
        }

        BlockPos targetPos = new BlockPos( i, j, k );

        int iOldBlockID = world.getBlockId( i, j, k );
        Block oldBlock = Block.blocksList[iOldBlockID];

        if ( oldBlock != null )
        {
            if ( oldBlock.isGroundCover() )
            {
                iFacing = 1;
            }
            else if ( !oldBlock.blockMaterial.isReplaceable() )
            {
                targetPos.addFacingAsOffset(iFacing);
            }
        }

        if ((!requireNoEntitiesInTargetBlock || isTargetFreeOfObstructingEntities(world, targetPos.x, targetPos.y, targetPos.z) ) &&
                world.canPlaceEntityOnSide(iNewBlockID, targetPos.x, targetPos.y, targetPos.z, false, iFacing, player, itemStack) )
        {
            Block newBlock = Block.blocksList[iNewBlockID];

            int iNewMetadata = getMetadata( itemStack.getItemDamage() );

            iNewMetadata = newBlock.onBlockPlaced(world, targetPos.x, targetPos.y, targetPos.z, iFacing, fClickX, fClickY, fClickZ, iNewMetadata);

            iNewMetadata = newBlock.preBlockPlacedBy(world, targetPos.x, targetPos.y, targetPos.z, iNewMetadata, player);

            if ( world.setBlockAndMetadataWithNotify(targetPos.x, targetPos.y,
                    targetPos.z, iNewBlockID, iNewMetadata) )
            {
                if (world.getBlockId(targetPos.x, targetPos.y, targetPos.z) == iNewBlockID )
                {
                    newBlock.onBlockPlacedBy(world, targetPos.x, targetPos.y,
                            targetPos.z, player, itemStack);

                    newBlock.onPostBlockPlaced(world, targetPos.x, targetPos.y, targetPos.z, iNewMetadata);

                    // Panick animals when blocks are placed near them
                    world.notifyNearbyAnimalsOfPlayerBlockAddOrRemove(player, newBlock, targetPos.x, targetPos.y, targetPos.z);
                }

                playPlaceSound(world, targetPos.x, targetPos.y, targetPos.z, newBlock);

                itemStack.stackSize--;
            }

            return true;
        }

        return false;
    }
}
