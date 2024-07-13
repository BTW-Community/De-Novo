package btw.community.denovo.tileentity;

import btw.block.tileentity.TileEntityDataPacketHandler;
import btw.community.denovo.recipes.LootEntry;
import btw.community.denovo.recipes.SiftingCraftingManager;
import btw.community.denovo.recipes.SiftingRecipe;
import btw.crafting.manager.HopperFilteringCraftingManager;
import btw.crafting.recipe.types.HopperFilterRecipe;
import btw.item.util.ItemUtils;
import net.minecraft.src.*;

public class SieveTileEntity extends TileEntity implements TileEntityDataPacketHandler {
    public static final byte MAX_PROGRESS = 8;

    private ItemStack filterStack;
    private ItemStack contentsStack;
    private byte progressCounter;

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);

        if (tag.hasKey("progress")) {
            progressCounter = tag.getByte("progress");
        }

        NBTTagCompound filterTag = tag.getCompoundTag("filter");
        if (filterTag != null) {
            filterStack = ItemStack.loadItemStackFromNBT(filterTag);
        }

        NBTTagCompound contentsTag = tag.getCompoundTag("contents");
        if (contentsTag != null) {
            contentsStack = ItemStack.loadItemStackFromNBT(contentsTag);
        }

        if (contentsStack == null) progressCounter = 0;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);

        if (filterStack != null) {
            NBTTagCompound filterTag = new NBTTagCompound();
            filterStack.writeToNBT(filterTag);
            tag.setCompoundTag("filter", filterTag);
        }

        if (contentsStack != null) {
            tag.setByte("progress", progressCounter);

            NBTTagCompound contentsTag = new NBTTagCompound();
            contentsStack.writeToNBT(contentsTag);
            tag.setCompoundTag("contents", contentsTag);
        }
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        this.writeToNBT(tag);

        return new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void readNBTFromPacket(NBTTagCompound tag) {
        this.readFromNBT(tag);
        worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
    }

    public void fill(ItemStack contents) {
        this.contentsStack = contents;
        this.progressCounter = MAX_PROGRESS;
        onInventoryChanged();
    }

    public void progress(byte progressCounter) {
        this.progressCounter = progressCounter;

        if (progressCounter <= 0 && contentsStack != null && filterStack != null) {
            HopperFilterRecipe hopperRecipe = HopperFilteringCraftingManager.instance.getRecipe(contentsStack, filterStack);
            if (hopperRecipe != null) processHopperRecipe(hopperRecipe);
        }

        if (progressCounter <= 0 && contentsStack != null && filterStack != null) {
            SiftingRecipe siftingRecipe = SiftingCraftingManager.getRecipe(contentsStack, filterStack);
            if (siftingRecipe != null) processSiftingRecipe(siftingRecipe);
        }

        onInventoryChanged();
    }

    public void processHopperRecipe(HopperFilterRecipe recipe) {
        ItemUtils.ejectStackAroundBlock(worldObj, xCoord, yCoord, zCoord, recipe.getHopperOutput().copy());
        ItemUtils.ejectStackAroundBlock(worldObj, xCoord, yCoord, zCoord, recipe.getFilteredOutput().copy());
        contentsStack = null;
    }

    public void processSiftingRecipe(SiftingRecipe recipe) {
        for (LootEntry entry : recipe.getLootTable()) {
            for (int i = 0; i < entry.getAmount(); i++) {
                double roll = worldObj.rand.nextDouble();
                if (roll < entry.getChance()) {
                    ItemUtils.ejectStackAroundBlock(worldObj, xCoord, yCoord, zCoord, entry.getResult().copy());
                }
            }
        }
        contentsStack = null;
    }

    public void setFilterStack(ItemStack filterStack) {
        this.filterStack = filterStack;
        onInventoryChanged();
    }

    public ItemStack getContentsStack() {
        return contentsStack;
    }

    public ItemStack getFilterStack() {
        return filterStack;
    }

    public byte getProgressCounter() {
        return progressCounter;
    }
}
