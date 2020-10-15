package jake.kitchenmod.screens.components;

import com.mojang.blaze3d.platform.GlStateManager;
import jake.kitchenmod.KitchenMod;
import jake.kitchenmod.containers.KMContainer;
import jake.kitchenmod.screens.KMScreen;
import net.minecraft.tileentity.TileEntity;

public interface IScreenDrawable {
  <C extends KMContainer, T extends TileEntity> void drawOnBackground(KMScreen<C, T> screen);
  <C extends KMContainer, T extends TileEntity> void drawOnForeground(KMScreen<C, T> screen);

  static void setGLColorFromInt(int color) {
    float red = (color >> 16 & 0xFF) / 255.0F;
    float green = (color >> 8 & 0xFF) / 255.0F;
    float blue = (color & 0xFF) / 255.0F;

//    KitchenMod.LOGGER.log(String.format("Setting tint color to RGBA(%f, %f, %f, 1.0) [from %d]",
//        red, green, blue, color));

    GlStateManager.color4f(red, green, blue, 1.0F);
  }

  static void resetGLColor() {
    GlStateManager.color4f(1, 1, 1, 1.0F);
  }
}
