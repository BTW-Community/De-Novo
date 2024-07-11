package btw.community.denovo;

import btw.AddonHandler;
import btw.BTWAddon;
import btw.community.denovo.block.DNBlocks;
import btw.community.denovo.item.DNItems;
import btw.community.denovo.recipes.DNRecipes;

public class DeNovoAddon extends BTWAddon {
    private static DeNovoAddon instance;

    private DeNovoAddon() {
        super("De Novo", "0.1.0", "DN");
    }

    @Override
    public void initialize() {
        AddonHandler.logMessage(this.getName() + " Version " + this.getVersionString() + " Initializing...");

        DNBlocks.initBlocks();
        DNItems.initItems();

        DNRecipes.addRecipes();
    }

    public static DeNovoAddon getInstance() {
        if (instance == null)
            instance = new DeNovoAddon();
        return instance;
    }
}
