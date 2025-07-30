package btw.community.denovo.mixins;

import btw.client.fx.BTWEffectManager;
import btw.item.BTWItems;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerMP.class)
public abstract class EntityPlayerMPMixin extends EntityPlayer {
    public EntityPlayerMPMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "updateGloomState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/EntityPlayerMP;attackEntityFrom(Lnet/minecraft/src/DamageSource;I)Z",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            ))
    public void makePlayerPoop(CallbackInfo ci){
        this.dropItem(BTWItems.dung.itemID, 1);

        worldObj.playSoundAtEntity( this, "random.explode", 0.2F, 1.25F );

        worldObj.playSoundAtEntity(this, "mob.wolf.growl", getSoundVolume(), (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);

    }
}
