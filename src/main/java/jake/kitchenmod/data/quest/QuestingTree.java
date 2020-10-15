package jake.kitchenmod.data.quest;

import jake.kitchenmod.KitchenMod;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

public class QuestingTree implements INBTSerializable<CompoundNBT> {
  private Quest root;
  private List<Integer> completedQuests;

  public QuestingTree(Quest root) {
    this.root = root;
    this.completedQuests = new ArrayList<>();
  }

  public QuestingTree() {
    this.root = QuestManager.BLANK_ROOT;
  }

  public Quest findNode(int index) {
    return null;
  }

  public static QuestingTree requestOne() {
    return null;
  }

  @Override
  public CompoundNBT serializeNBT() {
    CompoundNBT out = new CompoundNBT();

    ListNBT outList = new ListNBT();

    completedQuests.stream().map(IntNBT::new).forEach(outList::add);

    out.putString("root", root.getName().toString());
    out.put("completed", outList);

    return out;
  }

  @Override
  public void deserializeNBT(CompoundNBT nbt) {
    this.root = KitchenMod.QUEST_MANAGER.getRoot(new ResourceLocation(nbt.getString("root")));

    ListNBT completes = nbt.getList("completed", 3);
    completes.forEach((i) -> this.completedQuests.add(((IntNBT)i).getInt()));
  }
}
