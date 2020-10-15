package jake.kitchenmod.metallurgy.blocks;

import jake.kitchenmod.util.ModUtil;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.material.Material;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class MeteorBlock extends FallingBlock {

  private static VoxelShape SHAPE = Block.makeCuboidShape(4,0,4, 12, 8, 12);

  public static IntegerProperty HEAT = IntegerProperty.create("heat", 0, 2);

  public MeteorBlock() {
    super(Block.Properties.create(Material.ROCK)
        .hardnessAndResistance(30, 800)
        .harvestLevel(2)
        .harvestTool(ToolType.PICKAXE));

    this.setDefaultState(
        this.getStateContainer().getBaseState()
          .with(HEAT, 2)
    );
  }

  @Override
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos,
      ISelectionContext context) {
    return SHAPE;
  }

  @Override
  public void tick(BlockState state, World worldIn, BlockPos pos, Random random) {
    super.tick(state, worldIn, pos, random);

    int heat = state.get(HEAT);

    if (heat > 0 && random.nextInt(5) == 0) {
      worldIn.setBlockState(pos, state.with(HEAT, heat-1));
    }
  }

  @Override
  public void onEndFalling(World worldIn, BlockPos pos, BlockState fallingState,
      BlockState hitState) {
    if (!ModUtil.isServerWorld(worldIn)) return;

    if (fallingState.get(HEAT) != 2) {
      return;
    }

    worldIn.createExplosion(null, pos.getX(), pos.getY() + 2, pos.getZ(),
        2.0F, Mode.BREAK);
  }

  @Override
  protected void fillStateContainer(Builder<Block, BlockState> builder) {
    super.fillStateContainer(builder);
    builder.add(HEAT);
  }
}
