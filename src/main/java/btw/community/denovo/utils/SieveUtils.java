package btw.community.denovo.utils;

import btw.block.BTWBlocks;
import btw.community.denovo.item.DNItems;
import btw.item.BTWItems;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;

public class SieveUtils {
    public static boolean isValidHopperFilter(ItemStack item) {
        return isValidHopperFilter(item.itemID, item.getItemDamage());
    }

    public static boolean isValidHopperFilter(int id, int metadata) {
        return id == BTWItems.legacySlats.itemID
                || id == BTWItems.legacyGrate.itemID
                || id == BTWItems.legacyWickerPane.itemID
                || id == Block.slowSand.blockID
                || id == Block.ladder.blockID
                || id == Block.trapdoor.blockID
                || id == Block.fenceIron.blockID
                || id == BTWBlocks.slatsPane.blockID
                || id == BTWBlocks.gratePane.blockID
                || id == BTWBlocks.wickerPane.blockID
                || id == DNItems.mesh.itemID;
    }
}
