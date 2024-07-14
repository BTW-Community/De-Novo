package btw.community.denovo.tileentity;

import btw.block.blocks.PortalBlock;
import btw.block.tileentity.TileEntityDataPacketHandler;
import btw.client.fx.BTWEffectManager;
import btw.community.denovo.block.DNBlocks;
import btw.community.denovo.block.blocks.SieveBlock;
import net.minecraft.src.*;

public class SieveTileEntity extends TileEntity implements TileEntityDataPacketHandler {
    public static final int MAX_PROGRESS = 8;

    private static final int OVERLOAD_SOUL_COUNT = 8;
    private static final int POSSESSION_COUNT_ON_OVERLOAD = 4;

    private ItemStack filterStack;
    private ItemStack contentsStack;
    private int progressCounter;
    private int containedSoulCount;

    //------------- TileEntity ------------//

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);

        if (tag.hasKey("progress")) {
            progressCounter = tag.getInteger("progress");
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
            tag.setInteger("progress", progressCounter);

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

    //------------- TileEntityDataPacketHandler ------------//

    @Override
    public void readNBTFromPacket(NBTTagCompound tag) {
        this.readFromNBT(tag);
        worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
    }

    //------------- Class Specific Methods ------------//

    public void releaseSouls() {
        containedSoulCount += contentsStack.stackSize;

        worldObj.playAuxSFX(BTWEffectManager.GHAST_MOAN_EFFECT_ID, xCoord, yCoord, zCoord, 0);

        if (containedSoulCount >= OVERLOAD_SOUL_COUNT) {
            ((SieveBlock) DNBlocks.sieve).breakSieve(worldObj, xCoord, yCoord, zCoord);

            EntityCreature.attemptToPossessCreaturesAroundBlock(this.worldObj, this.xCoord, this.yCoord, this.zCoord, POSSESSION_COUNT_ON_OVERLOAD, PortalBlock.CREATURE_POSSESSION_RANGE);
        }
    }

    public void setContents(ItemStack contentsStack) {
        this.contentsStack = contentsStack;
        if (contentsStack != null) {
            this.progressCounter = MAX_PROGRESS;
        } else {
            this.progressCounter = 0;
        }
        onInventoryChanged();
    }

    public void setFilter(ItemStack filterStack) {
        this.filterStack = filterStack;
        onInventoryChanged();
    }

    public void decrementProgress() {
        this.progressCounter -= 1;
    }

    public ItemStack getContents() {
        return contentsStack;
    }

    public ItemStack getFilter() {
        return filterStack;
    }

    public int getProgressCounter() {
        return progressCounter;
    }

    public float getProgressPercentage() {
        return ((float) progressCounter) / MAX_PROGRESS;
    }
}
