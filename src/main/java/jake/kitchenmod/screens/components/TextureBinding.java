package jake.kitchenmod.screens.components;

public class TextureBinding {

  public int drawX;
  public int drawY;

  public int sourceX;
  public int sourceY;

  public int sourceWidth;
  public int sourceHeight;

  public static int NO_CHANGE = -1;

  public TextureBinding(int destX, int destY, int srcX, int srcY, int srcW, int srcH) {
    this.drawX = destX;
    this.drawY = destY;
    this.sourceX = srcX;
    this.sourceY = srcY;
    this.sourceWidth = srcW;
    this.sourceHeight = srcH;
  }
}
