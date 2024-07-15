package btw.community.denovo.block.blocks;

import btw.block.BTWBlocks;
import btw.block.util.Flammability;
import btw.client.render.util.RenderUtils;
import btw.community.denovo.block.models.ComposterModel;
import btw.community.denovo.block.tileentities.CisternBaseTileEntity;
import btw.community.denovo.block.tileentities.ComposterTileEntity;
import btw.community.denovo.item.DNItems;
import btw.item.BTWItems;
import btw.item.util.ItemUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class ComposterBlock extends CisternBaseBlock {
    private final ComposterModel model = new ComposterModel();
    public static ArrayList<ItemStack> validCompostables = new ArrayList<>();

    public ComposterBlock(int blockID) {
        super(blockID, Material.wood);

        setHardness(0.5F);

        setAxesEffectiveOn(true);

        setBuoyancy(1F);

        setFireProperties(Flammability.PLANKS);

        initBlockBounds(0D, 0D, 0D, 1D, 1D, 1D);

        setStepSound(soundWoodFootstep);

        setUnlocalizedName("DNComposter");

        setCreativeTab(CreativeTabs.tabRedstone);
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new ComposterTileEntity();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int facing, float clickX, float clickY, float clickZ) {

        ComposterTileEntity composter = (ComposterTileEntity) world.getBlockTileEntity(x, y, z);
        ItemStack heldStack = player.getHeldItem();

        if (composter != null ) {
            if (heldStack != null) {
                if (isValidCompostable(heldStack) ) {
                    if (composter.isEmpty() || (!composter.isFull() && composter.getFillType() == ComposterTileEntity.CONTENTS_COMPOST)) {
                        composter.addCompost(1);
                        composter.setFillType(ComposterTileEntity.CONTENTS_COMPOST);
                        world.markBlockForUpdate(x, y, z);

                        heldStack.stackSize--;
                        return true;
                    }
                }
            } else {
                if (composter.isFullWithCompostOrMaggots())
                {
                    if (composter.getFillType() == ComposterTileEntity.CONTENTS_COMPOST)
                    {
                        if (!world.isRemote) {
                            ItemUtils.ejectStackFromBlockTowardsFacing(world, x,y,z, new ItemStack(BTWItems.dirtPile), facing);
                        }
                    }
                    else if (composter.getFillType() == ComposterTileEntity.CONTENTS_MAGGOTS) {
                        if (!world.isRemote) {
                            ItemUtils.ejectStackFromBlockTowardsFacing(world, x,y,z, new ItemStack(DNItems.rawMaggots), facing);
                        }
                    }

                    composter.setFillLevel(0);
                    composter.setFillType(ComposterTileEntity.CONTENTS_EMPTY);
                    world.markBlockForUpdate(x, y, z);

                    return true;
                }
            }
        }
        return super.onBlockActivated(world, x, y, z, player, facing, clickX, clickY, clickZ);
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

    //Currently not used, since it can be broken by hand
    @Override
    public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k, int iMetadata, float fChanceOfDrop) {
        super.dropComponentItemsOnBadBreak(world, i, j, k, iMetadata, fChanceOfDrop);

        dropItemsIndividually(world, i, j, k, Item.stick.itemID, 2, 0, fChanceOfDrop);
        dropItemsIndividually(world, i, j, k, BTWItems.sawDust.itemID, 4, 0, fChanceOfDrop);

        return true;
    }

    // --- Client --- //

    private Icon compost;
    private Icon[] maggots = new Icon[8];

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
    public Icon getContentsIcon(CisternBaseTileEntity cisternBase) {
        ComposterTileEntity composter = (ComposterTileEntity) cisternBase;

        int fillType = composter.getFillType();
        int counter = composter.getProgressCounter();

        if (fillType != ComposterTileEntity.CONTENTS_EMPTY) {
            if (fillType == ComposterTileEntity.CONTENTS_COMPOST) {
                if (counter > 0 && counter < ComposterTileEntity.MAGGOT_CREATION_TIME)
                {
                    // Change icon based on counter
                    int iconIndex = (composter.getProgressCounter() / (ComposterTileEntity.MAGGOT_CREATION_TIME / maggots.length)) % maggots.length;
                    return maggots[iconIndex];
                }
                else return compost;

            }
            if (fillType == ComposterTileEntity.CONTENTS_MAGGOTS) return maggots[7];
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

        maggots[0] = register.registerIcon("DNBlock_composter_maggots_0");
        maggots[1] = register.registerIcon("DNBlock_composter_maggots_1");
        maggots[2] = register.registerIcon("DNBlock_composter_maggots_2");
        maggots[3] = register.registerIcon("DNBlock_composter_maggots_3");
        maggots[4] = register.registerIcon("DNBlock_composter_maggots_4");
        maggots[5] = register.registerIcon("DNBlock_composter_maggots_5");
        maggots[6] = register.registerIcon("DNBlock_composter_maggots_6");
        maggots[7] = register.registerIcon("DNBlock_composter_maggots_7");

        blockIcon = side;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public boolean renderBlock(RenderBlocks renderer, int x, int y, int z) {
        super.renderBlock(renderer,x,y,z);

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

    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        super.randomDisplayTick(world,x,y,z,rand);

        ComposterTileEntity composter = (ComposterTileEntity) world.getBlockTileEntity(x, y, z);

        if (composter.isFull()) {
            if (composter.getFillType() == ComposterTileEntity.CONTENTS_COMPOST) {
                if (composter.getProgressCounter() < ComposterTileEntity.MAGGOT_CREATION_TIME && rand.nextInt(5) == 0) {
                    spawnParticles(world, x, y, z, rand);
                }
            }
        }
    }
}
