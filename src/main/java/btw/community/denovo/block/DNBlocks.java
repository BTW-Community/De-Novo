package btw.community.denovo.block;

import btw.community.denovo.DeNovoAddon;
import btw.community.denovo.block.blocks.*;
import btw.community.denovo.block.tileentities.CisternTileEntity;
import btw.community.denovo.block.tileentities.ComposterTileEntity;
import btw.community.denovo.block.tileentities.SieveTileEntity;
import btw.community.denovo.block.tileentities.SmolderingPlacedSticksTileEntity;
import net.minecraft.src.*;

public class DNBlocks {
    public static Block composter;
    public static Block cistern;
    public static Block sieve;
    public static Block placedSticks;
    public static Block smolderingPlacedSticks;
    public static Block charcoalPile;

    public static void initBlocks() {
        Item.itemsList[Block.deadBush.blockID] = (new ItemMultiTextureTile(Block.deadBush.blockID - 256, Block.deadBush, new String[]{"mature", "medium", "small", "tiny"}));

        sieve = registerItemBlock(new SieveBlock(DeNovoAddon.instance.parseID("DNBlockSieveID")));
        TileEntity.addMapping(SieveTileEntity.class, "DNSieve");

        composter = registerItemBlock(new ComposterBlock(DeNovoAddon.instance.parseID("DNBlockComposterID")));
        TileEntity.addMapping(ComposterTileEntity.class, "DNComposter");

        cistern =  registerItemBlock(new CisternBlock(DeNovoAddon.instance.parseID("DNBlockCisternID")));
        TileEntity.addMapping(CisternTileEntity.class, "DNCistern");

        placedSticks = registerItemBlock(new PlacedSticksBlock(DeNovoAddon.instance.parseID("DNBlockPlacedSticksID")));

        smolderingPlacedSticks = registerItemBlock(new SmolderingPlacedSticksBlock(DeNovoAddon.instance.parseID("DNBlockSmolderingPlacedSticksID")));
        TileEntity.addMapping(SmolderingPlacedSticksTileEntity.class, "DNSmolderingPlacedSticks");

        charcoalPile = registerItemBlock(new CharcoalPileBlock(DeNovoAddon.instance.parseID("DNBlockCharcoalPileID")));
    }

    private static Block registerItemBlock(Block block) {
        Item.itemsList[block.blockID] = new ItemBlock(block.blockID - 256);

        return block;
    }
}