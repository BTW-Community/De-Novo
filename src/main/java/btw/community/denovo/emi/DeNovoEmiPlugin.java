package btw.community.denovo.emi;

import btw.community.denovo.item.DNItems;
import btw.crafting.recipe.types.customcrafting.FishingRodBaitingRecipe;
import btw.item.BTWItems;
import emi.dev.emi.emi.api.EmiPlugin;
import emi.dev.emi.emi.api.EmiRegistry;
import emi.dev.emi.emi.api.plugin.BTWPlugin;
import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.recipe.btw.EmiProgressiveRecipe;
import emi.dev.emi.emi.runtime.EmiReloadLog;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ResourceLocation;

import java.util.function.Supplier;

public class DeNovoEmiPlugin implements EmiPlugin {
    @Override
    public void register(EmiRegistry reg) {

        // Progressive Crafting
        addRecipeSafe(reg, () -> new EmiProgressiveRecipe(new ResourceLocation("denovo", "maggots_silk_extraction"),
                new ItemStack(DNItems.maggotsSilkExtraction), new ItemStack(Item.silk)));
        addRecipeSafe(reg, () -> new EmiProgressiveRecipe(new ResourceLocation("denovo", "rust_water_bowl"),
                new ItemStack(DNItems.rustWaterBowl), new ItemStack(DNItems.ironDust)));
    }

    private static void addRecipeSafe(EmiRegistry registry, Supplier<EmiRecipe> supplier) {
        try {
            registry.addRecipe(supplier.get());
        } catch (Throwable e) {
            EmiReloadLog.warn("Exception when parsing EMI recipe (no ID available)");
            EmiReloadLog.error(e);
        }
    }
}
