package btw.community.denovo.block.blocks;

import btw.block.model.BlockModel;
import btw.block.model.OreChunkLegacyModel;
import btw.block.model.OreChunkModel;
import btw.client.render.util.RenderUtils;
import btw.community.denovo.block.tileentities.LavaCobbleTileEntity;
import btw.community.denovo.utils.CisternUtils;
import btw.item.BTWItems;
import btw.item.util.ItemUtils;
import btw.world.util.WorldUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class LavaCobbleBlock extends BlockContainer {
    public LavaCobbleBlock(int blockID) {
        super(blockID, Material.rock);
        setTickRandomly(true);

        setHardness(1.0f);
        setResistance(5.0f);
        setPicksEffectiveOn();
        setChiselsEffectiveOn();
        setStepSound(Block.soundStoneFootstep);
        setUnlocalizedName("denovo.lava_cobble");

    }

    @Override
    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer player, int iFacing, float fXClick, float fYClick, float fZClick) {
        LavaCobbleTileEntity lavaCobble = (LavaCobbleTileEntity) world.getBlockTileEntity(i,j,k);

        ItemStack heldItem = player.getHeldItem();

        if (heldItem == null) return false;

        if (heldItem.itemID == BTWItems.stone.itemID){
            int currentStones = lavaCobble.getCobbleCounter();
            if (currentStones < LavaCobbleTileEntity.MAX_COBBLE){
                lavaCobble.setCobbleCounter(currentStones + 1);
                if (!player.capabilities.isCreativeMode) --heldItem.stackSize;
                lavaCobble.onInventoryChanged();
                return true;
            }
        } else if (heldItem.itemID == BTWItems.straw.itemID) {
            int currentStones = lavaCobble.getCobbleCounter();
            int currentStraw = lavaCobble.getStrawCounter();

            if (currentStones < (currentStraw + 1) * 2) return false;

            if (currentStraw < LavaCobbleTileEntity.MAX_STRAW){
                lavaCobble.setStrawCounter(currentStraw + 1);
                if (!player.capabilities.isCreativeMode) --heldItem.stackSize;
                lavaCobble.onInventoryChanged();
                return true;
            }
        }

        return false;
    }

    @Override
    public void onNeighborBlockChange(World world, int i, int j, int k, int iBlockID) {

        LavaCobbleTileEntity lavaCobble = (LavaCobbleTileEntity) world.getBlockTileEntity(i,j,k);
        if (lavaCobble != null) {
            if ((lavaCobble.getCobbleCounter() < 8 && lavaCobble.getStrawCounter() < 4) && !WorldUtils.doesBlockHaveSmallCenterHardpointToFacing(world, i, j - 1, k, 1, true)) {

                for (int count = 0; count < lavaCobble.getCobbleCounter(); count++) {
                    ItemUtils.ejectStackWithRandomOffset(world, i, j, k, new ItemStack(BTWItems.stone));
                }

                //super
                this.dropBlockAsItem(world, i, j, k, world.getBlockMetadata(i, j, k), 0);
                world.setBlockToAir(i, j, k);
            }
        }
    }

    @Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(IBlockAccess blockAccess, int x, int y, int z) {
        LavaCobbleTileEntity lavaCobble = (LavaCobbleTileEntity) blockAccess.getBlockTileEntity(x,y,z);

        if (lavaCobble != null){
            return getAABBForCobble(lavaCobble.getCobbleCounter() / 8D);
        }
        return super.getBlockBoundsFromPoolBasedOnState(blockAccess, x, y, z);
    }

    @Override
    public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing) {
        LavaCobbleTileEntity lavaCobble = (LavaCobbleTileEntity) blockAccess.getBlockTileEntity(i,j,k);

        if (lavaCobble != null){
            if (lavaCobble.getCobbleCounter() == 8 && iFacing == 1) return true;
        }

        return super.hasLargeCenterHardPointToFacing(blockAccess, i, j, k, iFacing);
    }

    //ITileEntityProvider

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new LavaCobbleTileEntity();
    }

    //OreChunkBlock

    @Override
    public int idDropped(int iMetadata, Random random, int iFortuneModifier) {
        return BTWItems.ironOreChunk.itemID;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public int idPicked(World world, int x, int y, int z) {
        return BTWItems.ironOreChunk.itemID;
    }

    protected static OreChunkModel model = new OreChunkModel();
    protected static OreChunkModel legacyModel = new OreChunkLegacyModel();

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean canGroundCoverRestOnBlock(World world, int i, int j, int k) {
        return world.doesBlockHaveSolidTopSurface(i, j - 1, k);
    }

    @Override
    public float groundCoverRestingOnVisualOffset(IBlockAccess blockAccess, int i, int j, int k) {
        return -1.0f;
    }

    @Override
    public int getFacing(int iMetadata) {
        return (iMetadata & 3) + 2;
    }

    @Override
    public int setFacing(int iMetadata, int iFacing) {
        iMetadata &= 0xFFFFFFFC;
        return iMetadata |= MathHelper.clamp_int(iFacing, 2, 5) - 2;
    }

    @Override
    public boolean canRotateOnTurntable(IBlockAccess blockAccess, int i, int j, int k) {
        return true;
    }

    @Environment(value=EnvType.CLIENT)
    public OreChunkModel getModel() {
        return RenderUtils.shouldRenderLegacyModel() ? legacyModel : model;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int iNeighborI, int iNeighborJ, int iNeighborK, int iSide) {
        return true;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean renderOreChunkBlock(RenderBlocks renderBlocks, int i, int j, int k) {
        AxisAlignedBB[] ores;
        OreChunkModel model = this.getModel();
        int facing = this.getFacing(renderBlocks.blockAccess, i, j, k);
        int variant = RenderUtils.getVariantFromCoordinates(i, j, k, model.variants.length);
        if (RenderUtils.shouldRenderLegacyModel()) {
            BlockModel transformedModel = model.makeTemporaryCopy();
            transformedModel.rotateAroundYToFacing(facing);
            return transformedModel.renderAsBlock(renderBlocks, this, i, j, k);
        }
        for (AxisAlignedBB ore : ores = model.variants[variant].getStoneModels()) {
            AxisAlignedBB box = ore.makeTemporaryCopy();
            box.rotateAroundYToFacing(facing);
            box.renderAsBlock(renderBlocks, this, i, j, k);
        }
        return true;
    }

    //Render
    @Environment(value= EnvType.CLIENT)
    private Icon lavaCobble;
    @Environment(value= EnvType.CLIENT)
    private Icon strawCracks;

    @Environment(value= EnvType.CLIENT)
    private Icon lavaCracks;

    @Override
    @Environment(value=EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        this.blockIcon = register.registerIcon("btw:iron_ore_chunk");
        this.lavaCobble = register.registerIcon("denovo:lava_cobble");
        this.strawCracks = register.registerIcon("denovo:lava_cobble_straw_overlay");
        this.lavaCracks = register.registerIcon("denovo:lava_cobble_lava_overlay");
    }

    @NotNull
    private static AxisAlignedBB getAABBForStraw(double maxHeight) {
        return new AxisAlignedBB(
                0, 0, 0,
                1, maxHeight - 0.0001, 1
        );
    }

    @NotNull
    private static AxisAlignedBB getAABBForCobble(double maxHeight) {
        return new AxisAlignedBB(
                0.0001, 0.0001, 0.0001,
                0.9999, maxHeight - 0.0002, 0.9999
        );
    }

    @Override
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k) {
        LavaCobbleTileEntity lavaCobble = (LavaCobbleTileEntity) renderer.blockAccess.getBlockTileEntity(i,j,k);

        renderer.setRenderBounds(getAABBForCobble(lavaCobble.getCobbleCounter() / 8D));
        RenderUtils.renderStandardBlockWithTexture(renderer, this, i,j,k, this.lavaCobble);

        //iron ore chunk
        return renderOreChunkBlock(renderer, i,j,k);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void renderBlockSecondPass(RenderBlocks renderBlocks, int i, int j, int k, boolean bFirstPassResult) {
        if (bFirstPassResult) {
            LavaCobbleTileEntity lavaCobble = (LavaCobbleTileEntity) renderBlocks.blockAccess.getBlockTileEntity(i,j,k);

            int strawCount = lavaCobble.getStrawCounter();

            if (strawCount > 0){

                renderBlocks.setRenderBounds(getAABBForStraw(lavaCobble.getStrawCounter()/4D));

                if (lavaCobble.getState() != LavaCobbleTileEntity.STATE_CONVERTING){
                    RenderUtils.renderStandardBlockWithTexture(renderBlocks, this, i, j, k, this.strawCracks);
                }
                else {
                    RenderUtils.renderBlockFullBrightWithTexture(renderBlocks, renderBlocks.blockAccess, i, j, k, this.lavaCracks);
                }
            }
        }
    }

    @Override
    public void renderBlockAsItem(RenderBlocks renderBlocks, int iItemDamage, float fBrightness) {

//        CisternUtils.pack(cobble, straw, 0, 0);
        int cobbleCount = CisternUtils.getLiquidFillLevel(iItemDamage);
        int strawCount = CisternUtils.getSolidFillLevel(iItemDamage);
        int state = CisternUtils.getFillType(iItemDamage);

        if (cobbleCount < 3) {
            this.getModel().renderAsItemBlock(renderBlocks, this, iItemDamage);
        }

        if (cobbleCount > 0){
            renderBlocks.setRenderBounds(getAABBForCobble(cobbleCount / 8D));
            RenderUtils.renderInvBlockWithTexture(renderBlocks, this, -0.5F, -0.5F, -0.5F, this.lavaCobble);
        }

        if (strawCount > 0){
            Icon fillIcon = state == 0 ? this.strawCracks : this.lavaCracks;
            renderBlocks.setRenderBounds(getAABBForStraw(strawCount /4D));
            RenderUtils.renderInvBlockWithTexture(renderBlocks, this, -0.5F, -0.5F, -0.5F, fillIcon);
        }
    }

    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        LavaCobbleTileEntity lavaCobble = (LavaCobbleTileEntity) world.getBlockTileEntity(x,y,z);

        if (lavaCobble.getState() == LavaCobbleTileEntity.STATE_CONVERTING){
            double xPos = x + 0.25F + rand.nextFloat() * 0.5F;
            double yPos = y + 1.0F + rand.nextFloat() * 0.25F;
            double zPos = z + 0.25F + rand.nextFloat() * 0.5F;

            if (world.getBlockId(x,y + 1,z) != 0) {
                yPos += 1.0F;
            }

            world.spawnParticle("fcwhitesmoke", xPos, yPos, zPos, 0.0D, 0.0D, 0.0D);
        }
    }
}
