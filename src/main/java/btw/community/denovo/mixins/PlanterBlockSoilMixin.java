package btw.community.denovo.mixins;

import btw.block.blocks.FarmlandBlockBase;
import btw.block.blocks.PlanterBlockSoil;
import btw.community.denovo.block.blocks.CisternBaseBlock;
import btw.community.denovo.block.tileentities.CisternBaseTileEntity;
import net.minecraft.src.Block;
import net.minecraft.src.Material;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlanterBlockSoil.class)
public class PlanterBlockSoilMixin {
    @Inject(method = "hasIrrigatingBlocks", at = @At(value = "HEAD"), cancellable = true)
    public void isConcideredIrrigationBlock(World world, int i, int j, int k, CallbackInfoReturnable<Boolean> cir) {

        if ( isFullWaterCistern( world, i, j - 1, k ) ||
                isFullWaterCistern( world, i, j + 1, k ) ||
                isFullWaterCistern( world, i, j, k - 1 ) ||
                isFullWaterCistern( world, i, j, k + 1 ) ||
                isFullWaterCistern( world, i - 1, j, k ) ||
                isFullWaterCistern( world, i + 1, j, k ) )
        {
            cir.setReturnValue(true);
        }
    }

    private boolean isFullWaterCistern(World world, int i, int j, int k){
        CisternBaseTileEntity cisternBase = (CisternBaseTileEntity) world.getBlockTileEntity(i,j,k);
        boolean isCisternBase = Block.blocksList[world.getBlockId( i, j, k )] instanceof CisternBaseBlock;

        return isCisternBase && cisternBase.isFullWithWater();
    }
}
