package btw.community.denovo.block.blocks;

import btw.block.BTWBlocks;
import btw.block.blocks.AshGroundCoverBlock;
import btw.block.blocks.FireBlock;
import btw.block.util.Flammability;
import btw.client.fx.BTWEffectManager;
import btw.client.render.util.RenderUtils;
import btw.community.denovo.block.DNBlocks;
import btw.community.denovo.block.tileentities.SmolderingPlacedSticksTileEntity;
import btw.world.util.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class SmolderingPlacedSticksBlock extends BlockContainer {
    private static final int CHANCE_OF_DECAY = 10;

    private static final int CHANCE_OF_EXTINGUISH_IN_RAIN = 2;

    private static final float EXPLOSION_STRENGTH = 1F;

    public SmolderingPlacedSticksBlock(int iBlockID) {
        super(iBlockID, Material.wood);

        setHardness( 2F );

        setAxesEffectiveOn();
        setChiselsEffectiveOn();

        setFireProperties(Flammability.HIGH);

        setBuoyant();

        setTickRandomly( true );

        setStepSound( soundWoodFootstep );

        setUnlocalizedName("DNBlock_smoldering_placed_sticks");
    }

    @Override
    public TileEntity createNewTileEntity(World var1) {
        return new SmolderingPlacedSticksTileEntity();
    }

    @Override
    public int idDropped( int iMetadata, Random rand, int iFortuneModifier )
    {
        return 0;
    }

    @Override
    public boolean getCanBlockBeIncinerated(World world, int i, int j, int k)
    {
        return true;
    }

    @Override
    public void updateTick( World world, int i, int j, int k, Random rand ) {
        //System.out.println("tick");

        if (hasWaterToSidesOrTop(world, i, j, k)) {
            // extinguish due to neighboring water blocks

            convertToCinders(world, i, j, k);

            world.playAuxSFX(BTWEffectManager.FIRE_FIZZ_EFFECT_ID, i, j, k, 0);
        }
        else if (getNeighboringAirBlock(world, i,j,k) != null)
        {
            BlockPos targetPos = getNeighboringAirBlock(world, i,j,k);
            world.setBlock(targetPos.x,targetPos.y,targetPos.z,Block.fire.blockID);
        }
    }

    @Override
    public void randomUpdateTick(World world, int i, int j, int k, Random rand)
    {
//        if (!world.isRemote) System.out.println("//------------- Rand tick ------------//");

        if (world.getGameRules().getGameRuleBooleanValue("doFireTick")) {
            if ( !checkForGoOutInRain(world, i, j, k) )
            {
                SmolderingPlacedSticksTileEntity tileEntity = (SmolderingPlacedSticksTileEntity) world.getBlockTileEntity(i,j,k);

                FireBlock.checkForSmoulderingSpreadFromLocation(world, i, j, k);

                int iBurnLevel = tileEntity.getBurnLevel();
//                if (!world.isRemote) System.out.println("burn level: " + iBurnLevel);

                convertNeighborBlock(world, i,j,k, rand);

                int chanceOfDecay = CHANCE_OF_DECAY;

                int smolderingNeighbors = hasNeighborSmolderingSticksInContact(world, i, j, k);
                int neighborBonus = Math.min(smolderingNeighbors, 4);

                chanceOfDecay -= neighborBonus;

                int pileSize = world.getBlockMetadata(i,j,k) + 1;
                int reduceChangeByPileSize = Math.max(pileSize / 4, 2);

                chanceOfDecay -= reduceChangeByPileSize;

//                if (!world.isRemote) System.out.println("chanceOfDecay: " + chanceOfDecay);

                if ( iBurnLevel < 1 )
                {
                    if ( !FireBlock.hasFlammableNeighborsWithinSmoulderRange(world, i, j, k) || hasNeighborSmolderingSticksInContact(world, i, j, k) > 0)
                    {
//                        if (!world.isRemote) System.out.println("NOThasFlammableNeighborsWithinSmoulderRange");

                        tileEntity.setBurnLevel( 1 );
                    }
                }
                else if ( rand.nextInt(chanceOfDecay) == 0 )
                {
                    if ( iBurnLevel < 3 )
                    {
//                        if (!world.isRemote) System.out.println("increasing burn level by chance");

                        tileEntity.increaseBurnLevelBy( 1 );
                    }
                    else
                    {
                        Block blockAbove = Block.blocksList[world.getBlockId(i,j + 1,k)];
                        Block blockBelow = Block.blocksList[world.getBlockId(i,j - 1,k)];

                        boolean hasSmolderingAbove = blockAbove != null && blockAbove.blockID == DNBlocks.smolderingPlacedSticks.blockID;

                        if (blockBelow != null)
                        {

                            if (!hasSmolderingAbove){
//                                if (!world.isRemote) System.out.println("convertToCharcoal");
                                convertToCharcoal(world, i, j, k);
                            }
                            else {
//                                if (!world.isRemote) System.out.println("hasSmolderingAbove");
                            }

                        }
                        else {
//                            if (!world.isRemote) System.out.println("CANT CONVERT");
                        }

                    }
                }
                else {
//                    if (!world.isRemote) System.out.println("FAILED TO BURN");
                }
            }
        }
    }

    @Override
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5) {
        this.updateTick(par1World,par2, par3,par4, par1World.rand);
    }

    public int hasNeighborSmolderingSticksInContact(World world, int i, int j, int k)
    {
        int count = 0;
        for ( int iTempFacing = 0; iTempFacing < 6; iTempFacing++ )
        {
            if ( hasNeighborSmolderingSticksToFacing(world, i, j, k, iTempFacing) )
            {
                count += 1;
            }
        }

        return count;
    }


    public boolean hasNeighborSmolderingSticksToFacing(World world, int i, int j, int k, int iFacing)
    {
        BlockPos tempBlockPos = new BlockPos( i, j, k, iFacing );

        int iTempBlockID = world.getBlockId(tempBlockPos.x, tempBlockPos.y, tempBlockPos.z);

        Block tempBlock = Block.blocksList[iTempBlockID];

        if ( tempBlock != null )
        {
            if ( tempBlock.blockID == this.blockID )
            {
                return true;
            }
        }

        return false;
    }



    private static BlockPos getNeighboringAirBlock(World world, int i, int j, int k) {
        BlockPos targetPos = new BlockPos(i, j, k);

        if (world.getBlockId(targetPos.x, targetPos.y-1, targetPos.z) == 0){
            targetPos.y = targetPos.y -1;
            return targetPos;
        }
        else if (world.getBlockId(targetPos.x, targetPos.y+1, targetPos.z) == 0) {
            targetPos.y = targetPos.y +1;
            return targetPos;
        }
        else if (world.getBlockId(targetPos.x-1, targetPos.y, targetPos.z) == 0) {
            targetPos.x = targetPos.x -1;
            return targetPos;
        }
        else if (world.getBlockId(targetPos.x+1, targetPos.y, targetPos.z) == 0) {
            targetPos.x = targetPos.x +1;
            return targetPos;
        }
        else if (world.getBlockId(targetPos.x, targetPos.y, targetPos.z-1) == 0) {
            targetPos.z = targetPos.z -1;
            return targetPos;
        }
        else if (world.getBlockId(targetPos.x, targetPos.y, targetPos.z+1) == 0) {
            targetPos.z = targetPos.z +1;
            return targetPos;
        }

        else return null;
    }

    private void convertNeighborBlock(World world, int i, int j, int k, Random rand) {
        if (world.getBlockId(i, j-1, k) == DNBlocks.placedSticks.blockID) {
            int meta = world.getBlockMetadata(i, j-1, k);
            world.setBlockAndMetadataWithNotify(i, j-1, k, DNBlocks.smolderingPlacedSticks.blockID, meta);
        }
        if (world.getBlockId(i, j+1, k) == DNBlocks.placedSticks.blockID) {
            int meta = world.getBlockMetadata(i, j+1, k);
            world.setBlockAndMetadataWithNotify(i, j+1, k, DNBlocks.smolderingPlacedSticks.blockID, meta);
        }
        if (world.getBlockId(i-1, j, k) == DNBlocks.placedSticks.blockID) {
            int meta = world.getBlockMetadata(i-1, j, k);
            world.setBlockAndMetadataWithNotify(i-1, j, k, DNBlocks.smolderingPlacedSticks.blockID, meta);
        }
        if (world.getBlockId(i+1, j, k) == DNBlocks.placedSticks.blockID) {
            int meta = world.getBlockMetadata(i+1, j, k);
            world.setBlockAndMetadataWithNotify(i+1, j, k, DNBlocks.smolderingPlacedSticks.blockID, meta);
        }
        if (world.getBlockId(i, j, k-1) == DNBlocks.placedSticks.blockID) {
            int meta = world.getBlockMetadata(i, j, k-1);
            world.setBlockAndMetadataWithNotify(i, j, k-1, DNBlocks.smolderingPlacedSticks.blockID, meta);
        }
        if (world.getBlockId(i, j, k+1) == DNBlocks.placedSticks.blockID) {
            int meta = world.getBlockMetadata(i, j, k+1);
            world.setBlockAndMetadataWithNotify(i, j, k+1, DNBlocks.smolderingPlacedSticks.blockID, meta);
        }
    }

    @Override
    public void onDestroyedByFire(World world, int i, int j, int k, int iFireAge, boolean bForcedFireSpread) {
        super.onDestroyedByFire(world, i, j, k, iFireAge, bForcedFireSpread);

        generateAshOnBurn(world, i, j, k);
    }

    protected void generateAshOnBurn(World world, int i, int j, int k) {
        for (int iTempJ = j; iTempJ > 0; iTempJ--) {
            if (AshGroundCoverBlock.canAshReplaceBlock(world, i, iTempJ, k)) {
                int iBlockBelowID = world.getBlockId(i, iTempJ - 1, k);
                Block blockBelow = Block.blocksList[iBlockBelowID];

                if (blockBelow != null && blockBelow.canGroundCoverRestOnBlock(world, i, iTempJ - 1, k)) {
                    world.setBlockWithNotify(i, iTempJ, k, BTWBlocks.ashCoverBlock.blockID);

                    break;
                }
            }
            else if (world.getBlockId(i, iTempJ, k) != Block.fire.blockID) {
                break;
            }
        }
    }

    @Override
    public boolean getIsBlockWarm(IBlockAccess blockAccess, int i, int j, int k)
    {
        return true;
    }

    @Override
    public boolean getCanBlockLightItemOnFire(IBlockAccess blockAccess, int i, int j, int k)
    {
        return true;
    }


    @Override
    public void onBlockDestroyedWithImproperTool(World world, EntityPlayer player, int i, int j, int k, int iMetadata)
    {
        explode(world, (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D);
    }




    //------------- Class Specific Methods ------------//

    private boolean checkForGoOutInRain(World world, int i, int j, int k)
    {
        if (world.rand.nextInt(CHANCE_OF_EXTINGUISH_IN_RAIN) == 0 )
        {
            if ( world.isRainingAtPos(i, j + 1, k) )
            {
                world.playAuxSFX( BTWEffectManager.FIRE_FIZZ_EFFECT_ID, i, j, k, 0 );

                convertToCinders(world, i, j, k);

                return true;
            }
        }

        return false;
    }

    private void convertToCinders(World world, int i, int j, int k)
    {
//        world.setBlockWithNotify( i, j, k, BTWBlocks.woodCinders.blockID );
        world.setBlockWithNotify( i, j, k, BTWBlocks.ashCoverBlock.blockID );
    }

    private void convertToCharcoal(World world, int i, int j, int k)
    {
        int  pileSize = world.getBlockMetadata(i, j, k) + 1;

        float guaranteedAmount = 0.25F;
        float remainingAmount = 0.5F;
        float amount = guaranteedAmount + world.rand.nextFloat() * remainingAmount;
        int newMeta = MathHelper.floor_float(pileSize * amount);


        world.removeBlockTileEntity(i,j,k);
        world.setBlockAndMetadataWithNotify( i, j, k, DNBlocks.charcoalPile.blockID, newMeta );


/*        SmolderingPlacedSticksTileEntity tileEntity = (SmolderingPlacedSticksTileEntity) world.getBlockTileEntity(i,j,k);
        if (tileEntity != null)
        {
            int counter = tileEntity.getCounter();
            float seconds = counter / 20F;

            System.out.println("it took this time (sec) to convert: " + seconds + " | at " + i + " " + j + " " + k);
        }*/

    }



    @Override
    public void onBlockDestroyedByExplosion( World world, int i, int j, int k, Explosion explosion )
    {
        if ( !world.isRemote )
        {
            // explode without audio/visual effects to cut down on overhead

            explosion.addSecondaryExplosionNoFX((double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, EXPLOSION_STRENGTH, true, false);
        }
    }

    private void explode(World world, double posX, double posY, double posZ)
    {
        world.newExplosionNoFX((Entity)null, posX, posY, posZ, EXPLOSION_STRENGTH, true, false);

        notifyNearbyAnimalsFinishedFalling(world, MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ));

        world.playAuxSFX( BTWEffectManager.SMOLDERING_LOG_EXPLOSION_EFFECT_ID,
                MathHelper.floor_double( posX ), MathHelper.floor_double( posY ), MathHelper.floor_double( posZ ),
                0 );
    }

    protected boolean isSupportedBySolidBlocks(World world, int i, int j, int k)
    {
        Block blockBelow = Block.blocksList[world.getBlockId( i, j - 1, k )];

        return blockBelow != null && blockBelow.hasLargeCenterHardPointToFacing( world, i, j - 1, k, 1, false );
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing) {
        return true;
    }

    @Override
    public boolean isNormalCube(IBlockAccess blockAccess, int i, int j, int k) {
        return blockAccess.getBlockMetadata(i, j, k) == 15;
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return !canBlockStay(world, x, y, z) ? false : super.canPlaceBlockAt(world, x, y, z);
    }

    @Override
    public boolean canBlockStay(World world, int x, int y, int z) {
        return !world.doesBlockHaveSolidTopSurface(x, y - 1, z) ? false : super.canBlockStay(world, x, y, z);
    }

    @Override
    public boolean isBlockSolid(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
        return par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 15;
    }

    //----------- Client Side Functionality -----------//

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int iNeighborI, int iNeighborJ, int iNeighborK, int iSide) {
        return true;
    }

    @Environment(EnvType.CLIENT)
    private Icon iconEmbers;

    @Environment(EnvType.CLIENT)
    private Icon[] top = new Icon[4];

    private Icon[] top_alt = new Icon[4];

    @Environment(EnvType.CLIENT)
    private Icon[] side = new Icon[2];

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        top[0] = register.registerIcon("DNBlock_smoldering_placed_shafts_top_0");
        top[1] = register.registerIcon("DNBlock_smoldering_placed_shafts_top_1");
        top[2] = register.registerIcon("DNBlock_smoldering_placed_shafts_top_2");
        top[3] = register.registerIcon("DNBlock_smoldering_placed_shafts_top_3");

        top_alt[0] = register.registerIcon("DNBlock_smoldering_placed_shafts_top_alt_0");
        top_alt[1] = register.registerIcon("DNBlock_smoldering_placed_shafts_top_alt_1");
        top_alt[2] = register.registerIcon("DNBlock_smoldering_placed_shafts_top_alt_2");
        top_alt[3] = register.registerIcon("DNBlock_smoldering_placed_shafts_top_alt_3");

        side[0] = register.registerIcon("DNBlock_smoldering_placed_shafts_side");
        side[1] = register.registerIcon("DNBlock_smoldering_placed_shafts_side_alt");

        blockIcon = side[0];
        iconEmbers = register.registerIcon("DNBlock_smoldering_placed_shafts_overlay");
    }

    @Override
    public Icon getBlockTexture(IBlockAccess blockAccess, int x, int y, int z, int face) {
        int meta = blockAccess.getBlockMetadata(x, y, z);
        int layer = meta / 4;
        boolean isEvenPos = (x + z) % 2 == 0;
        int textureIndex = meta % 4;
        boolean isEvenLayer = (layer % 2 == 0);

        // Determine top texture
        if (face <= 1) {
            if (isEvenPos) {
                return isEvenLayer ? top_alt[textureIndex] : top[textureIndex];
            } else {
                return isEvenLayer ? top[textureIndex] : top_alt[textureIndex];
            }
        }

        // Determine side texture
        if (face == 2 || face == 3) {
            return isEvenPos ? side[1] : side[0];
        } else {
            return isEvenPos ? side[0] : side[1];
        }
    }

    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int x, int y, int z) {

        int metadata = renderer.blockAccess.getBlockMetadata(x,y,z); // Implement this method to extract the correct value from metadata
        int numberOfLayers = (int) Math.floor(metadata/4D);

        for (int layer = 0; layer < numberOfLayers + 1; layer++) {

            double xMin = 0/16D;
            double xMax = 4/16D + (metadata%4)*4/16D;

            if (layer < numberOfLayers) {
                xMax = 1D;  // If the layer is full, set xMax to the full width of the block
            }

            double yMin = layer * 4/16D;
            double yMax = yMin + 4/16D;

            double zMin = 0D;
            double zMax = 1D;

            if (isEven(x,y,z))
            {
                if (layer%2 == 0)
                {
                    // x and z swapped
                    renderer.setRenderBounds(zMin, yMin, xMin, zMax, yMax, xMax);
                }
                else {
                    renderer.setRenderBounds(xMin, yMin, zMin, xMax, yMax, zMax);
                }
            }
            else {
                if (layer%2 == 1)
                {
                    // x and z swapped
                    renderer.setRenderBounds(zMin, yMin, xMin, zMax, yMax, xMax);
                }
                else {
                    renderer.setRenderBounds(xMin, yMin, zMin, xMax, yMax, zMax);
                }
            }
            renderer.renderStandardBlock(this, x, y, z);

        }
        return true;
    }

    private boolean isEven(int x, int y, int z) {
        if ((x + z) % 2 == 0) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockSecondPass(RenderBlocks renderer, int x, int y, int z, boolean bFirstPassResult)
    {
        if ( bFirstPassResult )
        {
            int metadata = renderer.blockAccess.getBlockMetadata(x,y,z); // Implement this method to extract the correct value from metadata
            int numberOfLayers = (int) Math.floor(metadata/4D);

            for (int layer = 0; layer < numberOfLayers + 1; layer++) {

                double xMin = 0/16D;
                double xMax = 4/16D + (metadata%4)*4/16D;

                if (layer < numberOfLayers) {
                    xMax = 1D;  // If the layer is full, set xMax to the full width of the block
                }

                double yMin = layer * 4/16D;
                double yMax = yMin + 4/16D;

                double zMin = 0D;
                double zMax = 1D;

                if (isEven(x,y,z))
                {
                    if (layer%2 == 0)
                    {
                        // x and z swapped
                        renderer.setRenderBounds(zMin, yMin, xMin, zMax, yMax, xMax);
                    }
                    else {
                        renderer.setRenderBounds(xMin, yMin, zMin, xMax, yMax, zMax);
                    }
                }
                else {
                    if (layer%2 == 1)
                    {
                        // x and z swapped
                        renderer.setRenderBounds(zMin, yMin, xMin, zMax, yMax, xMax);
                    }
                    else {
                        renderer.setRenderBounds(xMin, yMin, zMin, xMax, yMax, zMax);
                    }
                }
                RenderUtils.renderBlockFullBrightWithTexture(renderer, renderer.blockAccess, x, y, z, iconEmbers);

            }
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderBlocks, int iItemDamage, float fBrightness)
    {
        renderBlocks.renderBlockAsItemVanilla( this, iItemDamage, fBrightness );

        RenderUtils.renderInvBlockFullBrightWithTexture(renderBlocks, this, -0.5F, -0.5F, -0.5F, iconEmbers);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick( World world, int i, int j, int k, Random rand )
    {
        SmolderingPlacedSticksTileEntity tileEntity = (SmolderingPlacedSticksTileEntity) world.getBlockTileEntity(i,j,k);
        int metadata = tileEntity.getBurnLevel();

        //System.out.println("burnLevel: " + metadata);

        emitSmokeParticles(world, (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, rand, metadata); // getBurnLevel(world, i, j, k));

        if ( rand.nextInt( 24 ) == 0 )
        {
            float fVolume = 0.1F + rand.nextFloat() * 0.1F;

            world.playSound( i + 0.5D, j + 0.5D, k + 0.5D, "fire.fire",
                    fVolume, rand.nextFloat() * 0.7F + 0.3F, false );
        }
    }

    private void emitSmokeParticles(World world, double dCenterX, double dCenterY, double dCenterZ, Random rand, int iBurnLevel)
    {
        for ( int iTempCount = 0; iTempCount < 5; ++iTempCount )
        {
            double xPos = dCenterX - 0.60D + rand.nextDouble() * 1.2D;
            double yPos = dCenterY + 0.25D + rand.nextDouble() * 0.25D;
            double zPos = dCenterZ - 0.60D + rand.nextDouble() * 1.2D;

            if ( iBurnLevel > 0 )
            {
                world.spawnParticle( "fcwhitesmoke", xPos, yPos, zPos, 0D, 0D, 0D );
                //System.out.println("Particles fcwhitesmoke");
            }
            else
            {
                world.spawnParticle( "largesmoke", xPos, yPos, zPos, 0D, 0D, 0D );
                //System.out.println("Particles largesmoke");
            }
        }
    }

}
