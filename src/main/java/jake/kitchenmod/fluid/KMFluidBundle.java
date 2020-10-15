package jake.kitchenmod.fluid;

import jake.kitchenmod.KitchenMod;
import java.util.function.Consumer;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fluids.ForgeFlowingFluid.Flowing;
import net.minecraftforge.fluids.ForgeFlowingFluid.Properties;
import net.minecraftforge.fluids.ForgeFlowingFluid.Source;
import net.minecraftforge.registries.IForgeRegistry;

public class KMFluidBundle {
  private ForgeFlowingFluid.Source still;
  private ForgeFlowingFluid.Flowing flowing;

  private String name;

  private Consumer<Fluid> fieldSetter = (f) -> {};

  public KMFluidBundle(String name) {
    still = null;
    flowing = null;

    this.name = name;
  }

  public Flowing getFlowing() {
    return flowing;
  }

  public Source getStill() {
    return still;
  }

  public String getName() {
    return name;
  }

  public KMFluidBundle setField(Consumer<Fluid> setter) {
    this.fieldSetter = setter;

    return this;
  }

  public KMFluidBundle initialize(String textureName, Consumer<FluidAttributes.Builder> attrs) {
    FluidAttributes.Builder ab = FluidAttributes.builder(
        new ResourceLocation(textureName + "_still"),
        new ResourceLocation(textureName + "_flow")
    );

    attrs.accept(ab);

    ForgeFlowingFluid.Properties props =
        new Properties(this::getStill, this::getFlowing, ab);

    this.flowing = (Flowing)(new Flowing(props))
        .setRegistryName(new ResourceLocation(KitchenMod.MODID, "flowing_" + name));

    this.still = (Source)(new Source(props))
        .setRegistryName(new ResourceLocation(KitchenMod.MODID, name));

    this.fieldSetter.accept(still);

    return this;
  }

  public KMFluidBundle initializeDrink(String textureName, Consumer<FluidAttributes.Builder> attrs,
      int hunger, float sat) {
    FluidAttributes.Builder ab = FluidAttributes.builder(
        new ResourceLocation(textureName + "_still"),
        new ResourceLocation(textureName + "_flow")
    );

    ForgeFlowingFluid.Properties props =
        new Properties(this::getStill, this::getFlowing, ab);

    this.flowing = (Flowing)(new Flowing(props))
        .setRegistryName(new ResourceLocation(KitchenMod.MODID, "flowing_" + name));

    this.still = (KMConsumableFluid)(new KMConsumableFluid(props, hunger, sat))
        .setRegistryName(new ResourceLocation(KitchenMod.MODID, name));

    this.fieldSetter.accept(still);

    return this;
  }

  public Fluid[] getParts() {
    return new Fluid[]{still, flowing};
  }

  public void register(IForgeRegistry<Fluid> registry) {
    registry.registerAll(getParts());
  }
}
