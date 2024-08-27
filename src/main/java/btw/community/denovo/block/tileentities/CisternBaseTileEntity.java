package btw.community.denovo.block.tileentities;

import btw.block.tileentity.TileEntityDataPacketHandler;
import btw.community.denovo.utils.CisternUtils;
import btw.item.util.ItemUtils;
import net.minecraft.src.*;

public abstract class CisternBaseTileEntity extends TileEntity implements TileEntityDataPacketHandler {

    protected int fillType = 0;
    protected int solidFillLevel = 0;
    protected int liquidFillLevel = 0;
    protected int progressCounter = 0;
    protected boolean hasCollectedWaterToday = false;

    @Override
    public void updateEntity() {

        if (getProgressCounter() > 0)
        {
            if (worldObj.isRemote)
            {
                System.out.println("CLIENT: fillType= " + getFillType());
            }
            else System.out.println("SERVER: fillType= " + getFillType());
        }

        if (fillType == CisternUtils.CONTENTS_EMPTY){
            setProgressCounter(0);
            setSolidFillLevel(0);
            setLiquidFillLevel(0);
        }

        if (fillType == CisternUtils.CONTENTS_EMPTY || (fillType == CisternUtils.CONTENTS_WATER && !isFullWithWater())) {
            if (worldObj.canBlockSeeTheSky(xCoord, yCoord, zCoord) && !worldObj.isRemote) {
                attemptToFillWithWater();
            }
        }

        if (fillType == CisternUtils.CONTENTS_MUDDY_WATER) {
            handleMuddyWater();
        }else if (fillType == CisternUtils.CONTENTS_CLAY_WATER) {
            handleClayWater();
        } else if (fillType == CisternUtils.CONTENTS_INFECTED_WATER) {
            handleInfectedWater();
        }
    }

    private void handleMuddyWater() {
        if (progressCounter < CisternUtils.MUDDY_WATER_SETTLE_TIME) {
            progressCounter += 1;
            worldObj.markBlockRangeForRenderUpdate(xCoord,yCoord,zCoord,xCoord,yCoord,zCoord);
        } else {
            if (!worldObj.isRemote) {
                ItemUtils.ejectStackFromBlockTowardsFacing(worldObj, xCoord, yCoord, zCoord, new ItemStack(Item.clay), 1);
                worldObj.playSoundEffect(
                        (double)xCoord + 0.5D, (double)yCoord + 0.5D, (double)zCoord + 0.5D,
                        Block.blockClay.stepSound.getStepSound(),
                        1/8F,
                        1F);
            }
            if (!worldObj.isRemote) setFillType(CisternUtils.CONTENTS_WATER);
            if (!worldObj.isRemote) setProgressCounter(0);
            worldObj.markBlockRangeForRenderUpdate(xCoord,yCoord,zCoord,xCoord,yCoord,zCoord);
        }
    }

    private void handleClayWater() {
        if (progressCounter < CisternUtils.CLAY_WATER_CONVERSION_TIME) {
            progressCounter += 1;

            if (progressCounter == CisternUtils.CLAY_WATER_CONVERSION_TIME / 2)
            {
                if (worldObj.rand.nextFloat() > 0.1F)
                {
                    if (!worldObj.isRemote) setFillType(CisternUtils.CONTENTS_WATER);
                    if (!worldObj.isRemote) setProgressCounter(0);
                }
            }

            worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
        } else {
            if (!worldObj.isRemote) setFillType(CisternUtils.CONTENTS_INFECTED_WATER);
            if (!worldObj.isRemote) setProgressCounter(0);
            worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
        }
    }

