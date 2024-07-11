package btw.community.denovo.item;

import btw.item.BTWItems;
import btw.community.denovo.item.items.DirtPileItem;
import btw.item.items.PlaceAsBlockItem;
import net.minecraft.src.Block;

public class DNItems {
    public static void initItems() {
        BTWItems.goldenDung = new PlaceAsBlockItem(BTWItems.goldenDung.itemID - 256, Block.deadBush.blockID).setUnlocalizedName("fcItemDungGolden");
        BTWItems.dirtPile = new DirtPileItem(BTWItems.dirtPile.itemID - 256, Block.deadBush.blockID);
    }
}