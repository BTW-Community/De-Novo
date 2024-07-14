package btw.community.denovo.block.blocks;

import btw.client.render.util.RenderUtils;
import btw.community.denovo.block.models.ComposterModel;
import btw.community.denovo.block.tileentities.ComposterTileEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

public class ComposterBlock extends BlockContainer {
    private final ComposterModel model = new ComposterModel();
    public static ArrayList<ItemStack> validCompostables = new ArrayList<>();

    public ComposterBlock(int blockID) {
        super(blockID, Material.wood);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int iFacing, float fClickX, float fClickY, float fClickZ) {
        ItemStack heldStack = player.getCurrentEquippedItem();

        if (heldStack != null && isValidCompostable(heldStack)) {
            System.out.println("TRUE");
            return true;
        }
        return false;
    }

    private boolean isValidCompostable(ItemStack heldStack) {
        Iterator<ItemStack> validStacks = validCompostables.iterator();

        for (Iterator<ItemStack> it = validStacks; it.hasNext(); ) {
            ItemStack stack = it.next();

            if (heldStack.isItemEqual(stack)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new ComposterTileEntity();
    }

    // --- Client --- //

    private Icon top;
    private Icon side;
    private Icon bottom;
    private Icon compost;
    private Icon maggots;

    private Icon water;

    @Override
    public Icon getIcon(int face, int meta) {

        if (meta == -1) {
            return compost;
        }

        if (face == 0) return bottom;
        else if (face == 1) return top;
        else return side;
    }

    @Override
    public void registerIcons(IconRegister register) {
        top = register.registerIcon("DNBlock_composter_top");
        side = register.registerIcon("DNBlock_composter");
        bottom = register.registerIcon("DNBlock_composter_bottom");

        compost = register.registerIcon("DNBlock_composter_compost");
        maggots = register.registerIcon("DNBlock_composter_maggots");
        water = register.registerIcon("water");
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    private boolean secondPass;

    @Override
    public int colorMultiplier(IBlockAccess blockAccess, int x, int y, int z) {
        if (secondPass) {
            ComposterTileEntity composter = (ComposterTileEntity) blockAccess.getBlockTileEntity(x, y, z);
            int counter = composter.getProgressCounter();

            Color color = new Color(255, 130, 0);
            float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
            float saturation = hsb[1];

            saturation += counter;

            Color newColor = Color.getHSBColor(hsb[0], 1 - saturation / 256, hsb[2]);
            return newColor.getRGB() & 0x00FFFFFF;
        }

        return super.colorMultiplier(blockAccess, x, y, z);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public boolean renderBlock(RenderBlocks renderer, int x, int y, int z) {

        //floor render, didn't work as part of the ComposterModel???
        renderer.setRenderBounds(2 / 16D, 0 / 16D, 2 / 16D, 14 / 16D, 2 / 16D, 14 / 16D);
        RenderUtils.renderStandardBlockWithTexture(renderer, this, x, y, z, bottom);

        //render composter
        renderer.setRenderBounds(0D, 0D, 0D, 1D, 1D, 1D);
        return model.renderAsBlock(renderer, this, x, y, z);
    }

    @Override
    public void renderBlockSecondPass(RenderBlocks renderer, int x, int y, int z, boolean bFirstPassResult) {
        secondPass = true;

        ComposterTileEntity composter = (ComposterTileEntity) renderer.blockAccess.getBlockTileEntity(x, y, z);

        //render contents
        if (composter != null) {
            int fillLevel = composter.getFillLevel();

            if (fillLevel >= 0) {
                fillLevel = 15;
                renderer.setRenderBounds(2 / 16D, 2 / 16D, 2 / 16D, 14 / 16D, fillLevel / 16D, 14 / 16D);
                RenderUtils.renderStandardBlockWithTexture(renderer, this, x, y, z, water);
            }
        }

        secondPass = false;
    }

    @Override
    public void renderBlockAsItem(RenderBlocks renderer, int damage, float brightness) {
        model.renderAsItemBlock(renderer, this, damage);
    }
}
