package btw.community.denovo.mixins;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockGrass.class)
public abstract class GrassBlockMixin extends Block {
    public GrassBlockMixin(int blockID) {
        super(blockID, Material.grass);
    }

    protected float getPlacingChance() {
        return 0.25F;
    }

    @Inject(method = "convertBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/World;setBlockWithNotify(IIII)Z",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            ),
            cancellable = true)
    public void disableDroppingHempSeeds(ItemStack heldStack, World world, int x, int y, int z, int fromSide, CallbackInfoReturnable<Boolean> cir) {

        if (heldStack != null && heldStack.itemID == Item.hoeStone.itemID) {
            cir.setReturnValue(true);
        }
    }

    @Override
    public boolean onBlockActivated(World world, int xCoord, int yCoord, int zCoord, EntityPlayer player, int facing, float xClick, float yClick, float zClick) {
        if (player.getHeldItem() == null && player.isSneaking()) {

            player.foodStats.addExhaustion(1F);

            if (!world.isRemote && world.rand.nextFloat() <= getPlacingChance()) {
                Item deadBush = Item.itemsList[Block.deadBush.blockID];
                deadBush.onItemUse(new ItemStack(deadBush), player, world, xCoord, yCoord, zCoord, facing, xClick, yClick, zClick);
                world.setBlockMetadataWithNotify(xCoord, yCoord + 1, zCoord, 3);
            }

            if (world.isRemote) {
                //spawnParticles(world, player, xCoord, yCoord + 1, zCoord);

                world.playAuxSFX(2001, xCoord, yCoord, zCoord, Block.dirt.blockID); //break sfx
                world.playSound((double) xCoord + 0.5D, yCoord, (double) zCoord + 0.5D, Block.dirt.stepSound.getStepSound(),
                        0.5F, //Volume
                        1.0F); //Pitch
            }

            return true;
        }

        return super.onBlockActivated(world, xCoord, yCoord, zCoord, player, facing, xClick, yClick, zClick);
    }

    private void spawnParticles(World world, EntityPlayer player, int xCoord, int yCoord, int zCoord) {

        for (int i = 0; i < 3; ++i) {
            world.spawnParticle("happyVillager",
                    (double) xCoord + player.rand.nextFloat(),
                    (double) yCoord + player.rand.nextFloat(),
                    (double) zCoord + player.rand.nextFloat(),
                    player.rand.nextGaussian() * 0.02,
                    player.rand.nextGaussian() * 0.02,
                    player.rand.nextGaussian() * 0.02
            );
        }
    }
}
