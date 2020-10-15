package jake.kitchenmod.items;

import jake.kitchenmod.horticulture.IFertilizable;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CompostItem extends Item implements IFertilizer {

  public CompostItem() {
    super(new Item.Properties().group(ItemGroup.MATERIALS));
  }

  public ActionResultType onItemUse(ItemUseContext context) {
    World world = context.getWorld();
    BlockPos pos = context.getPos();
    BlockState state = world.getBlockState(pos);
    ItemStack itemStack = context.getItem();

    if (state.getBlock() instanceof IFertilizable) {
      IFertilizable block = (IFertilizable)state.getBlock();

      if (block.canGrow(world, pos, state, true) && !world.isRemote) {
        block.applyFertilizer(world, pos, state, this);

        itemStack.shrink(1);

        world.playEvent(2005, pos, 0);
      }

      return ActionResultType.SUCCESS;
    } else if (state.getBlock() instanceof IGrowable) {
      if (BoneMealItem.applyBonemeal(itemStack, world, pos, context.getPlayer())) {
        if (!world.isRemote) {
          world.playEvent(2005, pos, 0);
        }

        return ActionResultType.SUCCESS;
      }
    }

    return ActionResultType.PASS;
  }

  @Override
  public float getFertilityLevel() {
    return 1f;
  }
}
