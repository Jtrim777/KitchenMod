package jake.kitchenmod.horticulture.features;

import jake.kitchenmod.horticulture.PlantGene;
import jake.kitchenmod.horticulture.blocks.PlantBlocks;
import jake.kitchenmod.horticulture.tiles.PlantTileEntity;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.World;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeature;

public class KMTreeFeature extends TreeFeature {

  private static final BlockState TRUNK = Blocks.OAK_LOG.getDefaultState();
  private static final BlockState LEAVES = PlantBlocks.LEAVES.getDefaultState();

  private PlantGene gene;

  public KMTreeFeature(PlantGene gene) {
    super(NoFeatureConfig::deserialize, true, 5,
        TRUNK, LEAVES, false);

    this.gene = gene;
  }

  public boolean place(Set<BlockPos> changedBlocks, IWorldGenerationReader worldIn, Random rand,
      BlockPos position, MutableBoundingBox p_208519_5_) {
    int height = this.getHeight(rand);

    int topOfTree = position.getY() + height;
    
    if (position.getY() >= 1 && topOfTree + 1 <= worldIn.getMaxHeight()) {

      // Ensure sufficient space for tree generation
      for (int cy = position.getY(); cy <= topOfTree + 1; ++cy) {
        int treeRadius = 1;

        if (cy == position.getY()) {
          treeRadius = 0;
        }

        if (cy >= position.getY() + 1 + height - 2) {
          treeRadius = 2;
        }

        BlockPos.MutableBlockPos mbpos = new BlockPos.MutableBlockPos();

        for (int cx = position.getX() - treeRadius; cx <= position.getX() + treeRadius; ++cx) {
          for (int cz = position.getZ() - treeRadius; cz <= position.getZ() + treeRadius; ++cz) {
            if (cy >= 0 && cy < worldIn.getMaxHeight()) {
              if (!func_214587_a(worldIn, mbpos.setPos(cx, cy, cz))) {
                return false;
              }
            } else {
              return false;
            }
          }
        }
      }

      this.setDirtAt(worldIn, position.down(), position);

      int j2 = 3;
      int k2 = 0;

      for (int cy = topOfTree - 3; cy <= topOfTree; ++cy) {
        int distanceToTop = cy - topOfTree;

        int leavesRadius = 1 - (distanceToTop / 2);

        for (int cx = position.getX() - leavesRadius; cx <= position.getX() + leavesRadius; ++cx) {
          int xToCenter = cx - position.getX();

          for (int cz = position.getZ() - leavesRadius; cz <= position.getZ() + leavesRadius;
              ++cz) {
            int zToCenter = cz - position.getZ();

            if (Math.abs(xToCenter) != leavesRadius || Math.abs(zToCenter) != leavesRadius
                || rand.nextInt(2) != 0 && distanceToTop != 0) {
              BlockPos newLeafPos = new BlockPos(cx, cy, cz);
              if (isAirOrLeaves(worldIn, newLeafPos) || func_214576_j(worldIn, newLeafPos)) {
                this.setLogState(changedBlocks, worldIn, newLeafPos, LEAVES, p_208519_5_);
                PlantTileEntity pte = ((PlantTileEntity) ((World) worldIn)
                    .getTileEntity(newLeafPos));

                if (pte != null) {
                  pte.setGene(this.gene);
                }
              }
            }
          }
        }
      }

      for (int logY = 0; logY < height; ++logY) {
        if (isAirOrLeaves(worldIn, position.up(logY)) || func_214576_j(worldIn,
            position.up(logY))) {
          this.setLogState(changedBlocks, worldIn, position.up(logY), TRUNK, p_208519_5_);
        }
      }
      return true;
    } else {
      return false;
    }

  }
}
