package btw.community.denovo.block.tileentities;

import net.minecraft.src.NBTTagCompound;

public class ComposterTileEntity extends CisternBaseTileEntity {

    public static final int MAGGOT_CREATION_TIME = 1200; //2 min

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (isFullWithCompost()) {
            if (progressCounter < MAGGOT_CREATION_TIME) {
                progressCounter += 1;
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            } else {
                setFillType(CONTENTS_MAGGOTS);
                setProgressCounter(0);
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
