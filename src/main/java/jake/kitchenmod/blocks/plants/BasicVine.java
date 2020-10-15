package jake.kitchenmod.blocks.plants;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public class BasicVine extends KMVinePlant {

  private static final VoxelShape SHAPE = Block.makeCuboidShape(2, -1, 2, 14, 15, 14);

  private ResourceLocation seed;
  private ResourceLocation fruit;

  private Item seedItem;
  private Item fruitItem;

  public BasicVine(String seed, String fruit) {
    super(Block.Properties
        .create(Material.PLANTS)
        .tickRandomly()
        .sound(SoundType.SWEET_BERRY_BUSH)
        .doesNotBlockMovement());

    this.seed = new ResourceLocation(seed);
    this.fruit = new ResourceLocation(fruit);
  }

  @Override
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos,
      ISelectionContext context) {
    return SHAPE;
  }

  @Override
  public IItemProvider getSeedsItem() {
    if (seedItem == null) {
      configItems();
    }

    return seedItem;
  }

  @Override
  protected int getMaxAge() {
    return 3;
  }

  @Override
  protected int getMaxHeight() {
    return 2;
  }

  @Override
  protected ItemStack getHarvest(int age, Random rgen) {
    if (fruitItem == null) {
      configItems();
    }

    return age == 3 ? new ItemStack(fruitItem, rgen.nextInt(2) + 1)
        : null;
  }

  @Override
  protected boolean canHarvest(int age) {
    return age == 3;
  }

  @Override
  protected int getPostHarvestAge(int cAge) {
    return 2;
  }

  @Override
  protected float growthChance(int age) {
    return age < 2 ? (1f / 5f)
        : age == 2 ? (1f / 7f) : (1f / 6f);
  }

  private void configItems() {
    IForgeRegistry<Item> registry = GameRegistry.findRegistry(Item.class);
    seedItem = registry.getValue(seed);
    fruitItem = registry.getValue(fruit);
  }
}
