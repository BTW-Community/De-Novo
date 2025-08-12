package btw.community.denovo.emi;

import com.google.common.collect.Lists;
import emi.dev.emi.emi.EmiPort;
import emi.dev.emi.emi.api.recipe.BasicEmiRecipe;
import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.api.recipe.EmiRecipeCategory;
import emi.dev.emi.emi.api.render.EmiTexture;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.api.widget.WidgetHolder;
import emi.shims.java.com.unascribed.retroemi.RetroEMI;
import emi.shims.java.net.minecraft.client.gui.tooltip.TooltipComponent;
import emi.shims.java.net.minecraft.text.Text;
import emi.shims.java.net.minecraft.util.Formatting;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EmiComposterRecipe extends EmiCisternRecipe {

    public EmiComposterRecipe(ResourceLocation id, ItemStack input, EmiStack output, int processingTime, String inputString, String outputString){
        super(id, input, output, processingTime, inputString, outputString);

    }
    @Override
    public EmiRecipeCategory getCategory() {
        return DeNovoEmiRecipeCategories.COMPOSTER;
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


    public static List<EmiIngredient> convertInput(ItemStack stack) {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>(List.of(stack));
        return list.stream().map(RetroEMI::wildcardIngredientWithStackSize).collect(Collectors.toList());
    }

    public static List<EmiStack> convertOutput(EmiStack output) {
        ArrayList<EmiStack> list = Lists.newArrayList();
        list.add(output.setAmount(output.getItemStack().stackSize));
        return list;
    }

}
