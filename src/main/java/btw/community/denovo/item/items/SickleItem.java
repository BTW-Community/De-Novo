package btw.community.denovo.item.items;

import btw.block.BTWBlocks;
import btw.item.items.ToolItem;
import net.minecraft.src.*;

public class SickleItem extends ToolItem {
    public SickleItem(int itemID, EnumToolMaterial material, int maxUses) {
        super(itemID, 0, material);

        setMaxStackSize(1);
        setMaxDamage(maxUses);

        setUnlocalizedName("denovo.wood_sickle");
        setTextureName("denovo:wood_sickle");
        setCreativeTab(CreativeTabs.tabTools);
    }

    private boolean isValidHarvestable(int id){

        if (id == Block.leaves.blockID) return true;
        if (id == BTWBlocks.bloodWoodLeaves.blockID) return true;
        if (id == Block.tallGrass.blockID) return true;

        return false;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, int iBlockID, int i, int j, int k, EntityLivingBase usingEntity) {
        if (!isValidHarvestable(iBlockID)) {
            return super.onBlockDestroyed(stack, world, iBlockID, i, j, k, usingEntity);
        } else {
            stack.damageItem(1, usingEntity);
            return true;
        }
    }

    @Override
    public float getStrVsBlock(ItemStack stack, World world, Block block, int i, int j, int k) {
        if (isEfficientVsBlock(stack, world, block, i, j, k)) {
            if (isValidHarvestable(block.blockID)) {
                int toolLevel = toolMaterial.getHarvestLevel();
                return 1F + (toolLevel * 2);
            } else {
                return 5F;
            }
        }

        return super.getStrVsBlock(stack, world, block, i, j, k);
    }

    @Override
    public boolean isEfficientVsBlock(ItemStack stack, World world, Block block, int i, int j, int k) {
        if (!block.blockMaterial.isToolNotRequired()) {
            if (canHarvestBlock(stack, world, block, i, j, k)) {
                return true;
            }
        }

        return isValidHarvestable(block.blockID);
    }

    @Override
    public boolean isToolTypeEfficientVsBlockType(Block block) {
        return isValidHarvestable(block.blockID);
    }

    @Override
    public void playPlacementSound(ItemStack stack, Block blockStuckIn, World world, int i, int j, int k) {
        world.playSoundEffect((float) i + 0.5F, (float) j + 0.5F, (float) k + 0.5F,
                Block.leaves.stepSound.getStepSound(), 0.5F, world.rand.nextFloat() * 0.25F + 1.75F);
    }

}
