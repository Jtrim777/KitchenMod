package jake.kitchenmod.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jake.kitchenmod.KitchenMod;
import jake.kitchenmod.util.ModUtil;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class MixingRecipeFactory extends KMRecipeFactory<MixingRecipe> {

  Function<JsonElement, List<ItemStack>> LOIS_CONVERTER = parser.listOf(ModUtil::itemStackFromJson);
  Function<JsonElement, List<Item>> LOI_CONVERTER =
      parser.listOf((e) -> ModUtil.findItem(e.getAsString()));

  @Override
  public MixingRecipe read(ResourceLocation recipeId, JsonObject json) {
    parser.setContext(json, recipeId);

    List<ItemStack> ingredients;
    List<ItemStack> outputs;
    List<Item> tools;

    ingredients = parser.getOrThrow("ingredients", "List<ItemStack>", LOIS_CONVERTER);
    outputs = parser.getOrThrow("output", "List<ItemStack>", LOIS_CONVERTER);
    tools = parser.getOrThrow("tools", "List<ItemStack>", LOIS_CONVERTER).stream()
        .map(ItemStack::getItem).collect(Collectors.toList());

    MixingRecipe outp = new MixingRecipe(recipeId,
        ingredients.toArray(new ItemStack[0]),
        tools.toArray(new Item[0]),
        outputs.toArray(new ItemStack[0]));

    KitchenMod.LOGGER.log("Generated Recipe: " + outp.toString(), "MixingRecipeFactory");

    return outp;
  }

  @Nullable
  @Override
  public MixingRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
    int inpCount = buffer.readInt();
    ItemStack[] inps = new ItemStack[inpCount];
    for (int i = 0; i < inpCount; i++) {
      inps[i] = buffer.readItemStack();
    }

    int toolCount = buffer.readInt();
    ToolItem[] tools = new ToolItem[toolCount];
    for (int i = 0; i < toolCount; i++) {
      tools[i] =
          (ToolItem) ForgeRegistries.ITEMS.getValue(new ResourceLocation(buffer.readString()));
    }

    int outCount = buffer.readInt();
    ItemStack[] outs = new ItemStack[outCount];
    for (int i = 0; i < outCount; i++) {
      outs[i] = buffer.readItemStack();
    }

    return new MixingRecipe(recipeId, inps, tools, outs);
  }

  @Override
  public void write(PacketBuffer buffer, MixingRecipe recipe) {
    buffer.writeInt(recipe.getInputs().length);
    for (ItemStack is : recipe.getInputs()) {
      buffer.writeItemStack(is);
    }

    buffer.writeInt(recipe.getTools().length);
    for (Item is : recipe.getTools()) {
      buffer.writeString(is.getRegistryName().toString());
    }

    buffer.writeInt(recipe.getOutputs().length);
    for (ItemStack is : recipe.getOutputs()) {
      buffer.writeItemStack(is);
    }
  }
}
