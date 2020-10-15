package jake.kitchenmod.fluid;

import net.minecraft.item.Food;
import net.minecraft.potion.Effect;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class KMConsumableFluid extends ForgeFlowingFluid.Source {

  private int hunger;
  private float saturation;
  private Effect[] effects;

  public KMConsumableFluid(Properties props, int hunger, float saturation) {
    super(props);
    this.hunger = hunger;
    this.saturation = saturation;
    this.effects = null;
  }

  public KMConsumableFluid(Properties props, int hunger, int saturation, Effect... fx) {
    super(props);
    this.hunger = hunger;
    this.saturation = saturation;
    this.effects = fx;
  }

  public Food getFood() {
    return (new Food.Builder())
        .hunger(hunger)
        .saturation(saturation)
        .build();
  }

  public Effect[] getEffects() {
    return effects;
  }
}
