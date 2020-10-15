package jake.kitchenmod.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jake.kitchenmod.KitchenMod;
import jake.kitchenmod.util.JsonConverter;
import jake.kitchenmod.util.ModUtil;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public class PressRecipeFactory extends KMRecipeFactory<PressRecipe> {

  @Override
  public PressRecipe read(ResourceLocation recipeId, JsonObject json) {
    parser.setContext(json, recipeId);

    ItemStack inStack = new ItemStack(parser.getOrThrow("input", Item.class,
        JsonConverter.ITEM::read));

    FluidStack output = parser.getOrThrow("output", FluidStack.class,
        ModUtil::fluidStackFromJson);

    ResourceLocation by = parser.getOrNull("byproduct", ResourceLocation.class,
        JsonConverter.RESOURCE_LOCATION::read);

    float byChnc = parser.getOrDefault("byproduct_chance", Float.class,
        JsonElement::getAsFloat, 1f);

    ItemStack byStack = ItemStack.EMPTY;
    if (by != null) {
      byStack = new ItemStack(ModUtil.findItem(by));
    }

    if (byChnc <= 0 || byChnc > 1) {
      throw parser.badValue("byproduct_chance", String.valueOf(byChnc),
          "Byproduct chance must be in the range (0,1]");
    }

    PressRecipe outp = new PressRecipe(recipeId, inStack, output, byStack, byChnc);

    KitchenMod.LOGGER.log("Generated Recipe: " + outp.toString(), "PressRecipeFactory");

    return outp;
  }

  @Nullable
  @Override
  public PressRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
    ResourceLocation inp = new ResourceLocation(buffer.readString());

    FluidStack out = FluidStack.readFromPacket(buffer);

    ResourceLocation by = new ResourceLocation(buffer.readString());
    float byc = buffer.readFloat();

    ItemStack inStack = new ItemStack(ForgeRegistries.ITEMS.getValue(inp));

    ItemStack byStack = new ItemStack(ForgeRegistries.ITEMS.getValue(by));

    return new PressRecipe(recipeId, inStack, out, byStack, byc);
  }

  @Override
  public void write(PacketBuffer buffer, PressRecipe recipe) {
    buffer.writeString(recipe.getInput().toString());
    buffer.writeString(recipe.getOutput().toString());
    recipe.getOutput().writeToPacket(buffer);
  }
}
