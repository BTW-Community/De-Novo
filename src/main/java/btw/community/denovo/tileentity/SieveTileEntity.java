package btw.community.denovo.tileentity;

import btw.block.tileentity.TileEntityDataPacketHandler;
import net.minecraft.src.*;

public class SieveTileEntity extends TileEntity implements TileEntityDataPacketHandler {
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
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);

        tag.setByte("progress", progressCounter);

        if (filterStack != null) {
            NBTTagCompound filterTag = new NBTTagCompound();
            filterStack.writeToNBT(filterTag);
            tag.setCompoundTag("filter", filterTag);
        }

        if (contentsStack != null) {
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

    public void setContentsStack(ItemStack contents) {
        this.contentsStack = contents;
    }

    public ItemStack getContentsStack() {
        return contentsStack;
    }

    public void setFilterStack(ItemStack filterStack) {
        this.filterStack = filterStack;
    }

    public ItemStack getFilterStack() {
        return filterStack;
    }

    public void setProgressCounter(byte progressCounter) {
        this.progressCounter = progressCounter;
    }

    public byte getProgressCounter() {
        return progressCounter;
    }

    /*public void ejectContents(int iFacing)
    {
        if ( iFacing < 2 )
        {
            // always eject towards the sides

            iFacing = worldObj.rand.nextInt( 4 ) + 2;
        }

        if (legacyInventory != null )
        {
            for (int iTempSlot = 0; iTempSlot < legacyInventory.length; iTempSlot++ )
            {
                if (legacyInventory[iTempSlot] != null && legacyInventory[iTempSlot].stackSize > 0 )
                {
                    ItemUtils.ejectStackFromBlockTowardsFacing(worldObj, xCoord, yCoord, zCoord, legacyInventory[iTempSlot], iFacing);

                    legacyInventory[iTempSlot] = null;
                }
            }

            legacyInventory = null;
        }

        if (stackMilling != null )
        {
            ItemUtils.ejectStackFromBlockTowardsFacing(worldObj, xCoord, yCoord, zCoord, stackMilling, iFacing);

            stackMilling = null;

            onInventoryChanged();
        }
    }*/
}
