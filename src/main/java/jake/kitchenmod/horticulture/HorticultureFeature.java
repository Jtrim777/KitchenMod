package jake.kitchenmod.horticulture;

import jake.kitchenmod.KitchenMod;
import jake.kitchenmod.gen.ModStructureGen.PoolEntry;
import jake.kitchenmod.util.FeatureManager.FeaturePackage;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;

public class HorticultureFeature implements FeaturePackage {

  @Override
  public ResourceLocation getFeatureID() {
    return new ResourceLocation(KitchenMod.MODID, "horticulture");
  }

  @Override
  public List<Block> getBlocks() {
    return null;
  }

  @Override
  public List<Item> getItems() {
    return null;
  }

  @Override
  public List<TileEntityType<?>> getTileEntities() {
    return null;
  }

  @Override
  public Map<String, List<PoolEntry>> getVillagePoolOverrides() {
    return null;
  }
}
