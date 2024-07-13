package btw.community.denovo.block.tileentities;

import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;

public class ComposterTileEntity extends TileEntity {
    private final int fillLevel = 0;
    private int progressCounter;
    private final int timeToSettle = 255;

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (progressCounter < timeToSettle) {
            progressCounter += 5;
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }


    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);

        if (tag.hasKey("progress")) {
            progressCounter = tag.getInteger("progress");
        }

    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);

        tag.setInteger("progress", progressCounter);
    }

    public int getFillLevel() {
        return this.fillLevel;
    }

    public int getProgressCounter() {
        return progressCounter;
    }

    public void setProgressCounter(byte progressCounter) {
        this.progressCounter = progressCounter;
    }
}
