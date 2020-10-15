package jake.kitchenmod.recipes;

import jake.kitchenmod.util.JsonUtil;
import javax.annotation.Nullable;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;

public abstract class KMRecipeFactory<T extends IRecipe<?>> implements IRecipeSerializer<T> {

  private ResourceLocation factoryName;
  protected JsonUtil parser;

  public KMRecipeFactory() {
    parser = new JsonUtil(this.getClass().getSimpleName());
  }

  @Override
  public KMRecipeFactory<T> setRegistryName(ResourceLocation name) {
    this.factoryName = name;
    return this;
  }

  @Nullable
  @Override
  public ResourceLocation getRegistryName() {
    return this.factoryName;
  }

  @Override
  public Class getRegistryType() {
    return IRecipeSerializer.class;
  }
}
