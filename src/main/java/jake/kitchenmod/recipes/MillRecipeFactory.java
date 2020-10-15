package jake.kitchenmod.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import jake.kitchenmod.KitchenMod;
import jake.kitchenmod.util.JsonConverter;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class MillRecipeFactory extends KMRecipeFactory<MillRecipe> {

  @Override
  public MillRecipe read(ResourceLocation recipeId, JsonObject json) {
    parser.setContext(json, recipeId);

    ResourceLocation inp = parser.getOrThrow("input", ResourceLocation.class,
        JsonConverter.RESOURCE_LOCATION::read);
    ResourceLocation out = parser.getOrThrow("output", ResourceLocation.class,
        JsonConverter.RESOURCE_LOCATION::read);

    int out_q = parser.getOrDefault("output_quantity", int.class,
        JsonElement::getAsInt, 1);

    ItemStack inStack = new ItemStack(ForgeRegistries.ITEMS.getValue(inp));
    ItemStack outStack = new ItemStack(ForgeRegistries.ITEMS.getValue(out), out_q);

    if (inStack.getItem() == Items.AIR) {
      throw parser.badValue("input", inp.toString(), "Not a registered item");
    } else if (outStack.getItem() == Items.AIR) {
      throw parser.badValue("output", out.toString(), "Not a registered item");
    } else if (out_q < 1) {
      throw parser.badValue("output_quantity", out_q+"",
          "Output quantity must be at least 1");
    }

    MillRecipe outp = new MillRecipe(recipeId, inStack, outStack);

    KitchenMod.LOGGER.log("Generated Recipe: " + outp.toString(), "MillRecipeFactory");

    return outp;
  }

  @Nullable
  @Override
  public MillRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
    ResourceLocation inp = new ResourceLocation(buffer.readString());
    ResourceLocation out = new ResourceLocation(buffer.readString());
    int oq = buffer.readInt();

    ItemStack inStack = new ItemStack(ForgeRegistries.ITEMS.getValue(inp));
    ItemStack outStack = new ItemStack(ForgeRegistries.ITEMS.getValue(out), oq);

    return new MillRecipe(recipeId, inStack, outStack);
  }

  @Override
  public void write(PacketBuffer buffer, MillRecipe recipe) {
    buffer.writeString(recipe.getInput().toString());
    buffer.writeString(recipe.getOutput().toString());
    buffer.writeInt(recipe.getOutputQuantity());
  }
}
