package btw.community.denovo.block.models;

import btw.block.model.BlockModel;

public class CisternModel extends BlockModel {
    @Override
    protected void initModel() {
        addBox(0 / 16D, 3 / 16D, 0, 16 / 16D, 1D, 2 / 16D); //North
        addBox(0 / 16D, 3 / 16D, 14 / 16D, 16 / 16D, 1D, 1D); //South

        addBox(0 / 16D, 3 / 16D, 2 / 16D, 2 / 16D, 1D, 14 / 16D); //West
        addBox(14 / 16D, 3 / 16D, 2 / 16D, 16 / 16D, 1D, 14 / 16D); //East

        addBox(
                0 / 16D, 0 / 16D, 0 / 16D,
                2 / 16D, 3 / 16D, 4 / 16D);

        addBox(
                2 / 16D, 0 / 16D, 0 / 16D,
                4 / 16D, 3 / 16D, 2 / 16D);


        addBox(
                12 / 16D, 0 / 16D, 0 / 16D,
                14 / 16D, 3 / 16D, 2 / 16D);

        addBox(
                14 / 16D, 0 / 16D, 0 / 16D,
                16 / 16D, 3 / 16D, 4 / 16D);

        addBox(
                0 / 16D, 0 / 16D, 12 / 16D,
                2 / 16D, 3 / 16D, 16 / 16D);

        addBox(
                2 / 16D, 0 / 16D, 14 / 16D,
                4 / 16D, 3 / 16D, 16 / 16D);

        addBox(
                12 / 16D, 0 / 16D, 14 / 16D,
                14 / 16D, 3 / 16D, 16 / 16D);

        addBox(
                14 / 16D, 0 / 16D, 12 / 16D,
                16 / 16D, 3 / 16D, 16 / 16D);
    }
}
