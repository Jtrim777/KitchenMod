package jake.kitchenmod.screens.components;

import static jake.kitchenmod.screens.components.TextureBinding.NO_CHANGE;

import jake.kitchenmod.containers.KMContainer;
import jake.kitchenmod.screens.KMScreen;
import jake.kitchenmod.screens.KMScreen.CutoffDirection;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import net.minecraft.tileentity.TileEntity;

public class MultiProgressBar extends ScreenComponent<Float> {

  private final List<ProgressPart> parts;

  public MultiProgressBar(Supplier<Float> completionGetter) {
    super(0,0,0,0,completionGetter);

    this.parts = new ArrayList<>();
  }

  public MultiProgressBar addPart(TextureBinding loc, CutoffDirection dir, float start, float end) {
    this.parts.add(new ProgressPart(start, end, loc, dir, this::getAdjustedProgress));

    return this;
  }

  @Override
  public <C extends KMContainer, T extends TileEntity> void drawOnBackground(
      KMScreen<C, T> screen) {
    for (ProgressPart part : parts) {
      part.drawOn(screen);
    }
  }

  @Override
  public <C extends KMContainer, T extends TileEntity> void drawOnForeground(
      KMScreen<C, T> screen) {
  }

  float getAdjustedProgress(float min, float max) {
    float base = dataSupplier.get();

    if (base <= min) {
      return 0f;
    } else if (base >= max){
      return 1f;
    } else {
      return (base - min) / (max - min);
    }
  }

  private static class ProgressPart {
    private float min;
    private float max;
    private TextureBinding loc;
    private CutoffDirection dir;
    private BiFunction<Float, Float, Float> getter;

    public ProgressPart(float min, float max, TextureBinding loc,
        CutoffDirection dir,
        BiFunction<Float, Float, Float> getter) {
      this.min = min;
      this.max = max;
      this.loc = loc;
      this.dir = dir;
      this.getter = getter;
    }

    void drawOn(KMScreen<?, ?> screen) {
      int dim = (int)Math.ceil(getter.apply(min, max) *
          (float)(dir.isVertical() ? loc.sourceHeight : loc.sourceWidth));

      if (dir == CutoffDirection.UP || dir == CutoffDirection.DOWN) {
        int height = dim;

        if (dir == CutoffDirection.UP) {
          screen.drawBottomUpTexture(loc, height);
        } else {
          screen.drawTextureWithCutoff(loc, NO_CHANGE, height);
        }
      } else {
        int width = dim;

        screen.drawTextureWithCutoff(loc, width, NO_CHANGE);
      }
    }
  }
}
