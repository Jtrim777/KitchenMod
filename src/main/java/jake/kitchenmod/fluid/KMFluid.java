//package jake.kitchenmod.fluid;
//
//import jake.kitchenmod.KitchenMod;
//import javax.annotation.Nullable;
//import net.minecraft.util.ResourceLocation;
//import net.minecraftforge.registries.IForgeRegistryEntry;
//
//public class KMFluid implements IForgeRegistryEntry<KMFluid> {
//
//  public static int BOTTLE_VOLUME = 250;
//
//  private ResourceLocation registryName;
//  private int overlayColor;
//
//  public static ResourceLocation STANDARD_LIQUID = new ResourceLocation(KitchenMod.MODID, "block" +
//      "/liquid_overlay");
//
//  public KMFluid(String fluidName, int color) {
//    this.setRegistryName(KitchenMod.MODID, fluidName);
//    this.overlayColor = color;
//  }
//
//
//  @Nullable
//  @Override
//  public ResourceLocation getRegistryName() {
//    return registryName;
//  }
//
//  @Override
//  public KMFluid setRegistryName(ResourceLocation name) {
//    registryName = name;
//    return this;
//  }
//
//  public KMFluid setRegistryName(String namespace, String path) {
//    return this.setRegistryName(new ResourceLocation(namespace, path));
//  }
//
//  @Override
//  public Class getRegistryType() {
//    return KMFluid.class;
//  }
//
//  // Colors are in the format 0xAARRGGBB, in hex
//  public int getColorAsOverlay() {
//    return Math.min(this.overlayColor - 0x78000000, 0);
//  }
//
//  public int getItemColor() {
//    return this.overlayColor - 0xFF;
//  }
//
//
//}
//
