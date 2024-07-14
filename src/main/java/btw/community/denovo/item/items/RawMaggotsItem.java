// FCMOD

package btw.community.denovo.item.items;

import btw.item.items.FoodItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.IconRegister;

public class RawMaggotsItem extends FoodItem
{
    public RawMaggotsItem(int iItemID, int hunger, float sat, boolean wolf, String name)
    {
        super( iItemID, hunger, sat, wolf, name, true );

        setStandardFoodPoisoningEffect();
    }
}
