package jake.kitchenmod.containers;

import jake.kitchenmod.KitchenMod;
import jake.kitchenmod.blocks.ModBlocks;
import jake.kitchenmod.setup.RegistrySetup;
import jake.kitchenmod.util.FeatureManager.FeaturePackage;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = KitchenMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(KitchenMod.MODID)
public class ModContainers {

  @ObjectHolder("mill")
  public static final ContainerType<MillContainer> MILL_CT = null;

  @ObjectHolder("mixing_bowl")
  public static final ContainerType<MixingBowlContainer> MIXING_BOWL_CT = null;

  @ObjectHolder("press")
  public static final ContainerType<PressContainer> PRESS_CT = null;

  @ObjectHolder("oven")
  public static final ContainerType<OvenContainer> OVEN_CT = null;

  @ObjectHolder("sage")
  public static final ContainerType<SageContainer> SAGE_CT = null;

  public static ContainerType<?> container(String name, ContainerType<?> type, Block source,
      Maker maker) {
    return IForgeContainerType.create(((windowId, inv, data) -> {
      BlockPos pos = data.readBlockPos();
      return maker.make(windowId, type, source, KitchenMod.proxy.getClientWorld(), pos, inv);
    })).setRegistryName(KitchenMod.MODID, name);
  }

  public static ContainerType<?> entityContainer(String name, ContainerType.IFactory<?> factory) {
    return (new ContainerType<>(factory)).setRegistryName(KitchenMod.MODID, name);
  }

  @SubscribeEvent
  public static void registerContainers(RegistryEvent.Register<ContainerType<?>> event) {
    event.getRegistry().registerAll(
        container("mill", MILL_CT, ModBlocks.MILL, MillContainer::new),
        container("mixing_bowl", MIXING_BOWL_CT, ModBlocks.MIXING_BOWL,
            MixingBowlContainer::new),
        container("press", PRESS_CT, ModBlocks.PRESS, PressContainer::new),
        container("oven", OVEN_CT, ModBlocks.OVEN, OvenContainer::new),
        entityContainer("sage", SageContainer::new)
    );

    KitchenMod.FEATURE_MANAGER.getListFromFeatures(FeaturePackage::getContainers)
        .forEach(event.getRegistry()::register);

    KitchenMod.TASK_MANAGER.fireTask(RegistrySetup.REGISTER_CONTAINERS);
  }

  public interface Maker {

    Container make(int p1, ContainerType p2, Block p3, World p4, BlockPos p5, PlayerInventory p6);
  }
}
