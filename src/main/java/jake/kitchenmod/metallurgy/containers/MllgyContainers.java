package jake.kitchenmod.metallurgy.containers;

import jake.kitchenmod.KitchenMod;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(KitchenMod.MODID)
public class MllgyContainers {

  @ObjectHolder("smeltery")
  public static final ContainerType<SmelteryContainer> SMELTERY_CT = null;
}
