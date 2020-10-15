package jake.kitchenmod.screens;

import jake.kitchenmod.containers.OvenContainer;
import jake.kitchenmod.screens.components.TextureBinding;
import jake.kitchenmod.screens.components.ValueComponent;
import jake.kitchenmod.tiles.OvenTile;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class OvenScreen extends KMScreen<OvenContainer, OvenTile> {

  private static final TextureBinding FUEL_OVERLAY =
      new TextureBinding(63, 47, 176, 0, 14, 14);
  private static final TextureBinding FUEL_OVERLAY_2 =
      new TextureBinding(99, 47, 176, 0, 14, 14);
  private static final TextureBinding TEMP_OVERLAY =
      new TextureBinding(18, 9, 176, 16, 14, 52);
  private static final TextureBinding TEMP_OVERLAY_2 =
      new TextureBinding(144, 9, 176, 16, 14, 52);

  public OvenScreen(OvenContainer container, PlayerInventory inventory, ITextComponent name) {
    super(container, inventory, name, "kitchenmod:textures/gui/container/oven.png");

    manager.addComponent(new ValueComponent(FUEL_OVERLAY, CutoffDirection.UP,
        tileEntity::getFuelPercent1));
    manager.addComponent(new ValueComponent(FUEL_OVERLAY_2, CutoffDirection.UP,
        tileEntity::getFuelPercent2));
    manager.addComponent(new ValueComponent(TEMP_OVERLAY, CutoffDirection.UP,
        tileEntity::getTempPercent));
    manager.addComponent(new ValueComponent(TEMP_OVERLAY_2, CutoffDirection.UP,
        tileEntity::getTempPercent));
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    super.drawGuiContainerForegroundLayer(mouseX, mouseY);

    this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F,
        (float) (this.ySize - 96 + 2), 4210752);
  }
}
