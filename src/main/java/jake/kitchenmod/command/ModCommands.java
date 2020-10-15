package jake.kitchenmod.command;

import com.mojang.brigadier.CommandDispatcher;
import jake.kitchenmod.KitchenMod;
import jake.kitchenmod.util.FeatureManager.FeaturePackage;
import net.minecraft.command.CommandSource;

public class ModCommands {

  public static void registerCommands(CommandDispatcher<CommandSource> dispatcher) {
    (new FreezeTileCommand()).register(dispatcher);
    (new UnfreezeTileCommand()).register(dispatcher);

    KitchenMod.FEATURE_MANAGER.getListFromFeatures(FeaturePackage::getCommands)
        .forEach(cmd -> cmd.register(dispatcher));
  }
}
