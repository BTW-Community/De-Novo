package btw.community.denovo.block.tileentities;

import btw.block.tileentity.TileEntityDataPacketHandler;
import btw.community.denovo.block.DNBlocks;
import btw.world.util.BlockPos;
import net.minecraft.src.*;

public class LavaCobbleTileEntity extends TileEntity implements TileEntityDataPacketHandler {
    public static final int MAX_COBBLE = 8;
    public static final int MAX_STRAW = 4;

    public static final int STATE_BUILDING = 0;
    public static final int STATE_CONVERTING = 1;

    private int cobbleCounter;
    private int strawCounter;
    private int state;


    //------------- TileEntity ------------//

    @Override
    public void updateEntity() {

        if (state == STATE_BUILDING){

            if (getCobbleCounter() < 8 && getStrawCounter() < 4) return;

            int smolderingCount = checkForCountAdjacent(DNBlocks.smolderingPlacedSticks);
            if (smolderingCount >= 4){
                setState(STATE_CONVERTING);
            }
        }
        else if (state == STATE_CONVERTING){
            int charcoalCount = checkForCountAdjacent(DNBlocks.charcoalPile);
            int smolderingCount = checkForCountAdjacent(DNBlocks.smolderingPlacedSticks);

            //convert back to only cobble
            if (smolderingCount + charcoalCount < 4){
                setState(STATE_BUILDING);
                setStrawCounter(0);
            }

            //convert to lava
            if (charcoalCount >= 4){
                worldObj.setBlockToAir(xCoord, yCoord, zCoord);
                worldObj.removeBlockTileEntity(xCoord, yCoord, zCoord);

                worldObj.setBlock(xCoord, yCoord, zCoord, Block.lavaStill.blockID);
            }
        }
    }

    private int checkForCountAdjacent(Block block) {
        int count = 0;

        for (int facing = 0; facing < 6; facing++) {
            BlockPos blockPos = new BlockPos(xCoord, yCoord, zCoord);

            blockPos.addFacingAsOffset(facing);
            if (worldObj.getBlockId(blockPos.x, blockPos.y, blockPos.z) == block.blockID){
                count++;
            }
        }

        return count;
    }

    //------------- TileEntity ------------//

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);

        if (tag.hasKey("state")) state = tag.getInteger("state");
        if (tag.hasKey("cobbleCounter")) cobbleCounter = tag.getInteger("cobbleCounter");
        if (tag.hasKey("strawCounter")) strawCounter = tag.getInteger("strawCounter");
//        worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);

    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);

        tag.setInteger("state", getState());
        tag.setInteger("cobbleCounter", getCobbleCounter());
        tag.setInteger("strawCounter", getStrawCounter());
//        worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
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
        worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
    }

    //------------- Class Specific Methods ------------//


    public int getCobbleCounter() {

        return cobbleCounter;
    }

    public void setCobbleCounter(int cobbleCounter) {
        this.cobbleCounter = cobbleCounter;
        worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
    }

    public int getStrawCounter() {
        return strawCounter;
    }

    public void setStrawCounter(int strawCounter) {
        this.strawCounter = strawCounter;
        worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
    }

    public int getState() {

        return state;
    }

    public void setState(int state) {
        this.state = state;
        worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
    }
}
