package jake.kitchenmod.village;

import com.google.common.collect.ImmutableSet;
import jake.kitchenmod.KitchenMod;
import jake.kitchenmod.blocks.ModBlocks;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.village.PointOfInterestType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ObjectHolder;

@EventBusSubscriber(modid = KitchenMod.MODID, bus = Bus.MOD)
@ObjectHolder(KitchenMod.MODID)
public class ModProfessions {

  private static Method poiInjector;

  static {
    poiInjector = ObfuscationReflectionHelper
        .findMethod(PointOfInterestType.class, "func_221052_a", PointOfInterestType.class);
  }

  @ObjectHolder("minecraft:farmer")
  public static final PointOfInterestType FARMER_POI = null;

  @ObjectHolder("baker")
  public static final PointOfInterestType BAKER_POI = null;

  @ObjectHolder("minecraft:farmer")
  public static final VillagerProfession FARMER = null;

  @ObjectHolder("baker")
  public static final VillagerProfession BAKER = null;

  @SubscribeEvent
  public static void registerPointsOfInterest(RegistryEvent.Register<PointOfInterestType> event) {
    List<PointOfInterestType> pois = Arrays.asList(
        overridePOI("farmer", ModBlocks.COMPOSTER, SoundEvents.ENTITY_VILLAGER_WORK_FARMER),
        poi("baker", ModBlocks.OVEN, SoundEvents.ENTITY_VILLAGER_WORK_BUTCHER)
    );

    pois.forEach((poi) -> {
      event.getRegistry().register(poi);

      try {
        poiInjector.invoke(null, poi);
      } catch (IllegalAccessException | InvocationTargetException e) {
        e.printStackTrace();
      }
    });
  }

  @SubscribeEvent
  public static void registerVillagerProfessions(RegistryEvent.Register<VillagerProfession> event) {
    event.getRegistry().registerAll(
        profession("farmer", FARMER_POI,
            ImmutableSet.of(Items.WHEAT, Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS),
            ImmutableSet.of(Blocks.FARMLAND)).setRegistryName("minecraft:farmer"),
        profession("baker", BAKER_POI)
    );
  }

  private static PointOfInterestType poi(String name, Block block, SoundEvent sound) {
    return (new PointOfInterestType(name, getAllStates(block), 1, sound, 1))
        .setRegistryName(KitchenMod.MODID, name);
  }

  private static PointOfInterestType overridePOI(String name, Block block, SoundEvent sound) {
    return (new PointOfInterestType(name, getAllStates(block), 1, sound, 1))
        .setRegistryName("minecraft", name);
  }

  private static VillagerProfession profession(String name, PointOfInterestType poi) {
    return profession(name, poi, ImmutableSet.of(), ImmutableSet.of())
        .setRegistryName(KitchenMod.MODID, name);
  }

  private static VillagerProfession profession(String name, PointOfInterestType poi,
      ImmutableSet<Item> shareItems, ImmutableSet<Block> secondaryPOIs) {
    return new VillagerProfession(name, poi, shareItems, secondaryPOIs);
  }

  private static ImmutableSet<BlockState> getAllStates(Block block) {
    return ImmutableSet.copyOf(block.getStateContainer().getValidStates());
  }
}
