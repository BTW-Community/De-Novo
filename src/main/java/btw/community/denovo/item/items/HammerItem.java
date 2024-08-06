package btw.community.denovo.item.items;

import btw.item.items.PickaxeItem;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EnumToolMaterial;

public class HammerItem extends PickaxeItem {
    public HammerItem(int itemID, EnumToolMaterial material, int maxUses) {
        super( itemID, material, maxUses);

        setMaxStackSize(1);
        setMaxDamage( maxUses );

        setUnlocalizedName("DNItem_flint_hammer");
        setCreativeTab(CreativeTabs.tabTools);
    }
}
