package jake.kitchenmod.blocks.plants;

import jake.kitchenmod.items.ModItems;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class VanillaVine extends KMVinePlant {

  private static final VoxelShape SHAPE = Block.makeCuboidShape(2, -1, 2, 14, 15, 14);

  public VanillaVine() {
    super(Properties
        .create(Material.PLANTS)
        .tickRandomly()
        .sound(SoundType.SWEET_BERRY_BUSH)
        .doesNotBlockMovement());
  }

  @Override
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos,
      ISelectionContext context) {
    return SHAPE;
  }

  @Override
  public IItemProvider getSeedsItem() {
    return ModItems.vanilla_bean;
  }

  @Override
  protected int getMaxAge() {
    return 7;
  }

  @Override
  protected int getMaxHeight() {
    return 3;
  }

  @Override
  protected ItemStack getHarvest(int age, Random rgen) {
    return age == 7 ?
        new ItemStack(ModItems.vanilla_bean, rgen.nextInt(2) + 1)
        : age == 6 ? new ItemStack(ModItems.vanilla_bean, 1) : null;
  }

  @Override
  protected boolean canHarvest(int age) {
    return age >= 6;
  }

  @Override
  protected int getPostHarvestAge(int cAge) {
    return 5;
  }

  @Override
  protected float growthChance(int age) {
    return age < 6 ? (1f/7f) : (1f/8f);
  }
}
