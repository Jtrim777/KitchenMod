package jake.kitchenmod.entities;

import jake.kitchenmod.KitchenMod;
import jake.kitchenmod.setup.RegistrySetup;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = KitchenMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(KitchenMod.MODID)
public class ModEntities {

  @ObjectHolder("basilisk")
  public static final EntityType<BasiliskEntity> BASILISK = null;

  @ObjectHolder("lion")
  public static final EntityType<LionEntity> LION = null;

  @SubscribeEvent
  public static void registerEntityTypes(RegistryEvent.Register<EntityType<?>> event) {
    EntityType snek_ins =
        EntityType.Builder.<BasiliskEntity>create(BasiliskEntity::new, EntityClassification.MONSTER)
            .size(2.9F, 2.1F)
            .build("basilisk").setRegistryName(KitchenMod.MODID, "basilisk");

    EntityType<?> lion_ins = EntityType.Builder.<LionEntity>create(
        LionEntity::new, EntityClassification.CREATURE)
        .size(0.9f, 1.5f)
        .build("lion").setRegistryName(KitchenMod.MODID, "lion");

    event.getRegistry().registerAll(
//        snek_ins
        lion_ins
    );

    KitchenMod.TASK_MANAGER.fireTask(RegistrySetup.REGISTER_ENTITIES);
  }
}
