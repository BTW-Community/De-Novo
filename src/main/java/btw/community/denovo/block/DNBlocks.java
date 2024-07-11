package btw.community.denovo.block;

import net.minecraft.src.Block;
import net.minecraft.src.BlockLog;
import net.minecraft.src.Item;
import net.minecraft.src.ItemMultiTextureTile;

public class DNBlocks {
    public static void initBlocks() {
        Item.itemsList[Block.deadBush.blockID] = (new ItemMultiTextureTile(Block.deadBush.blockID - 256, Block.deadBush, new String[]{"mature", "medium", "small", "tiny"}));
    }
}