package btw.community.denovo.mixins;

import btw.community.denovo.block.DNBlocks;
import btw.community.denovo.block.tileentities.CisternBaseTileEntity;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Block.class)
public abstract class BlockMixin {
    @Inject(method = "isConsideredNeighbouringWaterForReedGrowthOn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/World;getBlockMaterial(III)Lnet/minecraft/src/Material;",
                    ordinal = 0,
                    shift = At.Shift.BEFORE
            ),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true)
    public void isConsideredNeighbouringWaterForReedGrowthOn(World world, int i, int j, int k, CallbackInfoReturnable<Boolean> cir, int iTempI, int iTempK) {
        if ( world.getBlockId( iTempI, j, iTempK ) == DNBlocks.cistern.blockID || world.getBlockId( iTempI, j, iTempK ) == DNBlocks.composter.blockID)
        {
            TileEntity tileEntity = world.getBlockTileEntity(iTempI, j, iTempK);
            CisternBaseTileEntity cisternBase = (CisternBaseTileEntity) tileEntity;
            if (cisternBase.isFullWithWater())
            {
                cir.setReturnValue(true);
            }

        }
    }
}
