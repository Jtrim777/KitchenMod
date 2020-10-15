package jake.kitchenmod.data.quest;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import jake.kitchenmod.KitchenMod;
import jake.kitchenmod.inventory.SageInventory;
import jake.kitchenmod.util.JsonConverter;
import jake.kitchenmod.util.JsonUtil;
import jake.kitchenmod.util.ModUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.ServerStatisticsManager;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.util.ResourceLocation;

public interface QuestPredicate extends BiPredicate<ServerPlayerEntity, SageInventory> {

  Map<ResourceLocation, JsonConverter<QuestPredicate>> PREDICATES = new HashMap<>();

  static JsonConverter<QuestPredicate> register(ResourceLocation name,
      JsonConverter<QuestPredicate> predicateMaker) {
    PREDICATES.put(name, predicateMaker);

    return predicateMaker;
  }

  @Override
  default QuestPredicate and(
      BiPredicate<? super ServerPlayerEntity, ? super SageInventory> other) {
    return (player, inv) -> this.test(player, inv) && other.test(player, inv);
  }

  QuestPredicate ALWAYS_TRUE = (serverPlayerEntity, inventory) -> true;

  JsonConverter<QuestPredicate> ITEMS_SERIALIZER = register(new ResourceLocation(KitchenMod.MODID, "items"),
      new JsonConverter<QuestPredicate>() {
    private final JsonUtil parser = new JsonUtil("QuestPredicateSerializer::Items");

    @Override
    public QuestPredicate read(JsonElement json) {
      parser.setContext(json.getAsJsonObject(), new ResourceLocation("kitchenmod:unknown"));

      List<ItemStack> itemConds = parser.getOrThrow("item_conditions", "List<ItemStack>",
          parser.listOf(ModUtil::itemStackFromJson));

      Predicate<SageInventory> inventoryCheck = (inv) -> true;

      if (itemConds.size() > 4) {
        throw parser.badValue("item_conditions", itemConds.toString(),
            "There may not be more than four required item conditions");
      } else if (itemConds.size() > 0) {
        return (player, inv) -> itemConds.stream()
            .map(stack -> inv.containsStackInRange(stack, 0, 5))
            .reduce(true, Boolean::logicalAnd);
      } else {
        return ALWAYS_TRUE;
      }
    }

    @Override
    public JsonElement write(QuestPredicate value) {
      return null;
    }
  });

  JsonConverter<QuestPredicate> STATS_SERIALIZER = register(new ResourceLocation(KitchenMod.MODID, "player_stats"),
      new JsonConverter<QuestPredicate>() {
    private final JsonUtil parser = new JsonUtil("QuestPredicateSerializer::Stats");

    @Override
    public QuestPredicate read(JsonElement json) {
      parser.setContext(json.getAsJsonObject(), new ResourceLocation("kitchenmod:unknown"));

      String typ1 = "List<Pair<ResourceLocation, Integer>>";

      List<Pair<ResourceLocation, Integer>> allStatPairs = parser.getOrThrow("stats", typ1,
          parser.listOf(this::parsePair));

      List<Pair<Stat<ResourceLocation>, Integer>> allStats = /*new ArrayList<>();*/allStatPairs.stream()
          .map((pair) -> new Pair<>(Stats.CUSTOM.get(pair.getFirst()), pair.getSecond()))
      .collect(Collectors.toList());

//      for (Pair<ResourceLocation, Integer> statPair : allStatPairs) {
//        allStats.add(new P)
//      }

      Predicate<ServerPlayerEntity> playerTest = (player) -> {
        ServerStatisticsManager stats = player.getStats();

        for (Pair<Stat<ResourceLocation>, Integer> statPair : allStats) {
          if (stats.getValue(statPair.getFirst()) != statPair.getSecond()) {
            return false;
          }
        }

        return true;
      };

      return (player, inv) -> playerTest.test(player);
    }

    @Override
    public JsonElement write(QuestPredicate value) {
      return null;
    }

    private Pair<ResourceLocation, Integer> parsePair(JsonElement jo) {
      parser.setContext(jo.getAsJsonObject(), new ResourceLocation("kitchenmod:unknown"));

      ResourceLocation id = parser.getOrThrow("id", ResourceLocation.class,
          JsonConverter.RESOURCE_LOCATION::read);
      int value = parser.getOrThrow("value", int.class, JsonElement::getAsInt);

      return new Pair<>(id, value);
    }
  });



  JsonConverter<QuestPredicate> SERIALIZER = new JsonConverter<QuestPredicate>() {
    final JsonUtil parser = new JsonUtil("QuestPredicateSerializer");

    @Override
    public QuestPredicate read(JsonElement json) {
      JsonArray allPredicates = json.getAsJsonArray();

      List<QuestPredicate> components = new ArrayList<>();
      allPredicates.iterator().forEachRemaining((elem) -> {
        parser.setContext(elem.getAsJsonObject());

        ResourceLocation type = parser.getOrThrow("type", ResourceLocation.class,
            JsonConverter.RESOURCE_LOCATION::read);

        if (PREDICATES.containsKey(type)) {
          components.add(PREDICATES.get(type).read(elem));
        } else {
          throw parser.badValue("type", type.toString(), "Not a recognized quest "
              + "completion predicate type");
        }
      });

      return components.stream().reduce(ALWAYS_TRUE, QuestPredicate::and);
    }

    @Override
    public JsonElement write(QuestPredicate value) {
      return null;
    }
  };
}
