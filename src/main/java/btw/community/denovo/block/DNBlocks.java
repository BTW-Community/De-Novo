package btw.community.denovo.block;

import btw.community.denovo.DeNovoAddon;
import btw.community.denovo.block.blocks.CisternBlock;
import btw.community.denovo.block.blocks.ComposterBlock;
import btw.community.denovo.block.blocks.SieveBlock;
import btw.community.denovo.block.tileentities.CisternTileEntity;
import btw.community.denovo.block.tileentities.ComposterTileEntity;
import btw.community.denovo.block.tileentities.SieveTileEntity;
import net.minecraft.src.*;

public class DNBlocks {
    public static Block composter;
    public static Block cistern;
    public static Block sieve;

    public static void initBlocks() {
        Item.itemsList[Block.deadBush.blockID] = (new ItemMultiTextureTile(Block.deadBush.blockID - 256, Block.deadBush, new String[]{"mature", "medium", "small", "tiny"}));

        sieve = registerItemBlock(new SieveBlock(DeNovoAddon.instance.parseID("DNBlockSieveID")));
        TileEntity.addMapping(SieveTileEntity.class, "DNSieve");

        composter = registerItemBlock(new ComposterBlock(DeNovoAddon.instance.parseID("DNBlockComposterID")));
        TileEntity.addMapping(ComposterTileEntity.class, "DNComposter");

        cistern = new CisternBlock(DeNovoAddon.instance.parseID("DNBlockCisternID"));
        TileEntity.addMapping(CisternTileEntity.class, "DNCistern");
    }

    private static Block registerItemBlock(Block block) {
        Item.itemsList[block.blockID] = new ItemBlock(block.blockID - 256);

        return block;
    }
}