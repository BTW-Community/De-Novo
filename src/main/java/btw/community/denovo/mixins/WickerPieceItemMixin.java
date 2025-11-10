package btw.community.denovo.mixins;

import btw.block.BTWBlocks;
import btw.item.items.WickerPieceItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Icon;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WickerPieceItem.class)
public class WickerPieceItemMixin {

    @Environment(value= EnvType.CLIENT)
    public Icon getHopperFilterIcon() {
        return BTWBlocks.wickerPane.getHopperFilterIcon();
    }
}
