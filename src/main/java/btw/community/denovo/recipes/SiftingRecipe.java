package btw.community.denovo.recipes;

import net.minecraft.src.ItemStack;

public class SiftingRecipe {
    private final LootEntry[] lootTable;
    private final ItemStack input;
    private final ItemStack filterUsed;
    private final boolean containsSouls;

    public SiftingRecipe(LootEntry[] lootTable, ItemStack input, ItemStack filterUsed, boolean containsSouls) {
        this.lootTable = lootTable;
        this.input = input;
        this.filterUsed = filterUsed;
        this.containsSouls = containsSouls;
    }

    public boolean matchesInputs(ItemStack inputToCheck, ItemStack filterToCheck) {
        return this.input.isItemEqual(inputToCheck) &&
                this.filterUsed.isItemEqual(filterToCheck);
    }

    public LootEntry[] getLootTable() {
        return lootTable;
    }

    public ItemStack getInput() {
        return input;
    }

    public ItemStack getFilterUsed() {
        return filterUsed;
    }

    public boolean getContainsSouls() {
        return containsSouls;
    }
}
