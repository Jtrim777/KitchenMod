package jake.kitchenmod.capabilities;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class KMFluidHandler implements IFluidHandler {

  private List<FluidTank> tanks;
  private List<Permission> permissions;

  protected KMFluidHandler(List<FluidTank> tanks, List<Permission> permissions) {
    this.tanks = tanks;
    this.permissions = permissions;
  }

  public KMFluidHandler() {
    this.tanks = new ArrayList<>();
    this.permissions = new ArrayList<>();
  }

  @Override
  public int getTanks() {
    return tanks.size();
  }

  public FluidTank getTank(int indx) {
    return tanks.get(indx);
  }

  @Nonnull
  @Override
  public FluidStack getFluidInTank(int tank) {
    return tanks.get(tank).getFluid();
  }

  @Override
  public int getTankCapacity(int tank) {
    return tanks.get(tank).getCapacity();
  }

  @Override
  public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
    return tanks.get(tank).isFluidValid(stack);
  }

  @Override
  public int fill(FluidStack resource, FluidAction action) {
    int filled = 0;
    int rem = resource.getAmount();

    for (int i = 0; i < getTanks(); i++) {
      if (permissions.get(i).canFill()) {
        if (tanks.get(i).isFluidValid(resource)) {
          int done = tanks.get(i).fill(new FluidStack(resource.getFluid(), rem), action);
          rem -= done;
          filled += done;

          if (rem == 0) break;
        }
      }
    }

    return filled;
  }

  public int fillTank(int tank, FluidStack resource, FluidAction action) {
    return tanks.get(tank).fill(resource, action);
  }

  public FluidStack drainTank(int tank, FluidStack resource, FluidAction action) {
    return tanks.get(tank).drain(resource, action);
  }

  public FluidStack drainTank(int tank, int maxAmt, FluidAction action) {
    return tanks.get(tank).drain(maxAmt, action);
  }

  @Nonnull
  @Override
  public FluidStack drain(FluidStack resource, FluidAction action) {
    FluidStack drained = new FluidStack(resource.getFluid(), 0);
    int rem = resource.getAmount();

    for (int i = 0; i < getTanks(); i++) {
      if (permissions.get(i).canDrain()) {
        if (tanks.get(i).isFluidValid(resource)) {
          FluidStack done = tanks.get(i).drain(new FluidStack(resource.getFluid(), rem), action);
          rem -= done.getAmount();
          drained.grow(done.getAmount());

          if (rem == 0) break;
        }
      }
    }

    return drained;
  }

  @Nonnull
  @Override
  public FluidStack drain(int maxDrain, FluidAction action) {
    FluidStack drained = FluidStack.EMPTY;
    int rem = maxDrain;

    for (int i = 0; i < getTanks(); i++) {
      if (permissions.get(i).canDrain()) {
        if (drained.isEmpty()) {
          drained = tanks.get(i).drain(rem, action);

          rem -= drained.getAmount();
        } else if (tanks.get(i).getFluid().getFluid() == drained.getFluid()) {
          FluidStack thisDrain = tanks.get(i).drain(rem, action);

          drained.grow(thisDrain.getAmount());
          rem -= thisDrain.getAmount();
        }

        if (rem == 0) break;
      }
    }

    return drained;
  }

  public void deserializeNBT(ListNBT tag) {
    for (int i = 0; i < tag.size(); i++) {
      CompoundNBT stackTag = tag.getCompound(i);

      FluidStack fluid = FluidStack.loadFluidStackFromNBT(stackTag);
      tanks.get(i).setFluid(fluid);
    }
  }

  public ListNBT writeToNBT() {
    ListNBT tag = new ListNBT();

    for (FluidTank tank : tanks) {
      tag.add(tank.writeToNBT(new CompoundNBT()));
    }

    return tag;
  }

  public void addTank(FluidTank tank) {
    this.tanks.add(tank);
    this.permissions.add(Permission.OPEN);
  }

  public void addTank(int capacity, Predicate<FluidStack> validator) {
    this.tanks.add(new FluidTank(capacity, validator));
    this.permissions.add(Permission.OPEN);
  }

  public void addUniversalTank(int capacity) {
    this.tanks.add(new FluidTank(capacity));
    this.permissions.add(Permission.OPEN);
  }

  public void addOneFluidTank(int capacity, Fluid fluid) {
    this.tanks.add(new FluidTank(capacity, (fs) -> fs.getFluid() == fluid));
    this.permissions.add(Permission.OPEN);
  }

  public void setPermission(int tank, Permission value) {
    this.permissions.set(tank, value);
  }

  @Override
  public String toString() {
    StringBuilder tankStr = new StringBuilder();

    for (int i = 0; i < tanks.size(); i++) {
      FluidStack stack = tanks.get(i).getFluid();

      tankStr.append("[fluid=")
          .append(stack.getFluid().toString())
          .append(", amount=")
          .append(stack.getAmount())
          .append(", permission=")
          .append(permissions.get(i).name())
          .append("], ");
    }

    return "KMFluidHandler{" + tankStr + '}';
  }

  public static String formatTank(FluidTank tank) {
    return String.format("[fluid=%s, amount=%d]",
        tank.getFluid().getFluid().getRegistryName().toString(), tank.getFluidAmount());
  }

  public enum Permission {
    OPEN,
    DRAIN_ONLY,
    FILL_ONLY,
    LOCKED;

    boolean canFill() {
      return this == OPEN || this == FILL_ONLY;
    }

    boolean canDrain() {
      return this == OPEN || this == DRAIN_ONLY;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private List<FluidTank> tanks;
    private List<Permission> permissions;

    private Builder() {
      tanks = new ArrayList<>();
      permissions = new ArrayList<>();
    }

    public Builder addTank(FluidTank tank) {
      this.tanks.add(tank);
      this.permissions.add(Permission.OPEN);

      return this;
    }

    public Builder addTank(int capacity, Predicate<FluidStack> validator) {
      this.tanks.add(new FluidTank(capacity, validator));
      this.permissions.add(Permission.OPEN);

      return this;
    }

    public Builder addUniversalTank(int capacity) {
      this.tanks.add(new FluidTank(capacity));
      this.permissions.add(Permission.OPEN);

      return this;
    }

    public Builder addOneFluidTank(int capacity, Fluid fluid) {
      this.tanks.add(new FluidTank(capacity, (fs) -> fs.getFluid() == fluid));
      this.permissions.add(Permission.OPEN);

      return this;
    }

    public Builder setPermission(int tank, Permission value) {
      this.permissions.set(tank, value);

      return this;
    }

    public KMFluidHandler build() {
      return new KMFluidHandler(tanks, permissions);
    }
  }
}
