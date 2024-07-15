package btw.community.denovo.block.tileentities;

import btw.item.util.ItemUtils;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;

public class ComposterTileEntity extends CisternBaseTileEntity {

    public static final int MAGGOT_CREATION_TIME = 255;

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (fillType == CONTENTS_MUDDY_WATER)
        {
            if (progressCounter < MUDDY_WATER_SETTLE_TIME) {
                progressCounter += 1;
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            }
            else {
                if ( !worldObj.isRemote )
                {
                    ItemUtils.ejectStackFromBlockTowardsFacing(worldObj, xCoord, yCoord, zCoord, new ItemStack(Item.clay), 1);
                }
                setFillType(CONTENTS_WATER);
                setProgressCounter(0);
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
