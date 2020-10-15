package jake.kitchenmod.screens.components;

import static jake.kitchenmod.screens.components.TextureBinding.NO_CHANGE;

import jake.kitchenmod.containers.KMContainer;
import jake.kitchenmod.screens.KMScreen;
import jake.kitchenmod.screens.KMScreen.CutoffDirection;
import java.util.function.Supplier;
import net.minecraft.tileentity.TileEntity;

public class ProgressBar extends ScreenComponent<Float> {

  private TextureBinding componentLocation;
  private CutoffDirection direction;

  public ProgressBar(TextureBinding componentLocation,
      CutoffDirection direction,
      Supplier<Float> completionGetter) {
    super(componentLocation.drawX, componentLocation.drawY, componentLocation.sourceWidth,
        componentLocation.sourceHeight, completionGetter);

    this.componentLocation = componentLocation;
    this.direction = direction;
  }

  @Override
  public <C extends KMContainer, T extends TileEntity> void drawOnBackground(
      KMScreen<C, T> screen) {
    if (direction.isVertical()) {
      int height = calculateSize(componentLocation.sourceHeight);

      if (direction == CutoffDirection.UP) {
        screen.drawBottomUpTexture(componentLocation, height);
      } else {
        screen.drawTextureWithCutoff(componentLocation, NO_CHANGE, height);
      }
    } else {
      int width = calculateSize(componentLocation.sourceWidth);

      screen.drawTextureWithCutoff(componentLocation, width, NO_CHANGE);
    }
  }

  @Override
  public <C extends KMContainer, T extends TileEntity> void drawOnForeground(
      KMScreen<C, T> screen) {
  }

  private int calculateSize(int max) {
    return (int)Math.ceil((float)max * dataSupplier.get());
  }
}
