package btw.community.denovo.item.items;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Item;

public class WaterBowlItem extends Item {
    public WaterBowlItem(int itemID) {
        super(itemID);

        setUnlocalizedName("DNItem_water_bowl");
        setCreativeTab(CreativeTabs.tabMisc);
    }

}
