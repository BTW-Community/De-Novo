package btw.community.denovo.item.items;

import btw.block.BTWBlocks;
import btw.item.items.ToolItem;
import net.minecraft.src.*;

public class SickleItem extends ToolItem {
    public SickleItem(int itemID, EnumToolMaterial material, int maxUses) {
        super(itemID, 2, material);

        setMaxStackSize(1);
        setMaxDamage( maxUses );

        setUnlocalizedName("DNItem_wood_sickle");
        setCreativeTab(CreativeTabs.tabTools);
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, int iBlockID, int i, int j, int k, EntityLiving usingEntity )
    {
        if ( iBlockID != Block.leaves.blockID  &&
                iBlockID != BTWBlocks.bloodWoodLeaves.blockID)
        {
            return super.onBlockDestroyed(stack, world, iBlockID, i, j, k, usingEntity);
        }
        else
        {
            stack.damageItem( 1, usingEntity );
            return true;
        }
    }

    @Override
    public float getStrVsBlock( ItemStack stack, World world, Block block, int i, int j, int k )
    {
        if ( isEfficientVsBlock(stack, world, block, i, j, k) )
        {
            if ( block.blockID == BTWBlocks.bloodWoodLeaves.blockID ||
                    block.blockID == Block.leaves.blockID )
            {
                int toolLevel = toolMaterial.getHarvestLevel();
                return 1F + (toolLevel * 2);
            }
            else
            {
                return 5F;
            }
        }

        return super.getStrVsBlock( stack, world, block, i, j, k );
    }

    @Override
    public boolean isEfficientVsBlock(ItemStack stack, World world, Block block, int i, int j, int k)
    {
        if ( !block.blockMaterial.isToolNotRequired() )
        {
            if ( canHarvestBlock( stack, world, block, i, j, k ) )
            {
                return true;
            }
        }

        if (  block == Block.leaves ||
                block == BTWBlocks.bloodWoodLeaves )
        {
            return true;
        }

        return false;
    }

    @Override
    public boolean isToolTypeEfficientVsBlockType(Block block)
    {
        return block.blockID == Block.leaves.blockID || block.blockID == BTWBlocks.bloodWoodLeaves.blockID;
    }

    @Override
    public void playPlacementSound(ItemStack stack, Block blockStuckIn, World world, int i, int j, int k)
    {
        world.playSoundEffect( (float)i + 0.5F, (float)j + 0.5F, (float)k + 0.5F,
                Block.leaves.stepSound.getStepSound(), 0.5F, world.rand.nextFloat() * 0.25F + 1.75F );
    }

}
