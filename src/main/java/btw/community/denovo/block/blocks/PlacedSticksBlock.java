package btw.community.denovo.block.blocks;

import btw.block.BTWBlocks;
import btw.block.util.Flammability;
import btw.community.denovo.block.DNBlocks;
import btw.item.BTWItems;
import btw.item.util.ItemUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class PlacedSticksBlock extends Block {
    public PlacedSticksBlock(int blockID) {
        super(blockID, Material.wood);

        setHardness( 2F );

        setAxesEffectiveOn();

        setFireProperties(Flammability.HIGH);

        setBuoyant();

        setTickRandomly( true );

        setStepSound( soundWoodFootstep );

        setUnlocalizedName("DNBlock_placed_sticks");
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int facing, float clickX, float clickY, float clickZ) {
        ItemStack heldStack = player.getHeldItem();
        int metadata = world.getBlockMetadata(x,y,z);
        if (heldStack == null)
        {
            if (player.isSneaking())
            {
                if (metadata > 0)
                {
                    metadata--;
                    world.setBlockMetadataWithNotify(x,y,z, metadata);
                    ItemUtils.givePlayerStackOrEject(player, new ItemStack(Item.stick, 1), x,y,z);
                    return true;
                }
                else {
                    world.setBlockToAir(x,y,z);
                    ItemUtils.givePlayerStackOrEject(player, new ItemStack(Item.stick, 1), x,y,z);
                    return true;
                }
            }
        }
        else if (heldStack != null)
        {
            if (heldStack.itemID == Item.stick.itemID )
            {
                if (hasPlaceToStore(metadata))
                {
                    if (!world.isRemote) addSticksToPile(world, x,y,z);
                    if (!player.capabilities.isCreativeMode) heldStack.stackSize--;
                    return true;
                }
                else {
                    if (facing == 1 && world.getBlockId(x, y + 1, z) == 0)
                    {
                        if (!world.isRemote) world.setBlockAndMetadataWithNotify(x,y + 1,z, DNBlocks.placedSticks.blockID, 0);
                        if (!player.capabilities.isCreativeMode) heldStack.stackSize--;
                        return true;
                    }

                }

            }
        }

        return false;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int par5) {


        if (!this.canBlockStay(world, x, y, z))
        {
            this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
            world.setBlockToAir(x, y, z);
        }
        else {
            if (world.getBlockId(x, y + 1, z) == Block.fire.blockID)
            {
                int meta = world.getBlockMetadata(x,y,z);
                world.setBlockAndMetadataWithNotify(x, y, z, DNBlocks.smolderingPlacedSticks.blockID, meta);
            }
        }
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return !canBlockStay(world, x, y, z) ? false : super.canPlaceBlockAt(world, x, y, z);
    }

    @Override
    public boolean canBlockStay(World world, int x, int y, int z) {
        return !world.doesBlockHaveSolidTopSurface(x, y - 1, z) ? false : super.canBlockStay(world, x, y, z);
    }

    @Override
    public boolean isBlockSolid(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
        return par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 15;
    }

    @Override
    public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int x, int y, int z, int iFacing) {
        int meta = blockAccess.getBlockMetadata(x,y,z);
        return iFacing == 1 && meta == 15;
    }

    @Override
    public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int x, int y, int z, int iFacing, boolean bIgnoreTransparency )
    {
        return hasLargeCenterHardPointToFacing(blockAccess, x,y,z,iFacing);
    }

    @Override
    public int idDropped(int par1, Random par2Random, int par3) {
        return Item.stick.itemID;
    }

    @Override
    public int idPicked(World par1World, int par2, int par3, int par4) {
        return Item.stick.itemID;
    }

    @Override
    public int quantityDropped(Random par1Random) {
        return 0;
    }

    @Override
    public void dropBlockAsItemWithChance( World world, int i, int j, int k, int iMetadata, float fChance, int iFortuneModifier )
    {
        if (!world.isRemote) {
            dropItemsIndividually(world, i, j, k, Item.stick.itemID, iMetadata + 1, 0, 1);
        }
    }

    //------------- Class Specific Methods ------------//

    private boolean hasPlaceToStore(int metadata) {
        return metadata < 15;
    }

    private void addSticksToPile(World world, int x, int y, int z) {
        int metadata = world.getBlockMetadata(x,y,z);
        world.setBlockMetadataWithNotify(x,y,z,metadata + 1);
    }

    @Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(
            IBlockAccess blockAccess, int x, int y, int z)
    {
        int metadata = blockAccess.getBlockMetadata(x, y, z) / 4;
        double minY = 0/16D;
        double maxY = 4/16D + 4/16D * metadata;

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

    //----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon[] top = new Icon[4];

    private Icon[] top_alt = new Icon[4];

    @Environment(EnvType.CLIENT)
    private Icon[] side = new Icon[2];

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int iNeighborI, int iNeighborJ, int iNeighborK, int iSide) {
        return true;
    }

    @Override
    public Icon getBlockTexture(IBlockAccess blockAccess, int x, int y, int z, int face) {
        int meta = blockAccess.getBlockMetadata(x, y, z);
        int layer = meta / 4;
        boolean isEvenPos = isEven(x, y, z);
        int textureIndex = meta % 4;
        boolean isEvenLayer = (layer % 2 == 0);

        // Determine top texture
        if (face <= 1) {
            if (isEvenPos) {
                return isEvenLayer ? top_alt[textureIndex] : top[textureIndex];
            } else {
                return isEvenLayer ? top[textureIndex] : top_alt[textureIndex];
            }
        }

        // Determine side texture
        if (face == 2 || face == 3) {
            return isEvenPos ? side[1] : side[0];
        } else {
            return isEvenPos ? side[0] : side[1];
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {

        top[0] = register.registerIcon("DNBlock_placed_shafts_top_0");
        top[1] = register.registerIcon("DNBlock_placed_shafts_top_1");
        top[2] = register.registerIcon("DNBlock_placed_shafts_top_2");
        top[3] = register.registerIcon("DNBlock_placed_shafts_top_3");

        top_alt[0] = register.registerIcon("DNBlock_placed_shafts_top_alt_0");
        top_alt[1] = register.registerIcon("DNBlock_placed_shafts_top_alt_1");
        top_alt[2] = register.registerIcon("DNBlock_placed_shafts_top_alt_2");
        top_alt[3] = register.registerIcon("DNBlock_placed_shafts_top_alt_3");

        side[0] = register.registerIcon("DNBlock_placed_shafts_side");
        side[1] = register.registerIcon("DNBlock_placed_shafts_side_alt");

        blockIcon = side[0];
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int x, int y, int z) {

        int metadata = renderer.blockAccess.getBlockMetadata(x,y,z); // Implement this method to extract the correct value from metadata
        int numberOfLayers = (int) Math.floor(metadata/4D);

        for (int layer = 0; layer < numberOfLayers + 1; layer++) {

            double xMin = 0/16D;
            double xMax = 4/16D + (metadata%4)*4/16D;

            if (layer < numberOfLayers) {
                xMax = 1D;  // If the layer is full, set xMax to the full width of the block
            }

            double yMin = layer * 4/16D;
            double yMax = yMin + 4/16D;

            double zMin = 0D;
            double zMax = 1D;

            if (isEven(x,y,z))
            {
                if (layer%2 == 0)
                {
                    // x and z swapped
                    renderer.setRenderBounds(zMin, yMin, xMin, zMax, yMax, xMax);
                }
                else {
                    renderer.setRenderBounds(xMin, yMin, zMin, xMax, yMax, zMax);
                }
            }
            else {
                if (layer%2 == 1)
                {
                    // x and z swapped
                    renderer.setRenderBounds(zMin, yMin, xMin, zMax, yMax, xMax);
                }
                else {
                    renderer.setRenderBounds(xMin, yMin, zMin, xMax, yMax, zMax);
                }
            }
            renderer.renderStandardBlock(this, x, y, z);

        }
        return true;
    }

    private boolean isEven(int x, int y, int z) {
        if ((x + z) % 2 == 0) {
            return true;
        } else {
            return false;
        }
    }

}
