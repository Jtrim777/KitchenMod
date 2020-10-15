package jake.kitchenmod.blocks.work_blocks;

import jake.kitchenmod.tiles.OvenTile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.util.Direction;

public class Oven extends KMMachine {

  private static Properties baseProperties = Properties
      .create(Blocks.STONE.getDefaultState().getMaterial())
      .hardnessAndResistance(3.5f, 17.5f)
      .sound(SoundType.STONE);

  public static final BooleanProperty IS_FUELED = BooleanProperty.create("is_fueled");

  public Oven() {
    super(baseProperties, OvenTile::new);
    setDefaultState(this.getDefaultState()
        .with(FACING, Direction.NORTH)
        .with(IS_FUELED, false));
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(IS_FUELED);
    super.fillStateContainer(builder);
  }

  public static class AssociatedValues {

    private static final Map<Item, Integer> FUEL_BURNS =
        AbstractFurnaceTileEntity.getBurnTimes();

    private static final Map<Item, Double> FUEL_TEMPS = cfgFuels();

    private static final ArrayList<Material> BURNABLE_MATERIALS =
        new ArrayList<>(Arrays.asList(
            Material.BAMBOO, Material.BAMBOO_SAPLING, Material.CARPET, Material.GOURD,
            Material.LEAVES, Material.MISCELLANEOUS, Material.ORGANIC, Material.PLANTS,
            Material.TALL_PLANTS, Material.WOOD, Material.WOOL
        ));

    private static Map<Item, Double> cfgFuels() {
      Map<Item, Double> rez = new HashMap<>();
      FUEL_BURNS.forEach((item, integer) -> rez.put(item, 0.5d));

      rez.replace(Items.LAVA_BUCKET, 1.5);
      rez.replace(Items.BLAZE_ROD, 1.5);

      rez.replace(Items.COAL, 1d);
      rez.replace(Items.CHARCOAL, 1d);
      rez.replace(Blocks.COAL_BLOCK.asItem(), 1d);

      rez.replace(Blocks.DRIED_KELP_BLOCK.asItem(), 0.75);

      return rez;
    }

    public static boolean isFuel(ItemStack i) {
      return FUEL_BURNS.containsKey(i.getItem());
    }

    public static int getBurnTime(Item i) {
      return FUEL_BURNS.get(i);
    }

    public static double getTemperature(Item i) {
      return FUEL_TEMPS.get(i);
    }

    public static boolean isBurnable(Item i) {
      Block bfi = Block.getBlockFromItem(i);
      if (bfi != Blocks.AIR) {
        return BURNABLE_MATERIALS.contains(bfi.getMaterial(null));
      }

      return i.isFood();
    }
  }
}
