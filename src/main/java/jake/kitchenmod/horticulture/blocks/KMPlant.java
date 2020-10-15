package jake.kitchenmod.horticulture.blocks;

import jake.kitchenmod.horticulture.IFertilizable;
import jake.kitchenmod.horticulture.PlantGene;
import jake.kitchenmod.horticulture.items.KMSeed;
import jake.kitchenmod.horticulture.items.PlantItems;
import jake.kitchenmod.horticulture.tiles.PlantTileEntity;
import jake.kitchenmod.items.IFertilizer;
import jake.kitchenmod.util.ModUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BushBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.RavagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootContext.Builder;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraftforge.common.PlantType;

public abstract class KMPlant extends BushBlock implements IFertilizable {

  public static final IntegerProperty AGE = BlockStateProperties.AGE_0_7;

  public KMPlant(Properties properties) {
    super(properties);

    this.setDefaultState(this.stateContainer.getBaseState()
        .with(AGE, 0));
  }

  // region BASE PROPERTIES
  @Override
  public PlantType getPlantType(IBlockReader world, BlockPos pos) {
    return PlantType.Crop;
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Nullable
  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new PlantTileEntity();
  }

  protected PlantTileEntity getTileEntity(IBlockReader world, BlockPos pos) {
    return (PlantTileEntity) world.getTileEntity(pos);
  }
  // endregion BASE PROPERTIES

  // region CONFIG METHODS
  protected abstract int getMaxAge();

  protected abstract boolean canPickFruit();

  protected abstract ItemStack getHarvest(MethodMeta data);

  protected abstract boolean canHarvest(MethodMeta data);

  protected abstract int getPostHarvestAge(MethodMeta data);

  protected float growthChance(MethodMeta data) {
    return data.gene.getPerTickGrowthChance();
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
  protected MethodMeta meta(IBlockReader world, BlockPos pos) {
    return new MethodMeta(world, pos);
  }

  protected int getAge(BlockState state) {
    return state.get(AGE);
  }

  public BlockState withAge(int age) {
    return this.getDefaultState().with(AGE, age);
  }

  protected void setAge(MethodMeta data, int age) {
    BlockState nState = withAge(age);

    ((World)data.world).setBlockState(data.pos, nState, 1 | 2);
  }

  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(AGE);
  }
  // endregion STATE METHODS

  // region BEHAVIOR METHODS
  public void tick(BlockState state, World worldIn, BlockPos pos, Random random) {
    super.tick(state, worldIn, pos, random);
    if (!worldIn.isAreaLoaded(pos, 1)) {
      return;
    }

    if (shouldGrow(meta(worldIn, pos))) {
      doGrow(meta(worldIn, pos));
    }
  }

  @Override
  public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos,
      PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {

    if (ModUtil.playerHolding(player, handIn, (f) -> f instanceof IFertilizer
        || f == Items.BONE_MEAL) && !canHarvest(meta(worldIn, pos))) {
      return false;
    } else {
      return harvest(meta(worldIn, pos));
    }
  }

  @Override
  public void applyFertilizer(World worldIn, BlockPos pos, BlockState state,
      IFertilizer fertilizer) {
    int growth = getGrowthForFertilizer(fertilizer, worldIn.rand);

    for (int i = 0; i < growth; i++) {
      doGrow(meta(worldIn, pos));
    }
  }

  // Normal growth
  protected abstract void doGrow(MethodMeta data);

  protected abstract boolean harvest(MethodMeta data);

  public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
    if (entityIn instanceof RavagerEntity && net.minecraftforge.event.ForgeEventFactory
        .getMobGriefingEvent(worldIn, entityIn)) {
      worldIn.destroyBlock(pos, true);
    }

    super.onEntityCollision(state, worldIn, pos, entityIn);
  }

  @Override
  public List<ItemStack> getDrops(BlockState state, Builder builder) {
    PlantTileEntity pte = (PlantTileEntity)builder.get(LootParameters.BLOCK_ENTITY);

    MethodMeta meta = meta(pte.getWorld(), pte.getPos());

    List<ItemStack> out = new ArrayList<>();

    ItemStack seedOut = KMSeed.withGene(pte.getGene());

    out.add(seedOut);

    if (canHarvest(meta)) {
      out.add(getHarvest(meta));
      for (int i=0; i<meta.rgen.nextInt(2); i++) {
        out.add(seedOut);
      }
    }

    return out;
  }

  // endregion BEHAVIOR METHODS

  // region CONDITION METHODS
  @Override
  protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
    return getTileEntity(worldIn, pos).getGene().isValidBlock(state.getBlock());
  }

  @Override
  public abstract boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient);

  protected boolean shouldGrow(MethodMeta data) {
    int growMax = (int) (1 / growthChance(data));

    return data.rgen.nextInt(growMax) == 0
        && data.gene.lightLevelIsValid(((World)data.world).getLightSubtracted(data.pos.up(), 0));
  }
  // endregion CONDITION METHODS

  protected static class MethodMeta {
    BlockState state;
    BlockPos pos;
    IBlockReader world;
    PlantGene gene;
    Random rgen;
    int age;

    public MethodMeta(IBlockReader world, BlockPos pos) {
      this.pos = pos;
      this.world = world;

      this.state = world.getBlockState(pos);
      this.gene = ((PlantTileEntity)world.getTileEntity(pos)).getGene();

      if (world instanceof World) {
        this.rgen = ((World)world).rand;
      } else {
        this.rgen = new Random();
      }

      this.age = state.get(AGE);
    }

    public BlockState getStateInDirection(Direction dir) {
      return world.getBlockState(pos.offset(dir));
    }
  }
}
