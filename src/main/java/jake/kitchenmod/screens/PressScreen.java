package jake.kitchenmod.screens;

import jake.kitchenmod.containers.PressContainer;
import jake.kitchenmod.screens.components.ProgressBar;
import jake.kitchenmod.screens.components.TankComponent;
import jake.kitchenmod.screens.components.TextureBinding;
import jake.kitchenmod.tiles.PressTile;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class PressScreen extends KMScreen<PressContainer, PressTile> {

  private static final TextureBinding PROGRESS_OVERLAY =
      new TextureBinding(98, 34, 197, 54, 15, 25);

  public PressScreen(PressContainer container, PlayerInventory inventory, ITextComponent name) {
    super(container, inventory, name, "kitchenmod:textures/gui/container/press.png");

    manager.addComponent(new TankComponent(25, 5, CutoffDirection.UP,
        tileEntity::getWaterTank));
    manager.addComponent(new TankComponent(94, 61, CutoffDirection.RIGHT,
        tileEntity::getOutputTank));
    manager.addComponent(new ProgressBar(PROGRESS_OVERLAY, CutoffDirection.DOWN,
        tileEntity::getPercentComplete));
  }
}
