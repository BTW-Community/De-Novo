package btw.community.denovo;

import btw.AddonHandler;
import btw.BTWAddon;
import btw.community.denovo.block.DNBlocks;
import btw.community.denovo.item.DNItems;
import btw.community.denovo.recipes.DNRecipes;
import com.prupe.mcpatcher.mal.block.RenderBlocksUtils;
import net.minecraft.server.MinecraftServer;

import java.util.Map;

public class DeNovoAddon extends BTWAddon {
    public static final DeNovoAddon instance = new DeNovoAddon();

    private Map<String, String> propertyValues;

    // configuration settings
    public static boolean allowGoldenDungOnHCS = false;

    private DeNovoAddon() {
        super("De Novo", "0.1.0", "DN");
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
        this.registerProperty("DNSieveID", "3900", "***Block IDs***\n\n");

        //Item IDs
        this.registerProperty("DNMeshID", "23000", "***Item IDs***\n\n");
    }

    @Override
    public void handleConfigProperties(Map<String, String> propertyValues) {
        this.propertyValues = propertyValues;

        allowGoldenDungOnHCS = Boolean.parseBoolean(this.propertyValues.get("AllowGoldenDungOnHCS"));
    }

    public int parseID(String name) {
        try {
            return Integer.parseInt(this.propertyValues.get(name));
        }
        catch (NumberFormatException e) {
            if (this.propertyValues.get(name) == null) {
                throw new IllegalArgumentException("Unable to find property " + name + " in addon " + this.addonName);
            }
            else {
                throw new IllegalArgumentException("Invalid id value for property " + name + " in addon " + this.addonName + ". Check for stray whitespace");
            }
        }
    }
}
