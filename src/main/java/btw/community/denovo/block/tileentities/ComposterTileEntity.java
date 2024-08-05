package btw.community.denovo.block.tileentities;

import net.minecraft.src.NBTTagCompound;

public class ComposterTileEntity extends CisternBaseTileEntity {

    public static final int MAGGOT_CREATION_TIME = 2400; //was 2 min (1200), now 4 min

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (isFullWithCompost()) {
            if (progressCounter < MAGGOT_CREATION_TIME) {
                progressCounter += 1;
                worldObj.markBlockRangeForRenderUpdate(xCoord,yCoord,zCoord,xCoord,yCoord,zCoord);

            } else {
                setFillType(CONTENTS_MAGGOTS);
                worldObj.markBlockRangeForRenderUpdate(xCoord,yCoord,zCoord,xCoord,yCoord,zCoord);
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);

    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);

    }


}
