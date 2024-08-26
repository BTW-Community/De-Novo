package btw.community.denovo.block.blocks;

import btw.client.render.util.RenderUtils;
import btw.community.denovo.block.tileentities.CisternBaseTileEntity;
import btw.community.denovo.block.tileentities.CisternTileEntity;
import btw.community.denovo.block.tileentities.ComposterTileEntity;
import btw.community.denovo.item.DNItems;
import btw.community.denovo.utils.CisternUtils;
import btw.item.BTWItems;
import btw.item.util.ItemUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.awt.*;
import java.util.Random;

public abstract class CisternBaseBlock extends BlockContainer {
    public CisternBaseBlock(int blockID, Material material) {
        super(blockID, material);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int facing, float clickX, float clickY, float clickZ) {

        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
        CisternBaseTileEntity cisternBase = (CisternBaseTileEntity) tileEntity;
        ItemStack heldStack = player.getHeldItem();

        int fill = cisternBase.getFillType();

        if (heldStack == null) return false;


        if (cisternBase.isEmpty())
        {
            if (CisternUtils.isValidWaterContainer(heldStack))
            {
                return CisternUtils.addWaterAndReturnContainer(world, x,y,z, facing, player, tileEntity, heldStack);
            }
        }
        else
        {
            if (cisternBase.getFillType() == CisternUtils.CONTENTS_WATER)
            {
                if (CisternUtils.isValidWaterContainer(heldStack))
                {
                    return CisternUtils.addWaterAndReturnContainer(world, x,y,z, facing, player, tileEntity, heldStack);
                }
                else if (CisternUtils.isValidEmptyContainer(cisternBase, heldStack))
                {
                    return CisternUtils.reduceWaterAndReturnContainer(world, x,y,z, facing, player, tileEntity, heldStack);
                }

                if (heldStack.isItemEqual(new ItemStack(Item.clay)) && cisternBase.getProgressCounter() == 0) {
                    return setType(world, x, y, z, player, cisternBase, heldStack, CisternUtils.CONTENTS_CLAY_WATER, "random.splash");
                }
                else if (heldStack.isItemEqual(new ItemStack(BTWItems.dirtPile)) && cisternBase.getProgressCounter() == 0) {
                    return setType(world, x, y, z, player, cisternBase, heldStack, CisternUtils.CONTENTS_MUDDY_WATER, "random.splash");
                }
            }
            else if (cisternBase.getFillType() == CisternUtils.CONTENTS_INFECTED_WATER) {

                if (CisternUtils.isValidDirt(heldStack) && cisternBase.getProgressCounter() == 0)
                {
                    if (cisternBase.getSolidFillLevel() < CisternUtils.MAX_SOLID_FILL_LEVEL)
                    {
                        if ( !world.isRemote ) cisternBase.addCompost(1);
                    }
                    else {
                        if ( !world.isRemote ) cisternBase.setProgressCounter(1);
                    }

                    return setType(world, x, y, z, player, cisternBase, heldStack, CisternUtils.CONTENTS_INFECTED_WATER, "random.splash");
                }
            }
            else if (cisternBase.getFillType() == CisternUtils.CONTENTS_RUST_WATER) {

                if (heldStack.isItemEqual(new ItemStack(Item.bowlEmpty)) && cisternBase.getProgressCounter() == 0)
                {
                    ItemUtils.givePlayerStackOrEjectFromTowardsFacing(player, new ItemStack(DNItems.rustWaterBowl, 1, DNItems.rustWaterBowl.getMaxDamage()), x, y, z, facing);

                    if (!world.isRemote  && cisternBase.getLiquidFillLevel() > 0 )
                    {
                        if (cisternBase instanceof ComposterTileEntity)
                        {
                            cisternBase.removeLiquid(3);
                        }
                        else cisternBase.removeLiquid(1);
                    }
                    int type = CisternUtils.CONTENTS_RUST_WATER;
                    if (cisternBase.getLiquidFillLevel() <= 0)
                    {
                        type = CisternUtils.CONTENTS_EMPTY;
                    }
                    return setType(world, x, y, z, player, cisternBase, heldStack, type, "random.splash");
                }
            }
        }

        return false;
    }

