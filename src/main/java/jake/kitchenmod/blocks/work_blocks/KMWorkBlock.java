package jake.kitchenmod.blocks.work_blocks;

import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public abstract class KMWorkBlock extends Block {

  private final Supplier<TileEntity> teCreator;

  public KMWorkBlock(Properties properties, Supplier<TileEntity> tileSupplier) {
    super(properties);

    this.teCreator = tileSupplier;
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return teCreator.get();
  }

  @Override
  public boolean onBlockActivated(BlockState blockState, World world, BlockPos pos,
      PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {

    if (!world.isRemote) {
      TileEntity tileEntity = world.getTileEntity(pos);
      if (tileEntity instanceof INamedContainerProvider) {
        NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity,
            tileEntity.getPos());
      } else {
        throw new IllegalStateException("Named Container Provider is Missing!");
      }
      return true;
    }

    return true;
  }
}
