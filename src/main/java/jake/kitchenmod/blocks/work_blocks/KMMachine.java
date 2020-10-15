package jake.kitchenmod.blocks.work_blocks;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class KMMachine extends KMWorkBlock {
  public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

  public KMMachine(Properties properties, Supplier<TileEntity> tileSupplier) {
    super(properties, tileSupplier);
    setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH));
  }

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, BlockState state,
      @Nullable LivingEntity entity, ItemStack stack) {
    if (entity != null) {
      world.setBlockState(pos, state.with(FACING, getFacingFromEntity(pos, entity)), 2);
    }
  }

  private static Direction getFacingFromEntity(BlockPos clickedBlock, LivingEntity entity) {
    return Direction.getFacingFromVector((float) (entity.posX - clickedBlock.getX()),
        0, (float) (entity.posZ - clickedBlock.getZ()));
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(BlockStateProperties.HORIZONTAL_FACING);
  }
}
