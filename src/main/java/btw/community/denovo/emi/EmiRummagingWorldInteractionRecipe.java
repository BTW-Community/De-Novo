package btw.community.denovo.emi;

import btw.community.denovo.block.DNBlocks;
import btw.community.denovo.block.blocks.CisternBaseBlock;
import btw.community.denovo.item.DNItems;
import btw.community.denovo.utils.CisternUtils;
import com.google.common.collect.Lists;
import emi.dev.emi.emi.EmiPort;
import emi.dev.emi.emi.EmiRenderHelper;
import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.api.recipe.EmiRecipeCategory;
import emi.dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import emi.dev.emi.emi.api.render.EmiTexture;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.api.widget.GeneratedSlotWidget;
import emi.dev.emi.emi.api.widget.SlotWidget;
import emi.dev.emi.emi.api.widget.WidgetHolder;
import emi.shims.java.net.minecraft.client.gui.tooltip.TooltipComponent;
import emi.shims.java.net.minecraft.text.Text;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EmiRummagingWorldInteractionRecipe implements EmiRecipe {

    protected final ResourceLocation id;
    protected final List<EmiRummagingWorldInteractionRecipe.WorldIngredient> left;
    protected final List<EmiRummagingWorldInteractionRecipe.WorldIngredient> right;
    protected final List<EmiRummagingWorldInteractionRecipe.WorldIngredient> outputIngredients;
    protected final List<EmiIngredient> inputs;
    protected final List<EmiIngredient> catalysts;
    protected final List<EmiStack> outputs;
    protected final List<Integer> liquidSteps;
    protected final List<Integer> solidSteps;
    protected final boolean supportsRecipeTree;
    protected final int slotHeight;
    protected final String arrowToolTip;
    protected final EmiTexture renderPlusOverlay;
    protected int width = 125;
    protected int height;
    protected int totalSize;
    protected int leftSize;
    protected int rightSize;
    protected int outputSize;
    protected int leftHeight;
    protected int rightHeight;
    protected int outputHeight;
    protected boolean renderInputBack;
    protected boolean renderInput2Back;
    protected boolean renderOutputBack;


    protected EmiRummagingWorldInteractionRecipe(Builder builder) {
        this.id = builder.id;
        this.left = builder.left;
        this.right = builder.right;
        this.liquidSteps = builder.liquidSteps;
        this.solidSteps = builder.solidSteps;
        this.arrowToolTip = builder.arrowToolTip;
        this.renderInputBack = builder.renderInputBack;
        this.renderInput2Back = builder.renderInput2Back;
        this.renderOutputBack = builder.renderOutputBack;
        this.renderPlusOverlay = builder.renderPlusOverlay;
        this.inputs = Stream.concat(this.left.stream(), this.right.stream()).filter(i -> !i.catalyst).map(i -> i.stack).collect(Collectors.toList());
        this.catalysts = Stream.concat(this.left.stream(), this.right.stream()).filter(i -> i.catalyst).map(i -> i.stack).collect(Collectors.toList());
        this.outputIngredients = builder.output;
        this.outputs = builder.output.stream().map(i -> (EmiStack)i.stack).collect(Collectors.toList());
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
        this.height = (this.slotHeight * 18) + 12;
        if (this.totalSize > 4) {
            this.width = 134;
        }
    }

    public static EmiRummagingWorldInteractionRecipe.Builder builder() {
        return new EmiRummagingWorldInteractionRecipe.Builder();
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
        return this.outputs;
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
        WorldIngredient wi;
        int i;
        int lr = this.leftSize * 18;
        int ol = this.width - this.outputSize * 18;
        int rl = (lr + ol) / 2 - this.rightSize * 9 - 4;
        int rr = rl + this.rightSize * 18;
        widgets.addTexture(EmiTexture.PLUS, (lr + rl) / 2 - 16 / 2, (-6 + this.slotHeight * 9)+6);
        if (this.arrowToolTip != null) {
            widgets.addTexture(EmiTexture.EMPTY_ARROW, (rr + ol) / 2 - EmiTexture.EMPTY_ARROW.width / 2, (-8 + this.slotHeight * 9) + 6)
                    .tooltip((mx, my) -> List.of(TooltipComponent.of(EmiPort.ordered(EmiPort.translatable(this.arrowToolTip)))));
//            widgets.addTexture(this.TEXTURE, 10 * i + 25, 5, 9, 9, 16, 27);
        }
        else widgets.addTexture(EmiTexture.EMPTY_ARROW, (rr + ol) / 2 - EmiTexture.EMPTY_ARROW.width / 2, (-8 + this.slotHeight * 9) + 6);
        int yo = (this.slotHeight - this.leftHeight) * 9;
        for (i = 0; i < this.left.size(); ++i) {
            wi = this.left.get(i);
            ItemStack item = wi.stack.getEmiStacks().get(0).getItemStack();
            if (item != null && (item.itemID == DNBlocks.cistern.blockID || item.itemID == DNBlocks.composter.blockID)){
                int fillType = CisternUtils.getFillType(wi.stack.getEmiStacks().get(0).getItemStack().getItemDamage());
                widgets.add(wi.mutator.apply(new SlotWidget(wi.stack, i % this.leftSize * 18, yo + i / this.leftSize * 18)))
                        .appendTooltip(getFillTypeString(fillType))
                        .drawBack(this.renderInputBack);
            }
            else if (wi.stack.getEmiStacks().get(0).isEmpty()){
                if (this.renderPlusOverlay != null) {
                    widgets.addTexture(this.renderPlusOverlay, (i % this.leftSize * 18) + 22, (yo + i / this.leftSize * 18) + 6)
                            .tooltip((mx, my) -> List.of(TooltipComponent.of(EmiPort.ordered(EmiPort.translatable("denovo.emi.shift_right_click")))));
                };
                widgets.add(wi.mutator.apply(new SlotWidget(wi.stack, i % this.leftSize * 18, (yo + i / this.leftSize * 18) + 6)))
                        .drawBack(this.renderInputBack);
            }
            else widgets.add(wi.mutator.apply(new SlotWidget(wi.stack, i % this.leftSize * 18, (yo + i / this.leftSize * 18) + 6)))
                        .drawBack(this.renderInputBack);

        }
        yo = (this.slotHeight - this.rightHeight) * 9;
        for (i = 0; i < this.right.size(); ++i) {
            wi = this.right.get(i);
            ItemStack item = wi.stack.getEmiStacks().get(0).getItemStack();
            if (item != null && (item.itemID == DNBlocks.cistern.blockID || item.itemID == DNBlocks.composter.blockID)){
                int fillType = CisternUtils.getFillType(wi.stack.getEmiStacks().get(0).getItemStack().getItemDamage());
                widgets.add(wi.mutator.apply(new SlotWidget(wi.stack, rl + i % this.rightSize * 18, (yo + i / this.rightSize * 18) + 6).catalyst(wi.catalyst)))
                        .appendTooltip(getFillTypeString(fillType))
                        .drawBack(this.renderInput2Back);
            }
            else widgets.add(wi.mutator.apply(new SlotWidget(wi.stack, rl + i % this.rightSize * 18, (yo + i / this.rightSize * 18) + 6).catalyst(wi.catalyst)))
                    .drawBack(this.renderInput2Back);
        }
        yo = (this.slotHeight - this.outputHeight) * 9;
        for (i = 0; i < this.outputIngredients.size(); ++i) {
            wi = this.outputIngredients.get(i);

//            ItemStack item = wi.stack.getEmiStacks().get(0).getItemStack();
//            if (item != null && (item.itemID == DNBlocks.cistern.blockID || item.itemID == DNBlocks.composter.blockID)){
//                int fillType = CisternUtils.getFillType(wi.stack.getEmiStacks().get(0).getItemStack().getItemDamage());
//                widgets.add(wi.mutator.apply(this.getWidget(wi.stack, ol + i % this.outputSize * 18, yo + i / this.outputSize * 18)).recipeContext(this))
//                        .appendTooltip(getFillTypeString(fillType))
//                        .drawBack(this.renderOutputBack);
//            }
//            else widgets.add(wi.mutator.apply(this.getWidget(wi.stack, ol + i % this.outputSize * 18, yo + i / this.outputSize * 18)).recipeContext(this))
//                    .drawBack(this.renderOutputBack);

            widgets.add(wi.mutator.apply(this.getWidget(EmiStack.of(Block.grass), ol + i % this.outputSize * 18, (yo + i / this.outputSize * 18) + 8 + 4)))
                    .drawBack(this.renderOutputBack);
            widgets.add(wi.mutator.apply(this.getWidget(EmiStack.of(new ItemStack(Block.deadBush, 1, 3)).setChance(0.25F), ol + i % this.outputSize * 18, (yo + i / this.outputSize * 18) - 8 + 4)))
                    .drawBack(this.renderOutputBack);

        }
    }

    private Text getFillTypeString(int fillType) {
        if (fillType == CisternUtils.CONTENTS_WATER) return Text.translatable("denovo.emi.water");
        if (fillType == CisternUtils.CONTENTS_MUDDY_WATER) return Text.translatable("denovo.emi.water.muddy");
        if (fillType == CisternUtils.CONTENTS_COMPOST) return Text.translatable("denovo.emi.compost");
        if (fillType == CisternUtils.CONTENTS_MAGGOTS) return Text.translatable("denovo.emi.maggots");
        if (fillType == CisternUtils.CONTENTS_CLAY_WATER) return Text.translatable("denovo.emi.water.clay");
        if (fillType == CisternUtils.CONTENTS_INFECTED_WATER) return Text.translatable("denovo.emi.water.infected");
        if (fillType == CisternUtils.CONTENTS_RUST_WATER) return Text.translatable("denovo.emi.water.rust");
        return Text.translatable("denovo.emi.cistern.empty");
    }

    private int index = 0;

    public SlotWidget getWidget(EmiIngredient stack, int x, int y) {

        if (liquidSteps.size() > 0 && builder().solidSteps.size() > 0){
            return new GeneratedSlotWidget(r -> {
//                int value = liquidSteps.get(index);
                ItemStack item = stack.getEmiStacks().get(0).getItemStack();
                int damage = item.getItemDamage();
                int liquidLevel = CisternUtils.getLiquidFillLevel(damage);
                int solidLevel = CisternUtils.getSolidFillLevel(damage);
                int fillType = CisternUtils.getFillType(damage);
                int progress = CisternUtils.getProgress(damage);
                item.setItemDamage(CisternUtils.pack(liquidSteps.get(index), solidSteps.get(index), fillType, progress));
                index = (index + 1) % liquidSteps.size();
                return EmiStack.of(item);
            }, 0, x, y);
        }
        else if (liquidSteps.size() > 0){
            return new GeneratedSlotWidget(r -> {
                int value = liquidSteps.get(index);
                ItemStack item = stack.getEmiStacks().get(0).getItemStack();
                int damage = item.getItemDamage();
                int liquidLevel = CisternUtils.getLiquidFillLevel(damage);
                int solidLevel = CisternUtils.getSolidFillLevel(damage);
                int fillType = CisternUtils.getFillType(damage);
                int progress = CisternUtils.getProgress(damage);
                item.setItemDamage(CisternUtils.pack(value, 0, fillType, progress));
                index = (index + 1) % liquidSteps.size();
                return EmiStack.of(item);
            }, 0, x, y);
        }
        else if (solidSteps.size() > 0){
            return new GeneratedSlotWidget(r -> {
                int value = solidSteps.get(index);
                ItemStack item = stack.getEmiStacks().get(0).getItemStack();
                int damage = item.getItemDamage();
                int liquidLevel = CisternUtils.getLiquidFillLevel(damage);
                int solidLevel = CisternUtils.getSolidFillLevel(damage);
                int fillType = CisternUtils.getFillType(damage);
                int progress = CisternUtils.getProgress(damage);
                item.setItemDamage(CisternUtils.pack(0, value, fillType, progress));
                index = (index + 1) % solidSteps.size();
                return EmiStack.of(item);
            }, 0, x, y);
        }
        else return new SlotWidget(stack, x,y);


    }

    public static class Builder {
        protected final List<EmiRummagingWorldInteractionRecipe.WorldIngredient> left = Lists.newArrayList();
        protected final List<EmiRummagingWorldInteractionRecipe.WorldIngredient> right = Lists.newArrayList();
        protected final List<EmiRummagingWorldInteractionRecipe.WorldIngredient> output = Lists.newArrayList();
        protected final List<Integer> liquidSteps = Lists.newArrayList();
        protected final List<Integer> solidSteps = Lists.newArrayList();
        protected boolean renderInputBack;
        protected boolean renderInput2Back;
        protected boolean renderOutputBack;
        protected EmiTexture renderPlusOverlay;
        protected boolean supportsRecipeTree = true;
        protected ResourceLocation id = null;
        protected String arrowToolTip = null;

        private Builder() {
        }

        public EmiRummagingWorldInteractionRecipe build() {
            if (this.left.isEmpty()) {
                throw new IllegalStateException("Cannot create a world interaction recipe without a left input");
            }
            if (this.right.isEmpty()) {
                throw new IllegalStateException("Cannot create a world interaction recipe without a right input");
            }
            if (this.output.isEmpty()) {
                throw new IllegalStateException("Cannot create a world interaction recipe without an output");
            }
            return new EmiRummagingWorldInteractionRecipe(this);
        }

        public EmiRummagingWorldInteractionRecipe.Builder id(ResourceLocation id) {
            this.id = id;
            return this;
        }

        public EmiRummagingWorldInteractionRecipe.Builder leftInput(EmiIngredient stack) {
            this.left.add(new EmiRummagingWorldInteractionRecipe.WorldIngredient(stack, false, s -> s));
            return this;
        }

        public EmiRummagingWorldInteractionRecipe.Builder leftInput(EmiIngredient stack, Function<SlotWidget, SlotWidget> mutator) {
            this.left.add(new EmiRummagingWorldInteractionRecipe.WorldIngredient(stack, false, mutator));
            return this;
        }

        public EmiRummagingWorldInteractionRecipe.Builder rightInput(EmiIngredient stack, boolean catalyst) {
            this.right.add(new EmiRummagingWorldInteractionRecipe.WorldIngredient(stack, catalyst, s -> s));
            return this;
        }

        public EmiRummagingWorldInteractionRecipe.Builder rightInput(EmiIngredient stack, boolean catalyst, Function<SlotWidget, SlotWidget> mutator) {
            this.right.add(new EmiRummagingWorldInteractionRecipe.WorldIngredient(stack, catalyst, mutator));
            return this;
        }

        public EmiRummagingWorldInteractionRecipe.Builder output(EmiStack stack) {
            this.output.add(new EmiRummagingWorldInteractionRecipe.WorldIngredient(stack, false, s -> s));
            return this;
        }

        public EmiRummagingWorldInteractionRecipe.Builder output(EmiStack stack, Function<SlotWidget, SlotWidget> mutator) {
            this.output.add(new EmiRummagingWorldInteractionRecipe.WorldIngredient(stack, false, mutator));
            return this;
        }

        public EmiRummagingWorldInteractionRecipe.Builder supportsRecipeTree(boolean supportsRecipeTree) {
            this.supportsRecipeTree = supportsRecipeTree;
            return this;
        }

        public EmiRummagingWorldInteractionRecipe.Builder animateOutputContents(int[] liquidsteps, int[] solidsteps) {
            if (liquidsteps != null) {
                for (int step : liquidsteps){
                    this.liquidSteps.add(step);
                }
            }

            if (solidsteps != null){
                for (int step : solidsteps){
                    this.solidSteps.add(step);
                }
            }
            return this;
        }

        public EmiRummagingWorldInteractionRecipe.Builder setArrowToolTip(String s) {
            this.arrowToolTip = s;
            return this;
        }

        public EmiRummagingWorldInteractionRecipe.Builder setRenderBack(boolean renderInputBack, boolean renderInput2Back,boolean renderOutputBack) {
            this.renderInputBack = renderInputBack;
            this.renderInput2Back = renderInput2Back;
            this.renderOutputBack = renderOutputBack;
            return this;
        }

        public EmiRummagingWorldInteractionRecipe.Builder renderPlusOverlay(EmiTexture emiTexture) {
            this.renderPlusOverlay = emiTexture;
            return this;
        }
    }

    private record WorldIngredient(EmiIngredient stack, boolean catalyst, Function<SlotWidget, SlotWidget> mutator) {
    }
}
