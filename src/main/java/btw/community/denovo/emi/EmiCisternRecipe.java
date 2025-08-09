package btw.community.denovo.emi;

import btw.community.denovo.block.DNBlocks;
import btw.community.denovo.item.DNItems;
import btw.community.denovo.recipes.LootEntry;
import btw.community.denovo.utils.CisternUtils;
import com.google.common.collect.Lists;
import emi.dev.emi.emi.EmiUtil;
import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.api.recipe.EmiRecipeCategory;
import emi.dev.emi.emi.api.render.EmiTexture;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.api.widget.GeneratedSlotWidget;
import emi.dev.emi.emi.api.widget.SlotWidget;
import emi.dev.emi.emi.api.widget.WidgetHolder;
import emi.shims.java.com.unascribed.retroemi.ItemStacks;
import emi.shims.java.com.unascribed.retroemi.RetroEMI;
import emi.shims.java.net.minecraft.text.Text;
import net.minecraft.src.BlockColored;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class EmiCisternRecipe implements EmiRecipe {

    private final ResourceLocation id;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;

    public EmiCisternRecipe(ResourceLocation id, ItemStack[] outputs, ItemStack input, int fillType) {
        this.id = id;
        this.inputs = convertInput(input, fillType);
        this.outputs = convertOutput(outputs);
    }

    public static List<EmiIngredient> convertInput(ItemStack input, int fillType) {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>(List.of(input));
//        if (containsSouls) {
//            list.add(new ItemStack(BTWItems.urn));
//        }
        return list.stream().map(RetroEMI::wildcardIngredientWithStackSize).collect(Collectors.toList());
    }

    public static List<EmiStack> convertOutput(ItemStack[] outputs) {
        ArrayList<EmiStack> list = Lists.newArrayList();
        for (ItemStack stack : outputs){
            list.add(EmiStack.of(stack).setAmount(stack.stackSize));
        }

        return list;
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

    @Override
    public int getDisplayWidth() {
        return 80;
    }

    @Override
    public int getDisplayHeight() {
        return 50;
    }

    /*
    @Override
    public void addWidgets(WidgetHolder widgets) {
//        widgets.addSlot(this.inputs.get(0), 8, 16).appendTooltip(Text.translatable("Input"));
//
//        for (int i = 0; i < Math.max(this.outputs.size(), 2); ++i) {
//            if (i < this.outputs.size()) {
//                widgets.addSlot(this.outputs.get(i), 30 + (i * 18), 16).appendTooltip(Text.translatable("Output"));
//                continue;
//            }
//            widgets.addSlot(EmiStack.of(ItemStacks.EMPTY), 30 + (i * 18), 16);
//        }

        // List of EmiStacks with different item damage (fill levels)
        List<EmiStack> fillStages = new ArrayList<>();
        for (int level = 9; level <= 15; level++) {
            if (level == 9 || level == 12 || level == 15) {
                int damage = this.inputs.get(0).getEmiStacks().get(0).getItemStack().getItemDamage();
                int fillType = CisternUtils.getFillType(damage);
                int progress = CisternUtils.getProgress(damage);
                int newDamage = CisternUtils.pack(level, 0, fillType, progress);
                ItemStack cisternStack = new ItemStack(DNBlocks.cistern, 1, newDamage);
                EmiStack stack = EmiStack.of(cisternStack);
                fillStages.add(stack);
            }
        }
        EmiIngredient animatedCistern = EmiIngredient.of(fillStages);

        widgets.addSlot(animatedCistern, 50, 20)
                .drawBack(false) // Optional: draw background slot
                .recipeContext(this); // Connects it to this recipe


    }
    */

    @Override
    public void addWidgets(WidgetHolder widgets) {

        widgets.addSlot(this.inputs.get(0), 8, 16).appendTooltip(Text.translatable("Input"));

        widgets.addTexture(EmiTexture.EMPTY_ARROW, 28, 16);

        widgets.add(this.getOutputWidget(52, 16).drawBack(false));
    }

    public SlotWidget getInputWidget(int slot, int x, int y) {
        int s = slot;
        return new GeneratedSlotWidget(r -> {
            List<EmiStack> items = getInputs().get(0).getEmiStacks();
            if (s < items.size()) {
                return items.get(s);
            }
            return EmiStack.EMPTY;
        }, 0, x, y);
    }

    private int index = 0;
    private final int[] steps = {
            9, 12, 15
    };

    public SlotWidget getOutputWidget(int x, int y) {

        return new GeneratedSlotWidget(r -> {
            int value = steps[index];
            ItemStack stack = this.outputs.get(0).getItemStack();
            stack.setItemDamage(CisternUtils.pack(value, 0, CisternUtils.CONTENTS_WATER, 0));
            index = (index + 1) % steps.length;
            return EmiStack.of(stack);
        }, 0, x, y);
    }
}
