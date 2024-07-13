package btw.community.denovo.utils;

import btw.block.BTWBlocks;
import btw.community.denovo.item.DNItems;
import btw.item.BTWItems;
import net.minecraft.src.Block;
import net.minecraft.src.Icon;
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

    public static Icon getBulkIcon(ItemStack item) {
        return getBulkIcon(item.itemID, item.getItemDamage());
    }

    public static Icon getBulkIcon(int id, int metadata) {
        {
            Icon icon = BTWBlocks.looseDirt.blockIcon;
            if (id == BTWBlocks.looseDirt.blockID) return icon;
            if (id == Block.dirt.blockID) return icon;
            if (id == BTWItems.dirtPile.itemID) return icon;
        }

        {
            Icon icon = Block.gravel.blockIcon;
            if (id == Block.gravel.blockID) return icon;
            if (id == BTWItems.gravelPile.itemID) return icon;
        }

        {
            Icon icon = Block.sand.blockIcon;
            if (id == Block.sand.blockID) return icon;
            if (id == BTWItems.sandPile.itemID) return icon;
        }

        {
            Icon icon = Block.slowSand.blockIcon;
            if (id == Block.slowSand.blockID) return icon;
            if (id == BTWItems.soulSandPile.itemID) return icon;
        }

        {
            Icon icon = BTWBlocks.looseCobblestone.blockIcon;
            if (id == BTWBlocks.looseCobblestone.blockID) return icon;
        }

        {
            Icon icon = BTWBlocks.aestheticEarth.getIcon(0, 7);
            if (id == BTWBlocks.aestheticEarth.blockID && metadata == 7) return icon;
            if (id == BTWItems.dung.itemID) return icon;
        }

        return Block.gravel.blockIcon;
    }
}
