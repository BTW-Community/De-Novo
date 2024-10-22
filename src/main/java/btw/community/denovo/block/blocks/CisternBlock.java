package btw.community.denovo.block.blocks;

import btw.client.render.util.RenderUtils;
import btw.community.denovo.block.tileentities.CisternBaseTileEntity;
import btw.community.denovo.block.tileentities.CisternTileEntity;
import btw.community.denovo.item.DNItems;
import btw.item.BTWItems;
import btw.item.util.ItemUtils;
import com.prupe.mcpatcher.cc.ColorizeBlock;
import com.prupe.mcpatcher.cc.Colorizer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;
import java.util.Random;

public class CisternBlock extends CisternBaseBlock {
    public CisternBlock(int blockID) {
        super(blockID, Material.iron);

        setHardness(0.5F);

        setAxesEffectiveOn(true);

        setBuoyancy(1F);

        initBlockBounds(0D, 0D, 0D, 1D, 1D, 1D);

        setStepSound(soundMetalFootstep);

        setUnlocalizedName("DNCistern");
    }

    @Override
    public TileEntity createNewTileEntity(World var1) {
        return new CisternTileEntity();
    }

    @Override
    public int idDropped(int par1, Random par2Random, int par3) {
        return DNItems.cistern.itemID;
    }

    @Override
    public int idPicked(World par1World, int par2, int par3, int par4) {
        return DNItems.cistern.itemID;
    }

    //Sock: copied from vanilla cistern
    @Override
    public void addCollisionBoxesToList(World world, int i, int j, int k,
                                        AxisAlignedBB intersectingBox, List list, Entity entity) {
        // parent method is super complicated for no apparent reason

        AxisAlignedBB tempBox = getCollisionBoundingBoxFromPool(world, i, j, k);

        tempBox.addToListIfIntersects(intersectingBox, list);
    }

    //----------- Class Specific Methods -----------//
    @Override
    protected boolean isValidWaterContainer(ItemStack stack) {
        if (stack.isItemEqual(new ItemStack(Item.bucketEmpty))) return true;
        return super.isValidWaterContainer(stack);
    }

    @Override
    protected ItemStack getFullWaterContainer(ItemStack stack) {
        if (stack.isItemEqual(new ItemStack(Item.bucketEmpty))) return new ItemStack(Item.bucketWater);
        return super.getFullWaterContainer(stack);
    }

    //----------- Client Side Functionality -----------//
    //Sock: copied from vanilla cistern

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int par1, int par2) {
        return par1 == 1 ? this.cauldronTopIcon : (par1 == 0 ? this.cauldronBottomIcon : this.blockIcon);
    }

    @Environment(EnvType.CLIENT)
    private Icon field_94378_a;
    @Environment(EnvType.CLIENT)
    private Icon cauldronTopIcon;
    @Environment(EnvType.CLIENT)
    private Icon cauldronBottomIcon;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister par1IconRegister) {
        super.registerIcons(par1IconRegister);
        this.field_94378_a = par1IconRegister.registerIcon("cauldron_inner");
        this.cauldronTopIcon = par1IconRegister.registerIcon("cauldron_top");
        this.cauldronBottomIcon = par1IconRegister.registerIcon("cauldron_bottom");
        this.blockIcon = par1IconRegister.registerIcon("cauldron_side");
    }

    @Environment(EnvType.CLIENT)
    public Icon func_94375_b(String par0Str) {
        return par0Str == "cauldron_inner" ? field_94378_a : (par0Str == "cauldron_bottom" ? cauldronBottomIcon : null);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k) {
        renderer.setRenderBounds(getBlockBoundsFromPoolBasedOnState(
                renderer.blockAccess, i, j, k));

        return renderBlockCauldron(renderer, this, i, j, k);
    }

    //Sock: copy from RenderBlocks
    public boolean renderBlockCauldron(RenderBlocks renderer, CisternBlock cistern, int par2, int par3, int par4) {
        renderer.renderStandardBlock(cistern, par2, par3, par4);
        Tessellator var5 = Tessellator.instance;
        var5.setBrightness(cistern.getMixedBrightnessForBlock(renderer.blockAccess, par2, par3, par4));
        float var6 = 1.0F;
        int var7 = cistern.colorMultiplier(renderer.blockAccess, par2, par3, par4);
        float var8 = (float) (var7 >> 16 & 255) / 255.0F;
        float var9 = (float) (var7 >> 8 & 255) / 255.0F;
        float var10 = (float) (var7 & 255) / 255.0F;
        float var12;

        var5.setColorOpaque_F(var6 * var8, var6 * var9, var6 * var10);
        Icon var16 = cistern.getBlockTextureFromSide(2);
        ColorizeBlock.computeWaterColor();
        var5.setColorOpaque_F(Colorizer.setColor[0], Colorizer.setColor[1], Colorizer.setColor[2]);
        var12 = 0.124F;
        renderer.renderFaceXPos(cistern, (float) par2 - 1.0F + var12, par3, par4, var16);
        renderer.renderFaceXNeg(cistern, (float) par2 + 1.0F - var12, par3, par4, var16);
        renderer.renderFaceZPos(cistern, par2, par3, (float) par4 - 1.0F + var12, var16);
        renderer.renderFaceZNeg(cistern, par2, par3, (float) par4 + 1.0F - var12, var16);
        Icon var17 = BlockCauldron.func_94375_b("cauldron_inner");
        renderer.renderFaceYPos(cistern, par2, (float) par3 - 1.0F + 0.25F, par4, var17);
        renderer.renderFaceYNeg(cistern, par2, (float) par3 + 1.0F - 0.75F, par4, var17);
        int var14 = renderer.blockAccess.getBlockMetadata(par2, par3, par4);

        /* Sock: water handled elsewhere
        if (var14 > 0)
        {
            Icon var15 = BlockFluid.func_94424_b("water");

            if (var14 > 3)
            {
                var14 = 3;
            }

            renderer.renderFaceYPos(cistern, (double)par2, (double)((float)par3 - 1.0F + (6.0F + (float)var14 * 3.0F) / 16.0F), (double)par4, var15);
        }
         */

        return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockSecondPass(RenderBlocks renderer, int x, int y, int z, boolean bFirstPassResult) {
        mudColorPass = true;

        CisternBaseTileEntity cisternBase = (CisternBaseTileEntity) renderer.blockAccess.getBlockTileEntity(x, y, z);

        //render contents
        if (renderer.blockAccess.getBlockTileEntity(x, y, z) instanceof CisternBaseTileEntity) {
            int fillLevel = cisternBase.getFillLevel();
            Icon contentsIcon = getContentsIcon(cisternBase);

            if (fillLevel >= 0 && contentsIcon != null) {
                renderer.setRenderBounds(2 / 16D, 9 / 32D, 2 / 16D, 14 / 16D, fillLevel / 16D, 14 / 16D);
                RenderUtils.renderStandardBlockWithTexture(renderer, this, x, y, z, contentsIcon);
            }
        }

        mudColorPass = false;
    }

}
