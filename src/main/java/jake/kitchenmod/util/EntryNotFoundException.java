package jake.kitchenmod.util;

import net.minecraft.util.ResourceLocation;

public class EntryNotFoundException extends RuntimeException {

  public EntryNotFoundException(ResourceLocation entry, String registry) {
    super("Could not find entry in registry " + registry + " for resource "+ entry.toString());
  }
}
