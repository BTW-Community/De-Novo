package btw.community.denovo.block;

import btw.community.denovo.DeNovoAddon;
import btw.community.denovo.block.blocks.SieveBlock;
import btw.community.denovo.tileentity.SieveTileEntity;
import net.minecraft.src.*;

public class DNBlocks {
    public static Block sieve;

    public static void initBlocks() {
        Item.itemsList[Block.deadBush.blockID] = (new ItemMultiTextureTile(Block.deadBush.blockID - 256, Block.deadBush, new String[]{"mature", "medium", "small", "tiny"}));

        sieve = registerItemBlock(new SieveBlock(DeNovoAddon.instance.parseID("DNSieveID")));
        TileEntity.addMapping(SieveTileEntity.class, "DNSieve");
    }

    private static Block registerItemBlock(Block block) {
        Item.itemsList[block.blockID] = new ItemBlock(block.blockID - 256);

        return block;
    }
}