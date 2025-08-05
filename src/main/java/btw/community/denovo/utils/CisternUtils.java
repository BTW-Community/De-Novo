package btw.community.denovo.utils;

import btw.community.denovo.block.tileentities.CisternBaseTileEntity;
import btw.community.denovo.block.tileentities.CisternTileEntity;
import btw.community.denovo.block.tileentities.ComposterTileEntity;
import btw.item.util.ItemUtils;
import net.minecraft.src.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CisternUtils {

    public static final ArrayList<ItemStack> validCompostables = new ArrayList<>();
    public static final ArrayList<ItemStack> validDirt = new ArrayList<>();

    private static final Map<ItemStack, ItemStack> liquidContainers = new HashMap<>();
    private static final Map<ItemStack, Integer> cisternFillValues = new HashMap<>();
    private static final Map<ItemStack, Integer> composterFillValues = new HashMap<>();
    public static final Map<ItemStack, ItemStack> rustWaterContainers = new HashMap<>();

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

    //
    public static final float RAIN_FILL_CHANCE = 1/8F;
    public static final float MORNING_FILL_CHANCE = 1/16F;

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

    public static void addLiquidContainers(ItemStack fullStack, ItemStack emptyStack, int cisternFillValue, int composterFillValue){
        liquidContainers.put(fullStack, emptyStack);

        cisternFillValues.put(fullStack, cisternFillValue);
        composterFillValues.put(fullStack, composterFillValue);

        cisternFillValues.put(emptyStack, cisternFillValue);
        composterFillValues.put(emptyStack, composterFillValue);
    }

    public static void addRustWaterContainer(ItemStack fullStack, ItemStack emptyStack, int cisternFillValue, int composterFillValue){
        rustWaterContainers.put(fullStack, emptyStack);

        cisternFillValues.put(fullStack, cisternFillValue);
        composterFillValues.put(fullStack, composterFillValue);

        cisternFillValues.put(emptyStack, cisternFillValue);
        composterFillValues.put(emptyStack, composterFillValue);
    }

    public static int getFillValue(ItemStack stack, CisternBaseTileEntity cisterBase){
        if (stack == null) return 0;

        if (cisterBase instanceof ComposterTileEntity){
            for (Map.Entry<ItemStack, Integer> entry : composterFillValues.entrySet()) {
                if (stack.isItemEqual(entry.getKey())) return entry.getValue();
            }
        }
        else if (cisterBase instanceof CisternTileEntity){
            for (Map.Entry<ItemStack, Integer> entry : cisternFillValues.entrySet()) {
                if (stack.isItemEqual(entry.getKey())) return entry.getValue();
            }
        }

        return 0;
    }

    public static boolean isValidEmptyContainer(ItemStack stack) {
        if (stack == null) return false;

        for (Map.Entry<ItemStack, ItemStack> entry : liquidContainers.entrySet()) {
            if (stack.isItemEqual(entry.getValue())) return true;
        }
        return false;
    }

    public static boolean isValidEmptyRustContainer(ItemStack stack) {
        if (stack == null) return false;

        for (Map.Entry<ItemStack, ItemStack> entry : rustWaterContainers.entrySet()) {
            if (stack.isItemEqual(entry.getValue())) return true;
        }
        return false;
    }

    public static ItemStack getFullRustWaterContainerForEmptyContainer(ItemStack stack) {
        if (stack == null) return null;

        for (Map.Entry<ItemStack, ItemStack> entry : rustWaterContainers.entrySet()) {
            if (stack.isItemEqual(entry.getValue())) return entry.getKey().copy();
        }
        return null;
    }

    public static boolean isValidWaterContainer(ItemStack stack) {
        if (stack == null) return false;

        for (Map.Entry<ItemStack, ItemStack> entry : liquidContainers.entrySet()) {
            if (stack.isItemEqual(entry.getKey())) return true;
        }
        return false;
    }

    public static ItemStack getFullWaterContainerForEmptyContainer(ItemStack stack) {
        if (stack == null) return null;

        for (Map.Entry<ItemStack, ItemStack> entry : liquidContainers.entrySet()) {
            if (stack.isItemEqual(entry.getValue())) return entry.getKey().copy();
        }
        return null;
    }

    public static ItemStack getEmptyContainerForFullWaterContainer(ItemStack stack) {
        if (stack == null) return null;

        for (Map.Entry<ItemStack, ItemStack> entry : liquidContainers.entrySet()) {
            if (stack.isItemEqual(entry.getKey())) return entry.getValue().copy();
        }
        return null;
    }

    public static boolean reduceWaterAndReturnContainer(World world, int x, int y, int z, int facing, EntityPlayer player, CisternBaseTileEntity cisternBase){
        ItemStack heldStack = player.getHeldItem();
        if (heldStack == null) return false;

        if (cisternBase.getFillType() == CONTENTS_WATER) {
            int remainingLiquidAmount = cisternBase.getLiquidFillLevel();
            int containerSize = getFillValue(heldStack, cisternBase);

            if (remainingLiquidAmount >= containerSize) {
                if (!world.isRemote) cisternBase.removeLiquid( containerSize );

                return exchangeContainers(world, x, y, z, facing, player, heldStack, getFullWaterContainerForEmptyContainer(heldStack));
            }

            return false;
        }

        return false;
    }

    public static boolean addWaterAndReturnContainer(World world, int x, int y, int z, int facing, EntityPlayer player, CisternBaseTileEntity cisternBase){
        ItemStack heldStack = player.getHeldItem();
        if (heldStack == null) return false;

        if (cisternBase.getFillType() == CONTENTS_WATER || cisternBase.getFillType() == CONTENTS_EMPTY) {
            int currentLiquidAmount = cisternBase.getLiquidFillLevel();
            int containerSize = getFillValue(heldStack, cisternBase);

            if (currentLiquidAmount + containerSize <= MAX_LIQUID_FILL_LEVEL) {
                if (!world.isRemote) cisternBase.addLiquid( containerSize );
                if (!world.isRemote) cisternBase.setFillType( CONTENTS_WATER );
                return exchangeContainers(world, x, y, z, facing, player, heldStack, getEmptyContainerForFullWaterContainer(heldStack));
            }

            return false;
        }

        return false;
    }

    private static boolean exchangeContainers(World world, int x, int y, int z, int facing, EntityPlayer player, ItemStack heldStack, ItemStack returnStack) {
        if (returnStack == null || heldStack == null) return false;

        if (!world.isRemote) heldStack.stackSize--;
        ItemUtils.givePlayerStackOrEjectFromTowardsFacing(player, new ItemStack(returnStack.itemID, 1, returnStack.getItemDamage()), x, y, z, facing);
        if (!world.isRemote) world.markBlockForUpdate(x, y, z);
        return true;
    }

    public static int getIconIndex(CisternBaseTileEntity cisternBase, int totalStages, int maxTime) {
        float progressRatio = (float) cisternBase.getProgressCounter() / maxTime;

        int iconIndex = (int) (progressRatio * totalStages);
        iconIndex = Math.min(iconIndex, totalStages - 1);
        return iconIndex;
    }

    public static boolean isValidCompostable(ItemStack heldStack) {
        if (heldStack == null) return false;

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
        if (heldStack == null) return false;

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
