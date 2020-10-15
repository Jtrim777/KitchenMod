package jake.kitchenmod.blocks.plants;

import jake.kitchenmod.blocks.plants.KMPickCropBlock;
import jake.kitchenmod.items.ModItems;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class StrawberryBush extends KMPickCropBlock {

  private static final VoxelShape BLOCK_SHAPE = Block.makeCuboidShape(
      0, 0, 0, 16, 3, 16
  );

  public StrawberryBush() {
    super(Block.Properties
        .create(Material.PLANTS)
        .tickRandomly()
        .doesNotBlockMovement()
        .sound(SoundType.SWEET_BERRY_BUSH));
  }

  @Override
  public int getMaxAge() {
    return 3;
  }

  @Override
  protected ItemStack getHarvest(int age, Random rgen) {
    if (!canHarvestForAge(age)) {
      return null;
    }

    int berryCount = 1;

    if (age == 3) {
      berryCount += rgen.nextInt(2);
    }

    return new ItemStack(ModItems.strawberry, berryCount);
  }

  @Override
  protected int getPostHarvestAge(int cage) {
    return 1;
  }

  @Override
  protected boolean canHarvestForAge(int age) {
    return age > 1;
  }

  @Override
  protected IItemProvider getSeedsItem() {
    return ModItems.strawberry_seeds;
  }

  @Override
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos,
      ISelectionContext context) {
    return BLOCK_SHAPE;
  }
}
