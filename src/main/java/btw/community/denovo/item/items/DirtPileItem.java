package btw.community.denovo.item.items;

import btw.item.items.PlaceAsBlockItem;
import net.minecraft.src.*;

public class DirtPileItem extends PlaceAsBlockItem {
    public DirtPileItem(int itemID, int blockID) {
        super(itemID, blockID);

        setBellowsBlowDistance(1);
        setFilterableProperties(FILTERABLE_FINE);

        setUnlocalizedName("fcItemPileDirt");

        setCreativeTab(CreativeTabs.tabMaterials);
    }

    protected float getPlacingChance() {
        return 0.25F;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int facing, float clickX, float clickY, float clickZ) {
        if (world.isRemote || world.getBlockId(x, y, z) != Block.grass.blockID) {
            return false;
        }

        if (world.rand.nextFloat() <= getPlacingChance()) {
            return super.onItemUse(stack, player, world, x, y, z, facing, clickX, clickY, clickZ);
        } else {
            stack.stackSize--;
            return false;
        }
    }
}