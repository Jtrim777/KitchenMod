package jake.kitchenmod.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public interface JsonConverter<T> {

  T read(JsonElement json);

  JsonElement write(T value);

  JsonConverter<ResourceLocation> RESOURCE_LOCATION =
      new JsonConverter<ResourceLocation>() {
        @Override
        public ResourceLocation read(JsonElement json) {
          return new ResourceLocation(json.getAsString());
        }

        @Override
        public JsonElement write(ResourceLocation value) {
          return new JsonPrimitive(value.toString());
        }
      };

  JsonConverter<Item> ITEM =
      new JsonConverter<Item>() {
        @Override
        public Item read(JsonElement json) {
          return ModUtil.findItem(json.getAsString());
        }

        @Override
        public JsonElement write(Item value) {
          return new JsonPrimitive(value.getRegistryName().toString());
        }
      };
}