    private boolean setType(World world, int x, int y, int z, EntityPlayer player, CisternBaseTileEntity cisternBase, ItemStack heldStack, int type, String sound) {
        if (world.isRemote) playSound(world, x, y, z, sound, 1/8F, 1F);
        if (world.isRemote && !CisternUtils.isValidEmptyContainer(cisternBase, heldStack)) spawnSplashParticles(world,x,y,z,world.rand);

        if (!world.isRemote && type > 0 ) cisternBase.setFillType(type);
        world.markBlockForUpdate(x, y, z);
        world.markBlockForRenderUpdate(x, y, z);

        if (!player.capabilities.isCreativeMode && world.isRemote) heldStack.stackSize--;

        return true;
    }

    @Environment(EnvType.CLIENT)
    protected static void spawnSplashParticles(World world, int x, int y, int z, Random rand) {



        for (int i = 0; i < 4; i++) {
            double xPos = x + 0.25F + rand.nextFloat() * 0.5F;
            double yPos = y + 1.0F;
            double zPos = z + 0.25F + rand.nextFloat() * 0.5F;

            world.spawnParticle("splash", xPos, yPos, zPos, 0.0D, 0.0D, 0.0D);
        }

    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    //----------- Class Specific Methods -----------//

    protected boolean isValidWaterContainer(ItemStack stack) {
        return stack.isItemEqual(new ItemStack(Item.bowlEmpty)) || stack.isItemEqual(new ItemStack(Item.glassBottle));
    }

    protected ItemStack getFullWaterContainer(ItemStack stack) {
        if (stack.isItemEqual(new ItemStack(Item.bowlEmpty))) return new ItemStack(DNItems.waterBowl);
        if (stack.isItemEqual(new ItemStack(Item.glassBottle))) return new ItemStack(Item.potion, 1, 0);
        return null;
    }

    protected void playSound(World world, int x, int y, int z, String soundName, float volume, float pitch) {
        if (!world.isRemote) {
            world.playSoundEffect(
                    (double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D,
                    soundName,
                    volume,
                    pitch);
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int par5) {
        if (!this.canBlockStay(world, x, y, z)) {
            this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
            world.setBlockToAir(x, y, z);
        }
    }
    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return canBlockStay(world, x, y, z) && super.canPlaceBlockAt(world, x, y, z);
    }

    @Override
    public boolean canBlockStay(World world, int x, int y, int z) {
        return world.doesBlockHaveSolidTopSurface(x, y - 1, z) && super.canBlockStay(world, x, y, z);
    }

    //----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int iNeighborI, int iNeighborJ, int iNeighborK, int iSide) {
        return true;
    }
    @Environment(EnvType.CLIENT)
    protected boolean mudColorPass;

    @Environment(EnvType.CLIENT)
    protected Icon top;
    @Environment(EnvType.CLIENT)
    protected Icon side;
    @Environment(EnvType.CLIENT)
    protected Icon bottom;
    @Environment(EnvType.CLIENT)
    protected Icon water;
    @Environment(EnvType.CLIENT)
    protected Icon compost;
    @Environment(EnvType.CLIENT)
    protected Icon maggotsDone;
    @Environment(EnvType.CLIENT)
    private final Icon[] maggotsGrowing = new Icon[8];

    @Environment(EnvType.CLIENT)
    private final Icon[] compostBreaking = new Icon[8];
    @Environment(EnvType.CLIENT)
    private final Icon[] dirtBreaking = new Icon[8];
    @Environment(EnvType.CLIENT)
    private final Icon[] gravelBreaking = new Icon[8];

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        water = register.registerIcon("water");

        compost = register.registerIcon("DNBlock_composter_compost");

        for (int i = 0; i < maggotsGrowing.length; i++) {
            maggotsGrowing[i] = register.registerIcon("DNBlock_composter_maggots_" + i);
        }

        for (int i = 0; i < compostBreaking.length; i++) {
            compostBreaking[i] = register.registerIcon("DNBlock_compost_breaking_" + i);
        }

        for (int i = 0; i < maggotsGrowing.length; i++) {
            dirtBreaking[i] = register.registerIcon("DNBlock_dirt_breaking_" + i);
        }

        for (int i = 0; i < maggotsGrowing.length; i++) {
            gravelBreaking[i] = register.registerIcon("DNBlock_gravel_breaking_" + i);
        }


        maggotsDone = register.registerIcon("DNBlock_composter_maggots");
    }


