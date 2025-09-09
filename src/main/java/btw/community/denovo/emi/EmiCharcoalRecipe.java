package btw.community.denovo.emi;

import btw.community.denovo.block.DNBlocks;
import emi.dev.emi.emi.EmiPort;
import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.api.recipe.EmiRecipeCategory;
import emi.dev.emi.emi.api.render.EmiTexture;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.api.widget.WidgetHolder;
import emi.shims.java.com.unascribed.retroemi.RetroEMI;
import emi.shims.java.net.minecraft.client.gui.tooltip.TooltipComponent;
import emi.shims.java.net.minecraft.text.Text;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiFunction;

public class EmiCharcoalRecipe implements EmiRecipe {
    private final ResourceLocation id;
    private final EmiIngredient input;
    private final EmiIngredient output;
    private String arrowToolTip = null;

    public EmiCharcoalRecipe(ResourceLocation id, ItemStack input, ItemStack output) {
        this.id = id;
        this.input = RetroEMI.wildcardIngredient(input);
        this.output = RetroEMI.wildcardIngredient(output);
    }

    public EmiRecipe setArrowToolTip(String s) {
        this.arrowToolTip = s;
        return this;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return DeNovoEmiRecipeCategories.CHARCOAL;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return this.id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(this.input);
    }

    @Override
    public List<EmiStack> getOutputs() {
        return this.output.getEmiStacks();
    }

    @Override
    public int getDisplayWidth() {
        return 74;
    }

    @Override
    public int getDisplayHeight() {
        return 22;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        if (this.output.getEmiStacks().get(0).getItemStack().itemID == DNBlocks.charcoalPile.blockID){
            widgets.addFillingArrow(25, 3, 60000).tooltip((mx, my) -> List.of(TooltipComponent.of(EmiPort.ordered(EmiPort.translatable("denovo.emi.charcoal_creation")))));
        }
        else {
            if (this.arrowToolTip != null){
                widgets.addTexture(EmiTexture.EMPTY_ARROW, 25, 3).tooltip((mx, my) -> List.of(TooltipComponent.of(EmiPort.ordered(EmiPort.translatable(arrowToolTip)))));
            }
            else widgets.addTexture(EmiTexture.EMPTY_ARROW, 25, 3);
        }

        widgets.addSlot(this.input, 3, 2);
        widgets.addSlot(this.output, 53, 2).recipeContext(this);
    }


}
