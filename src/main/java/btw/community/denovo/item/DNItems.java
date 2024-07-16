package btw.community.denovo.item;

import btw.community.denovo.DeNovoAddon;
import btw.community.denovo.block.DNBlocks;
import btw.community.denovo.item.items.DirtPileItem;
import btw.community.denovo.item.items.MeshItem;
import btw.community.denovo.item.items.RawMaggotsItem;
import btw.community.denovo.item.items.WaterBowlItem;
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
    public static Item waterBowl;
    public static Item cistern;

    //TODO: progressive maggots to string item?

    private static final int RAW_MAGGOTS_HUNGER_HEALED = 1;
    private static final float RAW_MAGGOTS_SATURATION_MOD = 0.125f;

    private static final int COOKED_MAGGOTS_HUNGER_HEALED = 2;
    private static final float COOKED_MAGGOTS_SATURATION_MOD = 0.25f;

    public static void initItems() {
        BTWItems.goldenDung = new PlaceAsBlockItem(BTWItems.goldenDung.itemID - 256, Block.deadBush.blockID).setUnlocalizedName("fcItemDungGolden");
        //BTWItems.dirtPile = new DirtPileItem(BTWItems.dirtPile.itemID - 256, Block.deadBush.blockID);

        mesh = new MeshItem(DeNovoAddon.instance.parseID("DNItemMeshID") - 256);

        rawMaggots = new RawMaggotsItem(DeNovoAddon.instance.parseID("DNItemRawMaggotsID") - 256, RAW_MAGGOTS_HUNGER_HEALED, RAW_MAGGOTS_SATURATION_MOD, false, "DNItem_maggots")
                .setCreativeTab(CreativeTabs.tabMisc);

        cookedMaggots = new FoodItem(DeNovoAddon.instance.parseID("DNItemCookedMaggotsID") - 256, COOKED_MAGGOTS_HUNGER_HEALED, COOKED_MAGGOTS_SATURATION_MOD, false, "DNItem_maggots_cooked")
                .setCreativeTab(CreativeTabs.tabMisc);

        waterBowl = new WaterBowlItem(DeNovoAddon.instance.parseID("DNItemWaterBowlID") - 256);

        cistern = new PlaceAsBlockItem(DeNovoAddon.instance.parseID("DNItemCisternID") - 256, DNBlocks.cistern.blockID).setUnlocalizedName("cauldron").setCreativeTab(CreativeTabs.tabBrewing);
        Item.cauldron.setCreativeTab(null);
    }
}