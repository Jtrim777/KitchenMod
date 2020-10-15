package jake.kitchenmod.metallurgy.blocks;

import jake.kitchenmod.blocks.work_blocks.KMWorkBlock;
import jake.kitchenmod.metallurgy.tiles.SmelteryTile;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SmelteryBlock extends KMWorkBlock {
  public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
  public static final BooleanProperty LIT = BooleanProperty.create("lit");

  public SmelteryBlock() {
    super(Block.Properties.create(Material.ROCK), SmelteryTile::new);

    setDefaultState(this.getDefaultState()
        .with(FACING, Direction.NORTH)
        .with(LIT, false));
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
    builder.add(FACING).add(LIT);
  }

  @OnlyIn(Dist.CLIENT)
  public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
    if (stateIn.get(LIT)) {
      double soundDX = (double)pos.getX() + 0.5D;
      double soundDY = (double)pos.getY();
      double soundDZ = (double)pos.getZ() + 0.5D;
      if (rand.nextDouble() < 0.1D) {
        worldIn.playSound(soundDX, soundDY, soundDZ, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
      }

      Direction direction = stateIn.get(FACING);
      Direction.Axis direction$axis = direction.getAxis();
      double d3 = 0.52D;
      double d4 = rand.nextDouble() * 0.6D - 0.3D;
      double d5 = direction$axis == Direction.Axis.X ? (double)direction.getXOffset() * 0.52D : d4;
      double d6 = rand.nextDouble() * 6.0D / 16.0D;
      double d7 = direction$axis == Direction.Axis.Z ? (double)direction.getZOffset() * 0.52D : d4;
      worldIn.addParticle(ParticleTypes.SMOKE, soundDX + d5, soundDY + d6, soundDZ + d7, 0.0D, 0.0D, 0.0D);
      worldIn.addParticle(ParticleTypes.FLAME, soundDX + d5, soundDY + d6, soundDZ + d7, 0.0D, 0.0D, 0.0D);
    }
  }

}
