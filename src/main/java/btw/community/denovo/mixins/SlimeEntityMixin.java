package btw.community.denovo.mixins;

import btw.community.denovo.DeNovoAddon;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntitySlime;
import net.minecraft.src.WorldType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntitySlime.class)
public class SlimeEntityMixin {

    @Inject(method = "getCanSpawnHere", at = @At(value = "HEAD"), cancellable = true)
    public void canSlimesSpawnInFlatWorlds(CallbackInfoReturnable<Boolean> cir) {
        Entity thisObject = (Entity) (Object) this;

        if (thisObject.worldObj.getWorldInfo().getTerrainType() == WorldType.FLAT) {
            if (DeNovoAddon.disableSlimeSpawningInFlatWorlds) cir.setReturnValue(false);
        }
    }

    @Inject(method = "canSpawnOnBlockInSlimeChunk", at = @At(value = "HEAD"), remap = false, cancellable = true)
    public void limitSlimeSpawningInSlimeChunk(int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        Entity thisObject = (Entity) (Object) this;

        int iBlockID = thisObject.worldObj.getBlockId(x, y, z);

        if (thisObject.worldObj.getWorldInfo().getTerrainType() == WorldType.FLAT && iBlockID == Block.grass.blockID) {
            if (DeNovoAddon.limitSlimeSpawningInFlatWorlds) cir.setReturnValue(false);
        }
    }


}
