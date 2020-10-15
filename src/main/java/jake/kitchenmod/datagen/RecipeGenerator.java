package jake.kitchenmod.datagen;

import jake.kitchenmod.metallurgy.data.KMMetal;
import jake.kitchenmod.metallurgy.data.MetalUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;

public class RecipeGenerator extends RecipeProvider {

  public RecipeGenerator(DataGenerator generatorIn) {
    super(generatorIn);
  }

  @Override
  protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
    super.registerRecipes(consumer);

    makeRecipes().forEach(consumer);
  }

  private List<IFinishedRecipe> makeRecipes() {
    List<IFinishedRecipe> out = new ArrayList<>();

    MetalUtil.REGISTRY.getEntries().stream().map(Entry::getValue).map(KMMetal::getSmeltingRecipes)
        .forEach(out::addAll);

    return out;
  }
}
