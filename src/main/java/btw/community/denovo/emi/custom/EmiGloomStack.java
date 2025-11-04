package btw.community.denovo.emi.custom;

import emi.dev.emi.emi.api.render.EmiTooltipComponents;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.runtime.EmiDrawContext;
import emi.shims.java.net.minecraft.client.gui.DrawContext;
import emi.shims.java.net.minecraft.client.gui.tooltip.TooltipComponent;
import emi.shims.java.net.minecraft.text.Text;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class EmiGloomStack extends EmiStack {
    private static final ResourceLocation ID = new ResourceLocation("denovo", "gloom");
    private static final ResourceLocation RAIN_TEXTURE = new ResourceLocation("denovo", "textures/emi/gloom.png");
    public static EmiGloomStack RAIN = new EmiGloomStack();

    @Override
    public void render(DrawContext draw, int x, int y, float delta, int flags) {
        draw.getMatrices().push();
        EmiDrawContext context = EmiDrawContext.wrap(draw);
        context.drawTexture(RAIN_TEXTURE, x, y, 0, 0, 0, 16, 16, 16, 16);
        draw.getMatrices().pop();
    }

    @Override
    public EmiStack copy() {
        return this;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public NBTTagCompound getNbt() {
        return null;
    }

    @Override
    public Object getKey() {
        return null; //I have no idea what this does :)
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public boolean isEqual(EmiStack stack) {
        return stack == RAIN;
    }

    @Override
    public List<Text> getTooltipText() {
        return List.of(Text.translatable("emi.denovo.gloom.desc"));
    }

    @Override
    public List<TooltipComponent> getTooltip() {
        List<TooltipComponent> list = new ArrayList<>();
        list.add(TooltipComponent.of(Text.translatable("emi.denovo.gloom")));
        EmiTooltipComponents.appendModName(list, "Better Than Wolves");
        return list;
    }

    @Override
    public Text getName() {
        return Text.translatable("emi.denovo.gloom");
    }

    @Override
    public List<EmiStack> getEmiStacks() {
        return List.of(this);
    }
}