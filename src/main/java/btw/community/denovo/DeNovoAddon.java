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
    private static DeNovoAddon instance;

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
    }

    @Override
    public void handleConfigProperties(Map<String, String> propertyValues) {
        this.propertyValues = propertyValues;

        allowGoldenDungOnHCS = Boolean.parseBoolean(this.propertyValues.get("AllowGoldenDungOnHCS"));

    }

    public static DeNovoAddon getInstance() {
        if (instance == null)
            instance = new DeNovoAddon();
        return instance;
    }
}
