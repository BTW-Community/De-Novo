package btw.community.denovo.block.blocks;

import btw.community.denovo.block.DNBlocks;
import btw.community.denovo.item.DNItems;
import btw.item.util.ItemUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class CharcoalPileBlock extends Block {

    public CharcoalPileBlock(int blockID) {
        super(blockID, Material.ground);

        setShovelsEffectiveOn();
        setPicksEffectiveOn();

        setHardness(0.25F);

        setTickRandomly(true);

        setUnlocalizedName("denovo.charcoal");
    }

    @Override
    public int idDropped(int iMetadata, Random rand, int iFortuneModifier) {
        return 0;
    }

    @Override
    public int idPicked(World par1World, int par2, int par3, int par4) {
        return DNItems.charcoalDust.itemID;
    }

    @Override
    public boolean canConvertBlock(ItemStack stack, World world, int x, int y, int z) {
        return true;
    }

    @Override
    public boolean convertBlock(ItemStack stack, World world, int x, int y, int z, int facing) {
        int oldMetadata = world.getBlockMetadata(x, y, z);
        int newMetadata = 0;

        if (oldMetadata > 0) {
            newMetadata = oldMetadata - 1;
            world.setBlockAndMetadataWithNotify(x, y, z, this.blockID, newMetadata);
        } else {
            world.setBlockToAir(x, y, z);
        }

        if (!world.isRemote) {
            ItemUtils.ejectStackFromBlockTowardsFacing(world, x, y, z, new ItemStack(DNItems.charcoalDust), facing);
        }

        return true;
    }

    //CHATGPT rewrite
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int iNeighborBlockID) {
        if (!this.canBlockStay(world, x, y, z)) {
            //this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
            world.playAuxSFX(2001, x, y, z, this.blockID); //break sfx
            ItemUtils.ejectSingleItemWithRandomOffset(world, x, y, z, DNItems.charcoalDust.itemID, 0);
            world.setBlockToAir(x, y, z);
            return;
        }
        //System.out.println("checking to merge with other charcoal");

        // Check if the block below is DNBlocks.charcoalPile.blockID
        int blockBelowID = world.getBlockId(x, y - 1, z);
        int blockBelowMeta = world.getBlockMetadata(x, y - 1, z);

        if (blockBelowID == DNBlocks.charcoalPile.blockID) {
            // Check if the metadata below is less than 15
            if (blockBelowMeta < 15) {
                // Get the current block's metadata
                int currentMeta = world.getBlockMetadata(x, y, z);

                // Calculate the new metadata for the block below
                int combinedMeta = blockBelowMeta + currentMeta;

                //System.out.println("currentMeta: " + currentMeta );
                //System.out.println("blockBelowMeta: " + blockBelowMeta);
                //System.out.println("combinedMeta: " + combinedMeta);

                if (combinedMeta > 15) {
                    // Set the block below's metadata to 15 (full)
                    world.setBlockMetadataWithNotify(x, y - 1, z, 15, 2);

                    // Set the current block's metadata to the remaining amount
                    int remainingMeta = combinedMeta - 15;
                    world.setBlockMetadataWithNotify(x, y, z, remainingMeta, 2);

                    //System.out.println("remaining: " + remainingMeta);
                } else {
                    // Set the block below's metadata to the combined amount
                    world.setBlockMetadataWithNotify(x, y - 1, z, combinedMeta, 2);

                    // Remove the current block since all of its metadata has been transferred
                    world.setBlockToAir(x, y, z);
                }
            }
        }
    }

    @Override
    public boolean canBlockStay(World world, int x, int y, int z) {
        return world.doesBlockHaveSolidTopSurface(x, y - 1, z) || world.getBlockId(x, y - 1, z) == this.blockID;
    }

    @Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(
            IBlockAccess blockAccess, int x, int y, int z) {
        int metadata = blockAccess.getBlockMetadata(x, y, z);
        return getBlockBoundsFromPoolBasedOnState(metadata);
    }

    private AxisAlignedBB getBlockBoundsFromPoolBasedOnState(int metadata) {
        double minY = 0 / 16D;
        double maxY = 1 / 16D + metadata / 16D;

        return new AxisAlignedBB(
                0D, minY, 0D,
                1D, maxY, 1D
        );
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean hasCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIgnoreTransparency) {
        return hasLargeCenterHardPointToFacing(blockAccess, i, j, k, iFacing);
    }

    @Override
    public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing) {
        if (blockAccess.getBlockMetadata(i, j, k) == 15) {
            return iFacing == 1;
        }

        return false;
    }

    //----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int iNeighborI, int iNeighborJ, int iNeighborK, int iSide) {
        return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        this.blockIcon = register.registerIcon("denovo:charcoal");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k) {
        renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(
                renderer.blockAccess, i, j, k));

        return renderer.renderStandardBlock(this, i, j, k);
    }
}
