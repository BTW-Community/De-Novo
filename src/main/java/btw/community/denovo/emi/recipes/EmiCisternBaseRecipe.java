package btw.community.denovo.emi.recipes;

import btw.community.denovo.emi.DeNovoEmiRecipeCategories;
import emi.dev.emi.emi.EmiPort;
import emi.dev.emi.emi.api.recipe.BTWEmiRecipeCategories;
import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.api.recipe.EmiRecipeCategory;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.api.widget.WidgetHolder;
import emi.shims.java.com.unascribed.retroemi.RetroEMI;
import emi.shims.java.net.minecraft.client.gui.tooltip.TooltipComponent;
import java.util.List;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class EmiCisternBaseRecipe
        implements EmiRecipe {
    private final ResourceLocation id;
    private final EmiRecipeCategory category;

    private final EmiIngredient input;
    private final List<EmiStack> output;
    private final int processingTime;

    public EmiCisternBaseRecipe(ResourceLocation id, EmiRecipeCategory category,
                                ItemStack input, List<EmiStack> output, int processingTime) {
        this.id = id;
        this.category = category;
        this.input = RetroEMI.wildcardIngredient(input);
        this.output = output;
        this.processingTime = processingTime;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return this.category;
    }

    @Override
    @Nullable
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(this.input);
    }

    @Override
    public List<EmiStack> getOutputs() {
        return this.output;
    }

    @Override
    public int getDisplayWidth() {
        if (this.output.size() > 1){
            return 92;
        }
        return 74;
    }

    @Override
    public int getDisplayHeight() {
        return 22;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addFillingArrow(25, 3, 60000).tooltip((mx, my) -> List.of(TooltipComponent.of(EmiPort.ordered(EmiPort.translatable("emi.cooking.time", this.processingTime/20)))));
        widgets.addSlot(this.input, 3, 2);
        widgets.addSlot(this.output.get(0), 53, 2).recipeContext(this);
        if (this.output.size() > 1){
            widgets.addSlot(this.output.get(1), 71, 2).recipeContext(this);
        }
    }
}


