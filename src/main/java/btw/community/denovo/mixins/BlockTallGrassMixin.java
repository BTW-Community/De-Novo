package btw.community.denovo.mixins;

import btw.community.denovo.item.items.SickleItem;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockTallGrass.class)
public abstract class BlockTallGrassMixin extends BlockFlower{

    protected BlockTallGrassMixin(int par1, Material par2Material) {
        super(par1, par2Material);
    }

    @Inject(method = "harvestBlock", at = @At(value = "HEAD"), cancellable = true)
    public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int metadata, CallbackInfo ci) {
        if (!world.isRemote && player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof SickleItem) {
            this.dropBlockAsItem_do(world, x, y, z, new ItemStack(Block.tallGrass, 1, metadata));
        }
    }
}
