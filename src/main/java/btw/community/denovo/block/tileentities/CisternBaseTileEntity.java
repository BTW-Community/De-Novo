package btw.community.denovo.block.tileentities;

import btw.block.tileentity.TileEntityDataPacketHandler;
import btw.item.util.ItemUtils;
import net.minecraft.src.*;

public abstract class CisternBaseTileEntity extends TileEntity implements TileEntityDataPacketHandler {

    public static final int CONTENTS_EMPTY = 0;
    public static final int CONTENTS_WATER = 1;
    public static final int CONTENTS_MUDDY_WATER = 2;
    public static final int CONTENTS_COMPOST = 3;
    public static final int CONTENTS_MAGGOTS = 4;
    protected int fillType = 0;
    protected int fillLevel = 0;
    protected int progressCounter = 0;
    protected boolean hasCollectedWaterToday = false;
    public static final int MAX_FILL_LEVEL = 16;
    public static final int MUDDY_WATER_SETTLE_TIME = 6000; //24000 is 20min

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (fillLevel == 0) {
            if (fillType != CONTENTS_EMPTY) setFillType(CONTENTS_EMPTY);
            if (progressCounter != 0) setProgressCounter(0);
        }

        if (fillType == CONTENTS_EMPTY || (fillType == CONTENTS_WATER && !isFullWithWater())) {
            if (worldObj.canBlockSeeTheSky(xCoord, yCoord, zCoord) && !worldObj.isRemote) {
                attemptToFillWithWater();
            }
        } else if (fillType == CONTENTS_MUDDY_WATER) {
            if (progressCounter < MUDDY_WATER_SETTLE_TIME) {
                progressCounter += 1;
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            } else {
                if (!worldObj.isRemote) {
                    ItemUtils.ejectStackFromBlockTowardsFacing(worldObj, xCoord, yCoord, zCoord, new ItemStack(Item.clay), 1);
                    worldObj.playSoundEffect(
                            (double)xCoord + 0.5D, (double)yCoord + 0.5D, (double)zCoord + 0.5D,
                            Block.blockClay.stepSound.getStepSound(),
                            1/8F,
                            1F);
                }
                setFillType(CONTENTS_WATER);
                setProgressCounter(0);
            }
        }
    }

    protected void attemptToFillWithWater() {
        int timeOfDay = (int) (worldObj.worldInfo.getWorldTime() % 24000L);
        boolean isMorning = timeOfDay < 500 || timeOfDay > 23500;
        boolean isNight = timeOfDay > 16000 && timeOfDay < 20000;
        boolean isRaining = worldObj.isRainingAtPos(xCoord, yCoord + 1, zCoord);

        if (isRaining) {
            if (worldObj.rand.nextFloat() <= 0.25F) {
                addWater(1);
                setFillType(CONTENTS_WATER);
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            }
        } else {
            if (isMorning && !getHasCollectedWaterToday() && worldObj.rand.nextFloat() <= 0.125F) {
                addWater(1);
                setFillType(CONTENTS_WATER);
                setHasCollectedWaterToday(true);
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            } else if (isNight && getHasCollectedWaterToday()) {
                setHasCollectedWaterToday(false);
            }
        }

    }

    public void removeWater(int amount) {
        this.fillLevel -= 5 * amount;
    }

    public void addWater(int amount) {
        if (this.fillType != CONTENTS_WATER) this.fillType = CONTENTS_WATER;
        this.fillLevel += 5 * amount;
    }

    public boolean isFullWithWater() {
        return this.fillLevel == 15 && this.fillType == CONTENTS_WATER;
    }

    public boolean hasWater() {
        return this.fillLevel > 0 && this.fillType == CONTENTS_WATER;
    }

    public boolean isFullWithMuddyWater() {
        return this.fillLevel == 15 && this.fillType == CONTENTS_MUDDY_WATER;
    }

    public boolean isFullWithCompostOrMaggots() {
        return this.fillLevel == MAX_FILL_LEVEL && (this.fillType == CONTENTS_COMPOST || this.fillType == CONTENTS_MAGGOTS);
    }

    public boolean isFullWithCompost() {
        return this.fillLevel == MAX_FILL_LEVEL && this.fillType == CONTENTS_COMPOST;
    }

    public boolean isFull() {
        if (this.fillType == CONTENTS_WATER || this.fillType == CONTENTS_MUDDY_WATER) {
            return this.fillLevel == 15;
        } else if (this.fillType == CONTENTS_COMPOST || this.fillType == CONTENTS_MAGGOTS) {
            return this.fillLevel == 16;
        } else return false;
    }

    public boolean isEmpty() {
        return this.fillLevel == 0 && this.fillType == CONTENTS_EMPTY;
    }

    public void addCompost(int amount) {
        this.fillLevel += amount;
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
