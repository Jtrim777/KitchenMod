package jake.kitchenmod.entities.renderers;

import jake.kitchenmod.entities.LionEntity;
import jake.kitchenmod.entities.models.LionModel;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LionRenderer extends MobRenderer<LionEntity, LionModel<LionEntity>> {
  private static final ResourceLocation TEXTURE_LOC = new ResourceLocation("textures/entity/lion.png");

  public LionRenderer(EntityRendererManager p_i50969_1_) {
    super(p_i50969_1_, new LionModel<>(), 0.4F);
  }

  protected void applyRotations(LionEntity entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
    super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
//    if (entityLiving.func_213480_dY() || entityLiving.func_213472_dX()) {
//      GlStateManager.rotatef(-MathHelper.lerp(partialTicks, entityLiving.prevRotationPitch, entityLiving.rotationPitch), 1.0F, 0.0F, 0.0F);
//    }

  }

  @Nullable
  protected ResourceLocation getEntityTexture(LionEntity entity) {
//    if (entity.getVariantType() == FoxEntity.Type.RED) {
//      return entity.isSleeping() ? field_217768_j : field_217767_a;
//    } else {
//      return entity.isSleeping() ? field_217770_l : field_217769_k;
//    }

    return TEXTURE_LOC;
  }
}
