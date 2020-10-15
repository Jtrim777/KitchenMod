package jake.kitchenmod.blocks.plants;

import jake.kitchenmod.blocks.ModBlocks;
import jake.kitchenmod.horticulture.IFertilizable;
import jake.kitchenmod.items.IFertilizer;
import jake.kitchenmod.util.ModUtil;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BushBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.RavagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Hand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.PlantType;

public abstract class KMVinePlant extends BushBlock implements IFertilizable, IStakeablePlant {

  public static final IntegerProperty AGE = BlockStateProperties.AGE_0_7;
  public static final IntegerProperty HEIGHT = IntegerProperty.create("vine_height", 1, 3);

  public KMVinePlant(Properties properties) {
    super(properties);

    this.setDefaultState(this.stateContainer.getBaseState()
        .with(AGE, 0).with(HEIGHT, 1));
  }

  // region BASE PROPERTIES
  @Override
  public PlantType getPlantType(IBlockReader world, BlockPos pos) {
    return PlantType.Crop;
  }
  // endregion BASE PROPERTIES

  // region CONFIG METHODS
  @Override
  public abstract IItemProvider getSeedsItem();

  protected abstract int getMaxAge();

  protected abstract int getMaxHeight();

  protected abstract ItemStack getHarvest(int age, Random rgen);

  protected abstract boolean canHarvest(int age);

  protected abstract int getPostHarvestAge(int cAge);

  protected float growthChance(int age) {
    return 0.2f;
  }

  @Override
  public int getMinFertilizerGrowth() {
    return 1;
  }

  @Override
  public int getMaxFertilizerGrowth() {
    return 3;
  }

  @Override
  public float getFertilizeGrowthChance() {
    return 1;
  }

  // endregion CONFIG METHODS

  // region STATE METHODS
  protected int getAge(BlockState state) {
    return state.get(AGE);
  }

  protected int getHeight(BlockState state) {
    return state.get(HEIGHT);
  }

  @Override
  public int getPlantHeight(BlockState state) {
    return getHeight(state);
  }

  public BlockState withState(int age, int height) {
    return this.getDefaultState().with(AGE, age).with(HEIGHT, height);
  }

  public BlockState withAge(int age) {
    return withState(age, 1);
  }

  public BlockState withHeight(int height) {
    return withState(0, height);
  }

