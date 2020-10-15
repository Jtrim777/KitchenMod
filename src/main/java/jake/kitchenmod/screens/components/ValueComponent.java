package jake.kitchenmod.screens.components;

import jake.kitchenmod.screens.KMScreen.CutoffDirection;
import java.util.function.Supplier;

public class ValueComponent extends ProgressBar {

  public ValueComponent(TextureBinding componentLocation,
      CutoffDirection direction,
      Supplier<Float> levelGetter) {
    super(componentLocation, direction, levelGetter);
  }
}
