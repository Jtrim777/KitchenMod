package jake.kitchenmod.screens.components;

import com.mojang.blaze3d.platform.GlStateManager;
import jake.kitchenmod.containers.KMContainer;
import jake.kitchenmod.screens.KMScreen;
import jake.kitchenmod.screens.KMScreen.CutoffDirection;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class TankComponent extends ScreenComponent<FluidTank> implements ITooltipProvider {

  private static final ResourceLocation TANK_OVERLAY_TEXTURE =
      new ResourceLocation("kitchenmod:textures/gui/components/tank_overlay.png");
  private static final TextureBinding TANK_OVERLAY_POS =
      new TextureBinding(0, 0, 0, 0, 18, 51);

  private static final ResourceLocation TANK_OVERLAY_TEXTURE_HZ =
      new ResourceLocation("kitchenmod:textures/gui/components/tank_overlay_hz.png");
  private static final TextureBinding TANK_OVERLAY_POS_HZ =
      new TextureBinding(0, 0, 0, 0, 51, 18);

  private final KMScreen.CutoffDirection direction;

  public TankComponent(int drawX, int drawY, CutoffDirection direction,
      Supplier<FluidTank> tankGetter) {
    super(drawX, drawY, direction.isVertical() ? 18 : 51, direction.isVertical() ? 51 : 18,
        tankGetter);
    this.direction = direction;
  }

  @Override
  public <C extends KMContainer, T extends TileEntity> void drawOnBackground(
      KMScreen<C, T> screen) {
    GlStateManager.disableBlend();

    FluidTank tank = dataSupplier.get();
    Fluid contents = tank.getFluid().getFluid();
    float size = (float)tank.getFluidAmount() / (float)tank.getCapacity();

    if (contents == null || contents == Fluids.EMPTY || size < 0.001f) {
      return;
    }

    IScreenDrawable.setGLColorFromInt(contents.getAttributes().getColor());

    ResourceLocation spriteLoc = contents.getAttributes().getStillTexture();
    TextureAtlasSprite fluidSprite = screen.getMinecraft().getTextureMap().getSprite(spriteLoc);

    screen.getMinecraft().getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);

    switch (direction) {
      case UP:
        drawTilesUp(fluidSprite, screen);
        break;
      case DOWN:
        drawTilesDown(fluidSprite, screen);
        break;
      case LEFT:
        drawTilesLeft(fluidSprite, screen);
        break;
      case RIGHT:
        drawTilesRight(fluidSprite, screen);
        break;
    }

    ResourceLocation relevantLoc;
    TextureBinding relevantComp;

    if (direction == CutoffDirection.UP || direction == CutoffDirection.DOWN) {
      relevantLoc = TANK_OVERLAY_TEXTURE;
      relevantComp = TANK_OVERLAY_POS;
    } else {
      relevantLoc = TANK_OVERLAY_TEXTURE_HZ;
      relevantComp = TANK_OVERLAY_POS_HZ;
    }

    IScreenDrawable.resetGLColor();

    screen.bindTexture(relevantLoc);
    screen.drawTexture(drawX, drawY, 0, 0,
        relevantComp.sourceWidth, relevantComp.sourceHeight,
        relevantComp.sourceWidth, relevantComp.sourceHeight, 101);

    screen.rebindGUITexture();
  }

  @Override
  public <C extends KMContainer, T extends TileEntity> void drawOnForeground(
      KMScreen<C, T> screen) {
  }

  @Override
  public boolean shouldShowTooltip(int mouseX, int mouseY) {
    return isInside(mouseX, mouseY);
  }

  @Override
  public List<ITextComponent> getTooltipLines() {
    FluidTank tank = dataSupplier.get();

    ITextComponent line1;
    ITextComponent line2;

    if (tank.isEmpty()) {
      line1 = new StringTextComponent("Empty Tank");
      line2 = new StringTextComponent("Capacity: "+tank.getCapacity()+" mB")
          .applyTextStyle(TextFormatting.GRAY);
    } else {
      line1 = tank.getFluid().getDisplayName();
      line2 = new StringTextComponent(String.format("%d/%d mB", tank.getFluidAmount(),
          tank.getCapacity()));
    }

    return Arrays.asList(line1, line2);
  }

  private int calculateSize(int max) {
    FluidTank tank = dataSupplier.get();

    return (int) ((float) max * ((float)tank.getFluidAmount()/(float)tank.getCapacity()));
  }

  private void drawTiles(TextureAtlasSprite tile, ScreenPos start, int dim,
      BiFunction<Integer, Integer, ScreenPos> delta, LastDrawer fin, KMScreen<?, ?> screen) {
    int cx = start.x;
    int cy = start.y;

    FluidTank tank = dataSupplier.get();
    float size = (float)tank.getFluidAmount() / (float)tank.getCapacity();

    int pixelSize = (int) (size * (float) dim);
    int count = pixelSize / 16;
    int last = pixelSize - (count * 16);

    for (int i = 0; i < count; i++) {
      drawTile(tile, cx, cy, screen);

      ScreenPos npos = delta.apply(cx, cy);
      cx = npos.x;
      cy = npos.y;
    }

    if (last > 0) {
      fin.draw(cx, cy, last, screen);
    }
  }

  private void drawTilesUp(TextureAtlasSprite tile, KMScreen<?, ?> screen) {
    int sx = drawX + 1;
    int sy = (drawY + TANK_OVERLAY_POS.sourceHeight) - 17;

    BiFunction<Integer, Integer, ScreenPos> delta = (x, y) -> new ScreenPos(x, y - 16);
    LastDrawer fin = (x, y, sz, scrn) -> drawPartTile(tile, x, y + (16 - sz), 16, sz, scrn);

    drawTiles(tile, new ScreenPos(sx, sy), TANK_OVERLAY_POS.sourceHeight, delta, fin, screen);
  }

  private void drawTilesDown(TextureAtlasSprite tile, KMScreen<?, ?> screen) {
    int sx = drawX + 1;
    int sy = drawY - 1;

    BiFunction<Integer, Integer, ScreenPos> delta = (x, y) -> new ScreenPos(x, y + 16);
    LastDrawer fin = (x, y, sz, scrn) -> drawPartTile(tile, x, y, 16, sz, scrn);

    drawTiles(tile, new ScreenPos(sx, sy), TANK_OVERLAY_POS.sourceHeight, delta, fin, screen);
  }

  private void drawTilesLeft(TextureAtlasSprite tile, KMScreen<?, ?> screen) {
    int sx = drawX + TANK_OVERLAY_POS_HZ.sourceWidth - 15;
    int sy = drawY + 1;

    BiFunction<Integer, Integer, ScreenPos> delta = (x, y) -> new ScreenPos(x - 16, y);
    LastDrawer fin = (x, y, sz, scrn) -> drawPartTile(tile, x + (16 - sz), y, sz, 16, scrn);

    drawTiles(tile, new ScreenPos(sx, sy), TANK_OVERLAY_POS_HZ.sourceWidth, delta, fin, screen);
  }

  private void drawTilesRight(TextureAtlasSprite tile, KMScreen<?, ?> screen) {
    int sx = drawX + 1;
    int sy = drawY + 1;

    BiFunction<Integer, Integer, ScreenPos> delta = (x, y) -> new ScreenPos(x + 16, y);
    LastDrawer fin = (x, y, sz, scrn) -> drawPartTile(tile, x, y, sz, 16, scrn);

    drawTiles(tile, new ScreenPos(sx, sy), TANK_OVERLAY_POS_HZ.sourceWidth, delta, fin, screen);
  }

  private void drawTile(TextureAtlasSprite tile, int x, int y, KMScreen<?, ?> screen) {
    double uMin = tile.getMinU();
    double uMax = tile.getMaxU();
    double vMin = tile.getMinV();
    double vMax = tile.getMaxV();

    drawTileCommon(x + screen.getGuiLeft(), y + screen.getGuiTop(), 16, 16, uMin, uMax, vMin, vMax);
  }

  private void drawPartTile(TextureAtlasSprite tile, int x, int y, int w, int h,
      KMScreen<?, ?> screen) {
    double maskVert = 16 - h;
    double maskHz = 16 - w;

    double uMin = tile.getMinU();
    double uMax = tile.getMaxU();
    double vMin = tile.getMinV();
    double vMax = tile.getMaxV();

    double adjVert = maskVert / 16.0 * (vMax - vMin);
    double adjHz = maskHz / 16.0 * (vMax - vMin);

    switch (direction) {
      case UP:
        vMin += adjVert;
        break;
      case DOWN:
        vMax -= adjVert;
        break;
      case LEFT:
        uMin += adjHz;
        break;
      case RIGHT:
        uMax -= adjHz;
        break;
    }

    drawTileCommon(x + screen.getGuiLeft(), y + screen.getGuiTop(), w, h, uMin, uMax, vMin, vMax);
  }

  private void drawTileCommon(int x, int y, int w, int h, double uMin, double uMax, double vMin,
      double vMax) {
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder buffer = tessellator.getBuffer();
    buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
    buffer.pos(x, y + h, 100).tex(uMin, vMax).endVertex();
    buffer.pos(x + w, y + h, 100).tex(uMax, vMax).endVertex();
    buffer.pos(x + w, y, 100).tex(uMax, vMin).endVertex();
    buffer.pos(x, y, 100).tex(uMin, vMin).endVertex();
    tessellator.draw();
  }

  private interface LastDrawer {

    void draw(int x, int y, int lastSize, KMScreen<?, ?> screen);
  }
}
