package btw.community.denovo.mixins;

import btw.block.blocks.RoughStoneBlock;
import btw.community.denovo.item.items.HammerItem;
import btw.item.BTWItems;
import btw.item.util.ItemUtils;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RoughStoneBlock.class)
public abstract class RoughStoneBlockMixin {

    @Inject(method = "onBlockDestroyedWithImproperTool", at = @At(value = "HEAD"), cancellable = true)
    public void onBlockDestroyedWithImproperTool(World world, EntityPlayer player, int i, int j, int k, int iMetadata, CallbackInfo ci) {

        ItemStack heldStack = player.getHeldItem();
        //hammer
        if (heldStack != null && heldStack.getItem() instanceof HammerItem) {
            this.dropComponentItemsWithChance(world, i, j, k);
            ci.cancel();
        }
    }

    private void dropComponentItemsWithChance(World world, int i, int j, int k) {
        ItemUtils.ejectStackWithRandomOffset(world, i, j, k,
                new ItemStack(BTWItems.gravelPile, 2));
    }
}
