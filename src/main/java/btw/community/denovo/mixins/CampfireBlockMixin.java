package btw.community.denovo.mixins;

import btw.block.BTWBlocks;
import btw.block.blocks.CampfireBlock;
import btw.community.denovo.block.DNBlocks;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(CampfireBlock.class
)
public abstract class CampfireBlockMixin extends BlockContainer {

    protected CampfireBlockMixin(int par1, Material par2Material) {
        super(par1, par2Material);
    }

    @Inject(method = "setOnFireDirectly",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/World;getBlockId(III)I",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            ))
    public void placeFire(World world, int i, int j, int k, CallbackInfoReturnable<Boolean> cir) {
        int iBlockBelowID = world.getBlockId( i, j - 1, k );

        if ( iBlockBelowID == DNBlocks.placedSticks.blockID )
        {
            world.setBlockWithNotify( i, j, k, Block.fire.blockID );
        }
    }

}
