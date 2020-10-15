package jake.kitchenmod.village;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import jake.kitchenmod.KitchenMod;
import jake.kitchenmod.items.ModItems;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.merchant.villager.VillagerTrades.ITrade;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KitchenMod.MODID)
public class ModTrades {

  private static LevelTradePair trade(int level, Item result, int rAmt, ItemStack... cost) {
    if (cost.length == 1) {
      return new LevelTradePair(
          new KMTrade(cost[0].getItem(), cost[0].getCount(), result, rAmt, 16,
              (int) Math.pow(2, level)), level);
    } else if (cost.length == 2) {
      return new LevelTradePair(new KMTrade(cost[0].getItem(), cost[0].getCount(),
          cost[1].getItem(), cost[1].getCount(), result, rAmt, 16, (int) Math.pow(2, level)),
          level);
    } else {
      throw new IllegalArgumentException("Max number of cost items is 2");
    }
  }

  private static LevelTradePair purchase(int level, Item result, int rCount, int emeraldCost) {
    return trade(level, result, rCount, new ItemStack(Items.EMERALD, emeraldCost));
  }

  private static LevelTradePair sell(int level, Item price, int pCount) {
    return trade(level, Items.EMERALD, 1, new ItemStack(price, pCount));
  }

  private static Map<VillagerProfession, List<LevelTradePair>> TRADES = null;

  private static void initTrades() {
    if (TRADES == null) {
      KitchenMod.LOGGER.log("Initialized trades map", "ModTrades");
      TRADES = Stream.of(
          new SimpleEntry<>(ModProfessions.FARMER, Arrays.asList(
              purchase(1, ModItems.strawberry_seeds, 4, 2),
              sell(1, ModItems.tomato, 10),
              purchase(2, ModItems.tomato_seeds, 4, 2),
              sell(2, ModItems.grape, 15),
              sell(2, ModItems.strawberry, 20),
              purchase(2, ModItems.grape_seeds, 4, 3),
              purchase(2, ModItems.stake, 5, 3),
              purchase(4, ModItems.vanilla_bean, 1, 5)
          )),
          new SimpleEntry<>(ModProfessions.BAKER, Arrays.asList(
              sell(1, ModItems.flour, 15),
              sell(1, Items.SUGAR, 20),
              purchase(1, ModItems.salt, 5, 1),
              purchase(1, Items.BREAD, 6, 1),
              purchase(2, ModItems.bread_starter, 1, 2),
              purchase(2, ModItems.stir_rod, 1, 1),
              purchase(3, Items.PUMPKIN_PIE, 4, 1),
              purchase(3, Items.COOKIE, 18, 3),
              purchase(3, ModItems.chocolate_cake_slice, 4, 2),
              purchase(4, Items.CAKE, 1, 1),
              purchase(4, ModItems.chocolate_cake, 1, 1),
              sell(4, ModItems.vanilla_bean, 2),
              sell(5, ModItems.cocoa_mass, 3)
//              purchase(5, ModItems.chocolate, 5, 6)
          ))
      ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
  }

  @SubscribeEvent
  public static void registerTrades(VillagerTradesEvent event) {
    Int2ObjectMap<List<ITrade>> eventTrades = event.getTrades();
    VillagerProfession prof = event.getType();

    initTrades();

    if (TRADES.containsKey(prof)) {
      KitchenMod.LOGGER.log("Registering trades for villager profession " + prof.toString(),
          "ModTrades");
      KitchenMod.LOGGER.startTask("registerTrades:" + prof.toString());
      TRADES.get(prof).forEach(ltp -> ltp.register(eventTrades));
      KitchenMod.LOGGER.endTask("registerTrades:" + prof.toString());
    }
  }

  private static class LevelTradePair {

    private KMTrade trade;
    private int level;

    LevelTradePair(KMTrade trade, int level) {
      this.trade = trade;
      this.level = level;
    }

    public void register(Int2ObjectMap<List<ITrade>> eventTrades) {
      KitchenMod.LOGGER.log("Registered level " + level + " trade: " + trade.toString());

      eventTrades.get(level).add(trade);
    }
  }
}
