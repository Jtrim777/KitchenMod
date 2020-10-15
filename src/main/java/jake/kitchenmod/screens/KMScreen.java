package jake.kitchenmod.screens;

import static jake.kitchenmod.screens.components.TextureBinding.NO_CHANGE;

import com.mojang.blaze3d.platform.GlStateManager;
import jake.kitchenmod.containers.KMContainer;
import jake.kitchenmod.screens.components.ComponentManager;
import jake.kitchenmod.screens.components.TextureBinding;
import java.util.List;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public abstract class KMScreen<T extends KMContainer, U extends TileEntity> extends
    ContainerScreen<T> {

  protected final U tileEntity;
  private final ResourceLocation baseTextureLocation;
  protected final ComponentManager manager;

  public KMScreen(T container, PlayerInventory inventory, ITextComponent name, String base) {
    super(container, inventory, name);

    this.tileEntity = (U) container.getTileEntity();
    this.baseTextureLocation = new ResourceLocation(base);
    this.manager = new ComponentManager(this);
  }

  // region MASTER RENDER METHODS
  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    rebindGUITexture();

    this.drawTexture(0, 0, 0, 0, this.xSize, this.ySize);

    drawBackgroundComponents(mouseX, mouseY);
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    super.drawGuiContainerForegroundLayer(mouseX, mouseY);

    drawForegroundComponents(mouseX,mouseY);
  }

  @Override
  public void render(int mouseX, int mouseY, float partialTicks) {
    this.renderBackground();
    super.render(mouseX, mouseY, partialTicks);
    this.renderHoveredToolTip(mouseX, mouseY);
  }

  // endregion

  // region RENDER DELEGATES
  protected void drawBackgroundComponents(int mx, int my) {
    manager.drawBackground(mx,my);
  }

  protected void drawForegroundComponents(int mx, int my) {
    manager.drawForeground(mx, my);
  }

  @Override
  public void renderTooltip(List<String> lines, int mx, int my) {
    super.renderTooltip(lines, mx - guiLeft, my - guiTop);
  }
  // endregion

  // region BEHAVIOR

  @Override
  public boolean mouseClicked(double mx, double my, int button) {
    manager.handleMouseClick((int)mx, (int)my);

    return super.mouseClicked(mx, my, button);
  }

  @Override
  public boolean mouseReleased(double mx, double my, int event) {
    manager.handleMouseRelease((int)mx, (int)my);

    return super.mouseReleased(mx, my, event);
  }

  // endregion

  // region DRAW HELPERS
  public void rebindGUITexture() {
    bindTexture(baseTextureLocation);
  }

  public void bindTexture(ResourceLocation texture) {
    this.minecraft.getTextureManager().bindTexture(texture);
  }

  public void drawTexture(int destX, int destY, int srcX, int srcY, int srcW, int srcH) {
    this.blit(this.guiLeft + destX, this.guiTop + destY, srcX, srcY, srcW, srcH);
  }

  public void drawTexture(int destX, int destY, int srcX, int srcY, int srcW, int srcH, int atlasW, int atlasH) {
    this.blitCustomSize(this.guiLeft + destX, this.guiTop + destY, blitOffset, srcX, srcY,
        srcW, srcH, atlasW, atlasH);
  }

  public void drawTexture(int destX, int destY, int srcX, int srcY, int srcW, int srcH, int atlasW, int atlasH, int zLevel) {
    this.blitCustomSize(this.guiLeft + destX, this.guiTop + destY, zLevel, srcX, srcY,
        srcW, srcH, atlasW, atlasH);
  }

  public void drawTexture(TextureBinding location) {
    this.blit(this.guiLeft + location.drawX, this.guiTop + location.drawY, location.sourceX,
        location.sourceY,
        location.sourceWidth, location.sourceHeight);
  }

  public void drawTextureWithCutoff(TextureBinding location, int width, int height) {
    if (width == NO_CHANGE && height == NO_CHANGE) {
      this.blit(this.guiLeft + location.drawX, this.guiTop + location.drawY, location.sourceX,
          location.sourceY,
          location.sourceWidth, location.sourceHeight);
    } else if (width != NO_CHANGE && height == NO_CHANGE) {
      this.blit(this.guiLeft + location.drawX, this.guiTop + location.drawY, location.sourceX,
          location.sourceY,
          width, location.sourceHeight);
    } else if (width == NO_CHANGE) {
      this.blit(this.guiLeft + location.drawX, this.guiTop + location.drawY, location.sourceX,
          location.sourceY,
          location.sourceWidth, height);
    } else {
      this.blit(this.guiLeft + location.drawX, this.guiTop + location.drawY, location.sourceX,
          location.sourceY,
          width, height);
    }
  }

  public void drawBottomUpTexture(TextureBinding location, int height) {
    int calcY = location.drawY + location.sourceHeight;
    int calcYI = location.sourceY + location.sourceHeight;

    this.drawTexture(location.drawX, calcY - height, location.sourceX, calcYI - height,
        location.sourceWidth, height);
  }

  @Override
  public void fillGradient(int x1, int y1, int x2, int y2, int startColor, int endColor) {
    super.fillGradient(x1, y1, x2, y2, startColor, endColor);
  }

  public void fillRect(int x1, int y1, int x2, int y2, int color) {
    super.fillGradient(x1, y1, x2, y2, color, color);
  }

  private static void innerBlitA(int x, int maxX, int y, int maxY, int offset, int w, int h,
      float sx, float sy, int atlasW, int atlasH) {
    innerBlit(x, maxX, y, maxY, offset, sx / (float) atlasW, (sx + (float) w) / (float) atlasW, (
        sy + 0.0F) / (float) atlasH, (sy + (float) h) / (float) atlasH);
  }

  protected static void innerBlit(int x, int maxX, int y, int maxY, int offset, float relSx,
      float relMaxSx, float relSy, float relMaxSy) {
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferbuilder = tessellator.getBuffer();
    bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
    bufferbuilder.pos(x, maxY, offset)
        .tex(relSx, relMaxSy).endVertex();
    bufferbuilder.pos(maxX, maxY, offset)
        .tex(relMaxSx, relMaxSy).endVertex();
    bufferbuilder.pos(maxX, y, offset)
        .tex(relMaxSx, relSy).endVertex();
    bufferbuilder.pos(x, y, offset).tex(relSx, relSy)
        .endVertex();
    tessellator.draw();
  }

  public void blit(int x, int y, int sx, int sy, int w, int h) {
    blitCustomSize(x, y, this.blitOffset, (float) sx, (float) sy, w, h, 256, 256);
  }

  public void blitCustomSize(int x, int y, int offset, float sx, float sy, int w, int h, int atlasW,
      int atlasH) {
    innerBlitA(x, x + w, y, y + h, offset, w, h, sx, sy, atlasW, atlasH);
  }

  // endregion

  public enum CutoffDirection {
    UP, DOWN, LEFT, RIGHT;

    public boolean isVertical() {
      return this == UP || this == DOWN;
    }

    public boolean isHorizontal() {
      return !isVertical();
    }
  }
}
