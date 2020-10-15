package jake.kitchenmod.inventory;

import jake.kitchenmod.capabilities.KMItemHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.item.ItemStack;

public class KMSlotMap {

  private List<NamedSlot> slots;
  private Map<String, IntRange> ranges;

  public KMSlotMap() {
    slots = new ArrayList<>();

    ranges = new HashMap<>();
  }

  public void addSlot(NamedSlot entry) {
    this.slots.add(entry);
  }

  public KMSlotMap addOne(String name, KMSlot.Type typ) {
    this.addSlot(new NamedSlot(name, typ));

    return this;
  }

  public KMSlotMap addRange(String name, KMSlot.Type typ, int lb, int ub) {
    for (int i = lb; i <= ub; i++) {
      addSlot(new NamedSlot(name+"##"+(i-lb), typ));
    }

    this.ranges.put(name, new IntRange(lb, ub));

    return this;
  }

  public int slotCount() {
    return slots.size();
  }

  public static KMSlotMap of(NamedSlot... inp) {
    KMSlotMap out = new KMSlotMap();

    Arrays.stream(inp).forEach(out::addSlot);

    return out;
  }

  public KMSlot.Type getType(String name) {
    return slots.stream().filter(s -> s.name.equals(name)).map(s -> s.type)
        .findFirst().orElse(null);
  }

  public KMSlot.Type getType(int slotNum) {
    return slots.get(slotNum).type;
  }

  public ItemStack getStackInSlot(String name, KMItemHandler inv) {
    return inv.getStackInSlot(slotIndexFor(name));
  }

  public void setStackInSlot(String name, ItemStack stack, KMItemHandler inv) {
    inv.setStackInSlot(slotIndexFor(name), stack);
  }

  public int slotIndexFor(String name) {
    for (int i=0; i < slots.size(); i++) {
      if (slots.get(i).name.equals(name)) {
        return i;
      }
    }

    return -1;
  }

  public boolean validForSlotIndex(int indx, ItemStack stack) {
    return slots.get(indx).type.validator.test(stack);
  }

  public boolean isValidForRange(String rangeName, ItemStack stack) {
    return slots.get(ranges.get(rangeName).ub).type.isItemValid(stack);
  }

  public int[] getRangeIndices(String rangeName) {
    return ranges.getOrDefault(rangeName, new IntRange(0, 0)).allIndices();
  }

  public static class NamedSlot {
    String name;
    KMSlot.Type type;

    public NamedSlot(String name, KMSlot.Type type) {
      this.name = name;
      this.type = type;
    }
  }

  private static class IntRange {
    int lb;
    int ub;

    IntRange(int lb, int ub) {
      this.lb = lb;
      this.ub = ub;
    }

    int[] allIndices() {
      int[] out = new int[ub-lb+1];

      Arrays.setAll(out, i -> i + lb);

      return out;
    }
  }
}
