package btw.community.denovo.block.blocks;

import btw.client.render.util.RenderUtils;
import btw.community.denovo.block.models.CisternModel;
import btw.community.denovo.block.tileentities.CisternTileEntity;
import com.prupe.mcpatcher.cc.ColorizeBlock;
import com.prupe.mcpatcher.cc.Colorizer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;
import java.util.Random;

public class CisternBlock extends CisternBaseBlock {
    private final CisternModel model = new CisternModel();
    public CisternBlock(int blockID) {
        super(blockID, Material.iron);

        setHardness(0.5F);

        setAxesEffectiveOn(true);

        setBuoyancy(1F);

        initBlockBounds(0D, 0D, 0D, 1D, 1D, 1D);

        setStepSound(soundMetalFootstep);

        setUnlocalizedName("DNCistern");

        setCreativeTab(CreativeTabs.tabBrewing);

        setTickRandomly(true);
    }

    @Override
    public TileEntity createNewTileEntity(World var1) {
        return new CisternTileEntity();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int facing, float clickX, float clickY, float clickZ) {
        return super.onBlockActivated(world, x, y, z, player, facing, clickX, clickY, clickZ);
    }

    @Override
    public int idDropped(int par1, Random par2Random, int par3) {
        return this.blockID;
        //return DNItems.cistern.itemID;
    }

    @Override
    public int idPicked(World par1World, int par2, int par3, int par4) {
        return this.blockID;
        //return DNItems.cistern.itemID;
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

    //----------- Client Side Functionality -----------//
    //Sock: copied and modified from vanilla cistern

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int par1, int par2) {
        return par1 == 1 ? this.top : (par1 == 0 ? this.bottom : this.blockIcon);
    }
    @Environment(EnvType.CLIENT)
    private Icon inner;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister par1IconRegister) {
        super.registerIcons(par1IconRegister);
        this.inner = par1IconRegister.registerIcon("cauldron_inner");
        this.top = par1IconRegister.registerIcon("cauldron_top");
        this.bottom = par1IconRegister.registerIcon("cauldron_bottom");
        this.blockIcon = this.side = par1IconRegister.registerIcon("cauldron_side");
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
    public void renderBlockAsItem(RenderBlocks renderer, int damage, float brightness) {
        //floor
        renderer.setRenderBounds(2 / 16D, 3 / 16D, 2 / 16D, 14 / 16D, 4 / 16D, 14 / 16D);
        RenderUtils.renderInvBlockWithTexture(renderer, this, -0.5F, -0.5F, -0.5F, this.inner);

        renderer.setRenderBounds(0D, 0D, 0D, 1D, 1D, 1D);
        model.renderAsItemBlock(renderer, this, damage);
    }

}
