package jake.kitchenmod.setup;

import jake.kitchenmod.KitchenMod;
import jake.kitchenmod.blocks.ModBlocks;
import jake.kitchenmod.gen.ModGen;
import jake.kitchenmod.gen.ModGen.LoadPhase;
import jake.kitchenmod.items.ModItems;
import jake.kitchenmod.util.FeatureManager.FeaturePackage;
import jake.kitchenmod.util.ModColors;
import jake.kitchenmod.command.ModCommands;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

@EventBusSubscriber(modid = KitchenMod.MODID)
public class ModSetup {

  /**
   * All mod-specific FMLCommonSetupEvent events should be called here.
   */
  public void commonSetup(FMLCommonSetupEvent e) {
    KitchenMod.LOGGER.log("Performing common setup tasks", "ModSetup");
    KitchenMod.LOGGER.startTask("commonSetup");

    ModColors.registerItemColors();

    ModBlocks.initializeBlockFields();

    ModItems.initializeItemFields();

    ModGen.executeGeneration(LoadPhase.COMMON);

    KitchenMod.LOGGER.endTask("commonSetup");
  }

  @SubscribeEvent
  public static void serverSetup(FMLDedicatedServerSetupEvent event) {
    event.getServerSupplier().get().getResourceManager()
        .addReloadListener(KitchenMod.QUEST_MANAGER);
  }

  @SubscribeEvent
  public static void aboutToStart(FMLServerAboutToStartEvent e) {
    ModGen.executeGeneration(LoadPhase.SERVER_PRE);

    KitchenMod.FEATURE_MANAGER.triggerRegistration(FeaturePackage::registerEntityHandlers);
  }

  @SubscribeEvent
  public static void starting(FMLServerStartingEvent e) {
    ModGen.executeGeneration(LoadPhase.SERVER_START);

    ModCommands.registerCommands(e.getCommandDispatcher());
  }

  @SubscribeEvent
  public static void started(FMLServerStartedEvent e) {
    ModGen.executeGeneration(LoadPhase.SERVER_POST);
  }
}
