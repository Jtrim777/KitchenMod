package jake.kitchenmod.gen;

import jake.kitchenmod.KitchenMod;
import jake.kitchenmod.util.Executor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModGen {
  private static Map<LoadPhase, List<Executor>> generationFunctions = new HashMap<>();

  static {
    for (LoadPhase phase : LoadPhase.values()) {
      generationFunctions.put(phase, new ArrayList<>());
    }
  }

  public static void registerFx(Executor fx) {
    generationFunctions.get(LoadPhase.COMMON).add(fx);
  }

  public static void registerFx(Executor fx, LoadPhase phase) {
    generationFunctions.get(phase).add(fx);
  }

  public static void executeGeneration(LoadPhase phase) {
    KitchenMod.LOGGER.log("Applying world generation functions [In phase "+phase.name()+"]",
        "ModGen");
    KitchenMod.LOGGER.startTask("modifyWorldGen:"+phase.name());

    for (Executor e : generationFunctions.get(phase)) {
      e.execute();
    }

    KitchenMod.LOGGER.endTask("modifyWorldGen:"+phase.name());
  }

  public enum LoadPhase {
    COMMON,
    SERVER_PRE,
    SERVER_START,
    SERVER_POST
  }
}
