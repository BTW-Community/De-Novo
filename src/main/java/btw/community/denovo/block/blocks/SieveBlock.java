package btw.community.denovo.block.blocks;

import btw.block.BTWBlocks;
import btw.block.util.Flammability;
import btw.client.fx.BTWEffectManager;
import btw.client.render.util.RenderUtils;
import btw.community.denovo.block.models.SieveModel;
import btw.community.denovo.block.tileentities.SieveTileEntity;
import btw.community.denovo.recipes.LootEntry;
import btw.community.denovo.recipes.SiftingCraftingManager;
import btw.community.denovo.recipes.SiftingRecipe;
import btw.community.denovo.utils.SieveUtils;
import btw.crafting.manager.HopperFilteringCraftingManager;
import btw.crafting.recipe.types.HopperFilterRecipe;
import btw.item.BTWItems;
import btw.item.util.ItemUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.ArrayList;

public class SieveBlock extends BlockContainer {
    private static final int MAX_PROGRESS = 8;

    private static final SieveModel model = new SieveModel();

    public SieveBlock(int id) {
        super(id, Material.wood);
        setFireProperties(Flammability.PLANKS);
        setAxesEffectiveOn(true);
        setStepSound(soundWoodFootstep);

        setHardness(0.5F);
        setBuoyancy(1F);

        setUnlocalizedName("DNSieve");
        setCreativeTab(CreativeTabs.tabRedstone);

        initBlockBounds(0D, 0D, 0D, 1D, 1D, 1D);
        setTickRandomly(true);
    }

