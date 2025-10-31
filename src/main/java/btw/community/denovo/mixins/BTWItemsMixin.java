package btw.community.denovo.mixins;


import btw.item.BTWItems;
import btw.item.items.PlaceAsBlockItem;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Mixin(BTWItems.class)
public class BTWItemsMixin {

    @Inject(method = "instantiateModItems", at = @At("RETURN"), remap = false)
    private static void onInstantiateModItems(CallbackInfo ci) {
        try {
            Item original = BTWItems.goldenDung;

            Item replacement = new PlaceAsBlockItem(original.itemID - 256, Block.deadBush.blockID)
                    .setUnlocalizedName("fcItemDungGolden")
                    .setTextureName("btw:golden_dung");

            // Reassign static final field using reflection
            Field field = BTWItems.class.getDeclaredField("goldenDung");
            field.setAccessible(true);
            field.set(null, replacement);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
