package btw.community.denovo.entity.mob.possession;

import net.minecraft.src.ResourceLocation;

public record PossessionType(ResourceLocation id) {
    public static final btw.entity.mob.possession.PossessionType SIFTING = new btw.entity.mob.possession.PossessionType(new ResourceLocation("denovo", "sifting"));
}