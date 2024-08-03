package btw.community.denovo.block.blocks;

import btw.block.blocks.FallingBlock;
import btw.community.denovo.item.DNItems;
import btw.item.util.ItemUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

//----------- Disabled! -----------//

public class CharcoalPileFallingBlock extends FallingBlock {

    public static final int FALLING_BLOCK_TICK_RATE = 2;

    public CharcoalPileFallingBlock(int blockID) {
        super(blockID, Material.ground);

        setShovelsEffectiveOn();
        setPicksEffectiveOn();

        initBlockBounds(0,0,0,1,1,1);

        setHardness(0.25F);

        setTickRandomly( true );

        setUnlocalizedName("DNBlock_charcoal_pile");
    }

//    @Override
//    public void randomUpdateTick(World world, int i, int j, int k, Random rand) {
//        scheduleCheckForFall(world, i, j, k);
//    }

    public int idDropped(int iMetadata, Random rand, int iFortuneModifier )
    {
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

        if (oldMetadata > 0)
        {
            newMetadata = oldMetadata - 1;
            world.setBlockAndMetadataWithNotify(x, y, z, this.blockID, newMetadata);
        }
        else {
            world.setBlockToAir(x,y,z);
        }

        if (!world.isRemote) {
            ItemUtils.ejectStackFromBlockTowardsFacing(world, x, y, z, new ItemStack(DNItems.charcoalDust), facing);
        }

        return true;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k) {
        int meta = world.getBlockMetadata(i,j,k);

        double maxY = 1/16D + meta/16D;

        return new AxisAlignedBB(

                0D, 0/16D, 0D,
                1D, maxY, 1D
        );
    }

    @Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(
            IBlockAccess blockAccess, int x, int y, int z)
    {
        int metadata = blockAccess.getBlockMetadata(x, y, z);
        return  getBlockBoundsFromPoolBasedOnState(metadata);
    }

    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(int metadata)
    {
        double minY = 0/16D;
        double maxY = 1/16D + metadata/16D;

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
    public boolean hasCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIgnoreTransparency )
    {
        return hasLargeCenterHardPointToFacing( blockAccess, i, j, k, iFacing );
    }

    @Override
    public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing) {
        if (blockAccess.getBlockMetadata(i, j, k) == 15)
        {
            return iFacing == 1;
        }

        return false;
    }

    @Override
    public void onNeighborBlockChange( World world, int i, int j, int k, int iNeighborBlockID )
    {
        scheduleCheckForFall(world, i, j, k);
    }


    //----------- Falling Block Functionality -----------//


    @Override
    public void onBlockAdded( World world, int i, int j, int k )
    {
        scheduleCheckForFall(world, i, j, k);
    }

    @Override
    public boolean dropComponentItemsOnBadBreak(World world, int x, int y, int z, int iMetadata, float chanceOfDrop) {
        dropItemsIndividually(world, x, y, z, DNItems.charcoalDust.itemID, 1, 0, 1);

        return true;
    }

    @Override
    public void onFinishFalling( World world, int i, int j, int k, int iMetadata )
    {
        //super.onFinishFalling(world, i, j, k, iMetadata);
        System.out.println("finished falling");
    }

    @Override
    public boolean attemptToCombineWithFallingEntity(World world, int i, int j, int k, EntityFallingSand entity)
    {
        if ( entity.blockID == blockID )
        {
            System.out.println("trying to merge");
            return mergeCoalBlocks(world, i, j, k, entity);
        }

        System.out.println("didn't merge");

        return false;
    }

    private boolean mergeCoalBlocks(World world, int i, int j, int k, EntityFallingSand entity) {
        int metadata = world.getBlockMetadata(i,j,k);
        int metadataAbove = entity.metadata + 1;
        int amountAddedWithAbove = metadata + metadataAbove;

        System.out.println("metadata: " + metadata);
        System.out.println("metadataAbove: " + metadataAbove);
        System.out.println("amountAddedWithAbove: " + amountAddedWithAbove);

        return world.setBlockMetadataWithNotify( i, j, k, amountAddedWithAbove );
    }

    //----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int iNeighborI, int iNeighborJ, int iNeighborK, int iSide) {
        return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
//        this.blockIcon = register.registerIcon("fcBlockLogSmouldering");
        this.blockIcon = register.registerIcon("DNBlock_charcoal");
    }

    @Override
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k) {
        renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(
                renderer.blockAccess, i, j, k) );

        return renderer.renderStandardBlock( this, i, j, k );
    }

    @Override
    public void renderFallingBlock(RenderBlocks renderBlocks, int i, int j, int k, int iMetadata) {
        renderBlocks.setRenderBounds(getBlockBoundsFromPoolBasedOnState(iMetadata));

        renderBlocks.renderStandardFallingBlock(this, i, j, k, iMetadata);
    }
}
