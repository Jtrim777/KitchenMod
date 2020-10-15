package jake.kitchenmod.gen.village_util;

import com.mojang.datafixers.util.Pair;
import jake.kitchenmod.KitchenMod;
import jake.kitchenmod.util.ModLogger.LogLevel;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern.PlacementBehaviour;
import net.minecraft.world.gen.feature.jigsaw.JigsawPatternRegistry;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;

public class ModJPR extends JigsawPatternRegistry {

  private static Field pbField;

  static {
    try {
      pbField = JigsawPattern.class.getDeclaredField("field_214955_g");
      pbField.setAccessible(true);
    } catch (Exception e) {
      e.printStackTrace();
      KitchenMod.LOGGER.log("Couldn't hack JigsawPattern fields!", LogLevel.ERROR);
    }
  }

  private Map<ResourceLocation, Consumer<List<Pair<JigsawPiece, Integer>>>> overrides;

  public ModJPR() {
    super();

    overrides = new HashMap<>();
  }

  @Override
  public void register(JigsawPattern entry) {
    ResourceLocation key = entry.func_214947_b();

    if (overrides == null) return;

    if (overrides.containsKey(key)) {
      List<Pair<JigsawPiece, Integer>> pool = extractPool(entry);

      KitchenMod.LOGGER.log("Overriding structure pool " + key.toString(),
          "ModJigsawPatternRegistry");

      KitchenMod.LOGGER.log("Original: " + formatPool(pool), 1, "ModJigsawPatternRegistry");

      overrides.get(key).accept(pool);

      KitchenMod.LOGGER.log("Final: " + formatPool(pool), 1, "ModJigsawPatternRegistry");

      super.register(new JigsawPattern(key, entry.func_214948_a(), pool,
          getPlacementBehavior(entry)));
    } else {
      super.register(entry);
    }
  }

  public void modRegister(JigsawPattern entry) {
    super.register(entry);
  }

  public void registerOverride(String poolName,
      Consumer<List<Pair<JigsawPiece, Integer>>> mod) {

    ResourceLocation pool = new ResourceLocation("minecraft:" + poolName);

    if (overrides.containsKey(pool)) {
      overrides.put(pool, mergeFx(overrides.get(pool), mod));
    } else {
      overrides.put(pool, mod);
    }
  }

  @Override
  public JigsawPattern get(ResourceLocation key) {
    return super.get(key);
  }

  private static <T> Consumer<T> mergeFx(Consumer<T> fx1, Consumer<T> fx2) {
    return (inp) -> {
      fx1.accept(inp);
      fx2.accept(inp);
    };
  }

  private static List<Pair<JigsawPiece, Integer>> extractPool(JigsawPattern p1) {

    List<Pair<JigsawPiece, Integer>> basePool = new ArrayList<>();

    List<JigsawPiece> baseList = new ArrayList<>(p1.func_214943_b(new Random()));

    while (baseList.size() > 0) {
      JigsawPiece piece = baseList.get(0);
//      seenPieces.add(piece.toString());

      int count = baseList.stream().reduce(0,
          (last, elem) -> last + (elem == piece ? 1 : 0), Integer::sum);

      basePool.add(new Pair<>(piece, count));

      baseList.removeAll(Collections.singletonList(piece));
    }

    return basePool;
  }

  private static PlacementBehaviour getPlacementBehavior(JigsawPattern jp) {
    try {
      return (PlacementBehaviour)pbField.get(jp);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
      return null;
    }
  }

  private static String formatPool(List<Pair<JigsawPiece, Integer>> pool) {
    return pool.stream().map(p -> "(" + p.getFirst().toString() + " | " + p.getSecond() + ")")
        .collect(Collectors.joining(", ", "[", "]"));
  }
}
