package btw.community.denovo.emi;

import com.google.common.collect.Lists;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EmiCisternRecipe implements EmiRecipe {

    protected final ResourceLocation id;

    protected final List<EmiIngredient> inputs;
    protected final List<EmiStack> outputs;
    protected List<EmiStack> secondaryOutput = null;
    protected String processingTime;
    protected String inputString;
    protected String outputString;


    public EmiCisternRecipe(ResourceLocation id, ItemStack input, EmiStack output, int processingTime, String inputString, String outputString){
        this.id = id;
        this.inputs = convertInput(input);
        this.outputs = convertOutput(output);
        this.processingTime = String.valueOf(processingTime);
        this.inputString = inputString;
        this.outputString = outputString;
    }

    public EmiCisternRecipe(ResourceLocation id, ItemStack input, ArrayList<EmiStack> output, int processingTime, String inputString, String outputString){
        this.id = id;
        this.inputs = convertInput(input);
        this.outputs = output;
        this.processingTime = String.valueOf(processingTime);
        this.inputString = inputString;
        this.outputString = outputString;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return DeNovoEmiRecipeCategories.CISTERN;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return this.id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return this.inputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return this.outputs;
    }

    public List<EmiStack> getSecondaryOutput() {
        return secondaryOutput;
    }

    @Override
    public int getDisplayWidth() {
        return 96;
    }

    @Override
    public int getDisplayHeight() {
        return 32;
    }

    public static List<EmiIngredient> convertInput(ItemStack stack) {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>(List.of(stack));
        return list.stream().map(RetroEMI::wildcardIngredientWithStackSize).collect(Collectors.toList());
    }

    public static List<EmiStack> convertOutput(EmiStack output) {
        ArrayList list = new ArrayList();
        list.add(output);
        return list;
    }


    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(this.inputs.get(0), 8, 8).appendTooltip(Text.translatable(inputString));

        widgets.addTexture(EmiTexture.EMPTY_ARROW, 32, 8).tooltip((mx, my) -> List.of(TooltipComponent.of(EmiPort.ordered(EmiPort.translatable("emi.cooking.time", this.processingTime)))));
        widgets.addAnimatedTexture(EmiTexture.FULL_ARROW, 32, 8, 40000, true, false, false);

        if (this.secondaryOutput != null){
            widgets.addSlot(this.outputs.get(0), 56, 8).appendTooltip(Text.translatable(outputString));
            widgets.addSlot(this.outputs.get(1), 56 + 17, 8).recipeContext(this);
        }
        else widgets.addSlot(this.outputs.get(0), 56+8, 9).appendTooltip(Text.translatable(outputString)).recipeContext(this);
    }
}
