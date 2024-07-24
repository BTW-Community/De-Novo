package btw.community.denovo;

import btw.AddonHandler;
import btw.BTWAddon;
import btw.community.denovo.block.DNBlocks;
import btw.community.denovo.item.DNItems;
import btw.community.denovo.recipes.DNRecipes;

import java.util.Map;

public class DeNovoAddon extends BTWAddon {
    public static DeNovoAddon instance;

    private Map<String, String> propertyValues;

    // configuration settings
    public static boolean allowGoldenDungOnHCS = false;

    private DeNovoAddon() {
        super("@NAME@", "@VERSION@", "@PREFIX@");
        DeNovoAddon.instance = this;
    }

    @Override
    public void preInitialize() {
        registerConfigProperties();
    }

    @Override
    public void initialize() {
        AddonHandler.logMessage(this.getName() + " Version " + this.getVersionString() + " Initializing...");

        DNBlocks.initBlocks();
        DNItems.initItems();

        DNRecipes.addRecipes();
    }

    private void registerConfigProperties() {
        //Gameplay config
        this.registerProperty("AllowGoldenDungOnHCS", "False", "Set the following to true to allow players to get a piece of golden dung on every HCS");

        //Block IDs
        this.registerProperty("DNBlockSieveID", "3900", "***Block IDs***\n\n");
        this.registerProperty("DNBlockComposterID", "3901");
        this.registerProperty("DNBlockCisternID", "3902");

        //Item IDs
        this.registerProperty("DNItemMeshID", "23000", "***Item IDs***\n\n");
        this.registerProperty("DNItemRawMaggotsID", "23001");
        this.registerProperty("DNItemCookedMaggotsID", "23002");
        this.registerProperty("DNItemWaterBowlID", "23003");
        this.registerProperty("DNItemCisternID", "23004");
        this.registerProperty("DNItemSickleWoodID", "23010");
    }

    @Override
    public void handleConfigProperties(Map<String, String> propertyValues) {
        this.propertyValues = propertyValues;

        allowGoldenDungOnHCS = Boolean.parseBoolean(this.propertyValues.get("AllowGoldenDungOnHCS"));
    }

    public int parseID(String name) {
        try {
            return Integer.parseInt(this.propertyValues.get(name));
        } catch (NumberFormatException e) {
            if (this.propertyValues.get(name) == null) {
                throw new IllegalArgumentException("Unable to find property " + name + " in addon " + this.addonName);
            } else {
                throw new IllegalArgumentException("Invalid id value for property " + name + " in addon " + this.addonName + ". Check for stray whitespace");
            }
        }
    }
}
