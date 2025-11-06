package btw.community.denovo.mixins;

import btw.community.denovo.block.DNBlocks;
import btw.community.denovo.block.tileentities.CisternBaseTileEntity;
import net.minecraft.src.BlockCactus;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockCactus.class)
public abstract class BlockCactusMixin {

    @Inject(method = "canPlaceBlockAt", at = @At("RETURN"), cancellable = true)
    private void canPlaceBlockAt(World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {

        if (!cir.getReturnValue()) {
            int blockBelowID = world.getBlockId(x, y - 1, z);
            CisternBaseTileEntity cisternBase = (CisternBaseTileEntity) world.getBlockTileEntity(x, y - 1, z);

            if (cisternBase != null){
                if (blockBelowID == DNBlocks.composter.blockID && cisternBase.isFullWithSand()) {
                    cir.setReturnValue(true);
                }
            }
        }
    }

    @Inject(method = "canBlockStay", at = @At("RETURN"), cancellable = true)
    private void canBlockStay(World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {

        if (!cir.getReturnValue()) {
            int blockBelowID = world.getBlockId(x, y - 1, z);
            CisternBaseTileEntity cisternBase = (CisternBaseTileEntity) world.getBlockTileEntity(x, y - 1, z);

            if (cisternBase != null){
                if (blockBelowID == DNBlocks.composter.blockID && cisternBase.isFullWithSand()) {
                    cir.setReturnValue(true);
                }
            }
        }
    }
}