package jake.kitchenmod.blocks.work_blocks;

import jake.kitchenmod.tiles.MixingBowlTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class MixingBowl extends KMWorkBlock {

  private static Properties baseProperties = Properties
      .create(Blocks.SPRUCE_PLANKS.getDefaultState().getMaterial())
      .hardnessAndResistance(2f, 15f)
      .sound(SoundType.WOOD);

  private static VoxelShape SHAPE = Block.makeCuboidShape(0, 0, 0, 16, 8, 16);

  private static final BooleanProperty HAS_CONTENT = BooleanProperty.create("has_content");

  public MixingBowl() {
    super(baseProperties, MixingBowlTile::new);
    setDefaultState(this.getDefaultState().with(HAS_CONTENT, false));
  }

  @Override
  public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_,
      ISelectionContext p_220053_4_) {
    return SHAPE;
  }

  @Override
  public VoxelShape getRenderShape(BlockState p_196247_1_, IBlockReader p_196247_2_,
      BlockPos p_196247_3_) {
    return SHAPE;
  }

  @Override
  public VoxelShape getCollisionShape(BlockState p_220071_1_, IBlockReader p_220071_2_,
      BlockPos p_220071_3_, ISelectionContext p_220071_4_) {
    return SHAPE;
  }

  @Override
  protected void fillStateContainer(Builder<Block, BlockState> builder) {
    builder.add(HAS_CONTENT);
  }
}
