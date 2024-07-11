package btw.community.denovo.mixins;

import btw.block.BTWBlocks;
import btw.block.blocks.DeadBushBlock;
import btw.item.BTWItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(DeadBushBlock.class)
public abstract class DeadBushBlockMixin extends BlockDeadBush {

    @Shadow protected abstract boolean canGrowOnBlock(World world, int i, int j, int k);

    public DeadBushBlockMixin(int blockID) {
        super(blockID);
    }

    private float getBaseGrowthChance() {
        return 0.25F; //Crops are 0.05F, Daily Crops 0.4F
    }

    private float getReduceGrowthChance() {
        return 1F;
    }

    protected int getLightLevelForGrowth() {
        return 9; //wheat is 11, crops 9
    }

    protected boolean requiresNaturalLight() {
        return true;
    }

    @Inject(method = "canGrowOnBlock", at = @At(value = "HEAD"), cancellable = true)
    public void canGrowOnBlock(World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {

        Block block = Block.blocksList[world.getBlockId(x,y,z)];

        if (block != null && (block.canSaplingsGrowOnBlock(world, x,y,z) || block.blockID == Block.bedrock.blockID)) {
            cir.setReturnValue(true);
        }
    }

    @Override
    public void randomUpdateTick(World world, int x, int y, int z, Random rand) {
        if ( world.provider.dimensionId != 1 && !isFullyGrown(world.getBlockMetadata(x,y,z)) )
        {
            attemptToGrow(world, x, y, z, rand);
        }
    }

    protected void attemptToGrow(World world, int x, int y, int z, Random rand) {
        if (canGrowAtCurrentLightLevel(world, x, y, z)) {
            Block blockBelow = Block.blocksList[world.getBlockId(x, y - 1, z)];

            if (blockBelow != null) {
                float fGrowthChance = getBaseGrowthChance();

                if (rand.nextFloat() <= fGrowthChance) {
                    incrementGrowthLevel(world, x, y, z);
                }
            }
        }
    }

    protected boolean canGrowAtCurrentLightLevel(World world, int x, int y, int z) {
        if (this.requiresNaturalLight()) {
            return world.getBlockNaturalLightValue(x, y, z) >= getLightLevelForGrowth() ||
                    world.getBlockId( x, y + 1, z ) == BTWBlocks.lightBlockOn.blockID ||
                    world.getBlockId( x, y + 2, z ) == BTWBlocks.lightBlockOn.blockID;
        }
        else {
            return world.getBlockLightValue(x, y, z) >= getLightLevelForGrowth();
        }
    }

    protected int getGrowthLevel(int meta)
    {
       switch (meta)
       {
           case 0:
               return 3;
           case 1:
               return 2;
           case 2:
               return 1;
           case 3:
               return 0;
       }

       return -1;
    }

    protected void setGrowthLevel(World world, int x, int y, int z, int level)
    {
        world.setBlockMetadataWithNotify(x, y, z, getGrowthLevel(level));
    }

    protected boolean isFullyGrown(int iMetadata)
    {
        return getGrowthLevel(iMetadata) == 3;
    }

    private void incrementGrowthLevel(World world, int x, int y, int z) {
        int oldMeta = world.getBlockMetadata(x,y,z);
        world.setBlockMetadataWithNotify(x,y,z, oldMeta - 1);
    }
    private void reduceGrowthLevel(World world, int x, int y, int z) {
        int oldMeta = world.getBlockMetadata(x,y,z);
        world.setBlockMetadataWithNotify(x,y,z, oldMeta + 1);
    }

    @Override
    public boolean onBlockActivated( World world, int x, int y, int z, EntityPlayer player, int iFacing, float fXClick, float fYClick, float fZClick )
    {
        if ( player.getCurrentEquippedItem() != null ) return false;

        if (isFullyGrown(world.getBlockMetadata(x,y,z)))
        {
            //reduce Growth Level
            if (world.rand.nextFloat() <= getReduceGrowthChance()){
                //set to the smallest growth stage
                setGrowthLevel(world, x,y,z, 0);

                //drop stick
                dropItemsIndividually(world, x, y, z, Item.stick.itemID, 1, 0, 1);
                return true;
            }
        }

        return false;
    }

    @Override
    public int getDamageValue(World par1World, int par2, int par3, int par4) {
        return super.getDamageValue(par1World, par2, par3, par4);
    }

    @Override
    public int damageDropped(int meta)
    {
        return meta;
    }

    @Override
    public Icon getBlockTexture(IBlockAccess blockAccess, int x, int y, int z, int side) {
        return dead_bush_stages[blockAccess.getBlockMetadata(x,y,z)];
    }

    @Override
    public Icon getIcon(int side, int meta) {
        return dead_bush_stages[meta];
    }

    private Icon[] dead_bush_stages = new Icon[4];

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        super.registerIcons(register);
        //DN added
        dead_bush_stages[0] = register.registerIcon("DNBlock_dead_bush_" + 3);
        dead_bush_stages[1] = register.registerIcon("DNBlock_dead_bush_" + 2);
        dead_bush_stages[2] = register.registerIcon("DNBlock_dead_bush_" + 1);
        dead_bush_stages[3] = register.registerIcon("DNBlock_dead_bush_" + 0);
        //DN end
    }
}
