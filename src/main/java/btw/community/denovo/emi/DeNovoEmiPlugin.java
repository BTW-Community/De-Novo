package btw.community.denovo.emi;

import btw.block.BTWBlocks;
import btw.community.denovo.block.DNBlocks;
import btw.community.denovo.item.DNItems;
import btw.community.denovo.recipes.LootEntry;
import btw.community.denovo.recipes.SiftingCraftingManager;
import btw.community.denovo.recipes.SiftingRecipe;
import btw.crafting.recipe.types.HopperFilterRecipe;
import emi.dev.emi.emi.EmiRenderHelper;
import emi.dev.emi.emi.api.EmiPlugin;
import emi.dev.emi.emi.api.EmiRegistry;
import emi.dev.emi.emi.api.plugin.BTWPlugin;
import emi.dev.emi.emi.api.recipe.BTWEmiRecipeCategories;
import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.api.recipe.EmiRecipeCategory;
import emi.dev.emi.emi.api.render.EmiTexture;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.recipe.btw.EmiHopperRecipe;
import emi.dev.emi.emi.recipe.btw.EmiProgressiveRecipe;
import emi.dev.emi.emi.runtime.EmiReloadLog;
import emi.shims.java.net.minecraft.util.SyntheticIdentifier;
import net.minecraft.src.IRecipe;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ResourceLocation;

import java.util.Comparator;
import java.util.function.Supplier;

public class DeNovoEmiPlugin implements EmiPlugin {


    @Override
    public void register(EmiRegistry reg) {
        // Categories
        reg.addCategory(DeNovoEmiRecipeCategories.SIEVE);
        reg.addWorkstation(DeNovoEmiRecipeCategories.SIEVE, EmiStack.of(new ItemStack(DNBlocks.sieve, 1, 0)));

        // Sieve
        for (SiftingRecipe siftingRecipe : SiftingCraftingManager.getRecipes()){
            LootEntry[] output = siftingRecipe.getLootTable();
            ItemStack filterUsed = siftingRecipe.getFilterUsed();
            ItemStack input = siftingRecipe.getInput();
            boolean containsSouls = siftingRecipe.getContainsSouls();
            addRecipeSafe(reg, () -> new EmiSieveRecipe(new SyntheticIdentifier((SiftingRecipe)siftingRecipe),
                (LootEntry[]) output, (ItemStack) input, (ItemStack)filterUsed, false));

        }

        // Progressive Crafting
        addRecipeSafe(reg, () -> new EmiProgressiveRecipe(new ResourceLocation("denovo", "maggots_silk_extraction"),
                new ItemStack(DNItems.maggotsSilkExtraction), new ItemStack(Item.silk)));
        addRecipeSafe(reg, () -> new EmiProgressiveRecipe(new ResourceLocation("denovo", "rust_water_bowl"),
                new ItemStack(DNItems.rustWaterBowl), new ItemStack(DNItems.ironDust)));
    }

    public static EmiRecipeCategory category(String id, EmiStack icon) {
        return new EmiRecipeCategory(new ResourceLocation("denovo", id), icon, new EmiTexture(new ResourceLocation("denovo", "textures/simple_icons/" + id + ".png"), 0, 0, 16, 16, 16, 16, 16, 16));
    }

    private static void addRecipeSafe(EmiRegistry registry, Supplier<EmiRecipe> supplier) {
        try {
            registry.addRecipe(supplier.get());
        } catch (Throwable e) {
            EmiReloadLog.warn("Exception when parsing EMI/DeNovo recipe (no ID available)");
            EmiReloadLog.error(e);
        }
    }

    private static void addRecipeSafe(EmiRegistry registry, Supplier<EmiRecipe> supplier, IRecipe recipe) {
        try {
            registry.addRecipe(supplier.get());
        } catch (Throwable e) {
            EmiReloadLog.warn("Exception when parsing DeNovo recipe " + recipe);
            EmiReloadLog.error(e);
        }
    }

    static {
        DeNovoEmiRecipeCategories.SIEVE = DeNovoEmiPlugin.category("sieve",  EmiStack.of(DNBlocks.sieve));
    }
}