    @Environment(EnvType.CLIENT)
    public Icon getSolidContentsIcon(CisternBaseTileEntity cisternBase) {
        int fillType = cisternBase.getFillType();
        int counter = cisternBase.getProgressCounter();

        if (fillType != CisternUtils.CONTENTS_EMPTY) {
            if (fillType == CisternUtils.CONTENTS_COMPOST) {
                if (counter > 0 && counter < CisternUtils.MAGGOT_CREATION_TIME) {
                    int iconIndex = CisternUtils.getIconIndex(cisternBase, 8, CisternUtils.MAGGOT_CREATION_TIME);
                    return maggotsGrowing[iconIndex];
                }
                else return compost;
            }
            else if (fillType == CisternUtils.CONTENTS_MAGGOTS) {
                return maggotsDone;
            }
            else if (fillType == CisternUtils.CONTENTS_INFECTED_WATER) {
                int iconIndex = CisternUtils.getIconIndex(cisternBase, 8, CisternUtils.INFECTED_WATER_CONVERSION_TIME);
                return dirtBreaking[iconIndex];
            }
            else return null;
        }
        else return null;
    }


    @Environment(EnvType.CLIENT)
    public Icon getLiquidContentsIcon(CisternBaseTileEntity cisternBase) {
        return water;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int colorMultiplier(IBlockAccess blockAccess, int x, int y, int z) {
        if (mudColorPass) {
            CisternBaseTileEntity cisternBase = (CisternBaseTileEntity) blockAccess.getBlockTileEntity(x, y, z);
            int fillType = cisternBase.getFillType();

            if (fillType ==CisternUtils.CONTENTS_MUDDY_WATER) {

                Color color = CisternUtils.getInterpolatedColor(
                        CisternUtils.COLOR_MUDDY_WATER,
                        CisternUtils.COLOR_WATER,
                        cisternBase.getProgressCounter(),
                        CisternUtils.MUDDY_WATER_SETTLE_TIME );

                return color.getRGB() & 0x00FFFFFF;
            }
            else if (fillType ==CisternUtils.CONTENTS_CLAY_WATER) {

                Color color = CisternUtils.getInterpolatedColor(
                        CisternUtils.COLOR_CLAY_WATER,
                        CisternUtils.COLOR_WATER,
                        CisternUtils.COLOR_INFECTED_WATER,
                        cisternBase.getProgressCounter(),
                        CisternUtils.CLAY_WATER_CONVERSION_TIME );

                return color.getRGB() & 0x00FFFFFF;
            }
            else if (fillType ==CisternUtils.CONTENTS_INFECTED_WATER) {
                Color color = CisternUtils.getInterpolatedColor(
                        CisternUtils.COLOR_INFECTED_WATER,
                        CisternUtils.COLOR_RUST_WATER,
                        cisternBase.getProgressCounter(),
                        CisternUtils.INFECTED_WATER_CONVERSION_TIME );

                return color.getRGB() & 0x00FFFFFF;
            }
            else if (fillType ==CisternUtils.CONTENTS_RUST_WATER) {

                return CisternUtils.COLOR_RUST_WATER.getRGB() & 0x00FFFFFF;
            }
        }

        return super.colorMultiplier(blockAccess, x, y, z);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockSecondPass(RenderBlocks renderer, int x, int y, int z, boolean bFirstPassResult) {

        CisternBaseTileEntity cisternBase = (CisternBaseTileEntity) renderer.blockAccess.getBlockTileEntity(x, y, z);
        renderContents(renderer, x, y, z, cisternBase);


    }

    private void renderContents(RenderBlocks renderer, int x, int y, int z, CisternBaseTileEntity cisternBase) {

        //render contents
        mudColorPass = true;
        //Liquids
        if (cisternBase.getLiquidFillLevel() >= 0 ) {
            renderer.setRenderBounds(2 / 16D, 4 / 16D, 2 / 16D, 14 / 16D, cisternBase.getLiquidFillLevel() / 16D, 14 / 16D);
            RenderUtils.renderStandardBlockWithTexture(renderer, this, x, y, z,  getLiquidContentsIcon(cisternBase));
        }
        mudColorPass = false;
        //Solids
        if (cisternBase.getSolidFillLevel() >= 0 ) {
            if (cisternBase.getFillType() == CisternUtils.CONTENTS_INFECTED_WATER)
            {
                renderer.setRenderBounds(3 / 16D, 4 / 16D, 3 / 16D, 13 / 16D, getMaxY(cisternBase), 13 / 16D);
                RenderUtils.renderStandardBlockWithTexture(renderer, this, x, y, z, getSolidContentsIcon(cisternBase));
            }
            else{
                renderer.setRenderBounds(2 / 16D, 4 / 16D, 2 / 16D, 14 / 16D, cisternBase.getSolidFillLevel() / 16D, 14 / 16D);
                RenderUtils.renderStandardBlockWithTexture(renderer, this, x, y, z,  getSolidContentsIcon(cisternBase));
            }

        }
    }

    private double getMaxY(CisternBaseTileEntity cisternBase)
    {
        int counter = cisternBase.getProgressCounter();

        double maxReduction = 1 / 16D;
        int maxCounterValue = CisternUtils.INFECTED_WATER_CONVERSION_TIME; // Replace this with the actual maximum value of the counter

        double reduction = (counter / (double) maxCounterValue) * maxReduction;

        reduction = Math.min(reduction, maxReduction);

        double zFightingFix = 1 / 1024D;

        return cisternBase.getSolidFillLevel() / 16D + zFightingFix - reduction;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        CisternBaseTileEntity cisternBase = (CisternBaseTileEntity) world.getBlockTileEntity(x, y, z);

        if (cisternBase.isFull()) {
            int fillType = cisternBase.getFillType();
            if (fillType == CisternUtils.CONTENTS_RUST_WATER
                    || fillType == CisternUtils.CONTENTS_INFECTED_WATER
                    || fillType == CisternUtils.CONTENTS_MUDDY_WATER
                    || fillType == CisternUtils.CONTENTS_COMPOST
                    || fillType == CisternUtils.CONTENTS_MAGGOTS) {
                if (rand.nextInt(3) == 0) {
                    world.spawnParticle("townaura", (float) x + rand.nextFloat(), (float) y + 1.1F, (float) z + rand.nextFloat(), 0.0D, 0.0D, 0.0D);
                }
            }
        }

        if (!cisternBase.isEmpty()) {
            if (cisternBase.getProgressCounter() > 0 && cisternBase.getFillType() != CisternUtils.CONTENTS_MAGGOTS) {
                if (rand.nextInt(3) == 0) {
                    spawnParticles(world, x, y, z, rand);
                }
            }
        }

    }

    @Environment(EnvType.CLIENT)
    protected static void spawnParticles(World world, int x, int y, int z, Random rand) {
        double xPos = x + 0.25F + rand.nextFloat() * 0.5F;
        double yPos = y + 1.0F + rand.nextFloat() * 0.25F;
        double zPos = z + 0.25F + rand.nextFloat() * 0.5F;

        world.spawnParticle("fcwhitesmoke", xPos, yPos, zPos, 0.0D, 0.0D, 0.0D);
    }
}
