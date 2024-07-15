package btw.community.denovo.block.blocks;

import btw.client.render.util.RenderUtils;
import btw.community.denovo.block.tileentities.CisternBaseTileEntity;
import btw.community.denovo.block.tileentities.ComposterTileEntity;
import btw.community.denovo.item.DNItems;
import btw.item.BTWItems;
import btw.item.util.ItemUtils;
import net.minecraft.src.*;

import java.awt.*;
import java.util.Random;

public abstract class CisternBaseBlock extends BlockContainer {
    public CisternBaseBlock(int blockID, Material material) {
        super(blockID, material);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int facing, float clickX, float clickY, float clickZ) {
        CisternBaseTileEntity cisternBase = (CisternBaseTileEntity) world.getBlockTileEntity(x, y, z);
        ItemStack heldStack = player.getHeldItem();

        if (cisternBase != null && cisternBase.isFullWithWater()) {
            if (heldStack != null) {
                if (isValidWaterContainer(heldStack)) {
                    ItemUtils.givePlayerStackOrEjectFromTowardsFacing(player, getFullWaterContainer(heldStack), x, y, z, facing);
                    cisternBase.setFillLevel(0);
                    cisternBase.setFillType(CisternBaseTileEntity.CONTENTS_EMPTY);
                    world.markBlockForUpdate(x, y, z);

                    heldStack.stackSize--;
                    return true;
                } else if (cisternBase.isFullWithWater() && heldStack.isItemEqual(new ItemStack(BTWItems.dirtPile)) && cisternBase.getProgressCounter() == 0) {
                    cisternBase.setFillType(CisternBaseTileEntity.CONTENTS_MUDDY_WATER);
                    world.markBlockForUpdate(x, y, z);

                    heldStack.stackSize--;
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean isValidWaterContainer(ItemStack stack) {
        return stack.isItemEqual(new ItemStack(Item.bowlEmpty)) || stack.isItemEqual(new ItemStack(Item.glassBottle));
    }

    protected ItemStack getFullWaterContainer(ItemStack stack) {
        if (stack.isItemEqual(new ItemStack(Item.bowlEmpty))) return new ItemStack(DNItems.waterBowl);
        if (stack.isItemEqual(new ItemStack(Item.glassBottle))) return new ItemStack(Item.potion, 1, 0);
        return null;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int iNeighborI, int iNeighborJ, int iNeighborK, int iSide) {
        return true;
    }

    // CLIENT //

    protected Icon top;
    protected Icon side;
    protected Icon bottom;
    protected Icon water;

    @Override
    public void registerIcons(IconRegister register) {
        water = register.registerIcon("water");
    }

    public Icon getContentsIcon(CisternBaseTileEntity cisternBase) {
        int fillType = cisternBase.getFillType();

        if (fillType != ComposterTileEntity.CONTENTS_EMPTY) {
            if (fillType == ComposterTileEntity.CONTENTS_WATER) return water;
            if (fillType == ComposterTileEntity.CONTENTS_MUDDY_WATER) return water;
        }
        return null;
    }

    protected boolean mudColorPass;

    @Override
    public int colorMultiplier(IBlockAccess blockAccess, int x, int y, int z) {
        if (mudColorPass) {
            CisternBaseTileEntity cisternBase = (CisternBaseTileEntity) blockAccess.getBlockTileEntity(x, y, z);
            int fillType = cisternBase.getFillType();

            if (fillType == CisternBaseTileEntity.CONTENTS_MUDDY_WATER) {
                int counter = cisternBase.getProgressCounter();

                Color color = new Color(255, 130, 0);
                float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
                float saturation = hsb[1];

                saturation += counter;

                Color newColor = Color.getHSBColor(hsb[0], 1 - saturation / CisternBaseTileEntity.MUDDY_WATER_SETTLE_TIME, hsb[2]);
                return newColor.getRGB() & 0x00FFFFFF;
            }
        }

        return super.colorMultiplier(blockAccess, x, y, z);
    }

    @Override
    public boolean renderBlock(RenderBlocks renderer, int x, int y, int z) {
        mudColorPass = true;

        CisternBaseTileEntity cisternBase = (CisternBaseTileEntity) renderer.blockAccess.getBlockTileEntity(x, y, z);

        //render contents
        if (cisternBase != null) {
            int fillLevel = cisternBase.getFillLevel();
            Icon contentsIcon = getContentsIcon(cisternBase);

            if (fillLevel >= 0 && contentsIcon != null) {
                renderer.setRenderBounds(2 / 16D, 2 / 16D, 2 / 16D, 14 / 16D, fillLevel / 16D, 14 / 16D);
                RenderUtils.renderStandardBlockWithTexture(renderer, this, x, y, z, contentsIcon);
            }
        }

        mudColorPass = false;

        return true;
    }



    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        CisternBaseTileEntity cisternBase = (CisternBaseTileEntity) world.getBlockTileEntity(x, y, z);

        if (cisternBase.isFull()) {
            int fillType = cisternBase.getFillType();
            if (fillType == CisternBaseTileEntity.CONTENTS_MUDDY_WATER || fillType == CisternBaseTileEntity.CONTENTS_COMPOST || fillType == CisternBaseTileEntity.CONTENTS_MAGGOTS) {
                if (rand.nextInt(5) == 0) {
                    world.spawnParticle("townaura", (float) x + rand.nextFloat(), (float) y + 1.1F, (float) z + rand.nextFloat(), 0.0D, 0.0D, 0.0D);
                }
            }

            if (cisternBase.getFillType() == CisternBaseTileEntity.CONTENTS_MUDDY_WATER) {
                if (cisternBase.getProgressCounter() < CisternBaseTileEntity.MUDDY_WATER_SETTLE_TIME && rand.nextInt(5) == 0) {
                    spawnParticles(world, x, y, z, rand);
                }
            }
        }
    }

    protected static void spawnParticles(World world, int x, int y, int z, Random rand) {
        double xPos = x + 0.25F + rand.nextFloat() * 0.5F;
        double yPos = y + 1.0F + rand.nextFloat() * 0.25F;
        double zPos = z + 0.25F + rand.nextFloat() * 0.5F;

        world.spawnParticle("fcwhitesmoke", xPos, yPos, zPos, 0.0D, 0.0D, 0.0D);
    }
}
