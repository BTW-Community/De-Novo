package btw.community.denovo.block.blocks;

import btw.block.BTWBlocks;
import btw.block.util.Flammability;
import btw.client.render.util.RenderUtils;
import btw.community.denovo.block.models.SieveModel;
import btw.community.denovo.tileentity.SieveTileEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class SieveBlock extends BlockContainer {
    private static final SieveModel model = new SieveModel();
    private Icon planks;

    public SieveBlock(int id) {
        super(id, BTWBlocks.plankMaterial);

        setHardness(2F);

        setAxesEffectiveOn(true);

        setBuoyancy(1F);

        setFireProperties(Flammability.PLANKS);

        initBlockBounds(0D, 0D, 0D, 1D, 1D, 1D);

        setStepSound(soundWoodFootstep);

        setUnlocalizedName("DNSieve");

        setTickRandomly(true);

        setCreativeTab(CreativeTabs.tabRedstone);
    }

    @Override
    public boolean onBlockActivated(World world, int xCoord, int yCoord, int zCoord, EntityPlayer player, int facing, float xClick, float yClick, float zClick) {
        SieveTileEntity sieve = (SieveTileEntity) world.getBlockTileEntity(xCoord, yCoord, zCoord);

        ItemStack heldStack = player.getHeldItem();
        ItemStack filterStack = sieve.getFilterStack();
        ItemStack contentsStack = sieve.getContentsStack();
        byte progressCounter = sieve.getProgressCounter();

        if (filterStack == null && heldStack != null) {
            // Add new filter
            if (!world.isRemote) {
                filterStack = heldStack.copy();
                filterStack.stackSize = 1;
                sieve.setFilterStack(filterStack);
                heldStack.stackSize--;
            }
        } else if (contentsStack != null && progressCounter > 0) {
            // Keep sieving
        } else if (contentsStack != null && progressCounter == 0) {
            // Done sieving, transform contents
            //TODO: implement
        } else if (filterStack != null && heldStack == null) {
            if (!world.isRemote) {
                player.addStackToCurrentHeldStackIfEmpty(filterStack);
                sieve.setFilterStack(null);
            }
        }

        world.markBlockForUpdate(xCoord, yCoord, zCoord);

        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World var1) {
        return new SieveTileEntity();
    }

    // Rendering

    @Override
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k) {
        IBlockAccess blockAccess = renderer.blockAccess;
        SieveTileEntity tileEntity = (SieveTileEntity) blockAccess.getBlockTileEntity(i, j, k);

        // render the filter
        ItemStack filterStack = tileEntity.getFilterStack();

        if (filterStack != null) {
            Icon filterIcon = filterStack.getItem().getHopperFilterIcon();

            if (filterIcon != null) {
                renderer.setRenderBounds(2F / 16F, 1F - (5F / 16F), 2F / 16F, 1F - (2F / 16F), 1F - (4F / 16F), 1F - (2F / 16F));

                RenderUtils.renderStandardBlockWithTexture(renderer, this, i, j, k, filterIcon);
            }
        }

        return SieveBlock.model.renderAsBlock(renderer, this, i, j, k);
    }

    //----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon filterIcon;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        this.blockIcon = this.planks = register.registerIcon("wood");

        filterIcon = register.registerIcon("DNBlock_mesh");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getHopperFilterIcon() {
        return filterIcon;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int iNeighborI, int iNeighborJ, int iNeighborK, int iSide) {
        return true;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public void renderBlockAsItem(RenderBlocks renderBlocks, int iItemDamage, float fBrightness) {
        SieveBlock.model.renderAsItemBlock(renderBlocks, this, iItemDamage);
    }
}
