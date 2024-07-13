package btw.community.denovo.item;

import btw.community.denovo.DeNovoAddon;
import btw.community.denovo.item.items.DirtPileItem;
import btw.community.denovo.item.items.MeshItem;
import btw.item.BTWItems;
import btw.item.items.FoodItem;
import btw.item.items.PlaceAsBlockItem;
import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Item;

public class DNItems {
    public static Item mesh;
    public static Item rawMaggots;
    public static Item cookedMaggots;

    //TODO: progressive maggots to string item?

    private static final int RAW_MAGGOTS_HUNGER_HEALED = 1;
    private static final float RAW_MAGGOTS_SATURATION_MOD = 0.125f;

    private static final int COOKED_MAGGOTS_HUNGER_HEALED = 2;
    private static final float COOKED_MAGGOTS_SATURATION_MOD = 0.25f;

    public static void initItems() {
        BTWItems.goldenDung = new PlaceAsBlockItem(BTWItems.goldenDung.itemID - 256, Block.deadBush.blockID).setUnlocalizedName("fcItemDungGolden");
        BTWItems.dirtPile = new DirtPileItem(BTWItems.dirtPile.itemID - 256, Block.deadBush.blockID);

        mesh = new MeshItem(DeNovoAddon.instance.parseID("DNItemMeshID"));

        rawMaggots = new FoodItem(DeNovoAddon.instance.parseID("DNItemRawMaggotsID"), RAW_MAGGOTS_HUNGER_HEALED, RAW_MAGGOTS_SATURATION_MOD, false, "DNItem_maggots")
                .setCreativeTab(CreativeTabs.tabMisc);

        cookedMaggots = new FoodItem(DeNovoAddon.instance.parseID("DNItemCookedMaggotsID"), COOKED_MAGGOTS_HUNGER_HEALED, COOKED_MAGGOTS_SATURATION_MOD, false, "DNItem_maggots_cooked")
                .setCreativeTab(CreativeTabs.tabMisc);
    }
}