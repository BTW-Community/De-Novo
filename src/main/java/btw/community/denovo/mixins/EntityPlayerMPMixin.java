package btw.community.denovo.mixins;

import btw.item.BTWItems;
import net.minecraft.src.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerMP.class)
public abstract class EntityPlayerMPMixin {

    @Unique
    private float prevHealth;

    @Inject(method = "updateGloomState", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/EntityPlayerMP;attackEntityFrom(Lnet/minecraft/src/DamageSource;F)Z",
            shift = At.Shift.BEFORE
    ))
    private void beforeAttack(CallbackInfo ci) {
        this.prevHealth = ((EntityPlayerMP)(Object)this).getHealth();
    }

    @Inject(method = "updateGloomState", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/EntityPlayerMP;attackEntityFrom(Lnet/minecraft/src/DamageSource;F)Z",
            shift = At.Shift.AFTER
    ))
    private void afterAttack(CallbackInfo ci) {
        EntityPlayerMP thisPlayer = (EntityPlayerMP)(Object)this;

        if (thisPlayer.getHealth() < this.prevHealth && thisPlayer.rand.nextInt(2) == 0) {
            thisPlayer.dropItem(BTWItems.dung.itemID, 1);
            thisPlayer.worldObj.playSoundAtEntity(thisPlayer, "random.explode", 0.2F, 1.25F);
            thisPlayer.worldObj.playSoundAtEntity(thisPlayer, "mob.wolf.growl",
                    0.5F, (thisPlayer.rand.nextFloat() - thisPlayer.rand.nextFloat()) * 0.2F + 1.0F);
        }
    }
}
