package btw.community.denovo.block.models;

import btw.block.model.BlockModel;

public class SieveModel extends BlockModel {
    @Override
    protected void initModel() {
        // Legs
        this.addBox(0 / 16D, 0 / 16D, 0 / 16D, 4 / 16D, 10 / 16D, 4 / 16D);
        this.addBox(0 / 16D, 0 / 16D, 12 / 16D, 4 / 16D, 10 / 16D, 16 / 16D);
        this.addBox(12 / 16D, 0 / 16D, 0 / 16D, 16 / 16D, 10 / 16D, 4 / 16D);
        this.addBox(12 / 16D, 0 / 16D, 12 / 16D, 16 / 16D, 10 / 16D, 16 / 16D);

        // Long sides
        this.addBox(0 / 16D, 10 / 16D, 0 / 16D, 2 / 16D, 16 / 16D, 16 / 16D);
        this.addBox(14 / 16D, 10 / 16D, 0 / 16D, 16 / 16D, 16 / 16D, 16 / 16D);

        // Short sides
        this.addBox(2 / 16D, 10 / 16D, 0 / 16D, 14 / 16D, 16 / 16D, 2 / 16D);
        this.addBox(2 / 16D, 10 / 16D, 14 / 16D, 14 / 16D, 16 / 16D, 16 / 16D);
    }
}
