package btw.community.denovo.utils;

import btw.community.denovo.block.tileentities.CisternBaseTileEntity;
import btw.community.denovo.block.tileentities.CisternTileEntity;
import btw.community.denovo.block.tileentities.ComposterTileEntity;
import btw.community.denovo.item.DNItems;
import btw.item.util.ItemUtils;
import net.minecraft.src.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

public class CisternUtils {

    public static ArrayList<ItemStack> validCompostables = new ArrayList<>();
    public static ArrayList<ItemStack> validDirt = new ArrayList<>();

    public static final int MAX_SOLID_FILL_LEVEL = 16;
    public static final int MAX_LIQUID_FILL_LEVEL = 15;

    // --- Fill types --- //
    public static final int CONTENTS_EMPTY = 0;
    public static final int CONTENTS_WATER = 1;
    public static final int CONTENTS_MUDDY_WATER = 2;
    public static final int CONTENTS_COMPOST = 3;
    public static final int CONTENTS_MAGGOTS = 4;
    public static final int CONTENTS_CLAY_WATER = 5;
    public static final int CONTENTS_INFECTED_WATER = 6;
    public static final int CONTENTS_RUST_WATER = 7;

    // --- Max time values --- //
    // 24000 is 20min
    public static final int MAGGOT_CREATION_TIME = 5 * 60 * 20; //5 min
    public static final int MUDDY_WATER_SETTLE_TIME = 5 * 60 * 20; //5 min
    public static final int CLAY_WATER_CONVERSION_TIME = 5 * 60 * 20; // 5 min
    public static final int INFECTED_WATER_CONVERSION_TIME = 10 * 60 * 20; // 10 min


    // --- Colors below are multiplied colors with water --- //
    public static final Color COLOR_WATER = new Color(255, 255, 255);
    public static final Color COLOR_MUDDY_WATER = new Color(255, 130, 0);
    public static final Color COLOR_CLAY_WATER = new Color(255, 223, 77);
    public static final Color COLOR_INFECTED_WATER = new Color(248, 133, 117);
    public static final Color COLOR_RUST_WATER = new Color(252, 69, 0);


    public static boolean isValidEmptyContainer(ItemStack stack) {
        return stack.isItemEqual(new ItemStack(Item.bowlEmpty)) || stack.isItemEqual(new ItemStack(Item.glassBottle)) || stack.isItemEqual(new ItemStack(Item.bucketEmpty));
    }

