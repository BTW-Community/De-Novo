package btw.community.denovo.block.models;

import btw.block.model.BlockModel;

public class ComposterModel extends BlockModel {
    @Override
    protected void initModel() {
        addBox(0 / 16D, 0, 0, 1D, 1D, 2 / 16D);
        addBox(0 / 16D, 0, 14 / 16D, 1D, 1D, 1D);

        addBox(0 / 16D, 0, 2 / 16D, 2 / 16D, 1D, 14 / 16D);
        addBox(14 / 16D, 0, 2 / 16D, 16 / 16D, 1D, 14 / 16D);
    }
}
