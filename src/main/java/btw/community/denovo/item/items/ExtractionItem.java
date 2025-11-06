package btw.community.denovo.item.items;

import btw.item.items.ProgressiveCraftingItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class ExtractionItem extends ProgressiveCraftingItem {
    public static final int TIME_TO_CRAFT_SILK = DEFAULT_MAX_DAMAGE / 2;
    public static final int TIME_TO_CRAFT_WATER = DEFAULT_MAX_DAMAGE;
    private final int timeToCraft;
    private final ItemStack returnItem;
    private final String soundCrafting;
    private final String soundEaten;
    private final String soundCreated;

    public ExtractionItem(int itemID, String name, int timeToCraft, ItemStack returnItem,
                          String soundCrafting, String soundEaten, String soundCreated) {
        super(itemID);

        setBuoyant();

        setUnlocalizedName(name);

        this.timeToCraft = timeToCraft;
        this.returnItem = returnItem;
        this.soundCrafting = soundCrafting;
        this.soundEaten = soundEaten;
        this.soundCreated = soundCreated;
    }

    private ItemStack getReturnStack() {
        return returnItem;
    }

    protected int getProgressiveCraftingMaxDamage() {
        //Sock: default (120s) seemed to take way too long
        return this.timeToCraft;
    }

    @Override
    protected void playCraftingFX(ItemStack stack, World world, EntityPlayer player) {
        player.playSound(this.soundCrafting, 0.125F, (world.rand.nextFloat() * 0.1F + 0.9F) / 20);
        //player.playSound("mob.slime.attack", 0.125F, (world.rand.nextFloat() * 0.1F + 0.9F)/20);
        //player.playSound("step.grass", 0.25F + 0.25F * (float) world.rand.nextInt(2), (world.rand.nextFloat() - world.rand.nextFloat()) * 0.25F + 1.75F);
    }

    @Override
    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
        player.playSound(this.soundEaten, 0.5F, world.rand.nextFloat() * 0.01F + 0.09F);
        return getReturnStack();
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        if (player.timesCraftedThisTick == 0 && world.isRemote) {
            player.playSound(this.soundCreated, 0.5F, world.rand.nextFloat() * 0.01F + 0.09F);
        }

        super.onCreated(stack, world, player);
    }
}
