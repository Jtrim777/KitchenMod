package jake.kitchenmod.datagen;

import jake.kitchenmod.KitchenMod;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@EventBusSubscriber(modid = KitchenMod.MODID, bus = Bus.MOD)
public class ModData {
  @SubscribeEvent
  public static void generateData(GatherDataEvent event) {
    DataGenerator generator = event.getGenerator();

    generator.addProvider(new RecipeGenerator(generator));
  }
}
