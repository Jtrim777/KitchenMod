//package jake.kitchenmod.fluid;
//
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import jake.kitchenmod.setup.RegistrySetup;
//import jake.kitchenmod.util.ModUtil;
//import net.minecraft.network.PacketBuffer;
//import net.minecraft.util.ResourceLocation;
//import net.minecraftforge.fml.common.registry.GameRegistry;
//
//public class KMFluidStack {
//
//  public static final KMFluidStack EMPTY = new KMFluidStack(null, 0);
//
//  private KMFluid fluid;
//  private int quantity;
//
//  public KMFluidStack(KMFluid fluid, int quantity) {
//    this.fluid = fluid;
//    this.quantity = quantity;
//  }
//
//  public KMFluid getFluid() {
//    return fluid;
//  }
//
//  public int getQuantity() {
//    return quantity;
//  }
//
//  public KMFluidStack copy() {
//    return new KMFluidStack(fluid, quantity);
//  }
//
//  public static KMFluidStack fromJson(JsonElement json) {
//    JsonObject tag = json.getAsJsonObject();
//
//    ResourceLocation fluidType = new ResourceLocation(tag.get("fluid").getAsString());
//    KMFluid fluid = RegistrySetup.FLUIDS.getValue(fluidType);
//
//    int amt = tag.get("amount").getAsInt();
//
//    return new KMFluidStack(fluid, amt);
//  }
//
//  public void write(PacketBuffer buffer) {
//    buffer.writeString(fluid.getRegistryName().toString());
//    buffer.writeInt(quantity);
//  }
//
//  public static KMFluidStack read(PacketBuffer buffer) {
//    KMFluid fluid = ModUtil.findFluid(buffer.readString());
//    int amt = buffer.readInt();
//
//    return new KMFluidStack(fluid, amt);
//  }
//}
