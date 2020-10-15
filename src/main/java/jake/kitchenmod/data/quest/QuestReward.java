package jake.kitchenmod.data.quest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jake.kitchenmod.util.JsonConverter;
import jake.kitchenmod.util.JsonUtil;
import jake.kitchenmod.util.ModUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class QuestReward {
  public static final QuestReward.Serializer SERIALIZER = new QuestReward.Serializer();

  private ItemStack itemReward;
  private int xpReward;

  public QuestReward(ItemStack itemReward, int xpReward) {
    this.itemReward = itemReward;
    this.xpReward = xpReward;
  }

  public QuestReward() {
    this(ItemStack.EMPTY, 0);
  }

  public QuestReward(ItemStack itemReward) {
    this(itemReward, 0);
  }

  public QuestReward(int xp) {
    this(ItemStack.EMPTY, xp);
  }

  public int getXpReward() {
    return xpReward;
  }

  public ItemStack getItemReward() {
    return itemReward;
  }

  static class Serializer implements JsonConverter<QuestReward> {
    private static final JsonUtil PARSER = new JsonUtil("QuestRewardSerializer");

    @Override
    public QuestReward read(JsonElement json) {
      JsonObject base = json.getAsJsonObject();
      PARSER.setContext(base, new ResourceLocation("kitchenmod:unknown"));

      QuestReward out = new QuestReward();

      PARSER.getOptional("items", ItemStack.class, ModUtil::itemStackFromJson)
          .ifPresent((is) -> out.itemReward = is);

      PARSER.getOptional("xp", int.class, JsonElement::getAsInt)
          .ifPresent((xp) -> out.xpReward = xp);

      return out;
    }

    @Override
    public JsonElement write(QuestReward value) {
      JsonObject out = new JsonObject();

      if (value.xpReward != 0) {
        out.addProperty("xp", value.xpReward);
      }

      if (value.itemReward != ItemStack.EMPTY) {
        out.add("items", ModUtil.writeItemStackToJson(value.itemReward));
      }

      return out;
    }
  }
}
