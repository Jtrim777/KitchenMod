package jake.kitchenmod.handlers;

import jake.kitchenmod.KitchenMod;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = KitchenMod.MODID)
public class EntityHandler {

  private static Map<Class<? extends LivingEntity>, List<Consumer<LivingEntity>>> listeners
      = new HashMap<>();

  private static List<Consumer<PlayerEntity>> playerListeners = new ArrayList<>();

  @SubscribeEvent
  public static void onEntityUpdate(LivingEvent.LivingUpdateEvent event) {
    LivingEntity entity = event.getEntityLiving();

    listeners.getOrDefault(entity.getClass(), new ArrayList<>()).forEach(a -> a.accept(entity));

    if (entity instanceof PlayerEntity) {
      playerListeners.forEach(pl -> pl.accept((PlayerEntity) entity));
    }
  }

  public static void registerPlayerHandler(Consumer<PlayerEntity> handler) {
    playerListeners.add(handler);

    KitchenMod.LOGGER.log("Registered new handler for entity type PlayerEntity",
        "EntityHandler");
  }
}
