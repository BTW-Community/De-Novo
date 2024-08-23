package btw.community.denovo.block.tileentities;

import btw.community.denovo.utils.CisternUtils;
import net.minecraft.src.NBTTagCompound;

public class ComposterTileEntity extends CisternBaseTileEntity {

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (isFullWithCompost()) {
            handleCompost();
        }
    }

    private void handleCompost() {
        if (progressCounter < CisternUtils.MAGGOT_CREATION_TIME) {
            progressCounter += 1;
            worldObj.markBlockRangeForRenderUpdate(xCoord,yCoord,zCoord,xCoord,yCoord,zCoord);

        } else {
            setFillType(CisternUtils.CONTENTS_MAGGOTS);
            worldObj.markBlockRangeForRenderUpdate(xCoord,yCoord,zCoord,xCoord,yCoord,zCoord);
        }
    }
}
