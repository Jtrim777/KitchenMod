package jake.kitchenmod.items;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemTier;
import net.minecraft.item.ToolItem;

public class StirRod extends ToolItem {

  private static float ATTACK_DAMAGE = 0f;
  private static float ATTACK_SPEED = 1f;
  private static IItemTier TIER = ItemTier.WOOD;
  private static Set<Block> EFFECTIVE_ON = Sets.newHashSet();
  private static Item.Properties PROPERTIES = new Item.Properties()
      .maxStackSize(1)
      .maxDamage(5)
      .group(ItemGroup.TOOLS);

  public StirRod() {
    super(ATTACK_DAMAGE, ATTACK_SPEED, TIER, EFFECTIVE_ON, PROPERTIES);
  }
}
