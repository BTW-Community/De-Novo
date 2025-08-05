package btw.community.denovo.mixins;

import btw.community.denovo.DeNovoAddon;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLiving.class)
public abstract class EntityLivingMixin extends Entity {

    public EntityLivingMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "getCanSpawnHere", at = @At(value = "HEAD"), cancellable = true)
    public void getCanSpawnHere(CallbackInfoReturnable<Boolean> cir) {
        boolean canBlockSeeTheSky = this.worldObj.canBlockSeeTheSky((int) this.posX, (int) this.posY, (int) this.posZ);

        int blockAboveMaxNaturalLight = worldObj.getBlockNaturalLightValueMaximum((int) posX, (int) posY + 1, (int) posZ);
        int blockAboveCurrentNaturalLight = blockAboveMaxNaturalLight - worldObj.skylightSubtracted;
        boolean isUnderground = blockAboveCurrentNaturalLight <= 0 && worldObj.getBlockNaturalLightValue((int) posX, (int) posY + 1, (int) posZ) == 0;

        //prevents spawns if we can see the sky or we are not underground
        if (DeNovoAddon.disableMobSpawnsOnSurface && (canBlockSeeTheSky || !isUnderground)) {
            cir.setReturnValue(false);
        }
    }
}
