package btw.community.denovo.item;

import btw.community.denovo.DeNovoAddon;
import btw.community.denovo.block.DNBlocks;
import btw.community.denovo.item.items.*;
import btw.crafting.util.FurnaceBurnTime;
import btw.item.BTWItems;
import btw.item.items.FoodItem;
import btw.item.items.PlaceAsBlockItem;
import emi.dev.emi.emi.config.EmiConfig;
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
    public static Item rustWaterBowl;
    public static Item ironDust;
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
        BTWItems.goldenDung = new PlaceAsBlockItem(BTWItems.goldenDung.itemID - 256, Block.deadBush.blockID)
                .setUnlocalizedName("fcItemDungGolden")
                .setTextureName("btw:golden_dung");

        mesh = new MeshItem(DeNovoAddon.instance.parseID("DNItemMeshID") - 256);

        rawMaggots = new RawMaggotsItem(DeNovoAddon.instance.parseID("DNItemRawMaggotsID") - 256, RAW_MAGGOTS_HUNGER_HEALED, RAW_MAGGOTS_SATURATION_MOD, false, "maggots")
                .setCreativeTab(CreativeTabs.tabMisc);

        cookedMaggots = new FoodItem(DeNovoAddon.instance.parseID("DNItemCookedMaggotsID") - 256, COOKED_MAGGOTS_HUNGER_HEALED, COOKED_MAGGOTS_SATURATION_MOD, false, "denovo.maggots_cooked")
                .setTextureName("denovo:maggots_cooked")
                .setCreativeTab(CreativeTabs.tabMisc);

        maggotsSilkExtraction = new RustWaterBowlItem(DeNovoAddon.instance.parseID("DNItemMaggotsSilkExtractionID") - 256, "maggots_silk_extraction");

        waterBowl = new WaterBowlItem(DeNovoAddon.instance.parseID("DNItemWaterBowlID") - 256);
        rustWaterBowl = new RustWaterBowlItem(DeNovoAddon.instance.parseID("DNItemRustWaterBowlID") - 256, "rust_water_bowl");

        ironDust = new Item(DeNovoAddon.instance.parseID("DNItemIronDustID") - 256)
                .setUnlocalizedName("denovo.iron_dust")
                .setTextureName("denovo:iron_dust");

        cistern = new PlaceAsBlockItem(DeNovoAddon.instance.parseID("DNItemCisternID") - 256, DNBlocks.cistern.blockID)
                .setUnlocalizedName("denovo.cauldron"); //.setCreativeTab(CreativeTabs.tabBrewing);
        Item.cauldron.setCreativeTab(null);

        charcoalDust = new Item(DeNovoAddon.instance.parseID("DNItemCharcoalDustID") - 256)
                .setUnlocalizedName("denovo.charcoal_dust")
                .setTextureName("denovo:charcoal_dust")
                .setIncineratedInCrucible()
                .setfurnaceburntime(FurnaceBurnTime.COAL)
                .setFilterableProperties(
                Item.FILTERABLE_SMALL).setCreativeTab(CreativeTabs.tabMaterials);

        woodSickle = new SickleItem(DeNovoAddon.instance.parseID("DNItemWoodSickleID") - 256, EnumToolMaterial.WOOD, 10);

        flintHammer = new HammerItem(DeNovoAddon.instance.parseID("DNItemFlintHammerID") - 256, EnumToolMaterial.STONE, 25);
    }
}