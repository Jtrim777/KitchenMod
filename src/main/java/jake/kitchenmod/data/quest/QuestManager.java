package jake.kitchenmod.data.quest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jake.kitchenmod.KitchenMod;
import jake.kitchenmod.data.quest.Quest.Builder;
import jake.kitchenmod.util.JsonConverter;
import jake.kitchenmod.util.JsonUtil;
import jake.kitchenmod.util.ModLogger.LogLevel;
import jake.kitchenmod.util.ModUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class QuestManager extends JsonReloadListener {
  private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
  private static final String FOLDER_NAME = "quests";
  private static final JsonUtil PARSER = new JsonUtil("QuestManager");

  public static final Quest BLANK_ROOT = new Quest(new ResourceLocation("kitchenmod:blank"));
  
  private Map<ResourceLocation, Quest> roots;
  private Map<ResourceLocation, List<Quest.Builder>> orphans;

  public QuestManager() {
    super(GSON, FOLDER_NAME);
    
    this.roots = new HashMap<>();
    this.orphans = new HashMap<>();
  }

  @Override
  protected void apply(Map<ResourceLocation, JsonObject> splashList,
      IResourceManager resourceManagerIn, IProfiler profilerIn) {

    roots = new HashMap<>();
    Map<ResourceLocation, Quest> built = new HashMap<>();

    for (Map.Entry<ResourceLocation, JsonObject> entry : splashList.entrySet()) {
      ResourceLocation questName = entry.getKey();
      JsonObject body = entry.getValue();

      PARSER.setContext(body, questName);

      Quest.Builder builder = new Builder(questName);
      
      PARSER.getOptional("conditions", QuestPredicate.class, QuestPredicate.SERIALIZER::read)
          .ifPresent(builder::conditions);

      PARSER.getOptional("reward", QuestReward.class, QuestReward.SERIALIZER::read)
          .ifPresent(builder::reward);

      PARSER.getOptional("level", int.class, JsonElement::getAsInt)
          .ifPresent(builder::level);

      PARSER.getOptional("tint_color", int.class, JsonElement::getAsInt)
          .ifPresent(builder::tint);

      PARSER.getOptional("icon_source", ResourceLocation.class,
          JsonConverter.RESOURCE_LOCATION::read).ifPresent(builder::icon);

      if (body.has("parent")) {
        ResourceLocation parentName = PARSER.getOrThrow("parent", ResourceLocation.class,
            JsonConverter.RESOURCE_LOCATION::read);

        if (built.containsKey(parentName)) {
          builder.parent(built.get(parentName));

          Quest done = builder.build();
          built.put(done.getName(), done);

          resolveOrphanage(done, built);
        } else {
          addOrphan(parentName, builder);
        }
      } else {
        Quest done = builder.build();
        built.put(done.getName(), done);

        resolveOrphanage(done, built);
      }
    }

    if (orphans.size() > 0) {
      KitchenMod.LOGGER.log("Failed to resolve parentage of some quests", "QuestManager",
          LogLevel.WARNING);
      KitchenMod.LOGGER.advanceLogLevel(1);
      KitchenMod.LOGGER.log(ModUtil.formatMap(orphans), "QuestManager", LogLevel.WARNING);
      KitchenMod.LOGGER.reduceLogLevel(1);
    }

    this.orphans = new HashMap<>();
    for (Quest quest : built.values()) {
      if (quest.isRoot()) {
        roots.put(quest.getName() ,quest);
      }
    }
  }
  
  private void addOrphan(ResourceLocation parentName, Quest.Builder orphan) {
    if (orphans.containsKey(parentName)) {
      orphans.get(parentName).add(orphan);
    } else {
      List<Quest.Builder> start = new ArrayList<>();
      start.add(orphan);
      orphans.put(parentName, start);
    }
  }
  
  private void resolveOrphanage(Quest parent, Map<ResourceLocation, Quest> outList) {
    if (orphans.containsKey(parent.getName())) {
      for (Quest.Builder qb : orphans.get(parent.getName())) {
        qb.parent(parent);
        
        Quest child = qb.build();
        
        outList.put(child.getName(), child);
      }

      orphans.remove(parent.getName());
    }
  }

  public Quest getRoot(ResourceLocation name) {
    return roots.getOrDefault(name, BLANK_ROOT);
  }
}