    @Override
    public boolean onBlockActivated(World world, int xCoord, int yCoord, int zCoord, EntityPlayer player, int facing, float xClick, float yClick, float zClick) {
        SieveTileEntity tileEntity = (SieveTileEntity) world.getBlockTileEntity(xCoord, yCoord, zCoord);

        if (tileEntity.getFilter() == null) {
            ItemStack heldItem = player.getHeldItem();

            // Set a new filter
            if (heldItem != null && SieveUtils.isValidHopperFilter(heldItem)) {
                if (!world.isRemote) {
                    ItemStack filter = heldItem.copy();
                    filter.stackSize = 1;
                    tileEntity.setFilter(filter);
                    heldItem.stackSize--;

                    world.markBlockForUpdate(xCoord, yCoord, zCoord);
                }

                return true;
            }

            return false;
        }

        // Keep sieving
        if (tileEntity.getContents() != null) {
            if (!world.isRemote) {
                if (tileEntity.getProgressCounter() > 0) {
                    tileEntity.decrementProgress();

                    world.playSoundAtEntity(player, "step.grass", 1F, 1F);
                }

                if (tileEntity.getProgressCounter() <= 0) {
                    HopperFilterRecipe hopperFilterRecipe = HopperFilteringCraftingManager.instance.getRecipe(tileEntity.getContents(), tileEntity.getFilter());
                    SiftingRecipe siftingRecipe = SiftingCraftingManager.getRecipe(tileEntity.getContents(), tileEntity.getFilter());

                    if ((hopperFilterRecipe != null && hopperFilterRecipe.getContainsSouls())
                            || (siftingRecipe != null && siftingRecipe.getContainsSouls())) {
                        tileEntity.releaseSouls();
                    }

                    ArrayList<ItemStack> output = new ArrayList<ItemStack>();

                    if (hopperFilterRecipe != null) {
                        if (hopperFilterRecipe.getHopperOutput() != null) {
                            output.add(hopperFilterRecipe.getHopperOutput().copy());
                        }
                        if (hopperFilterRecipe.getFilteredOutput() != null) {
                            output.add(hopperFilterRecipe.getFilteredOutput().copy());
                        }
                    }

                    if (siftingRecipe != null) {
                        for (LootEntry entry : siftingRecipe.getLootTable()) {
                            for (int i = 0; i < entry.getAmount(); i++) {
                                double roll = world.rand.nextDouble();
                                if (roll < entry.getChance()) {
                                    output.add(entry.getResult().copy());
                                }
                            }
                        }
                    }

                    if (!output.isEmpty()) {
                        world.playAuxSFX(BTWEffectManager.ITEM_COLLECTION_POP_EFFECT_ID, xCoord, yCoord, zCoord, 0);

                        for (ItemStack stack : output) {
                            ItemUtils.ejectStackAroundBlock(world, xCoord, yCoord, zCoord, stack);
                        }
                    }

                    tileEntity.setContents(null);
                }

                world.markBlockForUpdate(xCoord, yCoord, zCoord);
            }

            return true;
        }

        // Pull filter out
        if (player.getHeldItem() == null && player.isSneaking()) {
            if (!world.isRemote) {
                player.addStackToCurrentHeldStackIfEmpty(tileEntity.getFilter());
                tileEntity.setFilter(null);

                world.playAuxSFX(BTWEffectManager.ITEM_COLLECTION_POP_EFFECT_ID, xCoord, yCoord, zCoord, 0);

                world.markBlockForUpdate(xCoord, yCoord, zCoord);
            }

            return true;
        }

        // Fill sieve
        if (player.getHeldItem() != null) {
            HopperFilterRecipe hopperFilterRecipe = HopperFilteringCraftingManager.instance.getRecipe(player.getHeldItem(), tileEntity.getFilter());
            SiftingRecipe siftingRecipe = SiftingCraftingManager.getRecipe(player.getHeldItem(), tileEntity.getFilter());

            if (hopperFilterRecipe != null || siftingRecipe != null) {
                if (!world.isRemote) {
                    ItemStack input = player.getHeldItem().copy();
                    input.stackSize = 1;
                    tileEntity.setContents(input);
                    player.getHeldItem().stackSize--;

                    world.markBlockForUpdate(xCoord, yCoord, zCoord);
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World var1) {
        return new SieveTileEntity();
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
    public void breakBlock(World world, int xCoord, int yCoord, int zCoord, int blockID, int meta) {
        SieveTileEntity tileEntity = (SieveTileEntity) world.getBlockTileEntity(xCoord, yCoord, zCoord);

        if (tileEntity.getContents() != null) {
            ItemUtils.ejectStackAroundBlock(world, xCoord, yCoord, zCoord, tileEntity.getContents());
        }
        if (tileEntity.getFilter() != null) {
            ItemUtils.ejectStackAroundBlock(world, xCoord, yCoord, zCoord, tileEntity.getFilter());
        }

        tileEntity.setContents(null);
        tileEntity.setFilter(null);

        super.breakBlock(world, xCoord, yCoord, zCoord, blockID, meta);
    }

    //----------- Class Specific Methods -----------//

    public void breakSieve(World world, int i, int j, int k) {
        dropItemsIndividually(world, i, j, k, Item.stick.itemID, 2, 0, 1F);
        dropItemsIndividually(world, i, j, k, BTWItems.sawDust.itemID, 2, 0, 1F);

        world.playAuxSFX(BTWEffectManager.MECHANICAL_DEVICE_EXPLODE_EFFECT_ID, i, j, k, 0);

        world.setBlockWithNotify(i, j, k, 0);
    }

    //----------- Client Side Functionality -----------//

    @Environment(EnvType.CLIENT)
    private Icon filterIcon;

    @Environment(EnvType.CLIENT)
    private static Icon compostIcon;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        this.blockIcon = register.registerIcon("wood");

        filterIcon = register.registerIcon("DNBlock_mesh");
        compostIcon = register.registerIcon("DNBlock_composter_compost");
    }

    public static Icon getCompostIcon() {
        return compostIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k) {
        IBlockAccess blockAccess = renderer.blockAccess;
        SieveTileEntity tileEntity = (SieveTileEntity) blockAccess.getBlockTileEntity(i, j, k);

        // render the filter
        ItemStack filterStack = tileEntity.getFilter();

        if (filterStack != null) {
            Icon filterIcon = filterStack.getItem().getHopperFilterIcon();

            if (filterIcon != null) {
                renderer.setRenderBounds(
                        2 / 16D,
                        1 - (5 / 16D),
                        2 / 16D,

                        1 - (2 / 16D),
                        1 - (4 / 16D),
                        1 - (2 / 16D)
                );

                RenderUtils.renderStandardBlockWithTexture(renderer, this, i, j, k, filterIcon);
            }
        }

        float progress = tileEntity.getProgressPercentage();

        if (progress > 0) {
            Icon contentsIcon = SieveUtils.getBulkIcon(tileEntity.getContents());

            double base = 1 - (4 / 16D);
            double offset = (progress * 6) / 16D;
            renderer.setRenderBounds(
                    2 / 16D,
                    base,
                    2 / 16D,

                    1 - (2 / 16D),
                    base + offset,
                    1 - (2 / 16D)
            );

            RenderUtils.renderStandardBlockWithTexture(renderer, this, i, j, k, contentsIcon);
        }

        return SieveBlock.model.renderAsBlock(renderer, this, i, j, k);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getHopperFilterIcon() {
        return filterIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int iNeighborI, int iNeighborJ, int iNeighborK, int iSide) {
        return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderBlocks, int iItemDamage, float fBrightness) {
        SieveBlock.model.renderAsItemBlock(renderBlocks, this, iItemDamage);
    }
}
