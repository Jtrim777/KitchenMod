package jake.kitchenmod.screens;

import jake.kitchenmod.containers.MixingBowlContainer;
import jake.kitchenmod.tiles.MixingBowlTile;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class MixingBowlScreen extends KMScreen<MixingBowlContainer, MixingBowlTile> {


  public MixingBowlScreen(MixingBowlContainer container, PlayerInventory inventory,
      ITextComponent name) {
    super(container, inventory, name, "kitchenmod:textures/gui/container/mixing_bowl.png");
  }
}
