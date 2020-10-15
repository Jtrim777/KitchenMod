package jake.kitchenmod.metallurgy.gen;

import jake.kitchenmod.metallurgy.blocks.MllgyBlocks;
import jake.kitchenmod.util.ModUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class MeteorSpawner {

  public static void tick(PlayerEntity player) {
    World world = player.getEntityWorld();

    if (!ModUtil.isServerWorld(world)) {
      return;
    }

    BlockPos cPos = player.getPosition();

    ModUtil.executeWithFrequency(() -> spawnMeteor(world, cPos), 0.00001f, world.rand);
  }

  private static void spawnMeteor(World world, BlockPos playerPos) {
    int xDiff = MathHelper.nextInt(world.rand, 15, 40);
    int zDiff = MathHelper.nextInt(world.rand, 15, 40);

    BlockPos spawnPos = new BlockPos(playerPos.getX()+xDiff, 150, playerPos.getZ()+zDiff);

//    KitchenMod.LOGGER.log("Spawned meteor at "+spawnPos.toString(), "MeteorSpawner");

    if (world.isAirBlock(spawnPos)) {
      world.setBlockState(spawnPos, MllgyBlocks.METEOR.getDefaultState());
    }
  }
}
