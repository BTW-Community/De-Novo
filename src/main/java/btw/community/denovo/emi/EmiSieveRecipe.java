/*
 * Decompiled with CFR 0.2.1 (FabricMC 53fa44c9).
 */
package btw.community.denovo.emi;

import btw.block.BTWBlocks;
import btw.community.denovo.block.DNBlocks;
import btw.community.denovo.item.DNItems;
import btw.community.denovo.recipes.LootEntry;
import btw.item.BTWItems;
import com.google.common.collect.Lists;
import emi.dev.emi.emi.EmiPort;
import emi.dev.emi.emi.api.recipe.BTWEmiRecipeCategories;
import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.api.recipe.EmiRecipeCategory;
import emi.dev.emi.emi.api.render.EmiTexture;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.api.widget.WidgetHolder;
import emi.shims.java.com.unascribed.retroemi.ItemStacks;
import emi.shims.java.com.unascribed.retroemi.RetroEMI;
import emi.shims.java.net.minecraft.client.gui.tooltip.TooltipComponent;
import emi.shims.java.net.minecraft.text.Text;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class EmiSieveRecipe
        implements EmiRecipe {
//    private static final ResourceLocation BACKGROUND = new ResourceLocation("denovo", "textures/emi/sieve.png");

    public static final EmiTexture SIEVE_ARROW_EMPTY = new EmiTexture(new ResourceLocation("denovo", "textures/emi/sieve_arrow.png"),
            0, 0, 40, 54, 40, 54,40, 54);
    public static final EmiTexture SIEVE_ARROW_FULL = new EmiTexture(new ResourceLocation("denovo", "textures/emi/sieve_arrow_overlay.png"),
            0, 0, 40, 54, 40, 54,40, 54);

    private final ResourceLocation id;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;
    private final boolean containsSouls;

    public EmiSieveRecipe(ResourceLocation hopperId, LootEntry[] lootTable, ItemStack input, ItemStack filterItem, boolean containsSouls) {
        this.id = hopperId;
        this.inputs = EmiSieveRecipe.convertInput(input, filterItem, containsSouls);
        this.outputs = EmiSieveRecipe.convertOutput(lootTable);
        this.containsSouls = containsSouls;
    }

    public static List<EmiStack> convertOutput(LootEntry[] lootEntries) {
        ArrayList<EmiStack> list = Lists.newArrayList();
        for (LootEntry entry : lootEntries){
            list.add(EmiStack.of(entry.getResult()).setAmount(entry.getAmount()).setChance((float)entry.getChance()));
        }

        return list;
    }

    public static List<EmiIngredient> convertInput(ItemStack input, ItemStack filterItem, boolean containsSouls) {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>(List.of(input, filterItem));
//        if (containsSouls) {
//            list.add(new ItemStack(BTWItems.urn));
//        }
        return list.stream().map(RetroEMI::wildcardIngredientWithStackSize).collect(Collectors.toList());
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return DeNovoEmiRecipeCategories.SIEVE;
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
    public List<EmiStack> getOutputs() {
        return this.outputs;
    }

    @Override
    public int getDisplayWidth() {
        return 100;
    }

    @Override
    public int getDisplayHeight() {
        return 70;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(SIEVE_ARROW_EMPTY, 20, 8);
        widgets.addAnimatedTexture(SIEVE_ARROW_FULL, 20, 8, 4000, true, false, false);

        //input
        widgets.addSlot(this.inputs.get(0), 2, 8).appendTooltip(Text.translatable("Input"));
        //filter
        widgets.addSlot(this.inputs.get(1), 32, 18).drawBack(false).appendTooltip(Text.translatable("Filter Item")).recipeContext(this);
        //machine
        widgets.addSlot( EmiStack.of(DNBlocks.sieve), 32, 33).drawBack(false).appendTooltip(Text.translatable("Right Click to process the Input Block"));
        //output
        for (int i = 0; i < Math.max(this.outputs.size(), 6); ++i) {
            if (i < this.outputs.size()) {
                widgets.addSlot(this.outputs.get(i), (i % 2 * 18) + 60, (4 + i / 2 * 18) + 4).appendTooltip(Text.translatable("Output")).recipeContext(this);
                continue;
            }
            widgets.addSlot(EmiStack.of(ItemStacks.EMPTY), (i % 2 * 18) + 60, (4 + i / 2 * 18) + 4);
        }

//        if (this.containsSouls) {
//            widgets.addTexture(BACKGROUND, 50, 51, 13, 3, 0, 70).tooltip((mx, my) -> Collections.singletonList(TooltipComponent.of(Text.translatable("Mechanical power strongly recommended", Float.valueOf(10.0f)).asOrderedText())));
//            widgets.addTexture(BACKGROUND, 33, 29, 13, 13, 0, 86);
//            widgets.addSlot(EmiStack.of(BTWItems.urn), 30, 45).appendTooltip(Text.translatable("emi.info.hopper_urn"));
//        }
    }
}

