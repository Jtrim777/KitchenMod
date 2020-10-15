package jake.kitchenmod.setup;

import jake.kitchenmod.KitchenMod;
import jake.kitchenmod.util.Executor;
import jake.kitchenmod.util.FeatureManager.FeaturePackage;
import jake.kitchenmod.util.ModTaskManager.TaskKey;
import java.util.function.Consumer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegistryBuilder;

@Mod.EventBusSubscriber(modid = KitchenMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistrySetup {

  @SubscribeEvent
  public static void createRegistries(RegistryEvent.NewRegistry event) {
    KitchenMod.FEATURE_MANAGER.getListFromFeatures(FeaturePackage::getRegistryFactories)
        .forEach(Executor::execute);

    Executor blank = Executor.DO_NOTHING;

    KitchenMod.TASK_MANAGER
        .addTask(REGISTER_BLOCKS, blank)
        .addTask(REGISTER_ITEMS, blank)
        .addTask(REGISTER_CONTAINERS, blank)
        .addTask(REGISTER_ENTITIES, blank)
        .addTask(REGISTER_TILES, blank)
        .addTask(REGISTER_SCREENS, blank)
        .addTask(REGISTER_RECIPES, blank)
        .addTask(REGISTER_FLUIDS, blank);
  }

  public static <ET extends IForgeRegistryEntry<ET>> Executor
  registryFactory(Class<ET> entryType, String name) {
    return () -> new RegistryBuilder<ET>()
        .setType(entryType)
        .setName(new ResourceLocation(KitchenMod.MODID, name))
        .create();
  }

  public static <ET extends IForgeRegistryEntry<ET>> Executor
  registryFactory(Class<ET> entryType, String name, Consumer<IForgeRegistry<ET>> setter) {
    return () -> setter.accept(new RegistryBuilder<ET>()
        .setType(entryType)
        .setName(new ResourceLocation(KitchenMod.MODID, name))
        .create());
  }

  public static final TaskKey REGISTER_BLOCKS = new TaskKey("rblocks");
  public static final TaskKey REGISTER_ITEMS = new TaskKey("ritems");
  public static final TaskKey REGISTER_TILES = new TaskKey("rtiles");
  public static final TaskKey REGISTER_CONTAINERS = new TaskKey("rconts");
  public static final TaskKey REGISTER_RECIPES = new TaskKey("rrecipes");
  public static final TaskKey REGISTER_ENTITIES = new TaskKey("rentities");
  public static final TaskKey REGISTER_SCREENS = new TaskKey("rscreens");
  public static final TaskKey REGISTER_FLUIDS = new TaskKey("rfluids");
}
