package btw.community.denovo.emi.custom;

import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.runtime.EmiDrawContext;
import emi.shims.java.net.minecraft.client.gui.DrawContext;
import emi.shims.java.net.minecraft.client.gui.tooltip.TooltipComponent;
import emi.shims.java.net.minecraft.text.Text;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class EmiRightClickStack extends EmiStack {
    private static final ResourceLocation ID = new ResourceLocation("denovo", "right_click");
    private static final ResourceLocation RIGHT_CLICK_TEXTURE = new ResourceLocation("denovo", "textures/emi/right_click.png");
    public static EmiRightClickStack rightClickStack = new EmiRightClickStack();

    @Override
    public void render(DrawContext draw, int x, int y, float delta, int flags) {
        draw.getMatrices().push();
        EmiDrawContext context = EmiDrawContext.wrap(draw);
        context.drawTexture(RIGHT_CLICK_TEXTURE, x, y, 0, 0, 0, 16, 16, 16, 16);
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
        return stack == rightClickStack;
    }

    @Override
    public List<Text> getTooltipText() {
        return List.of(Text.translatable("emi.denovo.right_click"));
    }

    @Override
    public List<TooltipComponent> getTooltip() {
        List<TooltipComponent> list = new ArrayList<>();
        list.add(TooltipComponent.of(Text.translatable("emi.denovo.right_click")));
//        EmiTooltipComponents.appendModName(list, "btw");
        return list;
    }

    @Override
    public Text getName() {
        return Text.translatable("emi.denovo.right_click");
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