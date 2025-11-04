package btw.community.denovo.block.item;

import btw.community.denovo.utils.CisternUtils;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;

import java.util.List;

public class CisternBaseItemBlock extends ItemBlock {
    public CisternBaseItemBlock(int itemID) {
        super(itemID);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        int fillType = CisternUtils.getFillType(stack.getItemDamage());
        int liquidFillLevel = CisternUtils.getLiquidFillLevel(stack.getItemDamage());
        int solidFillLevel = CisternUtils.getSolidFillLevel(stack.getItemDamage());
        int progress = CisternUtils.getProgress(stack.getItemDamage());

        String fillTypeName = CisternUtils.getNameForFillType(fillType);
        String liquidCapacity = CisternUtils.getCurrentCapacityForLiquidFillLevel(liquidFillLevel, false);
        String solidCapacity = CisternUtils.getCurrentCapacityForLiquidFillLevel(solidFillLevel, true);

        if (fillType > 0){
            list.add(fillTypeName);

            if (fillType == CisternUtils.CONTENTS_CLAY_WATER){
                list.add(String.valueOf((int)((progress / (double) CisternUtils.CLAY_WATER_CONVERSION_TIME) * 100)).concat("% Progress"));
            }
//            if (liquidFillLevel > 0){
//                list.add(liquidCapacity + " Full");
//            }
//            else {
//                if (solidFillLevel > 0){
//                    list.add(solidCapacity + " Full");
//                }
//            }
        }
    }
}
