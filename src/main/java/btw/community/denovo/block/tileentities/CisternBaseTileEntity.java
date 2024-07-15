package btw.community.denovo.block.tileentities;

import btw.block.tileentity.TileEntityDataPacketHandler;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet132TileEntityData;
import net.minecraft.src.TileEntity;

public abstract class CisternBaseTileEntity extends TileEntity implements TileEntityDataPacketHandler {

    public static final int CONTENTS_EMPTY = 0;
    public static final int CONTENTS_WATER = 1;
    public static final int CONTENTS_MUDDY_WATER = 2;
    public static final int CONTENTS_COMPOST = 3;
    public static final int CONTENTS_MAGGOTS = 4;
    protected int fillType;
    protected int fillLevel;
    protected int progressCounter;
    protected boolean hasCollectedWaterToday = false;
    public static final int MAX_FILL_LEVEL = 16;
    public static final int MUDDY_WATER_SETTLE_TIME = 6000; //24000 is 20min

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (fillType == CONTENTS_EMPTY || (fillType == CONTENTS_WATER && !isFullWithWater())) {
           if (worldObj.canBlockSeeTheSky(xCoord,yCoord,zCoord) && !worldObj.isRemote)
           {
               attemptToFillWithWater();
           }
        }
    }

    private void attemptToFillWithWater() {
        int timeOfDay = (int)( worldObj.worldInfo.getWorldTime() % 24000L );
        boolean isMorning = timeOfDay < 2000 || timeOfDay > 23500;
        boolean isNight = timeOfDay > 16000 && timeOfDay < 20000;
        boolean isRaining = worldObj.isRainingAtPos(xCoord, yCoord + 1, zCoord);

        if (isRaining)
        {
            if (worldObj.rand.nextFloat() <= 0.25F){
                addWater(1);
                setFillType(CONTENTS_WATER);
                worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);
            }
        } else {
            if (isMorning && !getHasCollectedWaterToday() && worldObj.rand.nextFloat() <= 0.125F){
                addWater(1);
                setFillType(CONTENTS_WATER);
                setHasCollectedWaterToday(true);
                worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);
            } else if (isNight && getHasCollectedWaterToday()){
                setHasCollectedWaterToday(false);
            }
        }

    }
    public void removeWater(int amount) {
        this.fillLevel -= 5 * amount;
    }

    public void addWater(int amount){
        this.fillLevel += 5 * amount;
    }

    public boolean isFullWithWater(){
        return this.fillLevel == 15 && this.fillType == CONTENTS_WATER;
    }

    public boolean isFullWithMuddyWater(){
        return this.fillLevel == 15 && this.fillType == CONTENTS_MUDDY_WATER;
    }

    public boolean isFull() {
        return this.fillLevel >= 15;
    }


    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);

        if (tag.hasKey("progress")) {
            progressCounter = tag.getInteger("progress");
        }
        if (tag.hasKey("fillLevel")) {
            fillLevel = tag.getInteger("fillLevel");
        }
        if (tag.hasKey("fillType")) {
            fillType = tag.getInteger("fillType");
        }
        if (tag.hasKey("hasCollectedWaterToday")) {
            hasCollectedWaterToday = tag.getBoolean("hasCollectedWaterToday");
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

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);

        tag.setInteger("progress", progressCounter);
        tag.setInteger("fillLevel", fillLevel);
        tag.setInteger("fillType", fillType);
        tag.setBoolean("hasCollectedWaterToday", hasCollectedWaterToday);
    }

    public int getFillType() {
        return fillType;
    }

    public void setFillType(int fillType) {
        this.fillType = fillType;
    }

    public int getFillLevel() {
        return this.fillLevel;
    }

    public void setFillLevel(int fillLevel) {
        this.fillLevel = fillLevel;
    }

    public int getProgressCounter() {
        return progressCounter;
    }

    public void setProgressCounter(int progressCounter) {
        this.progressCounter = progressCounter;
    }

    public boolean getHasCollectedWaterToday() {
        return hasCollectedWaterToday;
    }

    public void setHasCollectedWaterToday(boolean hasCollectedWaterToday) {
        this.hasCollectedWaterToday = hasCollectedWaterToday;
    }


}
