package jake.kitchenmod.blocks.plants;

import jake.kitchenmod.blocks.ModBlocks;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class StakeBlock extends Block {

  private static final VoxelShape BLOCK_SHAPE =
      Block.makeCuboidShape(7, -1, 7, 9, 15, 9);

  private static final Map<Item, IStakeablePlant> VALID_SEEDS = new HashMap<>();

  private static final Material STAKE = (new Material.Builder(MaterialColor.WOOD))
      .notSolid().build();

  public StakeBlock() {
    super(Properties.create(STAKE).harvestTool(ToolType.AXE)
        .hardnessAndResistance(1.25f, 1).harvestLevel(0).tickRandomly());
  }

  @Override
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos,
      ISelectionContext context) {
    return BLOCK_SHAPE;
  }

  @Override
  public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
    BlockPos beneath = pos.down();

    if (state.getBlock() == this) {
      Block bbelow = worldIn.getBlockState(beneath).getBlock();

      //noinspection ConstantConditions
      return bbelow == Blocks.FARMLAND
          || bbelow == ModBlocks.STAKE
          || (bbelow instanceof IStakeablePlant && VALID_SEEDS.values().contains(bbelow));
    } else {
      return true;
    }
  }

  @Override
  public void tick(BlockState state, World worldIn, BlockPos pos, Random random) {
    if (!isValidPosition(state, worldIn, pos)) {
      worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
    }

    super.tick(state, worldIn, pos, random);
  }

  @Override
  public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos,
      PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
    ItemStack heldItem = player.getHeldItem(handIn);

    if (VALID_SEEDS.containsKey(heldItem.getItem())
        && worldIn.getBlockState(pos.down()).getBlock() == Blocks.FARMLAND) {
      Item seedItem = heldItem.getItem();

      if (!player.abilities.isCreativeMode) {
        heldItem.shrink(1);
      }

      worldIn.setBlockState(pos, VALID_SEEDS.get(seedItem).getDefaultState());
      return true;
    }

    return false;
  }

  public static void registerPlant(IStakeablePlant plant) {
    VALID_SEEDS.put(plant.getSeedsItem().asItem(), plant);
  }

  @Override
  public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
    return true;
  }
}
