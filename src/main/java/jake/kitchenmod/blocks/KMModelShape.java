package jake.kitchenmod.blocks;

import net.minecraft.block.Block;
import net.minecraft.util.math.shapes.VoxelShape;

public class KMModelShape {

  int minX;
  int minY;
  int minZ;

  int maxX;
  int maxY;
  int maxZ;

  public KMModelShape(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
    this.maxX = maxX;
    this.maxY = maxY;
    this.maxZ = maxZ;

    this.minX = minX;
    this.minY = minY;
    this.minZ = minZ;
  }

  public VoxelShape getVoxelShape() {
    return Block.makeCuboidShape(minX, minY, minZ, maxX, maxY, maxZ);
  }
}
