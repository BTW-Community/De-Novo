package btw.community.denovo.block.blocks;

import btw.client.render.util.RenderUtils;
import btw.community.denovo.block.models.ComposterModel;
import btw.community.denovo.block.tileentities.CisternBaseTileEntity;
import btw.community.denovo.block.tileentities.ComposterTileEntity;
import btw.item.BTWItems;
import btw.item.util.ItemUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.ArrayList;
import java.util.Iterator;

public class ComposterBlock extends CisternBaseBlock {
    private final ComposterModel model = new ComposterModel();
    public static ArrayList<ItemStack> validCompostables = new ArrayList<>();

    public ComposterBlock(int blockID) {
        super(blockID, Material.wood);
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new ComposterTileEntity();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int facing, float clickX, float clickY, float clickZ) {
        ComposterTileEntity composter = (ComposterTileEntity) world.getBlockTileEntity(x, y, z);
        ItemStack heldStack = player.getHeldItem();

        if (composter != null && composter.isFullWithWater()) {
            if (heldStack != null) {
                if (isValidWaterContainer(heldStack)) {
                    ItemUtils.givePlayerStackOrEjectFromTowardsFacing(player, getFullWaterContainer(heldStack), x, y, z, facing);
                    composter.setFillLevel(0);
                    composter.setFillType(CisternBaseTileEntity.CONTENTS_EMPTY);
                    world.markBlockForUpdate(x, y, z);

                    heldStack.stackSize--;
                    return true;
                } else if (heldStack.isItemEqual(new ItemStack(BTWItems.dirtPile)) && composter.getProgressCounter() == 0) {
                    composter.setFillType(CisternBaseTileEntity.CONTENTS_MUDDY_WATER);
                    heldStack.stackSize--;
                    return true;
                }
            }
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
    // --- Client --- //

    private Icon compost;
    private Icon maggots;


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
    public Icon getContentsIcon(ComposterTileEntity composter) {
        int fillType = composter.getFillType();

        if (fillType != ComposterTileEntity.CONTENTS_EMPTY) {
            if (fillType == ComposterTileEntity.CONTENTS_COMPOST) return compost;
            if (fillType == ComposterTileEntity.CONTENTS_WATER) return water;
            if (fillType == ComposterTileEntity.CONTENTS_MUDDY_WATER) return water;
        }

        return null;
    }

    @Override
    public void registerIcons(IconRegister register) {
        super.registerIcons(register);
        top = register.registerIcon("DNBlock_composter_top");
        side = register.registerIcon("DNBlock_composter");
        bottom = register.registerIcon("DNBlock_composter_bottom");

        compost = register.registerIcon("DNBlock_composter_compost");
        maggots = register.registerIcon("DNBlock_composter_maggots");

        blockIcon = side;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public boolean renderBlock(RenderBlocks renderer, int x, int y, int z) {

        //floor render, didn't work as part of the ComposterModel???
        renderer.setRenderBounds(2 / 16D, 0 / 16D, 2 / 16D, 14 / 16D, 1 / 32D, 14 / 16D);
        RenderUtils.renderStandardBlockWithTexture(renderer, this, x, y, z, bottom);

        //render composter
        renderer.setRenderBounds(0D, 0D, 0D, 1D, 1D, 1D);
        return model.renderAsBlock(renderer, this, x, y, z);
    }

    @Override
    public void renderBlockAsItem(RenderBlocks renderer, int damage, float brightness) {
        model.renderAsItemBlock(renderer, this, damage);
    }
}
