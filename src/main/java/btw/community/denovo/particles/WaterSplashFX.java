package btw.community.denovo.particles;

import net.minecraft.src.EntitySplashFX;
import net.minecraft.src.World;

public class WaterSplashFX extends EntitySplashFX {
    public WaterSplashFX(World par1World, double par2, double par4, double par6, double par8, double par10, double par12, float red, float green, float blue) {
        super(par1World, par2, par4, par6, par8, par10, par12);

        this.particleRed = (red / 255);
        this.particleGreen = (green / 255);
        this.particleBlue = (blue / 255);
    }
}
