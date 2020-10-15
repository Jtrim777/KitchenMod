package jake.kitchenmod.blocks.village;

import jake.kitchenmod.tiles.VillageManagerTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
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

public class VillageManagerBlock extends Block {
  public VillageManagerBlock() {
    super(Block.Properties.create(Material.GLASS)
        .hardnessAndResistance(-1, 3600000.0F).noDrops());
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new VillageManagerTile();
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
