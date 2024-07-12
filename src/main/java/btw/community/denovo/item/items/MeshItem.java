package btw.community.denovo.item.items;

import btw.community.denovo.block.DNBlocks;
import btw.community.denovo.item.DNItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Icon;
import net.minecraft.src.IconRegister;
import net.minecraft.src.Item;

public class MeshItem extends Item {
    public MeshItem(int id) {
        super(id);

        setUnlocalizedName("DNItem_mesh");

        setBuoyant();

        setCreativeTab(CreativeTabs.tabMisc);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getHopperFilterIcon() {
        return DNBlocks.sieve.getHopperFilterIcon();
    }
}
