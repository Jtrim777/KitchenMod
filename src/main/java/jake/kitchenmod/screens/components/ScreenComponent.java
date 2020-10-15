package jake.kitchenmod.screens.components;

import java.util.function.Supplier;

public abstract class ScreenComponent<D> implements IScreenDrawable {
  protected int drawX;
  protected int drawY;
  protected int componentWidth;
  protected int componentHeight;
  protected Supplier<D> dataSupplier;

  public ScreenComponent(int drawX, int drawY, int componentWidth, int componentHeight,
      Supplier<D> dataSupplier) {
    this.drawX = drawX;
    this.drawY = drawY;
    this.componentWidth = componentWidth;
    this.componentHeight = componentHeight;
    this.dataSupplier = dataSupplier;
  }

  public boolean isInside(int x, int y) {
    return x >= drawX && x < drawX + componentWidth
        && y >= drawY && y < drawY + componentHeight;
  }
}
