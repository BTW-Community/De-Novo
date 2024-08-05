package btw.community.denovo.utils;

import btw.community.denovo.block.tileentities.CisternBaseTileEntity;
import btw.community.denovo.block.tileentities.CisternTileEntity;
import btw.community.denovo.block.tileentities.ComposterTileEntity;
import btw.community.denovo.item.DNItems;
import btw.item.util.ItemUtils;
import net.minecraft.src.*;

public class CisternUtils {

    public static boolean isValidEmptyContainer(ItemStack stack) {
        return stack.isItemEqual(new ItemStack(Item.bowlEmpty)) || stack.isItemEqual(new ItemStack(Item.glassBottle)) || stack.isItemEqual(new ItemStack(Item.bucketEmpty));
    }

    public static boolean isValidWaterContainer(ItemStack stack) {
        return stack.isItemEqual(new ItemStack(DNItems.waterBowl)) || stack.isItemEqual(new ItemStack(Item.potion, 1, 0)) || stack.isItemEqual(new ItemStack(Item.bucketWater));
    }

    public static ItemStack getFullWaterContainerForEmptyContainer(ItemStack stack) {
        if (stack.isItemEqual(new ItemStack(Item.bowlEmpty))) return new ItemStack(DNItems.waterBowl);
        if (stack.isItemEqual(new ItemStack(Item.glassBottle))) return new ItemStack(Item.potion, 1, 0);
        if (stack.isItemEqual(new ItemStack(Item.bucketEmpty))) return new ItemStack(Item.bucketWater);
        return null;
    }

    public static ItemStack getEmptyContainerForFullWaterContainer(ItemStack stack) {
        if (stack.isItemEqual(new ItemStack(DNItems.waterBowl))) return new ItemStack(Item.bowlEmpty);
        if (stack.isItemEqual(new ItemStack(Item.potion, 1, 0))) return new ItemStack(Item.glassBottle);
        if (stack.isItemEqual(new ItemStack(Item.bucketWater))) return new ItemStack(Item.bucketEmpty);
        return null;
    }

    public static boolean reduceWaterAndReturnContainer(World world, int x, int y, int z, int facing, EntityPlayer player, TileEntity tileEntity, ItemStack heldStack){
        if (tileEntity instanceof CisternTileEntity)
        {
            CisternTileEntity cistern = (CisternTileEntity) tileEntity;
            if (cistern.getFillType() == CisternTileEntity.CONTENTS_WATER)
            {
                if (cistern.isFullWithWater())
                {
                    if (heldStack.itemID == Item.bucketEmpty.itemID)
                    {
                        cistern.removeWater(3);
                    }
                    else {
                        cistern.removeWater(1);
                    }

                    return exchangeContainers(world, x, y, z, facing, player, heldStack, getFullWaterContainerForEmptyContainer(heldStack));
                }
                else if (!cistern.isEmpty())
                {
                    if (heldStack.itemID == Item.bucketEmpty.itemID) return false;

                    cistern.removeWater(1);
                    return exchangeContainers(world, x, y, z, facing, player, heldStack, getFullWaterContainerForEmptyContainer(heldStack));
                }
            }
        }
        else if (tileEntity instanceof ComposterTileEntity)
        {
            ComposterTileEntity composter = (ComposterTileEntity) tileEntity;
            if (composter.getFillType() == CisternTileEntity.CONTENTS_WATER)
            {
                if (!composter.isEmpty())
                {
                    if (heldStack.itemID == Item.bucketEmpty.itemID) return false;

                    composter.removeWater(3);
                    return exchangeContainers(world, x, y, z, facing, player, heldStack, getFullWaterContainerForEmptyContainer(heldStack));
                }
            }
        }

        return false;
    }

    public static boolean addWaterAndReturnContainer(World world, int x, int y, int z, int facing, EntityPlayer player, TileEntity tileEntity, ItemStack heldStack){
        if (tileEntity instanceof CisternTileEntity)
        {
            CisternTileEntity cistern = (CisternTileEntity) tileEntity;
            if (cistern.isEmpty())
            {
                if (heldStack.itemID == Item.bucketWater.itemID)
                {
                    cistern.addWater(3);
                }
                else {
                    cistern.addWater(1);
                }
                return exchangeContainers(world, x, y, z, facing, player, heldStack, getEmptyContainerForFullWaterContainer(heldStack));
            }
            else if (cistern.hasWater() && !cistern.isFullWithWater())
            {
                if (heldStack.itemID == Item.bucketWater.itemID) return false;

                cistern.addWater(1);
                return exchangeContainers(world, x, y, z, facing, player, heldStack, getEmptyContainerForFullWaterContainer(heldStack));
            }
        }
        else if (tileEntity instanceof ComposterTileEntity)
        {
            if (heldStack.itemID == Item.bucketWater.itemID) return false;

            ComposterTileEntity composter = (ComposterTileEntity) tileEntity;
            if (composter.isEmpty())
            {
                composter.addWater(3);
                return exchangeContainers(world, x, y, z, facing, player, heldStack, getEmptyContainerForFullWaterContainer(heldStack));
            }
        }

        return false;
    }

    private static boolean exchangeContainers(World world, int x, int y, int z, int facing, EntityPlayer player, ItemStack heldStack, ItemStack returnStack) {
        if (returnStack == null || heldStack == null) return false;

        heldStack.stackSize--;
        ItemUtils.givePlayerStackOrEjectFromTowardsFacing(player, returnStack, x, y, z, facing);
        world.markBlockForUpdate(x, y, z);
        return true;
    }
}
