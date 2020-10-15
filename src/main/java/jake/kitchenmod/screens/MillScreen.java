package jake.kitchenmod.screens;

import jake.kitchenmod.containers.MillContainer;
import jake.kitchenmod.screens.components.TextureBinding;
import jake.kitchenmod.screens.components.ProgressBar;
import jake.kitchenmod.screens.components.TankComponent;
import jake.kitchenmod.tiles.MillTile;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class MillScreen extends KMScreen<MillContainer, MillTile> {

  private static final TextureBinding PROGRESS_OVERLAY =
      new TextureBinding(98, 29, 176, 0, 17, 24);

  public MillScreen(MillContainer container, PlayerInventory inventory, ITextComponent name) {
    super(container, inventory, name, "kitchenmod:textures/gui/container/mill.png");

    manager.addComponent(new TankComponent(29, 9, CutoffDirection.RIGHT,
        tileEntity::getWaterTank));
    manager.addComponent(new ProgressBar(PROGRESS_OVERLAY, CutoffDirection.DOWN,
        tileEntity::getPercentComplete));
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    super.drawGuiContainerForegroundLayer(mouseX, mouseY);

    this.font.drawString(this.title.getFormattedText(), 135f, 6f, 4210752);
    this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F,
        (float) (this.ySize - 96 + 2), 4210752);
  }
}
