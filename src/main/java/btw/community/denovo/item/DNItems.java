package btw.community.denovo.item;

import btw.community.denovo.DeNovoAddon;
import btw.community.denovo.item.items.MeshItem;
import btw.item.BTWItems;
import btw.community.denovo.item.items.DirtPileItem;
import btw.item.items.PlaceAsBlockItem;
import net.minecraft.src.Block;
import net.minecraft.src.Item;

public class DNItems {
    public static Item mesh;

    public static void initItems() {
        BTWItems.goldenDung = new PlaceAsBlockItem(BTWItems.goldenDung.itemID - 256, Block.deadBush.blockID).setUnlocalizedName("fcItemDungGolden");
        BTWItems.dirtPile = new DirtPileItem(BTWItems.dirtPile.itemID - 256, Block.deadBush.blockID);

        mesh = new MeshItem(DeNovoAddon.instance.parseID("DNMeshID"));
    }
}