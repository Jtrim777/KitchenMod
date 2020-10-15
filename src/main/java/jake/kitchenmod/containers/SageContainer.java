package jake.kitchenmod.containers;

import jake.kitchenmod.data.quest.Quest;
import jake.kitchenmod.entities.SageVillagerEntity;
import jake.kitchenmod.inventory.KMSlot.Types;
import jake.kitchenmod.inventory.KMSlotMap;
import jake.kitchenmod.inventory.SageInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;

public class SageContainer extends Container {

  public static KMSlotMap SLOTS;

  private SageVillagerEntity sage;
  private SageInventory inventory;
  private Quest activeQuest;

  public SageContainer(int windowID, PlayerInventory playerInventory) {
    this(windowID, null, playerInventory);
  }

  public SageContainer(int windowID, SageVillagerEntity sage, PlayerInventory playerInventory) {
    super(ModContainers.SAGE_CT, windowID);
    initSlotMap();

    this.sage = sage;
    this.activeQuest = sage.getQuestingTree().findNode(0);
  }

  public boolean canInteractWith(PlayerEntity playerIn) {
    return this.sage.getCustomer() == playerIn;
  }

  private static void initSlotMap() {
    if (SLOTS != null) {
      return;
    }

    SLOTS = (new KMSlotMap())
        .addRange("INPUT", Types.GENERIC_INGREDIENT, 0, 4)
        .addOne("OUTPUT", Types.OUTPUT);
  }

  public Quest getActiveQuest() {
    return activeQuest;
  }

  public void setActiveQuestIndex(int questIndex) {
    this.activeQuest = sage.getQuestingTree().findNode(questIndex);
  }
}
