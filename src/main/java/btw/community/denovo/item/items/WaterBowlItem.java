package btw.community.denovo.item.items;

import btw.block.BTWBlocks;
import btw.util.MiscUtils;
import btw.world.util.BlockPos;
import net.minecraft.src.*;

public class WaterBowlItem extends Item {
    public WaterBowlItem(int itemID) {
        super(itemID);

        setUnlocalizedName("denovo.water_bowl");
        setTextureName("denovo:water_bowl");
        setCreativeTab(CreativeTabs.tabMisc);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        MovingObjectPosition posClicked = this.getMovingObjectPositionFromPlayer(world, player, false);
        if (posClicked != null && posClicked.typeOfHit == EnumMovingObjectType.TILE && world.canMineBlock(player, posClicked.blockX, posClicked.blockY, posClicked.blockZ)) {
            BlockPos targetPos = new BlockPos(posClicked.blockX, posClicked.blockY, posClicked.blockZ, posClicked.sideHit);
            if (player.canPlayerEdit(targetPos.x, targetPos.y, targetPos.z, posClicked.sideHit, itemStack) && this.attemptPlaceContentsAtLocation(world, targetPos.x, targetPos.y, targetPos.z) && !player.capabilities.isCreativeMode) {
                return new ItemStack(Item.bowlEmpty);
            }
        }
        return itemStack;
    }

    private boolean attemptPlaceContentsAtLocation(World world, int i, int j, int k) {
        if (world.isAirBlock(i, j, k) || !world.getBlockMaterial(i, j, k).isSolid()) {
            if (!world.isRemote) {
                if (world.provider.isHellWorld) {
                    world.playAuxSFX(2278, i, j, k, 0);
                } else {
                    int iTargetBlockID = world.getBlockId(i, j, k);
                    int iTargetMetadata = world.getBlockMetadata(i, j, k);
                    if (iTargetBlockID == Block.lavaMoving.blockID || iTargetBlockID == Block.lavaStill.blockID) {
                        world.playAuxSFX(2278, i, j, k, 0);
                        if (iTargetMetadata == 0) {
                            world.setBlockWithNotify(i, j, k, Block.obsidian.blockID);
                        } else {
                            world.setBlockWithNotify(i, j, k, BTWBlocks.lavaPillow.blockID);
                        }
                    } else if (iTargetBlockID != Block.waterMoving.blockID && iTargetBlockID != Block.waterStill.blockID || iTargetMetadata != 0) {
                        placeNonPersistentWater(world, i, j, k);
                    }
                }
            }
            return true;
        }
        return false;
    }

    private void placeNonPersistentWater(World world, int i, int j, int k) {
        world.setBlockAndMetadataWithNotify(i, j, k, Block.waterMoving.blockID, 5);
        MiscUtils.flowWaterIntoBlockIfPossible(world, i + 1, j, k, 6);
        MiscUtils.flowWaterIntoBlockIfPossible(world, i - 1, j, k, 6);
        MiscUtils.flowWaterIntoBlockIfPossible(world, i, j, k + 1, 6);
        MiscUtils.flowWaterIntoBlockIfPossible(world, i, j, k - 1, 6);
    }

}
