package jake.kitchenmod.blocks;

import jake.kitchenmod.gen.ModWorldGen;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.storage.loot.LootContext;

public class KMOreBlock extends Block {
  private Item drop;
  private int minDropCount = 1;
  private int maxDropCount = 1;
  private boolean dropsSelf;
  private int minXp;
  private int maxXp;

  private KMOreBlock() {
    super(Block.Properties.create(Material.ROCK).hardnessAndResistance(3f, 3f));

    drop = null;
    dropsSelf = true;
    minXp = 0;
    maxXp = 0;
  }

  @Override
  public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
    if (dropsSelf) {
      return Collections.singletonList(new ItemStack(Item.BLOCK_TO_ITEM.get(this)));
    } else {
      int count = minDropCount == maxDropCount ? minDropCount :
          RANDOM.nextInt(maxDropCount - minDropCount) + minDropCount;

      return Collections.singletonList(new ItemStack(drop, count));
    }
  }

  @Override
  public int getExpDrop(BlockState state, IWorldReader world, BlockPos pos, int fortune,
      int silktouch) {
    return RANDOM.nextInt(maxXp - minXp) + minXp;
  }

  public static Builder create() {
    return new Builder();
  }

  public static class Builder {
    private Item drop = null;
    private int minDropCount = 1;
    private int maxDropCount = 1;
    private int minXp = 0;
    private int maxXp = 0;
    private Consumer<KMOreBlock> veinCreator;

    private Builder(){}

    public Builder drop(Item drop) {
      this.drop = drop;

      return this;
    }

    public Builder drop(Item drop, int count) {
      this.drop = drop;
      this.minDropCount = count;
      this.maxDropCount = count;

      return this;
    }

    public Builder drop(Item drop, int min, int max) {
      this.drop = drop;

      this.minDropCount = min;
      this.maxDropCount = max;

      return this;
    }

    public Builder xp(int min, int max) {
      this.minXp = min;
      this.maxXp = max;

      return this;
    }

    public Builder xp(int amt) {
      this.maxXp = amt;
      this.minXp = amt;

      return this;
    }

    public Builder vein(int count, int size, int minHeight, int maxHeight) {
      this.veinCreator = (block) -> {
        ModWorldGen.addOreToRegister(
            ModWorldGen.createOreFeature(block, count, size, minHeight, maxHeight)
        );
      };

      return this;
    }

    public KMOreBlock build() {
      KMOreBlock out = new KMOreBlock();

      if (drop != null) {
        out.drop = drop;
        out.dropsSelf = false;
        out.minDropCount = minDropCount;
        out.maxDropCount = maxDropCount;
      }

      if (minXp != 0 || maxXp != 0) {
        out.minXp = minXp;
        out.maxXp = maxXp;
      }

      if (veinCreator != null) {
        veinCreator.accept(out);
      }

      return out;
    }
  }
}
