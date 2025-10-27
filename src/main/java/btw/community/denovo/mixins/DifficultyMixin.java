package btw.community.denovo.mixins;

import btw.community.denovo.DeNovoAddon;
import btw.world.util.difficulty.Difficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Difficulty.class)
public class DifficultyMixin {
    @Inject(method = "hasHardcoreSpawn", at = @At(value = "HEAD"), cancellable = true, remap = false)
    public void disableHCSpawn(CallbackInfoReturnable<Boolean> cir){
        if (DeNovoAddon.disableHCSpawn){
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
