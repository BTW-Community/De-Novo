package btw.community.denovo;

import btw.AddonHandler;
import btw.BTWAddon;
import btw.community.denovo.block.DNBlocks;
import btw.community.denovo.item.DNItems;
import btw.community.denovo.particles.WaterSplashFX;
import btw.community.denovo.recipes.DNRecipes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.EntityFX;
import net.minecraft.src.EntityList;
import net.minecraft.src.World;

import java.util.Map;

public class DeNovoAddon extends BTWAddon {
    public static DeNovoAddon instance;

    private Map<String, String> propertyValues;

    // configuration settings
    /**
     * Set the following to True to allow players to get a piece of golden dung on every HCS
     */
    public static boolean allowGoldenDungOnHCS = false;

    /**
     * Set the following to True to disable all Slime spawning in flat worlds
     */
    public static boolean disableSlimeSpawningInFlatWorlds = false;

    /**
     * Set the following to True to disable Slime Spawning specifically on Grass Blocks in slime chunks in flat worlds
     */
    public static boolean limitSlimeSpawningInFlatWorlds = false;

    /**
     * Set the following to True to disable mob spawns on blocks with sky access
     */
    public static boolean disableMobSpawnsOnSurface = false;

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

        // Client only
        if (!MinecraftServer.getIsServer()) {
            EntityList.addMapping(WaterSplashFX.class, "DNWaterSplashFX", -50);
        }
    }

    @Override
    public EntityFX spawnCustomParticle(World world, String particleType, double x, double y, double z, double velX, double velY, double velZ) {

        if (particleType.startsWith("DNSplash_")) {
            String[] colors = particleType.split("_", 4);
            int red = Integer.parseInt(colors[1]);
            int green = Integer.parseInt(colors[2]);
            int blue = Integer.parseInt(colors[3]);

            return (EntityFX) EntityList.createEntityOfType(WaterSplashFX.class, world, x, y, z, velX, velY, velZ, red, green, blue);
        }

        return super.spawnCustomParticle(world, particleType, x, y, z, velX, velY, velZ);
    }

    private void registerConfigProperties() {
        //Gameplay config
        this.registerProperty("AllowGoldenDungOnHCS", "False", "Set the following to True to allow players to get a piece of golden dung on every HCS");
        this.registerProperty("DisableSlimeSpawningInFlatWorlds", "False", "Set the following to True to disable all Slime spawning in flat worlds");
        this.registerProperty("LimitSlimeSpawningInFlatWorlds", "False", "Set the following to True to disable Slime Spawning specifically on Grass Blocks in slime chunks in flat worlds");
        this.registerProperty("DiableMobSpawnsOnSurface", "False", "Set the following to True to disable mob spawns on blocks with sky access");

        //Block IDs
        this.registerProperty("DNBlockSieveID", "3900", "***Block IDs***\n\n");
        this.registerProperty("DNBlockComposterID", "3901");
        this.registerProperty("DNBlockCisternID", "3902");
        this.registerProperty("DNBlockPlacedSticksID", "3903");
        this.registerProperty("DNBlockSmolderingPlacedSticksID", "3904");
        this.registerProperty("DNBlockCharcoalPileID", "3905");

        //Item IDs
        this.registerProperty("DNItemMeshID", "23000", "***Item IDs***\n\n");
        this.registerProperty("DNItemRawMaggotsID", "23001");
        this.registerProperty("DNItemCookedMaggotsID", "23002");
        this.registerProperty("DNItemWaterBowlID", "23003");
        this.registerProperty("DNItemCisternID", "23004");
        this.registerProperty("DNItemMaggotsSilkExtractionID", "23005");
        this.registerProperty("DNItemCharcoalDustID", "23006");
        this.registerProperty("DNItemRustWaterBowlID", "23007");
        this.registerProperty("DNItemIronDustID", "23008");
        this.registerProperty("DNItemWoodSickleID", "23010");
        this.registerProperty("DNItemFlintHammerID", "23011");

    }

    @Override
    public void handleConfigProperties(Map<String, String> propertyValues) {
        this.propertyValues = propertyValues;

        allowGoldenDungOnHCS = Boolean.parseBoolean(this.propertyValues.get("AllowGoldenDungOnHCS"));
        disableSlimeSpawningInFlatWorlds = Boolean.parseBoolean(this.propertyValues.get("DisableSlimeSpawningInFlatWorlds"));
        limitSlimeSpawningInFlatWorlds = Boolean.parseBoolean(this.propertyValues.get("LimitSlimeSpawningInFlatWorlds"));
        disableMobSpawnsOnSurface = Boolean.parseBoolean(this.propertyValues.get("DisableMobSpawnsOnSurface"));
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
