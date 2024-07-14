package btw.community.denovo.recipes;

import net.minecraft.src.ItemStack;

public class LootEntry {
    private final double chance;
    private final ItemStack result;
    private final int amount;

    public LootEntry(double chance, int amount, ItemStack result) {
        this.chance = chance;
        this.amount = amount;
        this.result = result;
    }

    public double getChance() {
        return chance;
    }

    public int getAmount() {
        return amount;
    }

    public ItemStack getResult() {
        return result;
    }
}
