package jake.kitchenmod.entities.models;// Made with Blockbench 3.6.3
// Exported for Minecraft version 1.14
// Paste this class into your mod and generate all required imports


import jake.kitchenmod.entities.LionEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.ModelBox;

public class LionModel<T extends LionEntity> extends EntityModel<T> {
  private final RendererModel body;
  private final RendererModel head;
  private final RendererModel bb_main;

  public LionModel() {
    textureWidth = 128;
    textureHeight = 64;

    body = new RendererModel(this);
    body.setRotationPoint(6.0F, 15.0F, -9.0F);
    body.cubeList.add(new ModelBox(body, 0, 0, -11.0F, -10.0F, 0.0F, 10, 9, 19, 0.0F, false));
    body.cubeList.add(new ModelBox(body, 65, 0, -12.0F, -11.0F, -1.0F, 12, 11, 3, 0.0F, false));

    head = new RendererModel(this);
    head.setRotationPoint(0.0F, 0.0F, 0.0F);
    body.addChild(head);
    head.cubeList.add(new ModelBox(head, 0, 28, -8.0F, -7.0F, -9.0F, 4, 3, 2, 0.0F, false));
    head.cubeList.add(new ModelBox(head, 12, 44, -9.0F, -10.0F, -7.0F, 6, 6, 2, 0.0F, false));
    head.cubeList.add(new ModelBox(head, 12, 28, -10.0F, -11.0F, -5.0F, 8, 9, 5, 0.0F, false));

    bb_main = new RendererModel(this);
    bb_main.setRotationPoint(0.0F, 24.0F, 0.0F);
    bb_main.cubeList.add(new ModelBox(bb_main, 56, 29, 1.0F, -18.0F, -7.0F, 4, 18, 4, 0.0F, false));
    bb_main.cubeList.add(new ModelBox(bb_main, 39, 29, -5.0F, -18.0F, -7.0F, 4, 18, 4, 0.0F, false));
    bb_main.cubeList.add(new ModelBox(bb_main, 90, 34, -5.0F, -12.0F, 6.0F, 4, 12, 4, 0.0F, false));
    bb_main.cubeList.add(new ModelBox(bb_main, 74, 34, 1.0F, -12.0F, 6.0F, 4, 12, 4, 0.0F, false));
  }

  @Override
  public void render(T entity, float f, float f1, float f2, float f3, float f4, float f5) {
    body.render(f5);
    bb_main.render(f5);
  }

  public void setRotationAngle(RendererModel modelRenderer, float x, float y, float z) {
    modelRenderer.rotateAngleX = x;
    modelRenderer.rotateAngleY = y;
    modelRenderer.rotateAngleZ = z;
  }
}