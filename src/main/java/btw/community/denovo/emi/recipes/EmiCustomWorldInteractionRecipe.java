package btw.community.denovo.emi.recipes;

import btw.community.denovo.emi.tag.DeNovoTags;
import btw.item.BTWItems;
import btw.item.tag.Tag;
import btw.item.tag.TagInstance;
import btw.item.tag.TagOrStack;
import com.google.common.collect.Lists;
import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.api.recipe.EmiRecipeCategory;
import emi.dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import emi.dev.emi.emi.api.render.EmiTexture;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.api.widget.SlotWidget;
import emi.dev.emi.emi.api.widget.WidgetHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import emi.dev.emi.emi.registry.EmiTags;
import net.minecraft.src.Block;
import net.minecraft.src.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class EmiCustomWorldInteractionRecipe
        implements EmiRecipe {
    private final ResourceLocation id;
    private final List<EmiCustomWorldInteractionRecipe.WorldIngredient> left;
    private final List<EmiCustomWorldInteractionRecipe.WorldIngredient> right;
    private final List<EmiCustomWorldInteractionRecipe.WorldIngredient> outputIngredients;
    private final List<EmiIngredient> inputs;
    private final List<EmiIngredient> catalysts;
    private final List<EmiIngredient> outputs;
    private final boolean supportsRecipeTree;
    private final int slotHeight;
    private int width = 125;
    private int height;
    private int totalSize;
    private int leftSize;
    private int rightSize;
    private int outputSize;
    private int leftHeight;
    private int rightHeight;
    private int outputHeight;

    protected EmiCustomWorldInteractionRecipe(EmiCustomWorldInteractionRecipe.Builder builder) {
        this.id = builder.id;
        this.left = builder.left;
        this.right = builder.right;
        this.inputs = Stream.concat(this.left.stream(), this.right.stream()).filter(i -> !i.catalyst).map(i -> i.stack).collect(Collectors.toList());
        this.catalysts = Stream.concat(this.left.stream(), this.right.stream()).filter(i -> i.catalyst).map(i -> i.stack).collect(Collectors.toList());
        this.outputIngredients = builder.output;
        this.outputs = builder.output.stream().map(i -> (EmiIngredient)i.stack).collect(Collectors.toList());
        this.supportsRecipeTree = builder.supportsRecipeTree;
        for (EmiIngredient catalyst : this.catalysts) {
            for (EmiStack stack : catalyst.getEmiStacks()) {
                if (!stack.getRemainder().isEmpty()) continue;
                stack.setRemainder(stack.copy());
            }
        }
        this.totalSize = this.left.size() + this.right.size() + this.outputs.size();
        if (this.totalSize > 5) {
            int[] portions = new int[]{this.left.size(), this.right.size(), this.outputs.size()};
            int[] sizes = new int[]{1, 1, 1};
            for (int i2 = 0; i2 < 2; ++i2) {
                int largest = portions[0];
                int li = 0;
                for (int j = 1; j < 3; ++j) {
                    if (portions[j] < largest) continue;
                    largest = portions[j];
                    li = j;
                }
                int n = li;
                sizes[n] = sizes[n] + 1;
                portions[li] = portions[li] * 2 / 3;
            }
            this.leftSize = sizes[0];
            this.rightSize = sizes[1];
            this.outputSize = sizes[2];
        } else {
            this.leftSize = this.left.size();
            this.rightSize = this.right.size();
            this.outputSize = this.outputs.size();
        }
        this.leftHeight = (this.left.size() - 1) / this.leftSize + 1;
        this.rightHeight = (this.right.size() - 1) / this.rightSize + 1;
        this.outputHeight = (this.outputs.size() - 1) / this.outputSize + 1;
        this.slotHeight = Math.max(this.leftHeight, Math.max(this.rightHeight, this.outputHeight));
        this.height = this.slotHeight * 18;
        if (this.totalSize > 4) {
            this.width = 134;
        }
    }

    public static EmiCustomWorldInteractionRecipe.Builder builder() {
        return new EmiCustomWorldInteractionRecipe.Builder();
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return VanillaEmiRecipeCategories.WORLD_INTERACTION;
    }

    @Override
    @Nullable
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return this.inputs;
    }

    @Override
    public List<EmiIngredient> getCatalysts() {
        return this.catalysts;
    }

    @Override
    public List<EmiStack> getOutputs() {
        List<EmiStack> newList = new ArrayList<>();
        for (EmiIngredient ingredient : this.outputs) {
            newList.addAll(ingredient.getEmiStacks());
        }
        return newList;
    }

    @Override
    public boolean supportsRecipeTree() {
        return this.supportsRecipeTree;
    }

    @Override
    public int getDisplayWidth() {
        return this.width;
    }

    @Override
    public int getDisplayHeight() {
        return this.height;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        EmiCustomWorldInteractionRecipe.WorldIngredient wi;
        int i;
        int lr = this.leftSize * 18;
        int ol = this.width - this.outputSize * 18;
        int rl = (lr + ol) / 2 - this.rightSize * 9 - 4;
        int rr = rl + this.rightSize * 18;
        widgets.addTexture(EmiTexture.PLUS, (lr + rl) / 2 - EmiTexture.PLUS.width / 2, -6 + this.slotHeight * 9);
        widgets.addTexture(EmiTexture.EMPTY_ARROW, (rr + ol) / 2 - EmiTexture.EMPTY_ARROW.width / 2, -8 + this.slotHeight * 9);
        int yo = (this.slotHeight - this.leftHeight) * 9;
        for (i = 0; i < this.left.size(); ++i) {
            wi = this.left.get(i);
            widgets.add(wi.mutator.apply(new SlotWidget(wi.stack, i % this.leftSize * 18, yo + i / this.leftSize * 18)));
        }
        yo = (this.slotHeight - this.rightHeight) * 9;
        for (i = 0; i < this.right.size(); ++i) {
            wi = this.right.get(i);
            widgets.add(wi.mutator.apply(new SlotWidget(wi.stack, rl + i % this.rightSize * 18, yo + i / this.rightSize * 18).catalyst(wi.catalyst)));
        }
        yo = (this.slotHeight - this.outputHeight) * 9;
        for (i = 0; i < this.outputIngredients.size(); ++i) {
            wi = this.outputIngredients.get(i);
            widgets.add(wi.mutator.apply(new SlotWidget(wi.stack, ol + i % this.outputSize * 18, yo + i / this.outputSize * 18)).recipeContext(this));
        }
    }

    public static class Builder {
        private final List<EmiCustomWorldInteractionRecipe.WorldIngredient> left = Lists.newArrayList();
        private final List<EmiCustomWorldInteractionRecipe.WorldIngredient> right = Lists.newArrayList();
        private final List<EmiCustomWorldInteractionRecipe.WorldIngredient> output = Lists.newArrayList();
        private boolean supportsRecipeTree = true;
        private ResourceLocation id = null;

        private Builder() {
        }

        public EmiCustomWorldInteractionRecipe build() {
            if (this.left.isEmpty()) {
                throw new IllegalStateException("Cannot create a world interaction recipe without a left input");
            }
            if (this.right.isEmpty()) {
                throw new IllegalStateException("Cannot create a world interaction recipe without a right input");
            }
            if (this.output.isEmpty()) {
                throw new IllegalStateException("Cannot create a world interaction recipe without an output");
            }
            return new EmiCustomWorldInteractionRecipe(this);
        }

        public EmiCustomWorldInteractionRecipe.Builder id(ResourceLocation id) {
            this.id = id;
            return this;
        }

        public EmiCustomWorldInteractionRecipe.Builder leftInput(EmiIngredient stack) {
            this.left.add(new EmiCustomWorldInteractionRecipe.WorldIngredient(stack, false, s -> s));
            return this;
        }

        public EmiCustomWorldInteractionRecipe.Builder leftInput(EmiIngredient stack, Function<SlotWidget, SlotWidget> mutator) {
            this.left.add(new EmiCustomWorldInteractionRecipe.WorldIngredient(stack, false, mutator));
            return this;
        }

        public EmiCustomWorldInteractionRecipe.Builder leftInput(List<EmiIngredient> stacks) {
            for (EmiIngredient stack : stacks){
                this.left.add(new EmiCustomWorldInteractionRecipe.WorldIngredient(stack, false, s -> s));
            }
            return this;
        }

        public EmiCustomWorldInteractionRecipe.Builder rightInput(EmiIngredient stack, boolean catalyst) {
            this.right.add(new EmiCustomWorldInteractionRecipe.WorldIngredient(stack, catalyst, s -> s));
            return this;
        }

        public EmiCustomWorldInteractionRecipe.Builder rightInput(EmiIngredient stack, boolean catalyst, Function<SlotWidget, SlotWidget> mutator) {
            this.right.add(new EmiCustomWorldInteractionRecipe.WorldIngredient(stack, catalyst, mutator));
            return this;
        }

        public EmiCustomWorldInteractionRecipe.Builder output(List<EmiIngredient> stacks) {
            for (EmiIngredient stack : stacks){
                this.output.add(new EmiCustomWorldInteractionRecipe.WorldIngredient(stack, false, s -> s));
            }
            return this;
        }

        public EmiCustomWorldInteractionRecipe.Builder output(EmiIngredient stack) {
            this.output.add(new EmiCustomWorldInteractionRecipe.WorldIngredient(stack, false, s -> s));
            return this;
        }

        public EmiCustomWorldInteractionRecipe.Builder output(EmiIngredient stack, Function<SlotWidget, SlotWidget> mutator) {
            this.output.add(new EmiCustomWorldInteractionRecipe.WorldIngredient(stack, false, mutator));
            return this;
        }

        public EmiCustomWorldInteractionRecipe.Builder supportsRecipeTree(boolean supportsRecipeTree) {
            this.supportsRecipeTree = supportsRecipeTree;
            return this;
        }
    }

    private record WorldIngredient(EmiIngredient stack, boolean catalyst, Function<SlotWidget, SlotWidget> mutator) {
    }
}


