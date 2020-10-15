package jake.kitchenmod.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.ForgeRegistries;

public class ModUtil {

  private static Random rgen = new Random();

  public static int colorFromRGBA(int r, int g, int b, int a) {
    return (a << (8 * 3)) + (r << (8 * 2)) + (g << 8) + b;
  }

  public static int colorFromRGB(int r, int g, int b) {
    return (255 << (8 * 3)) + (r << (8 * 2)) + (g << 8) + b;
  }

  public static int[] ARGBFromColor(int color) {
    int a = color >> 24;
    int r = (color >> 16) - (a << 8);
    int g = (color >> 8) - (a << 16) - (r << 8);
    int b = (color) - (a << 24) - (r << 16) - (g << 8);

    return new int[]{a, r, g, b};
  }

  public static float[] floatARGBFromColor(int color) {
    int a = color >> 24;
    int r = (color >> 16) - (a << 8);
    int g = (color >> 8) - (a << 16) - (r << 8);
    int b = (color) - (a << 24) - (r << 16) - (g << 8);

    return new float[]{(float)a/255f, (float)r/255f, (float)g/255f, (float)b/255f};
  }

  public static Integer[] intRange(int l, int u) {
    Integer[] outs = new Integer[u - l];

    for (int i = l; i < u; i++) {
      outs[i - l] = i;
    }

    return outs;
  }

  public static boolean chance(float likelihood) {
    final float rand = rgen.nextFloat();
    boolean rez = rand <= likelihood;

//    KitchenMod.log("Chance: "+likelihood+"; Selected: "+rand+"; Result: "+rez);

    return rez;
  }

  public static <T> boolean listContainsWhere(ArrayList<T> list, Predicate<T> test) {
    return getWhere(list, test) != null;
  }

  public static <T> T getWhere(ArrayList<T> list, Predicate<T> pred) {
    for (T item : list) {
      if (pred.test(item)) {
        return item;
      }
    }

    return null;
  }

  public static boolean playerHolding(PlayerEntity p, Hand h, Item i) {
    return p.getHeldItem(h).getItem() == i;
  }

  public static boolean playerHolding(PlayerEntity p, Hand h, Predicate<Item> iCheck) {
    return iCheck.test(p.getHeldItem(h).getItem());
  }

  public static <K, V> String formatMap(Map<K, V> input) {
    return input.entrySet().stream()
        .map(e -> e.getKey().toString() + ": "+e.getValue().toString())
        .collect(Collectors.joining(", ", "{", "}"));
  }

  public static void executeWithFrequency(Executor action, float frequency, Random rgen) {
    if (rgen.nextInt((int)(1.0f/frequency)) == 0) {
      action.execute();
    }
  }

  public static boolean isServerWorld(World w) {
    return !w.isRemote;
  }

  @SafeVarargs
  public static <K, V> Map<K, V> makeMap(Map.Entry<K,V>... source) {
    Map<K, V> map = new HashMap<>();

    for (Map.Entry<K, V> entry : source) {
      map.put(entry.getKey(), entry.getValue());
    }

    return map;
  }

  public static ItemStack itemStackFromJson(JsonElement json) {
    JsonObject tag = json.getAsJsonObject();

    String itemID;
    if (tag.has("item")) {
      itemID = tag.get("item").getAsString();
    } else if (tag.has("id")) {
      itemID = tag.get("id").getAsString();
    } else {
      throw new IllegalArgumentException("Couldn't find item tag");
    }

    ResourceLocation itemType = new ResourceLocation(itemID);
    Item item = GameRegistry.findRegistry(Item.class).getValue(itemType);

    int count;
    if (tag.has("count")) {
      count = tag.get("count").getAsInt();
    } else {
      count = 1;
    }

    return new ItemStack(item, count);
  }

  public static FluidStack fluidStackFromJson(JsonElement json) {
    JsonObject tag = json.getAsJsonObject();

    ResourceLocation itemType = new ResourceLocation(tag.get("fluid").getAsString());
    Fluid fluid = ForgeRegistries.FLUIDS.getValue(itemType);

    int amt = tag.get("amount").getAsInt();

    return new FluidStack(fluid, amt);
  }

  public static JsonObject writeFluidStackToJson(FluidStack stack) {
    JsonObject json = new JsonObject();

    json.addProperty("fluid", stack.getFluid().getRegistryName().toString());
    json.addProperty("amount", stack.getAmount());

    return json;
  }

  public static JsonObject writeItemStackToJson(ItemStack stack) {
    JsonObject json = new JsonObject();

    json.addProperty("item", stack.getItem().getRegistryName().toString());
    json.addProperty("count", stack.getCount());

    return json;
  }

  public static Item findItem(ResourceLocation rl) {
    Item out = ForgeRegistries.ITEMS.getValue(rl);

    if (out == Items.AIR) {
      throw new EntryNotFoundException(rl, "items");
    }

    return out;
  }

  public static Block findBlock(ResourceLocation rl) {
    Block out = ForgeRegistries.BLOCKS.getValue(rl);

    if (out == Blocks.AIR) {
      throw new EntryNotFoundException(rl, "blocks");
    }

    return out;
  }

  public static Fluid findFluid(ResourceLocation rl) {
    Fluid out = ForgeRegistries.FLUIDS.getValue(rl);

    if (out == null) {
      throw new EntryNotFoundException(rl, "inventory_fluids");
    }

    return out;
  }

  public static Item findItem(String rl) {
    return findItem(new ResourceLocation(rl));
  }

  public static Block findBlock(String rl) {
    return findBlock(new ResourceLocation(rl));
  }

  public static Fluid findFluid(String rl) {
    return findFluid(new ResourceLocation(rl));
  }
}
