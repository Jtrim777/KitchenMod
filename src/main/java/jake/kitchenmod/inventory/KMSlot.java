package jake.kitchenmod.inventory;

import jake.kitchenmod.blocks.work_blocks.Oven;
import jake.kitchenmod.items.KMFluidContainerItem;
import jake.kitchenmod.items.ModItems;
import java.util.function.Predicate;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ToolItem;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class KMSlot extends SlotItemHandler {

  private KMSlot.Type type;

  public KMSlot(IItemHandler handler, int index, int x, int y, String typeName) {
    super(handler, index, x, y);

    this.type = new Type(typeName);
  }

  public KMSlot(IItemHandler handler, int index, int x, int y, KMSlot.Type baseType) {
    super(handler, index, x, y);

    this.type = baseType;
  }

  public KMSlot.Type getType() {
    return type;
  }

  public boolean isValidItem(Item item) {
    return this.type.isItemValid(item);
  }

  public static class Type {

    protected String name;
    protected Predicate<ItemStack> validator;

    public Type(String name, Predicate<ItemStack> validator) {
      this.name = name;
      this.validator = validator;
    }

    public Type(String name) {
      this.name = name;

      this.validator = item -> true;
    }

    public Type(String name, Item[] valids) {
      this.name = name;

      this.validator = item -> {
        for (Item i : valids) {
          if (item.getItem() == i) {
            return true;
          }
        }
        return false;
      };
    }

    public boolean isItemValid(Item in) {
      return this.validator.test(new ItemStack(in));
    }
    public boolean isItemValid(ItemStack in) {
      return this.validator.test(in);
    }

    public KMSlot.Type or(KMSlot.Type other) {
      return new KMSlot.Type(name + "|" + other.name, validator.or(other.validator));
    }

    public KMSlot.Type and(KMSlot.Type other) {
      return new KMSlot.Type(name + "&" + other.name, validator.and(other.validator));
    }
  }

  public static class Types {

    private static Predicate<ItemStack> isFluid(Fluid f) {
      return is -> FluidUtil.getFluidContained(is).orElse(FluidStack.EMPTY).getFluid() == f;
    }

    private static Predicate<ItemStack> itemConformsTo(Class<?> typ) {
      return (stack) -> stack.getItem().getClass().isAssignableFrom(typ);
    }

    private static Predicate<ItemStack> containerEmpty = (stack) ->
        FluidUtil.getFluidContained(stack).orElse(null).isEmpty();

    public static final KMSlot.Type PLAYER_INVENTORY = new KMSlot.Type("player_inventory");
    public static final KMSlot.Type PLAYER_HOTBAR = new KMSlot.Type("player_hotbar");

    public static final KMSlot.Type WATER = new KMSlot.Type("water", isFluid(Fluids.WATER));
    public static final KMSlot.Type LAVA = new KMSlot.Type("lava", isFluid(Fluids.LAVA));

    public static final KMSlot.Type LIQUID_CONTAINER = new KMSlot.Type("liquid_container",
        KMFluidContainerItem::isFluidContainer);
    public static final KMSlot.Type EMPTY_CONTAINER = LIQUID_CONTAINER.and(
        new KMSlot.Type("empty", containerEmpty));

    public static final KMSlot.Type OUTPUT = new KMSlot.Type("output", item -> false);

    public static final KMSlot.Type GENERIC_INGREDIENT = new KMSlot.Type("generic_ingredient");

    public static final KMSlot.Type TOOL =
        new KMSlot.Type("tool", itemConformsTo(ToolItem.class));

    public static final KMSlot.Type COOKING_TOOL =
        new KMSlot.Type("cooking_tool", stack -> {
          Item item = stack.getItem();

          return item instanceof ToolItem
              || item == ModItems.bread_starter
              || item == Items.BUCKET;
        });

    public static final KMSlot.Type FUEL = new KMSlot.Type("fuel", Oven.AssociatedValues::isFuel);

    public static KMSlot.Type CONFORMS_TO(Class<?> typ) {
      return new KMSlot.Type(typ.getName(), itemConformsTo(typ));
    }

    public static KMSlot.Type IS_ITEM(Item inp) {
      return new KMSlot.Type(inp.toString(), (s) -> s.getItem() == inp);
    }
  }
}
