package btw.community.denovo.item.items;

import btw.block.BTWBlocks;
import btw.item.items.PickaxeItem;
import btw.item.items.ToolItem;
import net.minecraft.src.*;

public class HammerItem extends PickaxeItem {
    public HammerItem(int itemID, EnumToolMaterial material, int maxUses) {
        super( itemID, material, maxUses);

        setMaxStackSize(1);
        setMaxDamage( maxUses );

        setUnlocalizedName("DNItem_flint_hammer");
        setCreativeTab(CreativeTabs.tabTools);
    }
}
