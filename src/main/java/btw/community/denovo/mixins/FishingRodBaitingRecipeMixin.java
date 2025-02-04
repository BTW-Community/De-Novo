package btw.community.denovo.mixins;

import btw.community.denovo.item.DNItems;
import btw.crafting.recipe.types.customcrafting.FishingRodBaitingRecipe;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FishingRodBaitingRecipe.class)
public abstract class FishingRodBaitingRecipeMixin {

    @Inject(method = "isFishingBait", at = @At(value = "HEAD"), cancellable = true)
    private static void isFishingBait(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        int itemID = stack.itemID;
        if (itemID == DNItems.rawMaggots.itemID) {
            cir.setReturnValue(true);
        }
    }
}
