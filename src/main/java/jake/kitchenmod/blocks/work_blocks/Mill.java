package jake.kitchenmod.blocks.work_blocks;

import jake.kitchenmod.tiles.MillTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;

public class Mill extends KMMachine {

  private static Properties baseProperties = Properties
      .create(Blocks.STONE.getDefaultState().getMaterial())
      .hardnessAndResistance(3.5f, 17.5f)
      .sound(SoundType.STONE);

  private static final BooleanProperty HAS_WATER = BooleanProperty.create("has_water");

  public Mill() {
    super(baseProperties, MillTile::new);
    setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH)
        .with(HAS_WATER, false));
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(HAS_WATER);
    super.fillStateContainer(builder);
  }
}