  protected void setAge(World world, BlockPos pos, BlockState cState, int age) {
    BlockState nState = withState(age, getHeight(cState));

    world.setBlockState(pos, nState, 1 | 2);
  }

  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(AGE, HEIGHT);
  }
  // endregion STATE METHODS

  // region BEHAVIOR METHODS
  public void tick(BlockState state, World worldIn, BlockPos pos, Random random) {
    super.tick(state, worldIn, pos, random);
    if (!worldIn.isAreaLoaded(pos, 1)) {
      return;
    }

    if (shouldGrow(worldIn, pos, random)) {
      doGrow(worldIn, pos, state);
    }
  }

  @Override
  public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos,
      PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {

    if (ModUtil.playerHolding(player, handIn, (f) -> f instanceof IFertilizer
        || f == Items.BONE_MEAL) && !canHarvest(getAge(state))) {
      return false;
    } else {
      return harvest(worldIn, pos, state);
    }
  }

  @Override
  public void applyFertilizer(World worldIn, BlockPos pos, BlockState state,
      IFertilizer fertilizer) {
    int growth = getGrowthForFertilizer(fertilizer, worldIn.rand);

    for (int i = 0; i < growth; i++) {
      doGrow(worldIn, pos, state);
    }
  }

  // Normal growth
  protected void doGrow(World world, BlockPos pos, BlockState state) {
    // Try to grow lower plants first
    if (world.getBlockState(pos.down()).getBlock() == this
        && world.getBlockState(pos.down()).get(AGE) < getAge(state)) {
      doGrow(world, pos.down(), world.getBlockState(pos.down()));
      return;
    }

    int age = getAge(state);
    int height = getHeight(state);

    if (age < getMaxAge() - 1) {
      setAge(world, pos, state, age + 1);
    } else if (canGrowUp(world, pos)) {
      world.setBlockState(pos.up(), withHeight(height + 1), 1 | 2);
    } else if (age == getMaxAge() - 1) {
      setAge(world, pos, state, age + 1);
    }
  }

  protected boolean harvest(World world, BlockPos pos, BlockState state) {
    int age = getAge(state);

    ItemStack harvest = propagate(world, pos, null, (nstate, hvst) -> {
      int cage = getAge(nstate);

      ItemStack nhvst = getHarvest(cage, world.rand);

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

    spawnAsEntity(world, pos, harvest);
    world.playSound(null, pos, SoundEvents.ITEM_SWEET_BERRIES_PICK_FROM_BUSH,
        SoundCategory.BLOCKS, 1.0F, 0.8F + world.rand.nextFloat() * 0.4F);

    propagateAct(world, pos, (npos, nstate) -> {
      if (canHarvest(getAge(nstate))) {
        setAge(world, npos, nstate, getPostHarvestAge(getAge(nstate)));
      }
    });

    return true;
  }

  public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
    if (entityIn instanceof RavagerEntity && net.minecraftforge.event.ForgeEventFactory
        .getMobGriefingEvent(worldIn, entityIn)) {
      worldIn.destroyBlock(pos, true);
    }

    super.onEntityCollision(state, worldIn, pos, entityIn);
  }

  protected <T> T propagate(World world, BlockPos startPos, T base,
      BiFunction<BlockState, T, T> reducer) {
    T value = reducer.apply(world.getBlockState(startPos), base);

    BlockPos plantUp = startPos.up();
    while (world.getBlockState(plantUp).getBlock() == this) {
      value = reducer.apply(world.getBlockState(plantUp), value);

      plantUp = plantUp.up();
    }

    BlockPos plantDown = startPos.down();
    while (world.getBlockState(plantDown).getBlock() == this) {
      value = reducer.apply(world.getBlockState(plantDown), value);

      plantDown = plantDown.down();
    }

    return value;
  }

  protected <T> T propagatePos(World world, BlockPos startPos, T base,
      BiFunction<BlockPos, T, T> reducer) {
    T value = reducer.apply(startPos, base);

    BlockPos plantUp = startPos.up();
    while (world.getBlockState(plantUp).getBlock() == this) {
      value = reducer.apply(plantUp, value);

      plantUp = plantUp.up();
    }

    BlockPos plantDown = startPos.down();
    while (world.getBlockState(plantDown).getBlock() == this) {
      value = reducer.apply(plantDown, value);

      plantDown = plantDown.down();
    }

    return value;
  }

  protected void propagateAct(World world, BlockPos startPos,
      BiConsumer<BlockPos, BlockState> actor) {
    actor.accept(startPos, world.getBlockState(startPos));

    BlockPos plantUp = startPos.up();
    while (world.getBlockState(plantUp).getBlock() == this) {
      actor.accept(plantUp, world.getBlockState(plantUp));

      plantUp = plantUp.up();
    }

    BlockPos plantDown = startPos.down();
    while (world.getBlockState(plantDown).getBlock() == this) {
      actor.accept(plantDown, world.getBlockState(plantDown));

      plantDown = plantDown.down();
    }
  }
  // endregion BEHAVIOR METHODS

  // region CONDITION METHODS
  @Override
  protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
    return state.getBlock() == Blocks.FARMLAND
        || (getHeight(worldIn.getBlockState(pos.up())) > 1 && state.getBlock() == this);
  }

  @Override
  public boolean canAddStakeAbove(BlockState state) {
    return getHeight(state) < getMaxHeight();
  }

  @Override
  public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {

    return propagatePos((World) worldIn, pos, false,
        (npos, prev) -> canGrow((World) worldIn, npos));
  }

  private boolean canGrow(World world, BlockPos pos) {
    return this.getAge(world.getBlockState(pos)) < getMaxAge() || canGrowUp(world, pos);
  }

  protected boolean shouldGrow(World world, BlockPos pos, Random rgen) {
    return rgen.nextInt((int) (1 / growthChance(getAge(world.getBlockState(pos))))) == 0
        && world.getLightSubtracted(pos.up(), 0) >= 9;
  }

  protected boolean canGrowUp(World world, BlockPos pos) {
    //noinspection ConstantConditions
    return world.getBlockState(pos.up()).getBlock() == ModBlocks.STAKE
        && getHeight(world.getBlockState(pos)) < getMaxHeight();
  }
  // endregion CONDITION METHODS
}
