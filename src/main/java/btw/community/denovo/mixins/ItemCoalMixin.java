package btw.community.denovo.mixins;

import net.minecraft.src.Icon;
import net.minecraft.src.IconRegister;
import net.minecraft.src.Item;
import net.minecraft.src.ItemCoal;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemCoal.class)
public abstract class ItemCoalMixin extends Item {
    public ItemCoalMixin(int itemID) {
        super(itemID);
    }

    private Icon charcoal;

    @Override
    public void registerIcons(IconRegister register) {
        super.registerIcons(register);
        charcoal = register.registerIcon("charcoal");
    }

    @Override
    public Icon getIconFromDamage(int damage) {
        if (damage == 1) return charcoal;
        return super.getIconFromDamage(damage);
    }
}
