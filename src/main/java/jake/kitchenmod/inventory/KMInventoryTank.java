//package jake.kitchenmod.inventory;
//
//import jake.kitchenmod.KitchenMod;
//import jake.kitchenmod.fluid.KMFluid;
//import jake.kitchenmod.fluid.KMFluidStack;
//import jake.kitchenmod.fluid.ModFluids;
//import net.minecraft.nbt.CompoundNBT;
//import net.minecraft.util.ResourceLocation;
//import net.minecraftforge.registries.IForgeRegistry;
//import net.minecraftforge.registries.RegistryManager;
//
//public class KMInventoryTank {
//
//  private double capacity;
//  private double level;
//
//  private KMFluid content;
//  private boolean canChange;
//
//  public KMInventoryTank(KMFluid content, double capacity, boolean changeable) {
//    this.content = content;
//    this.capacity = capacity;
//
//    this.level = 0;
//    this.canChange = changeable;
//  }
//
//  public KMInventoryTank(KMFluid content, double capacity) {
//    this(content, capacity, false);
//  }
//
//  public KMInventoryTank(double capacity) {
//    this(ModFluids.EMPTY, capacity, true);
//  }
//
//  public KMInventoryTank(CompoundNBT tag) {
//    read(tag);
//  }
//
//  public void setLevel(int nl) {
//    if (this.content != ModFluids.EMPTY) {
//      this.level = nl;
//    }
//  }
//
//  public boolean add(KMFluidStack cont) {
//    if (cont.getFluid() != this.content && this.content != ModFluids.EMPTY) {
//      return false;
//    } else if (cont.getQuantity() + this.level > this.capacity) {
//      return false;
//    }
//
//    this.content = cont.getFluid();
//
//    this.level += cont.getQuantity();
//
//    onLevelChange();
//
//    return true;
//  }
//
//  public boolean addRound(KMFluidStack cont) {
//    if (cont.getFluid() != this.content && this.content != ModFluids.EMPTY) {
//      return false;
//    }
//
//    this.content = cont.getFluid();
//
//    this.level = cont.getQuantity() + level <= capacity ? cont.getQuantity() + level : capacity;
//
//    onLevelChange();
//
//    return true;
//  }
//
//  public KMFluidStack take(double amt) {
//    if (level >= amt) {
//      this.level -= amt;
//      onLevelChange();
//      return new KMFluidStack(this.content, (int) amt);
//    } else {
//      return KMFluidStack.EMPTY;
//    }
//  }
//
//  public KMFluidStack takeRound(double amt) {
//    double tamt = Math.min(amt, level);
//    this.level -= tamt;
//    onLevelChange();
//
//    if (tamt > 0) {
//      return new KMFluidStack(this.content, (int) tamt);
//    } else {
//      return KMFluidStack.EMPTY;
//    }
//  }
//
//  private void onLevelChange() {
//    if (level == 0 && canChange) {
//      this.content = ModFluids.EMPTY;
//    }
//  }
//
//  public void read(CompoundNBT tag) {
//    this.level = tag.getDouble("level");
//    this.capacity = tag.getDouble("capacity");
//
//    IForgeRegistry fluidRegistry = RegistryManager.ACTIVE.getRegistry(
//        new ResourceLocation(KitchenMod.MODID, "inventory_fluids"));
//
//    this.content = (KMFluid) fluidRegistry.getValue(new ResourceLocation(tag.getString("content")));
//    this.canChange = tag.getBoolean("canChange");
//  }
//
//  public CompoundNBT write() {
//    CompoundNBT tag = new CompoundNBT();
//
//    tag.putDouble("level", this.level);
//    tag.putDouble("capacity", this.capacity);
//    tag.putString("content", this.content.getRegistryName().toString());
//    tag.putBoolean("canChange", this.canChange);
//
//    return tag;
//  }
//
//  public boolean hasContent() {
//    return level > 0;
//  }
//
//  public boolean isEmpty() {
//    return !this.hasContent();
//  }
//
//  public boolean hasEnough(double thresh) {
//    return this.level >= thresh;
//  }
//
//  public double getCapacity() {
//    return capacity;
//  }
//
//  public double getLevel() {
//    return level;
//  }
//
//  public KMFluid getContent() {
//    return content;
//  }
//
//  public int getOverlayHeight(int maxHeight) {
//    return (int) ((this.level / this.capacity) * (double) maxHeight);
//  }
//}
