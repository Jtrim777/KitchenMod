package jake.kitchenmod.blocks.plants;

import jake.kitchenmod.horticulture.IFertilizable;
import jake.kitchenmod.items.IFertilizer;
import jake.kitchenmod.util.ModUtil;
import java.util.Random;
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
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.PlantType;

public abstract class KMPickCropBlock extends BushBlock implements IFertilizable {

  public static final IntegerProperty AGE = BlockStateProperties.AGE_0_7;

  protected KMPickCropBlock(Block.Properties builder) {
    super(builder);
    this.setDefaultState(
        this.stateContainer.getBaseState().with(this.getAgeProperty(), Integer.valueOf(0)));
  }

  protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
    return state.getBlock() == Blocks.FARMLAND;
  }

  @Override
  public PlantType getPlantType(IBlockReader world, BlockPos pos) {
    return PlantType.Crop;
  }

  public IntegerProperty getAgeProperty() {
    return AGE;
  }

  public int getMaxAge() {
    return 7;
  }

  protected int getAge(BlockState state) {
    return state.get(this.getAgeProperty());
  }

  public BlockState withAge(int age) {
    return this.getDefaultState().with(this.getAgeProperty(), age);
  }

  public boolean isMaxAge(BlockState state) {
    return state.get(this.getAgeProperty()) >= this.getMaxAge();
  }

  public void tick(BlockState state, World worldIn, BlockPos pos, Random random) {
    super.tick(state, worldIn, pos, random);
    if (!worldIn.isAreaLoaded(pos, 1)) {
      return;
    }

    int i = state.get(AGE);
    if (i < 3 && random.nextInt(5) == 0 && worldIn.getLightSubtracted(pos.up(), 0) >= 9) {
      worldIn.setBlockState(pos, state.with(AGE, i + 1), 2);
    }
  }

  /**
   * @param age The age of the plant when this query is made
   * @param rgen The random number generator to use
   * @return The items to be spawned at harvest, or null if harvest is not valid for this age
   */
  protected abstract ItemStack getHarvest(int age, Random rgen);

  /**
   * @param cage The current age of the plant
   * @return The age to revert to after a harvest has been made
   */
  protected abstract int getPostHarvestAge(int cage);

  /**
   * @param age The age of the plant when this query is made
   * @return True if some harvest is available for the given age
   */
  protected abstract boolean canHarvestForAge(int age);

  public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos,
      PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
    int age = getAge(state);

    //noinspection ConstantConditions
    if (ModUtil.playerHolding(player, handIn, (f) -> f instanceof IFertilizer
        || f == Items.BONE_MEAL) && !canHarvestForAge(getAge(state))) {
      return false;
    } else if (canHarvestForAge(age)) {
      spawnAsEntity(worldIn, pos, getHarvest(age, worldIn.rand));

      worldIn.playSound(null, pos, SoundEvents.ITEM_SWEET_BERRIES_PICK_FROM_BUSH,
          SoundCategory.BLOCKS, 1.0F, 0.8F + worldIn.rand.nextFloat() * 0.4F);
      setAge(worldIn, pos, getPostHarvestAge(age));
      return true;
    } else {
      return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }
  }

  @Override
  public void applyFertilizer(World worldIn, BlockPos pos, BlockState state,
      IFertilizer fertilizer) {
    int growth = getGrowthForFertilizer(fertilizer, worldIn.rand);

    setAge(worldIn, pos, getAge(state) + growth);
  }

  protected void setAge(World world, BlockPos pos, int age) {
    world.setBlockState(pos, this.withAge(Math.min(getMaxAge(), age)), 2);
  }

  @Override
  public int getMinFertilizerGrowth() {
    return 1;
  }

  @Override
  public int getMaxFertilizerGrowth() {
    return 2;
  }

  @Override
  public float getFertilizeGrowthChance() {
    return 1;
  }

  public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
    return (worldIn.getLightSubtracted(pos, 0) >= 8 || worldIn.isSkyLightMax(pos)) && super
        .isValidPosition(state, worldIn, pos);
  }

  public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
    if (entityIn instanceof RavagerEntity && net.minecraftforge.event.ForgeEventFactory
        .getMobGriefingEvent(worldIn, entityIn)) {
      worldIn.destroyBlock(pos, true);
    }

    super.onEntityCollision(state, worldIn, pos, entityIn);
  }

  protected abstract IItemProvider getSeedsItem();

  public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
    return new ItemStack(this.getSeedsItem());
  }

  /**
   * Whether this IGrowable can grow
   */
  public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
    return !this.isMaxAge(state);
  }

  public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
    return true;
  }

  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(AGE);
  }
}
