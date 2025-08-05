package btw.community.denovo.item.items;

import btw.community.denovo.item.DNItems;
import btw.item.BTWItems;
import btw.item.items.ProgressiveCraftingItem;
import btw.item.util.ItemUtils;
import net.minecraft.src.*;

public class RustWaterBowlItem extends ProgressiveCraftingItem {
    public static final int TIME_TO_CRAFT = DEFAULT_MAX_DAMAGE/64;
    public RustWaterBowlItem(int itemID, String name) {
        super(itemID);

        setBuoyant();

        setUnlocalizedName(name);
    }

    private ItemStack getReturnStack() {
        return new ItemStack(DNItems.ironDust, 1, 0);
    }

    protected int getProgressiveCraftingMaxDamage()
    {
        return this.TIME_TO_CRAFT;
    }

    @Override
    protected void playCraftingFX(ItemStack stack, World world, EntityPlayer player) {
        player.playSound("step.gravel", 0.125F, (world.rand.nextFloat() * 0.1F + 0.9F)/20);
        //player.playSound("mob.slime.attack", 0.125F, (world.rand.nextFloat() * 0.1F + 0.9F)/20);
        //player.playSound("step.grass", 0.25F + 0.25F * (float) world.rand.nextInt(2), (world.rand.nextFloat() - world.rand.nextFloat()) * 0.25F + 1.75F);
    }

    @Override
    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
        player.playSound("step.gravel", 0.5F, world.rand.nextFloat() * 0.01F + 0.09F);
        ItemUtils.givePlayerStackOrEject(player, new ItemStack(Item.bowlEmpty));
        return getReturnStack();
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        if (player.timesCraftedThisTick == 0 && world.isRemote) {
            player.playSound("step.gravel", 0.5F, world.rand.nextFloat() * 0.01F + 0.09F);
        }

        super.onCreated(stack, world, player);
    }

}
