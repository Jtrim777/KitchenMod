package jake.kitchenmod.entities;

import jake.kitchenmod.data.quest.QuestingTree;
import jake.kitchenmod.util.ModUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class SageVillagerEntity extends VillagerEntity {

  private QuestingTree questingTree;

  public SageVillagerEntity(EntityType<? extends VillagerEntity> entityType, World world) {
    super(entityType, world);
  }

  public boolean processInteract(PlayerEntity player, Hand hand) {
    if (questingTree == null) {
      questingTree = QuestingTree.requestOne();
    }

    ItemStack holding = player.getHeldItem(hand);
    if (holding.getItem() != Items.VILLAGER_SPAWN_EGG
        && this.isAlive()
        && !this.func_213716_dX()
        && !this.isSleeping()
        && !player.isSneaking()
        && !this.isChild()
        && ModUtil.isServerWorld(world)) {

      this.openQuestingContainer(player);

      return true;
    }

    return super.processInteract(player, hand);
  }

  private void openQuestingContainer(PlayerEntity player) {

  }

  @Override
  protected void populateTradeData() {

  }

  public QuestingTree getQuestingTree() {
    return questingTree;
  }
}
