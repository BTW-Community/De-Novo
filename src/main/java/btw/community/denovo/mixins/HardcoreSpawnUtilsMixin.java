package btw.community.denovo.mixins;

import btw.community.denovo.DeNovoAddon;
import btw.item.BTWItems;
import btw.util.hardcorespawn.HardcoreSpawnUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Random;

@Mixin(HardcoreSpawnUtils.class)
public class HardcoreSpawnUtilsMixin {
    @Inject(method = "assignNewHardcoreSpawnLocation", at = @At(value = "TAIL"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private static void givePlayerGoldenDung(World world, MinecraftServer server, EntityPlayerMP player, CallbackInfoReturnable<Boolean> cir, boolean locationFound) {
        if (locationFound && DeNovoAddon.allowGoldenDungOnHCS)
        {
            player.inventory.addItemStackToInventory(new ItemStack(BTWItems.goldenDung));
        }


    }
}