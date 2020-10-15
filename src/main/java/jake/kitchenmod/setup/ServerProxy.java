package jake.kitchenmod.setup;

import jake.kitchenmod.data.quest.QuestManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class ServerProxy implements IProxy {

  private QuestManager questManager = new QuestManager();

  @Override
  public void init() {
    Minecraft.getInstance().world.getRecipeManager();
  }

  @Override
  public World getClientWorld() {
    throw new IllegalStateException("Only run this on the client!");
  }

  @Override
  public PlayerEntity getClientPlayer() {
    throw new IllegalStateException("Only run this on the client!");
  }
}
