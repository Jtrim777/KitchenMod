package jake.kitchenmod.data.quest;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class Quest {

  private static double LOG2 = Math.log(2);

  private ResourceLocation name;
  private final int pathIndex;
  private boolean isRoot;
  private Quest parent;
  private final List<Quest> children;

  private QuestPredicate completionConditions;
  private QuestReward reward;
  private int level;

  private Item iconSource;
  private int tintColor;

  private Quest(ResourceLocation name, Quest parent) {
    this.name = name;
    this.parent = parent;
    this.isRoot = false;

    this.pathIndex = parent.addChild(this);
    this.children = new ArrayList<>();

    this.completionConditions = QuestPredicate.ALWAYS_TRUE;
    this.reward = new QuestReward();
    this.level = 0;

    this.iconSource = Items.DIRT;
    this.tintColor = 0xFFFFFFFF;
  }

  Quest(ResourceLocation name) {
    this.name = name;
    this.parent = null;
    this.isRoot = true;

    this.pathIndex = 0;
    this.children = new ArrayList<>();

    this.completionConditions = QuestPredicate.ALWAYS_TRUE;
    this.reward = new QuestReward();
    this.level = 0;

    this.iconSource = Items.DIRT;
    this.tintColor = 0xFFFFFFFF;
  }

  int addChild(Quest child) {
    int depth = getDepth();

    if (depth > 64) {
      throw new IllegalStateException("No more than 64 layers of quests may be added");
    }

    int childIndex = children.size();

    if (childIndex > 16) {
      throw new IllegalStateException("No more than 16 direct children may be added per node");
    }

    this.children.add(child);

    return pathIndex + (childIndex << (depth * 4));
  }

  public Quest findChild(int index) {
    if (index == 0) {
      return this;
    } else {
      int childPos = (index % 16) - 1;
      int nextIndex = index >> 4;

      if (this.children.size() > childPos) {
        try {
          return this.children.get(childPos).findChild(nextIndex);
        } catch (IllegalArgumentException e) {
          throw new IllegalArgumentException("No child quest exists with index " + index);
        }
      } else {
        throw new IllegalArgumentException("No child quest exists with index " + index);
      }
    }
  }

  public int getDepth() {
    return (int) (Math.log(this.pathIndex) / LOG2) / 4 + 1;
  }

  public int getLayerIndex() {
    return (this.pathIndex >> (getDepth() * 4)) % 16;
  }

  public ResourceLocation getName() {
    return name;
  }


  public boolean isRoot() {
    return isRoot;
  }

  public static class Builder {

    private final ResourceLocation name;
    private Quest parent;

    private QuestPredicate completionConditions;
    private QuestReward reward;
    private int level = -1;

    private ResourceLocation iconSource;
    private int tintColor = -1;

    public Builder(ResourceLocation name) {
      this.name = name;
    }

    public Builder parent(Quest parent) {
      this.parent = parent;

      return this;
    }

    public Builder conditions(QuestPredicate conds) {
      this.completionConditions = conds;

      return this;
    }

    public Builder reward(QuestReward reward) {
      this.reward = reward;

      return this;
    }

    public Builder level(int level) {
      this.level = level;

      return this;
    }

    public Builder icon(ResourceLocation source) {
      this.iconSource = source;

      return this;
    }

    public Builder tint(int tint) {
      this.tintColor = tint;

      return this;
    }

    public Quest build() {
      Quest quest;
      if (parent != null) {
        quest = new Quest(name, parent);
      } else {
        quest = new Quest(name);
      }

      if (completionConditions != null) {
        quest.completionConditions = completionConditions;
      }

      if (reward != null) {
        quest.reward = reward;
      }

      if (level != -1) {
        quest.level = level;
      }

      if (iconSource != null) {
        quest.iconSource = ForgeRegistries.ITEMS.getValue(iconSource);
      }

      if (tintColor != -1) {
        quest.tintColor = tintColor;
      }

      return quest;
    }
  }
}
