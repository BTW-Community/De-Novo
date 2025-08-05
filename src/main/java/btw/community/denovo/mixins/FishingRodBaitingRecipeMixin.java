package btw.community.denovo.mixins;

import btw.community.denovo.item.DNItems;
import btw.crafting.recipe.types.customcrafting.FishingRodBaitingRecipe;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(FishingRodBaitingRecipe.class)
public abstract class FishingRodBaitingRecipeMixin {

//    @Inject(method = "isFishingBait", at = @At(value = "HEAD"), cancellable = true)
//    private static void isFishingBait(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
//        int itemID = stack.itemID;
//        if (itemID == DNItems.rawMaggots.itemID) {
//            cir.setReturnValue(true);
//        }
//    }

    @Inject(method = "fishingBaits", at = @At("RETURN"), cancellable = true, remap = false)
    private static void onFishingBaitsReturn(CallbackInfoReturnable<List<ItemStack>> cir) {
        List<ItemStack> baits = cir.getReturnValue();
        baits.add(new ItemStack(DNItems.rawMaggots));
        cir.setReturnValue(baits);
    }
}
