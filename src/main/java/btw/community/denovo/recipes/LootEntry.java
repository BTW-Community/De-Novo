package btw.community.denovo.recipes;

import net.minecraft.src.ItemStack;

public class LootEntry {
    private final double chance;
    private final ItemStack result;

    public LootEntry(double chance, ItemStack result) {
        this.chance = chance;
        this.result = result;
    }

    public double getChance() {
        return chance;
    }

    public ItemStack getResult() {
        return result;
    }
}
