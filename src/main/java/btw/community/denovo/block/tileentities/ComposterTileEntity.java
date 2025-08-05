package btw.community.denovo.block.tileentities;

import btw.community.denovo.utils.CisternUtils;

public class ComposterTileEntity extends CisternBaseTileEntity {

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (isFullWithCompost()) {
            handleCompost();
        }
    }

    private void handleCompost() {
        if (getProgressCounter() < CisternUtils.MAGGOT_CREATION_TIME) {
            setProgressCounter(getProgressCounter() + 1);
            worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);

        } else {
            if (!worldObj.isRemote) setFillType(CisternUtils.CONTENTS_MAGGOTS);
            worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
        }
    }
}
