package jake.kitchenmod.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.util.ResourceLocation;

public class JsonUtil {

  private final String serializerName;
  private JsonObject json;
  private ResourceLocation itemName;

  public JsonUtil(String serializerName) {
    this.serializerName = serializerName;
  }

  public void setContext(JsonObject json, ResourceLocation itemName) {
    this.json = json;
    this.itemName = itemName;
  }

  public void setContext(JsonObject json) {
    this.json = json;
    this.itemName = new ResourceLocation("kitchenmod:unknown");
  }

  private void ensureContext() {
    if (json == null || itemName == null) {
      throw new JsonParseError("Could not perform parsing operation, context was not set");
    }
  }

  public <T> Optional<T> getOptional(String key, Class<T> typ, Function<JsonElement, T> converter) {
    ensureContext();

    if (!json.has(key)) {
      return Optional.empty();
    }

    JsonElement raw = json.get(key);

    T out;
    try {
      out = converter.apply(raw);
    } catch (JsonParseError e) {
      throw badValue(key, raw.toString(), e.getMessage());
    } catch (Exception e) {
      throw badType(key, raw.toString(), typ);
    }

    return Optional.of(out);
  }

  public <T> T getOrThrow(String key, Class<T> typ, Function<JsonElement, T> converter) {
    Optional<T> optResult = getOptional(key, typ, converter);

    optResult.orElseThrow(() -> missingValue(key, typ));

    return optResult.get();
  }

  public <T> T getOrNull(String key, Class<T> typ, Function<JsonElement, T> converter) {
    Optional<T> optResult = getOptional(key, typ, converter);

    return optResult.orElse(null);
  }

  public <T> T getOrDefault(String key, Class<T> typ, Function<JsonElement, T> converter, T def) {
    Optional<T> optResult = getOptional(key, typ, converter);

    return optResult.orElse(def);
  }


  public <T> Optional<T> getOptional(String key, String typ, Function<JsonElement, T> converter) {
    ensureContext();

    if (!json.has(key)) {
      return Optional.empty();
    }

    JsonElement raw = json.get(key);

    T out;
    try {
      out = converter.apply(raw);
    } catch (JsonParseError e) {
      throw badValue(key, raw.toString(), e.getMessage());
    } catch (Exception e) {
      throw badType(key, raw.toString(), typ);
    }

    return Optional.of(out);
  }

  public <T> T getOrThrow(String key, String typ, Function<JsonElement, T> converter) {
    Optional<T> optResult = getOptional(key, typ, converter);

    optResult.orElseThrow(() -> missingValue(key, typ));

    return optResult.get();
  }

  public <T> T getOrNull(String key, String typ, Function<JsonElement, T> converter) {
    Optional<T> optResult = getOptional(key, typ, converter);

    return optResult.orElse(null);
  }

  public <T> T getOrDefault(String key, String typ, Function<JsonElement, T> converter, T def) {
    Optional<T> optResult = getOptional(key, typ, converter);

    return optResult.orElse(def);
  }

  public JsonParseError badValue(String fkey, String fval, String problem) {
    return new JsonParseError(String.format("Key %s had value %s; %s", fkey, fval,
        problem), true);
  }

  public JsonParseError badType(String fkey, String fval, Class<?> expectedType) {
    return new JsonParseError(String.format("Could not convert value of key %s "
        + "to expected type %s; %s is not valid", fkey, expectedType.getName(), fval), true);
  }

  public JsonParseError badType(String fkey, String fval, String expectedTypeName) {
    return new JsonParseError(String.format("Could not convert value of key %s "
        + "to expected type %s; %s is not valid", fkey, expectedTypeName, fval), true);
  }

  public JsonParseError missingValue(String fkey, Class<?> expectedType) {
    return new JsonParseError(String.format("Object type expects key %s with value "
        + "of type %s", fkey, expectedType.getName()), true);
  }

  public JsonParseError missingValue(String fkey, String expectedTypeName) {
    return new JsonParseError(String.format("Object type expects key %s with value "
        + "of type %s", fkey, expectedTypeName), true);
  }

  public <M> Function<JsonElement, List<M>> listOf(Function<JsonElement, M> elemConverter) {
    return (obj) -> {
      JsonArray raw = obj.getAsJsonArray();
      List<M> list = new ArrayList<>();

      int i=0;
      for (JsonElement elem : raw) {
        try {
          M val = elemConverter.apply(elem);
          list.add(val);
        } catch (Exception e) {
          throw badType("["+i+"]", elem.toString(), "Unknown");
        }
      }

      return list;
    };
  }

  public class JsonParseError extends IllegalArgumentException {

    private JsonParseError(String message) {
      super(message);
    }

    JsonParseError(String problem, boolean flag) {
      this(String.format("[%s] Failed to object quest %s: %s",
          JsonUtil.this.serializerName, JsonUtil.this.itemName.toString(), problem));
    }
  }

}
