package btw.community.denovo.emi;

import emi.dev.emi.emi.EmiPort;
import emi.dev.emi.emi.EmiUtil;
import emi.dev.emi.emi.api.render.EmiTooltipComponents;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.config.EmiConfig;
import emi.dev.emi.emi.runtime.EmiDrawContext;
import emi.shims.java.net.minecraft.client.gui.DrawContext;
import emi.shims.java.net.minecraft.client.gui.tooltip.TooltipComponent;
import emi.shims.java.net.minecraft.text.Text;
import emi.shims.java.net.minecraft.util.Formatting;
import net.minecraft.src.Item;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class EmiRainStack extends EmiStack {
    private static final ResourceLocation ID = new ResourceLocation("denovo", "rain");
    private static final ResourceLocation RAIN_LIGHT = new ResourceLocation("denovo", "textures/emi/rain_light.png");
    private static final ResourceLocation RAIN_DARK = new ResourceLocation("denovo", "textures/emi/rain_dark.png");
    public static EmiRainStack RAIN = new EmiRainStack();

    @Override
    public void render(DrawContext draw, int x, int y, float delta, int flags) {
        draw.getMatrices().push();
        EmiDrawContext context = EmiDrawContext.wrap(draw);
        context.drawTexture(RAIN_LIGHT, x, y, 0, 0, 0, 16, 16, 16, 16);
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
        return List.of(Text.translatable("emi.info.fist"));
    }

    @Override
    public List<TooltipComponent> getTooltip() {
        List<TooltipComponent> list = new ArrayList<>();
        list.add(TooltipComponent.of(Text.translatable("denovo.emi.rain")));
//        EmiTooltipComponents.appendModName(list, "btw");
        return list;
    }

    @Override
    public Text getName() {
        return Text.translatable("denovo.emi.rain");
    }

    @Override
    public List<EmiStack> getEmiStacks() {
        return List.of(this);
    }

//    public static void appendModName(List<TooltipComponent> components, String namespace) {
//        if (EmiConfig.appendModId) {
//            String mod = EmiUtil.getModName(namespace);
//            components.add( of(EmiPort.literal(mod, Formatting.BLUE, Formatting.ITALIC)));
//        }
//    }
}