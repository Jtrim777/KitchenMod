package jake.kitchenmod.recipes;

import jake.kitchenmod.KitchenMod;
import jake.kitchenmod.setup.RegistrySetup;
import jake.kitchenmod.util.FeatureManager.FeaturePackage;
import java.util.Optional;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = KitchenMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModRecipes {

  public static final IRecipeType<MillRecipe> MILL_RT = registerRT("mill");
  ;
  public static final IRecipeType<PressRecipe> PRESS_RT = registerRT("press");
  public static final IRecipeType<MixingRecipe> MIXING_RT = registerRT("mixing");
  public static final IRecipeType<OvenRecipe> OVEN_RT = registerRT("oven");

  public static IRecipeSerializer factory(String name, IRecipeSerializer value) {
    return (IRecipeSerializer) value.setRegistryName(new ResourceLocation(KitchenMod.MODID, name));
  }

  @SubscribeEvent
  public static void registerRecipeFactories(RegistryEvent.Register<IRecipeSerializer<?>> event) {
    IForgeRegistry<IRecipeSerializer<?>> registry = event.getRegistry();

    registry.registerAll(
        factory("mill", new MillRecipeFactory()),
        factory("mixing", new MixingRecipeFactory()),
        factory("press", new PressRecipeFactory()),
        factory("oven", new OvenRecipeFactory())
    );

    KitchenMod.FEATURE_MANAGER.getListFromFeatures(FeaturePackage::getRecipeFactories)
        .forEach(registry::register);

    KitchenMod.TASK_MANAGER.fireTask(RegistrySetup.REGISTER_RECIPES);
  }

  public static <T extends IRecipe<?>> IRecipeType<T> registerRT(String name) {
    return Registry.register(Registry.RECIPE_TYPE,
        new ResourceLocation(KitchenMod.MODID, name),
        new IRecipeType<T>() {
          public String toString() {
            return name;
          }
        });
  }
}

