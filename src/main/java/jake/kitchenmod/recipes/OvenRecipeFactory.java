package jake.kitchenmod.recipes;

import static jake.kitchenmod.util.JsonConverter.RESOURCE_LOCATION;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import jake.kitchenmod.KitchenMod;
import jake.kitchenmod.util.JsonConverter;
import jake.kitchenmod.util.ModUtil;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class OvenRecipeFactory extends KMRecipeFactory<OvenRecipe> {

  @Override
  public OvenRecipe read(ResourceLocation recipeId, JsonObject json) {
    parser.setContext(json, recipeId);

    ResourceLocation inp = parser.getOrThrow("input", ResourceLocation.class,
        RESOURCE_LOCATION::read);
    ResourceLocation out = parser.getOrThrow("output", ResourceLocation.class,
        RESOURCE_LOCATION::read);

    ItemStack inStack = new ItemStack(ModUtil.findItem(inp));
    ItemStack outStack = new ItemStack(ModUtil.findItem(out));

    if (inStack.getItem() == Items.AIR) {
      throw parser.badValue("input", inp.toString(), "Not a registered item");
    } else if (outStack.getItem() == Items.AIR) {
      throw parser.badValue("output", out.toString(), "Not a registered item");
    }

    OvenRecipe outp = new OvenRecipe(recipeId, inStack, outStack);

    KitchenMod.LOGGER.log("Generated Recipe: " + outp.toString(), "OvenRecipeFactory");

    return outp;
  }

  @Nullable
  @Override
  public OvenRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
    ResourceLocation inp = new ResourceLocation(buffer.readString());
    ResourceLocation out = new ResourceLocation(buffer.readString());

    ItemStack inStack = new ItemStack(ForgeRegistries.ITEMS.getValue(inp));
    ItemStack outStack = new ItemStack(ForgeRegistries.ITEMS.getValue(inp));

    return new OvenRecipe(recipeId, inStack, outStack);
  }

  @Override
  public void write(PacketBuffer buffer, OvenRecipe recipe) {
    buffer.writeString(recipe.getInput().toString());
    buffer.writeString(recipe.getOutput().toString());
  }
}
