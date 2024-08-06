package btw.community.denovo.item.items;

import btw.item.items.ProgressiveCraftingItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class MaggotsSilkExtractionItem extends ProgressiveCraftingItem {
    public static final int TIME_TO_CRAFT = DEFAULT_MAX_DAMAGE / 2;
    public MaggotsSilkExtractionItem(int itemID, String name) {
        super(itemID);

        setBuoyant();

        setUnlocalizedName(name);
    }

    private ItemStack getReturnStack() {
        return new ItemStack(Item.silk, 1, 0);
    }

    protected int getProgressiveCraftingMaxDamage()
    {
        //Sock: default (120s) seemed to take way too long
        return this.TIME_TO_CRAFT;
    }

    @Override
    protected void playCraftingFX(ItemStack stack, World world, EntityPlayer player) {
        player.playSound("mob.slime.attack", 0.125F, (world.rand.nextFloat() * 0.1F + 0.9F)/20);
        //player.playSound("mob.slime.attack", 0.125F, (world.rand.nextFloat() * 0.1F + 0.9F)/20);
        //player.playSound("step.grass", 0.25F + 0.25F * (float) world.rand.nextInt(2), (world.rand.nextFloat() - world.rand.nextFloat()) * 0.25F + 1.75F);
    }

    @Override
    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
        player.playSound("mob.slime.small", 0.5F, world.rand.nextFloat() * 0.01F + 0.09F);
        return getReturnStack();
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        if (player.timesCraftedThisTick == 0 && world.isRemote) {
            player.playSound("mob.slime.attack", 0.5F, world.rand.nextFloat() * 0.01F + 0.09F);
        }

        super.onCreated(stack, world, player);
    }
}
