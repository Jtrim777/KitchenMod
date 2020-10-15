package jake.kitchenmod;

import jake.kitchenmod.data.quest.QuestManager;
import jake.kitchenmod.horticulture.HorticultureFeature;
import jake.kitchenmod.metallurgy.MetallurgyFeature;
import jake.kitchenmod.setup.ClientProxy;
import jake.kitchenmod.setup.IProxy;
import jake.kitchenmod.setup.ModSetup;
import jake.kitchenmod.setup.ServerProxy;
import jake.kitchenmod.util.FeatureManager;
import jake.kitchenmod.util.ModLogger;
import jake.kitchenmod.util.ModTaskManager;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("kitchenmod")
@Mod.EventBusSubscriber(modid = KitchenMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class KitchenMod {

  public static final String MODID = "kitchenmod";

  public static IProxy proxy =
      DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);

  public static ModSetup setup = new ModSetup();

  public static ModLogger LOGGER;

  public static final FeatureManager FEATURE_MANAGER = new FeatureManager();

  public static final ModTaskManager TASK_MANAGER = new ModTaskManager();

//  @OnlyIn(Dist.DEDICATED_SERVER)
  public static final QuestManager QUEST_MANAGER = new QuestManager();

  // Setup
  public KitchenMod() {
    MinecraftForge.EVENT_BUS.register(this);
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

    try {
      File logFile = new File("kitchenmod.log");
      System.out.println("[~KM~] Log File Path: " + logFile.getAbsolutePath());

      LOGGER = new ModLogger(new FileWriter(logFile));
    } catch (IOException e) {
      System.out.println("!!!!!!" + e.getLocalizedMessage());
    }

    FEATURE_MANAGER.registerFeature(new HorticultureFeature());
    FEATURE_MANAGER.registerFeature(new MetallurgyFeature());


    FEATURE_MANAGER.disableFeature("kitchenmod:horticulture");

    FEATURE_MANAGER.initializeFeatures();
    FEATURE_MANAGER.dumpFeatures(LOGGER);
  }

  public void setup(final FMLCommonSetupEvent event) {
    setup.commonSetup(event);
    proxy.init();
  }
}
