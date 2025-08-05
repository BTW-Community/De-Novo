package btw.community.denovo.item;

import btw.community.denovo.DeNovoAddon;
import btw.community.denovo.block.DNBlocks;
import btw.community.denovo.item.items.*;
import btw.crafting.util.FurnaceBurnTime;
import btw.item.BTWItems;
import btw.item.items.FoodItem;
import btw.item.items.PlaceAsBlockItem;
import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EnumToolMaterial;
import net.minecraft.src.Item;

public class DNItems {
    public static Item mesh;
    public static Item rawMaggots;
    public static Item cookedMaggots;
    public static Item maggotsSilkExtraction;
    public static Item waterBowl;
    public static Item cistern;
    public static Item charcoalDust;
    public static Item woodSickle;
    public static Item flintHammer;

    //TODO: progressive maggots to string item?

    private static final int RAW_MAGGOTS_HUNGER_HEALED = 1;
    private static final float RAW_MAGGOTS_SATURATION_MOD = 0.125f;

    private static final int COOKED_MAGGOTS_HUNGER_HEALED = 2;
    private static final float COOKED_MAGGOTS_SATURATION_MOD = 0.25f;

    public static void initItems() {
        BTWItems.goldenDung = new PlaceAsBlockItem(BTWItems.goldenDung.itemID - 256, Block.deadBush.blockID).setUnlocalizedName("fcItemDungGolden");

        mesh = new MeshItem(DeNovoAddon.instance.parseID("DNItemMeshID") - 256);

        rawMaggots = new RawMaggotsItem(DeNovoAddon.instance.parseID("DNItemRawMaggotsID") - 256, RAW_MAGGOTS_HUNGER_HEALED, RAW_MAGGOTS_SATURATION_MOD, false, "DNItem_maggots")
                .setCreativeTab(CreativeTabs.tabMisc);

        cookedMaggots = new FoodItem(DeNovoAddon.instance.parseID("DNItemCookedMaggotsID") - 256, COOKED_MAGGOTS_HUNGER_HEALED, COOKED_MAGGOTS_SATURATION_MOD, false, "DNItem_maggots_cooked")
                .setCreativeTab(CreativeTabs.tabMisc);

        maggotsSilkExtraction = new MaggotsSilkExtractionItem(DeNovoAddon.instance.parseID("DNItemMaggotsSilkExtractionID") - 256, "DNItem_maggots_silk_extraction");

        waterBowl = new WaterBowlItem(DeNovoAddon.instance.parseID("DNItemWaterBowlID") - 256);

        cistern = new PlaceAsBlockItem(DeNovoAddon.instance.parseID("DNItemCisternID") - 256, DNBlocks.cistern.blockID).setUnlocalizedName("cauldron").setCreativeTab(CreativeTabs.tabBrewing);
        Item.cauldron.setCreativeTab(null);

        charcoalDust = new Item(DeNovoAddon.instance.parseID("DNItemCharcoalDustID") - 256).setUnlocalizedName("DNItem_charcoal_dust").setIncineratedInCrucible().setfurnaceburntime(FurnaceBurnTime.COAL).setFilterableProperties(
                Item.FILTERABLE_SMALL).setCreativeTab(CreativeTabs.tabMaterials);

        woodSickle = new SickleItem(DeNovoAddon.instance.parseID("DNItemWoodSickleID") - 256, EnumToolMaterial.WOOD, 10);

        flintHammer = new HammerItem(DeNovoAddon.instance.parseID("DNItemFlintHammerID") - 256,EnumToolMaterial.STONE, 25);
    }
}