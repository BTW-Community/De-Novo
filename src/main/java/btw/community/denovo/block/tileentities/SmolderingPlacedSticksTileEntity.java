package btw.community.denovo.block.tileentities;

import btw.block.tileentity.TileEntityDataPacketHandler;
import btw.community.denovo.block.blocks.SmolderingPlacedSticksBlock;
import net.minecraft.src.*;

public class SmolderingPlacedSticksTileEntity extends TileEntity implements TileEntityDataPacketHandler {

    private int burnLevel;
    /*private int counter;*/

    @Override
    public void updateEntity() {


        //TODO: REMOVE BEFORE RELEASE!!! -SOCK
       /* this.counter++;*/
        /*
        if (worldObj.rand.nextFloat() <= 1/64F)
        {
            Block block = Block.blocksList[worldObj.getBlockId(xCoord,yCoord,zCoord)];
            if (block instanceof SmolderingPlacedSticksBlock)
            {
                block.randomUpdateTick(worldObj, xCoord, yCoord, zCoord, worldObj.rand);
            }
        }
        */
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);

        if (tag.hasKey("burnLevel")) {
            burnLevel = tag.getInteger("burnLevel");
        }
/*        if (tag.hasKey("counter")) {
            counter = tag.getInteger("counter");
        }*/
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setInteger("burnLevel", burnLevel);
/*        tag.setInteger("counter", counter);*/
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


    public int getBurnLevel() {
        return burnLevel;
    }

/*    public int getCounter() {
        return counter;
    }*/

    public int setBurnLevel(int burnLevel) {

        this.burnLevel = burnLevel;
        this.worldObj.markBlockForRenderUpdate(xCoord,yCoord,zCoord);

        return this.burnLevel;
    }

    public int increaseBurnLevelBy(int amount)
    {
        this.worldObj.markBlockForRenderUpdate(xCoord,yCoord,zCoord);
        return this.burnLevel += amount;
    }
}
