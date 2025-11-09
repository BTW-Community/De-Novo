package btw.community.denovo.mixins;

import btw.community.denovo.item.items.HammerItem;
import btw.item.BTWItems;
import btw.item.util.ItemUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockLog.class)
public abstract class BlockLogMixin {

    @Shadow public abstract boolean getIsStump(IBlockAccess blockAccess, int i, int j, int k);

    @Inject(method = "canConvertBlock", at = @At(value = "HEAD"), cancellable = true)
    public void dropExtraItems(ItemStack stack, World world, int i, int j, int k, CallbackInfoReturnable<Boolean> cir) {

        if (stack != null && stack.getItem() instanceof HammerItem && this.getIsStump(world, i,j,k)) {
            if (!world.isRemote) ItemUtils.ejectStackWithRandomOffset(world, i, j, k,  new ItemStack(BTWItems.sawDust, 4)); //6 already get dropped

            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