    private void handleInfectedWater() {
        if (solidFillLevel == CisternUtils.MAX_SOLID_FILL_LEVEL) {
            if (progressCounter < CisternUtils.MUDDY_WATER_SETTLE_TIME)
            {
                progressCounter += 1;
                worldObj.markBlockRangeForRenderUpdate(xCoord,yCoord,zCoord,xCoord,yCoord,zCoord);
            } else {
                if (!worldObj.isRemote) setFillType(CisternUtils.CONTENTS_RUST_WATER);
                if (!worldObj.isRemote) setSolidFillLevel(0);
                if (!worldObj.isRemote) setProgressCounter(0);
                worldObj.markBlockRangeForRenderUpdate(xCoord,yCoord,zCoord,xCoord,yCoord,zCoord);
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
                if (!worldObj.isRemote) addLiquid(1);
                if (!worldObj.isRemote) setFillType(CisternUtils.CONTENTS_WATER);
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            }
        } else {
            if (isMorning && !getHasCollectedWaterToday() && worldObj.rand.nextFloat() <= 0.125F) {
                if (!worldObj.isRemote) addLiquid(1);
                if (!worldObj.isRemote)setFillType(CisternUtils.CONTENTS_WATER);
                if (!worldObj.isRemote) setHasCollectedWaterToday(true);
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            } else if (isNight && getHasCollectedWaterToday()) {
                if (!worldObj.isRemote) setHasCollectedWaterToday(false);
            }
        }

    }

    public void removeLiquid(int amount) {
        this.liquidFillLevel -= 5 * amount;
        if (this.liquidFillLevel == 0) setFillType(CisternUtils.CONTENTS_EMPTY);
        worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
    }

    public void addLiquid(int amount) {
        this.liquidFillLevel += 5 * amount;
        worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
    }

    public boolean isFullWithWater() {
        return this.liquidFillLevel == CisternUtils.MAX_LIQUID_FILL_LEVEL && this.fillType == CisternUtils.CONTENTS_WATER;
    }

    public boolean hasWater() {
        return this.liquidFillLevel > 0 && this.fillType == CisternUtils.CONTENTS_WATER;
    }

    public boolean isFullWithMuddyWater() {
        return this.liquidFillLevel == CisternUtils.MAX_LIQUID_FILL_LEVEL && this.fillType == CisternUtils.CONTENTS_MUDDY_WATER;
    }

    public boolean isFullWithCompostOrMaggots() {
        return this.solidFillLevel == CisternUtils.MAX_SOLID_FILL_LEVEL && (this.fillType == CisternUtils.CONTENTS_COMPOST || this.fillType == CisternUtils.CONTENTS_MAGGOTS);
    }

    public boolean isFullWithCompost() {
        return this.solidFillLevel == CisternUtils.MAX_SOLID_FILL_LEVEL && this.fillType == CisternUtils.CONTENTS_COMPOST;
    }

    public boolean isFull() {
        if ( getLiquidFillLevel() == CisternUtils.MAX_LIQUID_FILL_LEVEL) {
            return true;
        } else if (getSolidFillLevel() == CisternUtils.MAX_SOLID_FILL_LEVEL) {
            return true;
        }
        else return false;
    }

    public boolean isEmpty() {
        return (this.solidFillLevel == 0 || this.liquidFillLevel == 0 ) && this.fillType == CisternUtils.CONTENTS_EMPTY;
    }

    public void addCompost(int amount) {
        this.solidFillLevel += amount;
        worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);

        if (tag.hasKey("progress")) {
            progressCounter = tag.getInteger("progress");
        }
        if (tag.hasKey("solidFillLevel")) {
            solidFillLevel = tag.getInteger("solidFillLevel");
        }
        if (tag.hasKey("liquidFillLevel")) {
            liquidFillLevel = tag.getInteger("liquidFillLevel");
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
        tag.setInteger("solidFillLevel", solidFillLevel);
        tag.setInteger("liquidFillLevel", liquidFillLevel);
        tag.setInteger("fillType", fillType);
        tag.setBoolean("hasCollectedWaterToday", hasCollectedWaterToday);
    }

    public int getFillType() {
        return fillType;
    }

    public void setFillType(int fillType) {
        this.fillType = fillType;
    }

    public int getSolidFillLevel() {
        return this.solidFillLevel;
    }

    public void setSolidFillLevel(int solidFillLevel) {
        this.solidFillLevel = solidFillLevel;
    }

    public int getLiquidFillLevel() {
        return this.liquidFillLevel;
    }

    public void setLiquidFillLevel(int liquidFillLevel) {
        this.liquidFillLevel = liquidFillLevel;
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
