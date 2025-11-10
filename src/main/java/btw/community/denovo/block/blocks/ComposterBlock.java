package btw.community.denovo.block.blocks;

import btw.block.BTWBlocks;
import btw.block.util.Flammability;
import btw.client.render.util.RenderUtils;
import btw.community.denovo.block.models.ComposterModel;
import btw.community.denovo.block.tileentities.CisternBaseTileEntity;
import btw.community.denovo.block.tileentities.ComposterTileEntity;
import btw.community.denovo.emi.tag.DeNovoTags;
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

        setUnlocalizedName("denovo.composter");

        setCreativeTab(CreativeTabs.tabRedstone);

        setTickRandomly(true);
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new ComposterTileEntity();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int facing, float clickX, float clickY, float clickZ) {
        //since we want the base functionality of the cistern as well
        super.onBlockActivated(world, x, y, z, player, facing, clickX, clickY, clickZ);

        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
        CisternBaseTileEntity cisternBase = (CisternBaseTileEntity) tileEntity;

        if (cisternBase.isEmptyOrHasCompost() || cisternBase.isEmptyOrHasSand()) {
            handleContentsEmptyOrContents(world, x, y, z, facing, player, cisternBase);
        }
        else if (cisternBase.isFullWithCompostOrMaggots() || cisternBase.isFullWithGrass()) {
            handleContentsCompostOrMaggots(world, x, y, z, facing, player, cisternBase);
        }
        else if (cisternBase.isFullWithSand()) {
            handleContentsSand(world, x, y, z, facing, player, cisternBase);
        }

        return true;
    }

    @Override
    public void randomUpdateTick(World world, int x, int y, int z, Random rand) {
        CisternBaseTileEntity cisternBase = (CisternBaseTileEntity) world.getBlockTileEntity(x, y, z);
        if (cisternBase.isFullWithCompostOrMaggots()) {
            checkForSpread(world, x, y, z, rand);
        }
        else if (cisternBase.isFullWithGrass()) {
            if (BlockGrass.canGrassSpreadFromLocation(world, x, y, z)) {
//                System.out.println("spreading?");
                if (rand.nextFloat() <= 0.8f) {
//                    System.out.println("spreading!");
                    BlockGrass.checkForGrassSpreadFromLocation(world, x, y, z);
                }
            }
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

    public boolean canCactusGrowOnBlock(World world, int x, int y, int z) {
        CisternBaseTileEntity cisternBase = (CisternBaseTileEntity) world.getBlockTileEntity(x, y, z);

        return cisternBase.isFullWithSand();
    }

    //----------- Class Specific Methods -----------//

    protected boolean handleContentsCompostOrMaggots(World world, int x, int y, int z, int facing, EntityPlayer player, CisternBaseTileEntity cisternBase) {


        if (cisternBase.getFillType() == CisternUtils.CONTENTS_COMPOST || cisternBase.getFillType() == CisternUtils.CONTENTS_GRASS ) {
            if (player.getHeldItem() == null){
                if (!world.isRemote) {
                    returnItemsWhenFullWithCompost(world, x, y, z, facing);

                    CisternUtils.playSound(world, x, y, z, Block.dirt.stepSound.getStepSound(), 1 / 4F, 1F);
                }
                cisternBase.setFillType(CisternUtils.CONTENTS_EMPTY);
            }
            else if (player.getHeldItem().itemID == BTWItems.dung.itemID){
                if (!player.capabilities.isCreativeMode) --player.getHeldItem().stackSize;

                CisternUtils.playSound(world, x, y, z, Block.grass.stepSound.getStepSound(), 1 / 4F, 1F);
                cisternBase.setFillType(CisternUtils.CONTENTS_GRASS);
                cisternBase.setProgressCounter(0);
            }
        } else if (cisternBase.getFillType() == CisternUtils.CONTENTS_MAGGOTS) {
            if (player.getHeldItem() != null) return false;

            if (!world.isRemote) {

                returnItemsWhenFullWithMaggots(world, x, y, z, facing);
                cisternBase.setSolidFillLevel(cisternBase.getSolidFillLevel() - 2);

                CisternUtils.playSound(world, x, y, z, Block.dirt.stepSound.getStepSound(), 1 / 4F, 1F);
                CisternUtils.playSound(world, x, y, z, Block.blockClay.stepSound.getStepSound(), 1 / 8F, 1F);
            }

            cisternBase.setFillType(CisternUtils.CONTENTS_COMPOST);
            cisternBase.setProgressCounter(0);
        }

        world.markBlockForRenderUpdate(x, y, z);

        return true;
    }

    protected boolean handleContentsSand(World world, int x, int y, int z, int facing, EntityPlayer player, CisternBaseTileEntity cisternBase) {
        if (player.getHeldItem() != null) return false;

        if (cisternBase.getFillType() == CisternUtils.CONTENTS_SAND) {
            if (!world.isRemote) {
                returnItemsWhenFullWithSand(world, x, y, z, facing);

                CisternUtils.playSound(world, x, y, z, Block.sand.stepSound.getStepSound(), 1 / 4F, 1F);
            }

            cisternBase.setFillType(CisternUtils.CONTENTS_EMPTY);
        }

        world.markBlockForRenderUpdate(x, y, z);
        world.notifyBlockChange(x, y, z, this.blockID);

        return true;
    }

    protected static void returnItemsWhenFullWithSand(World world, int x, int y, int z, int facing) {
//        ItemUtils.ejectStackFromBlockTowardsFacing(world, x, y, z, new ItemStack(BTWItems.dirtPile), facing);
        ItemUtils.ejectStackFromBlockTowardsFacing(world, x, y, z, new ItemStack(Block.sand), facing);
    }

    protected static void returnItemsWhenFullWithCompost(World world, int x, int y, int z, int facing) {
//        ItemUtils.ejectStackFromBlockTowardsFacing(world, x, y, z, new ItemStack(BTWItems.dirtPile), facing);
        ItemUtils.ejectStackFromBlockTowardsFacing(world, x, y, z, new ItemStack(BTWBlocks.looseDirt), facing);
    }

    protected static void returnItemsWhenFullWithMaggots(World world, int x, int y, int z, int facing) {
//        ItemUtils.ejectStackFromBlockTowardsFacing(world, x, y, z, new ItemStack(BTWItems.dirtPile), facing);
        ItemUtils.ejectStackFromBlockTowardsFacing(world, x, y, z, new ItemStack(DNItems.rawMaggots), facing);
    }

    protected boolean handleContentsEmptyOrContents(World world, int x, int y, int z, int facing, EntityPlayer player, CisternBaseTileEntity cisternBase) {
        ItemStack heldStack = player.getHeldItem();
        if (heldStack == null) return false;

        if (cisternBase.isEmptyOrHasSand() && CisternUtils.isSand(heldStack) > 0){
            int amountFilled = CisternUtils.isSand(heldStack);
            int containsAmount = cisternBase.getSolidFillLevel();

            if (containsAmount + amountFilled <= CisternUtils.MAX_SOLID_FILL_LEVEL){
                cisternBase.addSolid(amountFilled);
                cisternBase.setFillType(CisternUtils.CONTENTS_SAND);
                world.markBlockForRenderUpdate(x, y, z);

                if (!player.capabilities.isCreativeMode) heldStack.stackSize--;
                CisternUtils.playSound(world, x, y, z, Block.sand.stepSound.getStepSound(), 0.25F, 1F);
                return true;
            }
        }

        if (cisternBase.isEmptyOrHasCompost() && CisternUtils.isValidCompostable(heldStack)) {
            if (CisternUtils.doesTagContainsStack(DeNovoTags.rich_compostables, heldStack)){
                cisternBase.addSolid(2);
            }
            else cisternBase.addSolid(1);
            cisternBase.setFillType(CisternUtils.CONTENTS_COMPOST);
            world.markBlockForRenderUpdate(x, y, z);

            if (!player.capabilities.isCreativeMode) heldStack.stackSize--;
            CisternUtils.playSound(world, x, y, z, Block.leaves.stepSound.getStepSound(), 0.25F, 1F);
            return true;
        }

        return false;
    }

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
    public Icon getBlockTexture(IBlockAccess blockAccess, int x, int y, int z, int face) {
        CisternBaseTileEntity cisternBase = (CisternBaseTileEntity) blockAccess.getBlockTileEntity(x, y, z);

        if (cisternBase != null){
            if (face == 0) return this.bottom;
            else if (face == 1) return this.top;
            else return cisternBase.getFillType() == CisternUtils.CONTENTS_MAGGOTS ? this.maggotsSide : this.side;
        }

        return super.getBlockTexture(blockAccess,  x, y, z, face);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int face, int meta) {

        if (face == 0) return this.bottom;
        else if (face == 1) return this.top;
        else return this.side;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        super.registerIcons(register);

        this.top = register.registerIcon("denovo:composter_top");
        this.blockIcon = this.side = register.registerIcon("denovo:composter");
        this.bottom = register.registerIcon("denovo:composter_bottom");

        this.maggotsSide = register.registerIcon("denovo:composter_side_maggots");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int x, int y, int z) {
        //floor
        renderer.setRenderBounds(2 / 16D, 0 / 16D, 2 / 16D, 14 / 16D, 0.99 / 16D, 14 / 16D);
        RenderUtils.renderStandardBlockWithTexture(renderer, this, x, y, z, bottom);


        //render composter
        renderer.setRenderBounds(0D, 0D, 0D, 1D, 1D, 1D);
        return model.renderAsBlock(renderer, this, x, y, z);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderer, int damage, float brightness) {

        //floor
        renderer.setRenderBounds(2 / 16D, 0 / 16D, 2 / 16D, 14 / 16D, 0.99 / 16D, 14 / 16D);
        RenderUtils.renderInvBlockWithTexture(renderer, this, -0.5F, -0.5F, -0.5F, bottom);

        //contents
        renderBlockContentsAsItem(renderer, this, damage);

        if (CisternUtils.getFillType(damage) == CisternUtils.CONTENTS_MAGGOTS){
            this.side = this.maggotsSide;
        }
        else this.side = this.blockIcon;

        renderer.setRenderBounds(0D, 0D, 0D, 1D, 1D, 1D);
        model.renderAsItemBlock(renderer, this, damage);
    }
}
