package jake.kitchenmod.items.overrides;

import jake.kitchenmod.KitchenMod;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.HoeItem;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;


public class KMHoeItem extends HoeItem {

  protected static Map<Block, BlockState> HOE_LOOKUP;

  public KMHoeItem(IItemTier tier, float attackSpeedIn) {
    super(tier, attackSpeedIn, new Item.Properties().group(ItemGroup.TOOLS));
  }

  public static void initLookupMap() {
//    HOE_LOOKUP = Maps.newHashMap(ImmutableMap
//        .of(Blocks.GRASS_BLOCK, ModBlocks.FARMLAND.getDefaultState(),
//            Blocks.GRASS_PATH, ModBlocks.FARMLAND.getDefaultState(),
//            Blocks.DIRT, ModBlocks.FARMLAND.getDefaultState(),
//            Blocks.COARSE_DIRT, Blocks.DIRT.getDefaultState()));
  }

  @Override
  public ActionResultType onItemUse(ItemUseContext ctxt) {
    KitchenMod.LOGGER.log("Used hoe. Here's the map: \n{"
      + HOE_LOOKUP.keySet().stream()
        .map(k -> k.toString()+": "+HOE_LOOKUP.get(k).getBlock().toString())
        .collect(Collectors.joining(", ")) + "}");

    return super.onItemUse(ctxt);
  }
}
