package btw.community.denovo.block.blocks;

import btw.block.util.Flammability;
import btw.client.render.util.RenderUtils;
import btw.community.denovo.block.models.ComposterModel;
import btw.community.denovo.block.tileentities.CisternBaseTileEntity;
import btw.community.denovo.block.tileentities.ComposterTileEntity;
import btw.community.denovo.item.DNItems;
import btw.community.denovo.utils.CisternUtils;
import btw.item.BTWItems;
import btw.item.util.ItemUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class ComposterBlock extends CisternBaseBlock {
    private final ComposterModel model = new ComposterModel();

    public ComposterBlock(int blockID) {
        super(blockID, Material.wood);

        setHardness(0.5F);

        setAxesEffectiveOn(true);

        setBuoyancy(1F);

        setFireProperties(Flammability.PLANKS);

        initBlockBounds(0D, 0D, 0D, 1D, 1D, 1D);

        setStepSound(soundWoodFootstep);

        setUnlocalizedName("DNBlock_composter");

        setCreativeTab(CreativeTabs.tabRedstone);

        setTickRandomly(true);
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new ComposterTileEntity();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int facing, float clickX, float clickY, float clickZ) {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
        CisternBaseTileEntity cisternBase = (CisternBaseTileEntity) tileEntity;
        ItemStack heldStack = player.getHeldItem();

        if (cisternBase.getProgressCounter() > 0) return false;

        if (CisternUtils.isValidCompostable(heldStack)) {
            if (cisternBase.isEmpty() || (!cisternBase.isFull() && cisternBase.getFillType() == CisternUtils.CONTENTS_COMPOST)) {
                if ( !world.isRemote ) cisternBase.addCompost(1);
                if ( !world.isRemote ) cisternBase.setFillType(CisternUtils.CONTENTS_COMPOST);
                world.markBlockForRenderUpdate(x, y, z);

                heldStack.stackSize--;
                if ( world.isRemote ) playSound(world, x, y, z, Block.leaves.stepSound.getStepSound(), 0.25F, 1F);
                return true;
            }
        } else if (cisternBase.isFullWithCompostOrMaggots()) {
            if (cisternBase.getFillType() == CisternUtils.CONTENTS_COMPOST) {
                if (!world.isRemote) {
                    ItemUtils.ejectStackFromBlockTowardsFacing(world, x, y, z, new ItemStack(BTWItems.dirtPile), facing);

                    playSound(world, x, y, z, Block.dirt.stepSound.getStepSound(), 1/4F, 1F);
                }
            } else if (cisternBase.getFillType() == CisternUtils.CONTENTS_MAGGOTS) {
                if (!world.isRemote) {
                    ItemUtils.ejectStackFromBlockTowardsFacing(world, x, y, z, new ItemStack(DNItems.rawMaggots), facing);
                    ItemUtils.ejectStackFromBlockTowardsFacing(world, x, y, z, new ItemStack(BTWItems.dirtPile), facing);

                    playSound(world, x, y, z, Block.dirt.stepSound.getStepSound(), 1/4F, 1F);
                    playSound(world, x, y, z, Block.blockClay.stepSound.getStepSound(), 1/8F, 1F);
                }
            }

            if (!world.isRemote) cisternBase.setFillType(CisternUtils.CONTENTS_EMPTY);
            world.markBlockForRenderUpdate(x, y, z);

            return true;
        }

        return super.onBlockActivated(world, x, y, z, player, facing, clickX, clickY, clickZ);
    }

    @Override
    public void randomUpdateTick(World world, int x, int y, int z, Random rand) {
        CisternBaseTileEntity cisternBase = (CisternBaseTileEntity) world.getBlockTileEntity(x, y, z);
        if (cisternBase.isFullWithCompostOrMaggots()) {
            checkForSpread(world, x, y, z, rand);
        }

    }

    //Currently not used, since it can be broken by hand
    @Override
    public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k, int iMetadata, float fChanceOfDrop) {
        super.dropComponentItemsOnBadBreak(world, i, j, k, iMetadata, fChanceOfDrop);

        dropItemsIndividually(world, i, j, k, Item.stick.itemID, 2, 0, fChanceOfDrop);
        dropItemsIndividually(world, i, j, k, BTWItems.sawDust.itemID, 4, 0, fChanceOfDrop);

        return true;
    }
    //----------- Class Specific Methods -----------//

    //----------- Mushroom Related Methods -----------//

    protected void checkForSpread(World world, int i, int j, int k, Random rand) {
        // copy of MushroomBlockBrown
        // basically a copy/paste of the BlockMushroom updateTick cleaned up and with additional requirements that brown mushrooms can only grow in complete darkness
        int brownMushroomBlockID = Block.mushroomBrown.blockID;

        if (rand.nextInt(25) == 0 && canSpreadToOrFromLocation(world, i, j, k)) {
            int iHorizontalSpreadRange = 4;
            int iNeighboringMushroomsCountdown = 5;

            for (int iTempI = i - iHorizontalSpreadRange; iTempI <= i + iHorizontalSpreadRange; ++iTempI) {
                for (int iTempK = k - iHorizontalSpreadRange; iTempK <= k + iHorizontalSpreadRange; ++iTempK) {
                    for (int iTempJ = j - 1; iTempJ <= j + 1; ++iTempJ) {
                        if (world.getBlockId(iTempI, iTempJ, iTempK) == brownMushroomBlockID) {
                            --iNeighboringMushroomsCountdown;

                            if (iNeighboringMushroomsCountdown <= 0) {
                                return;
                            }
                        }
                    }
                }
            }

            int iSpreadI = i + rand.nextInt(3) - 1;
            int iSpreadK = j + rand.nextInt(2) - rand.nextInt(2);
            int iSpreadJ = k + rand.nextInt(3) - 1;

            for (int iTempCount = 0; iTempCount < 4; ++iTempCount) {
                if (world.isAirBlock(iSpreadI, iSpreadK, iSpreadJ) && Block.mushroomBrown.canBlockStay(world, iSpreadI, iSpreadK, iSpreadJ) &&
                        canSpreadToOrFromLocation(world, iSpreadI, iSpreadK, iSpreadJ)) {
                    i = iSpreadI;
                    j = iSpreadK;
                    k = iSpreadJ;
                }

                iSpreadI = i + rand.nextInt(3) - 1;
                iSpreadK = j + rand.nextInt(2) - rand.nextInt(2);
                iSpreadJ = k + rand.nextInt(3) - 1;
            }

            if (world.isAirBlock(iSpreadI, iSpreadK, iSpreadJ) && Block.mushroomBrown.canBlockStay(world, iSpreadI, iSpreadK, iSpreadJ) &&
                    canSpreadToOrFromLocation(world, iSpreadI, iSpreadK, iSpreadJ)) {
                world.setBlock(iSpreadI, iSpreadK, iSpreadJ, brownMushroomBlockID);
            }
        }
    }

    protected boolean canSpreadToOrFromLocation(World world, int i, int j, int k) {
        int iBlockBelowID = world.getBlockId(i, j - 1, k);

        return iBlockBelowID == Block.mycelium.blockID || world.getFullBlockLightValue(i, j, k) == 0;
    }

    //----------- Client Side Functionality -----------//


    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int face, int meta) {

        if (meta == -1) {
            return this.compost;
        }

        if (face == 0) return this.bottom;
        else if (face == 1) return this.top;
        else return this.side;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        super.registerIcons(register);
        this.top = register.registerIcon("DNBlock_composter_top");
        this.blockIcon = this.side = register.registerIcon("DNBlock_composter");
        this.bottom = register.registerIcon("DNBlock_composter_bottom");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int x, int y, int z) {
        //floor
        renderer.setRenderBounds(2 / 16D, 0 / 16D, 2 / 16D, 14 / 16D, 1 / 16D, 14 / 16D);
        RenderUtils.renderStandardBlockWithTexture(renderer, this, x, y, z, bottom);


        //render composter
        renderer.setRenderBounds(0D, 0D, 0D, 1D, 1D, 1D);
        return model.renderAsBlock(renderer, this, x, y, z);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderer, int damage, float brightness) {

        //floor
        renderer.setRenderBounds(2 / 16D, 0 / 16D, 2 / 16D, 14 / 16D, 1 / 16D, 14 / 16D);
        RenderUtils.renderInvBlockWithTexture(renderer, this, -0.5F, -0.5F, -0.5F, bottom);

        renderer.setRenderBounds(0D, 0D, 0D, 1D, 1D, 1D);
        model.renderAsItemBlock(renderer, this, damage);
    }
}
