package btw.community.denovo.block.blocks;

import btw.block.util.Flammability;
import btw.client.fx.BTWEffectManager;
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

        setTickRandomly(true);
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new ComposterTileEntity();
    }

    @Override
    public void randomUpdateTick(World world, int x, int y, int z, Random rand) {
        ComposterTileEntity composter = (ComposterTileEntity) world.getBlockTileEntity(x, y, z);
        if (composter.isFullWithCompostOrMaggots()) {
            checkForSpread(world, x, y, z, rand);
        }

    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int facing, float clickX, float clickY, float clickZ) {

        ComposterTileEntity composter = (ComposterTileEntity) world.getBlockTileEntity(x, y, z);
        ItemStack heldStack = player.getHeldItem();

        if (composter != null) {
            if (heldStack != null) {
                if (isValidCompostable(heldStack)) {
                    if (composter.isEmpty() || (!composter.isFull() && composter.getFillType() == ComposterTileEntity.CONTENTS_COMPOST)) {
                        composter.addCompost(1);
                        composter.setFillType(ComposterTileEntity.CONTENTS_COMPOST);
                        world.markBlockForUpdate(x, y, z);

                        heldStack.stackSize--;

                        if (!world.isRemote) {
                            playSound(world, x, y, z, Block.leaves.stepSound.getStepSound(), 0.25F, 1F);
                        }
                        return true;
                    }
                }
            } else {
                if (composter.isFullWithCompostOrMaggots()) {
                    if (composter.getFillType() == ComposterTileEntity.CONTENTS_COMPOST) {
                        if (!world.isRemote) {
                            ItemUtils.ejectStackFromBlockTowardsFacing(world, x, y, z, new ItemStack(BTWItems.dirtPile), facing);

                            playSound(world, x, y, z, Block.dirt.stepSound.getStepSound(), 1/4F, 1F);
                        }
                    } else if (composter.getFillType() == ComposterTileEntity.CONTENTS_MAGGOTS) {
                        if (!world.isRemote) {
                            ItemUtils.ejectStackFromBlockTowardsFacing(world, x, y, z, new ItemStack(DNItems.rawMaggots), facing);
                            ItemUtils.ejectStackFromBlockTowardsFacing(world, x, y, z, new ItemStack(BTWItems.dirtPile), facing);

                            playSound(world, x, y, z, Block.dirt.stepSound.getStepSound(), 1/4F, 1F);
                            playSound(world, x, y, z, Block.blockClay.stepSound.getStepSound(), 1/8F, 1F);
                        }
                    }

                    composter.setFillLevel(0);
                    composter.setFillType(ComposterTileEntity.CONTENTS_EMPTY);
                    world.markBlockForUpdate(x, y, z);

                    return true;
                }
            }
        }
        //handle water
        return super.onBlockActivated(world, x, y, z, player, facing, clickX, clickY, clickZ);
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
    @Environment(EnvType.CLIENT)
    private Icon compost;
    @Environment(EnvType.CLIENT)
    private Icon maggotsDone;
    @Environment(EnvType.CLIENT)
    private final Icon[] maggotsGrowing = new Icon[8];

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int face, int meta) {

        if (meta == -1) {
            return compost;
        }

        if (face == 0) return bottom;
        else if (face == 1) return top;
        else return side;
    }


    @Override
    @Environment(EnvType.CLIENT)
    public Icon getContentsIcon(CisternBaseTileEntity cisternBase) {
        ComposterTileEntity composter = (ComposterTileEntity) cisternBase;

        int fillType = composter.getFillType();
        int counter = composter.getProgressCounter();

        if (fillType != ComposterTileEntity.CONTENTS_EMPTY) {
            if (fillType == ComposterTileEntity.CONTENTS_COMPOST) {
                if (counter > 0 && counter <= ComposterTileEntity.MAGGOT_CREATION_TIME) {
                    // Change icon based on counter
                    int totalStages = maggotsGrowing.length;
                    float progressRatio = (float) composter.getProgressCounter() / ComposterTileEntity.MAGGOT_CREATION_TIME;

                    // Use Math.min to ensure the maximum progress maps to the final index
                    int iconIndex = (int) (progressRatio * totalStages);
                    iconIndex = Math.min(iconIndex, totalStages - 1);

                    return maggotsGrowing[iconIndex];
                } else return compost;

            }
            if (fillType == ComposterTileEntity.CONTENTS_MAGGOTS) return maggotsDone;
            if (fillType == ComposterTileEntity.CONTENTS_WATER) return water;
            if (fillType == ComposterTileEntity.CONTENTS_MUDDY_WATER) return water;
        }

        return null;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        super.registerIcons(register);
        top = register.registerIcon("DNBlock_composter_top");
        side = register.registerIcon("DNBlock_composter");
        bottom = register.registerIcon("DNBlock_composter_bottom");

        compost = register.registerIcon("DNBlock_composter_compost");
        maggotsGrowing[0] = register.registerIcon("DNBlock_composter_maggots_0");
        maggotsGrowing[1] = register.registerIcon("DNBlock_composter_maggots_1");
        maggotsGrowing[2] = register.registerIcon("DNBlock_composter_maggots_2");
        maggotsGrowing[3] = register.registerIcon("DNBlock_composter_maggots_3");
        maggotsGrowing[4] = register.registerIcon("DNBlock_composter_maggots_4");
        maggotsGrowing[5] = register.registerIcon("DNBlock_composter_maggots_5");
        maggotsGrowing[6] = register.registerIcon("DNBlock_composter_maggots_6");
        maggotsGrowing[7] = register.registerIcon("DNBlock_composter_maggots_7");

        maggotsDone = register.registerIcon("DNBlock_composter_maggots");

        this.blockIcon = side;
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

    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        super.randomDisplayTick(world, x, y, z, rand);

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
