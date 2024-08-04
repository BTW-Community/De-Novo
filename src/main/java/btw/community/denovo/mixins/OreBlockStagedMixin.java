package btw.community.denovo.mixins;

import btw.block.blocks.OreBlock;
import btw.block.blocks.OreBlockStaged;
import btw.community.denovo.item.items.HammerItem;
import btw.item.BTWItems;
import btw.item.util.ItemUtils;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(OreBlockStaged.class)
public abstract class OreBlockStagedMixin extends OreBlock {
    protected OreBlockStagedMixin(int iBlockID) {
        super(iBlockID);
    }

    @Inject(method = "getConversionLevelForTool", at = @At(value = "HEAD"), cancellable = true)
    public void getConversionLevelForTool(ItemStack heldStack, World world, int i, int j, int k, CallbackInfoReturnable<Integer> cir) {
        //hammer
        if (heldStack != null && heldStack.getItem() instanceof HammerItem) {
            cir.setReturnValue(-1);
        }
    }

    @Inject(method = "convertBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lbtw/block/blocks/OreBlockStaged;getConversionLevelForTool(Lnet/minecraft/src/ItemStack;Lnet/minecraft/src/World;III)I",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            ),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILHARD)
    public void convertBlock(ItemStack heldStack, World world, int x, int y, int z, int fromSide, CallbackInfoReturnable<Boolean> cir, int iOldMetadata, int iStrata) {
        //Hammer
        if (heldStack != null && heldStack.getItem() instanceof HammerItem) {
            if (iStrata == 0) {
                world.setBlockToAir(x, y, z);

                if (!world.isRemote) {
                    int numberOfPiles = 4;

                    ItemUtils.ejectStackFromBlockTowardsFacing(world, x, y, z,
                            new ItemStack(BTWItems.gravelPile, numberOfPiles), fromSide);
                }

                cir.setReturnValue(true);
            }

        }
    }
}