    public static boolean isValidEmptyContainer(CisternBaseTileEntity cisternBase, ItemStack stack) {
        if (cisternBase instanceof ComposterTileEntity)
        {
            if (!cisternBase.isFullWithWater())
            {
                return false;
            }
        }
        return isValidEmptyContainer(stack);
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
            if (cistern.getFillType() == CONTENTS_WATER)
            {
                if (cistern.isFullWithWater())
                {
                    if (heldStack.itemID == Item.bucketEmpty.itemID)
                    {
                        if ( !world.isRemote ) cistern.removeLiquid(3);
                    }
                    else {
                        if ( !world.isRemote ) cistern.removeLiquid(1);
                    }

                    return exchangeContainers(world, x, y, z, facing, player, heldStack, getFullWaterContainerForEmptyContainer(heldStack));
                }
                else if (!cistern.isEmpty())
                {
                    if (heldStack.itemID == Item.bucketEmpty.itemID) return false;

                    if ( !world.isRemote ) cistern.removeLiquid(1);
                    return exchangeContainers(world, x, y, z, facing, player, heldStack, getFullWaterContainerForEmptyContainer(heldStack));
                }
            }
        }
        else if (tileEntity instanceof ComposterTileEntity)
        {
            ComposterTileEntity composter = (ComposterTileEntity) tileEntity;
            if (composter.getFillType() == CONTENTS_WATER)
            {
                if (!composter.isEmpty())
                {
                    if (heldStack.itemID == Item.bucketEmpty.itemID) return false;

                    if ( !world.isRemote ) composter.removeLiquid(3);
                    if ( !world.isRemote ) composter.setFillType(CisternUtils.CONTENTS_EMPTY);
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
                    if ( !world.isRemote ) cistern.addLiquid(3);
                }
                else {
                    if ( !world.isRemote ) cistern.addLiquid(1);
                }
                if ( !world.isRemote ) cistern.setFillType(CisternUtils.CONTENTS_WATER);
                return exchangeContainers(world, x, y, z, facing, player, heldStack, getEmptyContainerForFullWaterContainer(heldStack));
            }
            else if (cistern.hasWater() && !cistern.isFullWithWater())
            {
                if (heldStack.itemID == Item.bucketWater.itemID) return false;

                if ( !world.isRemote ) cistern.addLiquid(1);
                if ( !world.isRemote ) cistern.setFillType(CisternUtils.CONTENTS_WATER);
                return exchangeContainers(world, x, y, z, facing, player, heldStack, getEmptyContainerForFullWaterContainer(heldStack));
            }
        }
        else if (tileEntity instanceof ComposterTileEntity)
        {
            if (heldStack.itemID == Item.bucketWater.itemID) return false;

            ComposterTileEntity composter = (ComposterTileEntity) tileEntity;
            if (composter.isEmpty())
            {
                if ( !world.isRemote ) composter.addLiquid(3);
                if ( !world.isRemote ) composter.setFillType(CisternUtils.CONTENTS_WATER);
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

    public static int getIconIndex(CisternBaseTileEntity cisternBase, int totalStages, int maxTime) {
        float progressRatio = (float) cisternBase.getProgressCounter() / maxTime;

        int iconIndex = (int) (progressRatio * totalStages);
        iconIndex = Math.min(iconIndex, totalStages - 1);
        return iconIndex;
    }

    public static boolean isValidCompostable(ItemStack heldStack) {
        Iterator<ItemStack> validStacks = validCompostables.iterator();

        for (Iterator<ItemStack> it = validStacks; it.hasNext(); ) {
            ItemStack stack = it.next();

            if (heldStack.isItemEqual(stack)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidDirt(ItemStack heldStack) {
        Iterator<ItemStack> validStacks = validDirt.iterator();

        for (Iterator<ItemStack> it = validStacks; it.hasNext(); ) {
            ItemStack stack = it.next();

            if (heldStack.isItemEqual(stack)) {
                return true;
            }
        }
        return false;
    }

    private static Color interpolateColor(Color color1, Color color2, float ratio) {
        // Ensure the ratio is within [0, 1]
        ratio = Math.max(0, Math.min(1, ratio));

        // Interpolate between the two colors
        int red = (int) (color1.getRed() * (1 - ratio) + color2.getRed() * ratio);
        int green = (int) (color1.getGreen() * (1 - ratio) + color2.getGreen() * ratio);
        int blue = (int) (color1.getBlue() * (1 - ratio) + color2.getBlue() * ratio);

        // Return the new interpolated color
        return new Color(red, green, blue);
    }

    public static Color getInterpolatedColor(Color color1, Color color2, Color color3, int progressCounter, int maxCounter) {

        // Calculate the phase ratio
        float ratio = Math.max(0, Math.min(1, (float) progressCounter / maxCounter));

        // Determine the current phase and interpolate accordingly
        if (ratio <= 0.5f) {
            // Phase 1: color1 to color2
            return interpolateColor(color1, color2, ratio * 2); // ratio * 2 scales it to [0, 1]
        } else {
            // Phase 2: color2 to color3
            return interpolateColor(color2, color3, (ratio - 0.5f) * 2); // (ratio - 0.5) * 2 scales it to [0, 1]
        }
    }

    public static Color getInterpolatedColor(Color color1, Color color2, int progressCounter, int maxCounter) {

        // Calculate the phase ratio
        float ratio = Math.max(0, Math.min(1, (float) progressCounter / maxCounter));

        return interpolateColor(color1, color2, ratio);
    }

}
