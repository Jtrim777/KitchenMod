package jake.kitchenmod.tiles;

import jake.kitchenmod.capabilities.KMItemHandler;

public class KMTileConvenience {

  public static boolean removeDestroyedItems(KMItemHandler handler, int slotCount, String invName) {
    boolean result = false;
    for (int slot = 0; slot < slotCount; slot++) {
      int maxDmg = handler.getStackInSlot(slot).getMaxDamage();
      int curDmg = handler.getStackInSlot(slot).getDamage();

      if (curDmg >= maxDmg && maxDmg > 1) {
        handler.extractItem(slot, 1, false);
        result = true;
      }
    }

    return result;
  }
}
