package jake.kitchenmod.metallurgy.recipes;

import com.google.gson.JsonObject;
import jake.kitchenmod.KitchenMod;
import jake.kitchenmod.recipes.KMRecipeFactory;
import jake.kitchenmod.util.JsonConverter;
import jake.kitchenmod.util.ModLogger.LogLevel;
import jake.kitchenmod.util.ModUtil;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public class SmelteryRecipeFactory extends KMRecipeFactory<SmelteryRecipe> {

  @Override
  public SmelteryRecipe read(ResourceLocation recipeId, JsonObject json) {
    parser.setContext(json, recipeId);

    ResourceLocation in1 = parser.getOrThrow("input", ResourceLocation.class,
        JsonConverter.RESOURCE_LOCATION::read);

    ResourceLocation in2 = parser.getOrNull("secondary_input", ResourceLocation.class,
        JsonConverter.RESOURCE_LOCATION::read);

    Item tin1 = ForgeRegistries.ITEMS.getValue(in1);

    Item tin2 = in2 == null ? null : ForgeRegistries.ITEMS.getValue(in2);

    FluidStack mo = parser.getOrThrow("molten_output", FluidStack.class,
        ModUtil::fluidStackFromJson);

    ItemStack byproduct = parser.getOrNull("byproduct", ItemStack.class,
        ModUtil::itemStackFromJson);

    SmelteryRecipe recipe = new SmelteryRecipe(recipeId, tin1, tin2, mo, byproduct);
    KitchenMod.LOGGER.log("Loaded recipe "+recipe.toString(),
        "SmelteryRecipeFactory");

    return recipe;
  }

  public static void write(SmelteryRecipe recipe, JsonObject json) {

    json.addProperty("input", recipe.input1.getRegistryName().toString());

    if (recipe.input2 != null) {
      json.addProperty("secondary_input", recipe.input2.getRegistryName().toString());
    }

    json.add("molten_output", ModUtil.writeFluidStackToJson(recipe.moltenOutput));

    if (recipe.byproduct != null) {
      json.add("byproduct", ModUtil.writeItemStackToJson(recipe.byproduct));
    }
  }

  @Nullable
  @Override
  public SmelteryRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
    Item inp1 = ModUtil.findItem(buffer.readString());

    String inp2Raw = buffer.readString();
    Item inp2 = inp2Raw.equals("NONE") ? null : ModUtil.findItem(inp2Raw);

    FluidStack mo = FluidStack.readFromPacket(buffer);
    ItemStack by = buffer.readItemStack();

    return new SmelteryRecipe(recipeId, inp1, inp2, mo, by.isEmpty() ? null : by);
  }

  @Override
  public void write(PacketBuffer buffer, SmelteryRecipe recipe) {
    buffer.writeString(recipe.input1.getRegistryName().toString());
    buffer.writeString(recipe.input2 == null ? "NONE" : recipe.input2.getRegistryName().toString());

    recipe.moltenOutput.writeToPacket(buffer);
    buffer.writeItemStack(recipe.byproduct == null ? ItemStack.EMPTY : recipe.byproduct);
  }
}
