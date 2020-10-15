package jake.kitchenmod.screens;

import jake.kitchenmod.KitchenMod;
import jake.kitchenmod.containers.ModContainers;
import jake.kitchenmod.screens.MillScreen;
import jake.kitchenmod.screens.MixingBowlScreen;
import jake.kitchenmod.screens.OvenScreen;
import jake.kitchenmod.screens.PressScreen;
import jake.kitchenmod.setup.RegistrySetup;
import jake.kitchenmod.util.Executor;
import jake.kitchenmod.util.FeatureManager.FeaturePackage;
import java.util.Arrays;
import java.util.List;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;

public class ModScreens {

  private static final List<Executor> SCREEN_REGISTERS = Arrays.asList(
      screen(ModContainers.MILL_CT, MillScreen::new),

      screen(ModContainers.PRESS_CT, PressScreen::new),

      screen(ModContainers.MIXING_BOWL_CT, MixingBowlScreen::new),

      screen(ModContainers.OVEN_CT, OvenScreen::new)
  );

  public static <M extends Container, U extends Screen & IHasContainer<M>> Executor
  screen(ContainerType<M> ct, ScreenManager.IScreenFactory<M, U> creator) {
    return () -> ScreenManager.registerFactory(ct, creator);
  }

  public static void registerScreenFactories() {
    SCREEN_REGISTERS.forEach(Executor::execute);

    KitchenMod.FEATURE_MANAGER.getListFromFeatures(FeaturePackage::getScreenFactories)
        .forEach(Executor::execute);

    KitchenMod.TASK_MANAGER.fireTask(RegistrySetup.REGISTER_SCREENS);
  }
}
