package btw.community.denovo.block.blocks;

import btw.block.BTWBlocks;
import btw.block.util.Flammability;
import btw.client.render.util.RenderUtils;
import btw.community.denovo.block.models.SieveModel;
import btw.community.denovo.recipes.SiftingCraftingManager;
import btw.community.denovo.recipes.SiftingRecipe;
import btw.community.denovo.tileentity.SieveTileEntity;
import btw.community.denovo.utils.SieveUtils;
import btw.crafting.manager.HopperFilteringCraftingManager;
import btw.crafting.recipe.types.HopperFilterRecipe;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class SieveBlock extends BlockContainer {
    private static final SieveModel model = new SieveModel();

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
        HopperFilteringCraftingManager crafting = HopperFilteringCraftingManager.instance;

        ItemStack heldStack = player.getHeldItem();
        ItemStack filterStack = sieve.getFilterStack();
        ItemStack contentsStack = sieve.getContentsStack();
        byte progressCounter = sieve.getProgressCounter();

        if (!world.isRemote) {
            if (filterStack == null && heldStack != null && SieveUtils.isValidHopperFilter(heldStack)) {
                // Add new filter
                filterStack = heldStack.copy();
                filterStack.stackSize = 1;
                sieve.setFilterStack(filterStack);
                heldStack.stackSize--;
            } else if (contentsStack != null) {
                if (progressCounter > 0) {
                    // Keep sieving
                    progressCounter -= 1;
                    sieve.progress(progressCounter);
                }
            } else if (filterStack != null) {
                if (heldStack == null && player.isSneaking()) {
                    // Pull filter out
                    player.addStackToCurrentHeldStackIfEmpty(filterStack);
                    sieve.setFilterStack(null);
                } else if (heldStack != null) {
                    // Fill the sieve
                    HopperFilterRecipe recipe = crafting.getRecipe(heldStack, filterStack);
                    if (recipe != null) {
                        contentsStack = heldStack.copy();
                        contentsStack.stackSize = 1;
                        heldStack.stackSize--;

                        sieve.fill(contentsStack);
                    }

                    SiftingRecipe siftingRecipe = SiftingCraftingManager.getRecipe(heldStack, filterStack);
                    if (siftingRecipe != null) {
                        contentsStack = heldStack.copy();
                        contentsStack.stackSize = 1;
                        heldStack.stackSize--;

                        sieve.fill(contentsStack);
                    }
                }
            }

            world.markBlockForUpdate(xCoord, yCoord, zCoord);
        }

        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World var1) {
        return new SieveTileEntity();
    }

    //----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon filterIcon;

    @Environment(EnvType.CLIENT)
    private Icon contentsIcon;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        this.blockIcon = register.registerIcon("wood");

        filterIcon = register.registerIcon("DNBlock_mesh");
        contentsIcon = register.registerIcon("gravel");
    }

    @Override
    @Environment(EnvType.CLIENT)
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

        byte progressCounter = tileEntity.getProgressCounter();

        if (progressCounter > 0) {
            Icon contentsIcon = SieveUtils.getBulkIcon(tileEntity.getContentsStack());

            double base = 1F - (4F / 16F);
            double offset = progressCounter / 16F / (SieveTileEntity.MAX_PROGRESS - 1);
            renderer.setRenderBounds(2F / 16F, base, 2F / 16F, 1F - (2F / 16F), base + (offset * 6), 1F - (2F / 16F));

            RenderUtils.renderStandardBlockWithTexture(renderer, this, i, j, k, contentsIcon);
        }

        return SieveBlock.model.renderAsBlock(renderer, this, i, j, k);
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
