package jake.kitchenmod.blocks.overrides;

import jake.kitchenmod.blocks.plants.StakeBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class KMFarmlandBlock extends FarmlandBlock {

  public KMFarmlandBlock() {
    super(Block.Properties.create(Material.EARTH).tickRandomly().hardnessAndResistance(0.6F).sound(
        SoundType.GROUND));
  }

  @Override
  public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
    BlockState blockstate = worldIn.getBlockState(pos.up());
    return !blockstate.getMaterial().isSolid()
        || blockstate.getBlock() instanceof FenceGateBlock
        || blockstate.getBlock() instanceof StakeBlock;
  }

  @Override
  public String toString() {
    return super.toString() + "[MOD OVERRIDE]";
  }
}
