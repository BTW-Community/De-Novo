package btw.community.denovo.block.tileentities;

import btw.item.util.ItemUtils;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

public class CisternTileEntity extends CisternBaseTileEntity {
    @Override
    public void updateEntity() {
        super.updateEntity();

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
                }
                setFillType(CONTENTS_WATER);
                setProgressCounter(0);
            }
        }
    }
}
