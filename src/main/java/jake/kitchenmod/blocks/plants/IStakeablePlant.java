package jake.kitchenmod.blocks.plants;

import net.minecraft.block.BlockState;
import net.minecraft.util.IItemProvider;

public interface IStakeablePlant {
  BlockState getDefaultState();

  IItemProvider getSeedsItem();

  int getPlantHeight(BlockState state);

  boolean canAddStakeAbove(BlockState state);
}
