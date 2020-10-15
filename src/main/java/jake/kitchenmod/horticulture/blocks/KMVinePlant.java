package jake.kitchenmod.horticulture.blocks;

import jake.kitchenmod.blocks.ModBlocks;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class KMVinePlant extends KMPlant {

  public static final IntegerProperty HEIGHT = IntegerProperty.create("vine_height", 1, 5);

  public KMVinePlant(Properties properties) {
    super(properties);
  }

  public int getHeight(BlockState state) {
    return state.get(HEIGHT);
  }

  protected BlockState withHeight(int h) {
    return this.getDefaultState().with(HEIGHT, h);
  }

  @Override
  protected int getMaxAge() {
    return 3;
  }

  protected int getMaxHeight() {
    return 2;
  }

  @Override
  protected boolean canPickFruit() {
    return true;
  }

  @Override
  protected ItemStack getHarvest(MethodMeta data) {
    if (data.age == (getMaxAge() - 1) && data.gene.getProductionMin() > 0) {
      return new ItemStack(data.gene.getProduce(), data.gene.getProductionMin());
    } else if (data.age == getMaxAge()) {
      return data.gene.generateHarvest(data.rgen);
    } else {
      return null;
    }
  }

  @Override
  protected boolean canHarvest(MethodMeta data) {
    return (data.age == (getMaxAge() - 1) && data.gene.getProductionMin() > 0)
        || data.age == getMaxAge();
  }

  @Override
  protected int getPostHarvestAge(MethodMeta data) {
    return getMaxAge() - 2;
  }

  @Override
  protected void doGrow(MethodMeta data) {
    // Try to grow lower plants first
    if (data.getStateInDirection(Direction.DOWN).getBlock() == this
        && data.getStateInDirection(Direction.DOWN).get(AGE) < getAge(data.state)) {
      doGrow(data);
      return;
    }

    int age = data.age;
    int height = getHeight(data.state);

    if (age < getMaxAge() - 1) {
      setAge(data, age + 1);
    } else if (canGrowUp(data)) {
      ((World) data.world).setBlockState(data.pos.up(), withHeight(height + 1), 1 | 2);
    } else if (age == getMaxAge() - 1) {
      setAge(data, age + 1);
    }
  }

  @Override
  protected boolean harvest(MethodMeta data) {
    int age = getAge(data.state);

    ItemStack harvest = propagate(data, null, (nstate, hvst) -> {
      int cage = getAge(nstate);

      ItemStack nhvst = getHarvest(data);

      if (hvst == null && nhvst != null) {
        return nhvst;
      } else if (hvst != null && nhvst != null) {
        hvst.grow(nhvst.getCount());
      }

      return hvst;
    });

    if (harvest == null) {
      return false;
    }

    spawnAsEntity((World)data.world, data.pos, harvest);
    ((World)data.world).playSound(null, data.pos, SoundEvents.ITEM_SWEET_BERRIES_PICK_FROM_BUSH,
        SoundCategory.BLOCKS, 1.0F, 0.8F + data.rgen.nextFloat() * 0.4F);

    propagateAct(data, (npos, nstate) -> {
      MethodMeta ndata = meta(data.world, npos);

      if (canHarvest(ndata)) {
        setAge(ndata, getPostHarvestAge(ndata));
      }
    });

    return true;
  }

  @Override
  public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {

    return propagatePos(meta(worldIn, pos), false,
        (npos, prev) -> oneCanGrow((World) worldIn, npos));
  }

  private boolean oneCanGrow(World world, BlockPos pos) {
    MethodMeta data = meta(world, pos);

    return data.age < getMaxAge() || canGrowUp(data);
  }

  protected boolean canGrowUp(MethodMeta data) {
    //noinspection ConstantConditions
    return data.getStateInDirection(Direction.UP).getBlock() == ModBlocks.STAKE
        && getHeight(data.state) < getMaxHeight();
  }

  protected <T> T propagate(MethodMeta data, T base, BiFunction<BlockState, T, T> reducer) {
    T value = reducer.apply(data.state, base);

    BlockPos plantUp = data.pos.up();
    while (data.world.getBlockState(plantUp).getBlock() == this) {
      value = reducer.apply(data.world.getBlockState(plantUp), value);

      plantUp = plantUp.up();
    }

    BlockPos plantDown = data.pos.down();
    while (data.world.getBlockState(plantDown).getBlock() == this) {
      value = reducer.apply(data.world.getBlockState(plantDown), value);

      plantDown = plantDown.down();
    }

    return value;
  }

  protected <T> T propagatePos(MethodMeta data, T base, BiFunction<BlockPos, T, T> reducer) {
    T value = reducer.apply(data.pos, base);

    BlockPos plantUp = data.pos.up();
    while (data.world.getBlockState(plantUp).getBlock() == this) {
      value = reducer.apply(plantUp, value);

      plantUp = plantUp.up();
    }

    BlockPos plantDown = data.pos.down();
    while (data.world.getBlockState(plantDown).getBlock() == this) {
      value = reducer.apply(plantDown, value);

      plantDown = plantDown.down();
    }

    return value;
  }

  protected void propagateAct(MethodMeta data, BiConsumer<BlockPos, BlockState> actor) {
    actor.accept(data.pos, data.state);

    BlockPos plantUp = data.pos.up();
    while (data.world.getBlockState(plantUp).getBlock() == this) {
      actor.accept(plantUp, data.world.getBlockState(plantUp));

      plantUp = plantUp.up();
    }

    BlockPos plantDown = data.pos.down();
    while (data.world.getBlockState(plantDown).getBlock() == this) {
      actor.accept(plantDown, data.world.getBlockState(plantDown));

      plantDown = plantDown.down();
    }
  }

  public boolean canAddStakeAbove(BlockState state) {
    return getHeight(state) < getMaxHeight();
  }
}
