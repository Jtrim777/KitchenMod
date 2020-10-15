package jake.kitchenmod.horticulture.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class KMCropPlant extends KMPlant {

  public KMCropPlant(Properties properties) {
    super(properties);
  }

  @Override
  protected int getMaxAge() {
    return 3;
  }

  @Override
  protected boolean canPickFruit() {
    return false;
  }

  @Override
  protected ItemStack getHarvest(MethodMeta data) {
    if (data.age == getMaxAge() && data.gene.getProductionMin() > 0) {
      return data.gene.generateHarvest(data.rgen);
    } else {
      return null;
    }
  }

  @Override
  protected boolean canHarvest(MethodMeta data) {
    return data.age == getMaxAge();
  }

  @Override
  protected int getPostHarvestAge(MethodMeta data) {
    return 0;
  }

  @Override
  protected void doGrow(MethodMeta data) {
    int age = data.age;

    if (age < getMaxAge() - 1) {
      setAge(data, age + 1);
    }
  }

  @Override
  protected boolean harvest(MethodMeta data) {
    return false;
  }

  @Override
  public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {

    return meta(worldIn, pos).age < getMaxAge();
  }

}
