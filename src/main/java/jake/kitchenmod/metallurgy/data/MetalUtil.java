package jake.kitchenmod.metallurgy.data;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

public class MetalUtil {
  public static IForgeRegistry<KMMetal> REGISTRY;

  public static KMMetal getMetal(Item source) {
    return REGISTRY.getValues().stream()
        .filter(m -> m.formsContains(source)).findFirst().orElse(null);
  }

  public static KMMetal getMetal(Fluid moltenForm) {
    return REGISTRY.getValues().stream()
        .filter(m -> m.isMoltenForm(moltenForm)).findFirst().orElse(null);
  }
}
