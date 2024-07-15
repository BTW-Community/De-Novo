package btw.community.denovo.block.tileentities;

import btw.item.util.ItemUtils;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;

public class ComposterTileEntity extends CisternBaseTileEntity {

    public static final int MAGGOT_CREATION_TIME = 255;

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (isFullWithCompost()) {
            if (progressCounter < MAGGOT_CREATION_TIME) {
                progressCounter += 1;
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
