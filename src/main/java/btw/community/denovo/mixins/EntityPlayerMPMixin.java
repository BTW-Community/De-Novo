package btw.community.denovo.mixins;

import btw.item.BTWItems;
import net.minecraft.src.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerMP.class)
public abstract class EntityPlayerMPMixin {

    @Inject(method = "updateGloomState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/EntityPlayerMP;attackEntityFrom(Lnet/minecraft/src/DamageSource;I)Z",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            ),remap = false)
    public void makePlayerPoop(CallbackInfo ci){
        EntityPlayerMP thisPlayer = (EntityPlayerMP)(Object)this;

        if (thisPlayer.rand.nextInt(2) == 0) {
            thisPlayer.dropItem(BTWItems.dung.itemID, 1);

            thisPlayer.worldObj.playSoundAtEntity( thisPlayer, "random.explode", 0.2F, 1.25F );
            thisPlayer.worldObj.playSoundAtEntity(thisPlayer, "mob.wolf.growl", 0.5F, (thisPlayer.rand.nextFloat() - thisPlayer.rand.nextFloat()) * 0.2F + 1.0F);
        }
    }
}
