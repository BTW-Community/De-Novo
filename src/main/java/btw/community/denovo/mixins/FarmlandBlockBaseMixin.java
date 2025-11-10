package btw.community.denovo.mixins;

import btw.block.blocks.FarmlandBlockBase;
import btw.community.denovo.block.blocks.CisternBaseBlock;
import btw.community.denovo.block.tileentities.CisternBaseTileEntity;
import btw.community.denovo.block.tileentities.ComposterTileEntity;
import net.minecraft.src.Block;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FarmlandBlockBase.class)
public class FarmlandBlockBaseMixin {

    @Inject(method = "hasIrrigatingBlocks", at = @At(value = "HEAD"), cancellable = true)
    public void isConcideredIrrigationBlock(World world, int i, int j, int k, CallbackInfoReturnable<Boolean> cir) {
        int iHorizontalRange = 2;

        for (int iTempI = i - iHorizontalRange; iTempI <= i + iHorizontalRange; iTempI++) {
            for (int iTempJ = j; iTempJ <= j + 1; iTempJ++) {
                for (int iTempK = k - iHorizontalRange; iTempK <= k + iHorizontalRange; iTempK++) {
                    TileEntity tileEntity  =  world.getBlockTileEntity(iTempI, iTempJ, iTempK);

                    if (tileEntity instanceof ComposterTileEntity composter) {
                        if (composter.isFullWithWater()) {
                            cir.setReturnValue(true);
                        }
                    }
                }
            }
        }
    }


}
