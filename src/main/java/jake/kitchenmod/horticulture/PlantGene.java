package jake.kitchenmod.horticulture;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class PlantGene implements INBTSerializable<CompoundNBT> {

  private static final List<String> CORE_KEYS = Arrays.asList(
      "plant_type", "production_level", "light_level", "water_level", "growth_rate",
      "valid_block", "produce"
  );

  private PlantType plantType;
  private Degree productionLevel;
  private LightLevel lightLevel;
  private WaterLevel waterLevel;
  private Degree growthRate;
  private Block validBlock;
  private Item produce;
  private Map<String, String> otherProperties;

  public PlantGene(PlantType plantType, Degree productionLevel,
      LightLevel lightLevel, WaterLevel waterLevel,
      Degree growthRate, Block validBlock, Item produce) {
    this.plantType = plantType;
    this.productionLevel = productionLevel;
    this.lightLevel = lightLevel;
    this.waterLevel = waterLevel;
    this.growthRate = growthRate;
    this.validBlock = validBlock;
    this.produce = produce;
    this.otherProperties = new HashMap<>();
  }

  public static PlantGene getDefault() {
    return new PlantGene(PlantType.CROP, Degree.LOWEST, LightLevel.ANY, WaterLevel.DAMP,
        Degree.NORMAL, Blocks.FARMLAND, Items.WHEAT);
  }

  public int getProductionMin() {
    switch (productionLevel) {
      case LOWEST:
      case LOW:
      case NORMAL:
      default:
        return 1;
      case HIGH:
      case HIGHEST:
        return 2;
    }
  }

  public int getProductionMax() {
    switch (productionLevel) {
      case LOWEST: return 1;
      case LOW: return 2;
      case NORMAL: return 3;
      default: return 3;
      case HIGH: return 4;
      case HIGHEST: return 5;
    }
  }

  public boolean lightLevelIsValid(int ll) {
    switch (lightLevel) {
      case ANY: return true;
      case DARK: return ll < 2;
      case BRIGHT:
      default: return ll > 14;
    }
  }

  public boolean waterLevelIsValid(boolean nextToWater, boolean soilMoist) {
    switch (waterLevel) {
      default:
      case DAMP: return nextToWater || soilMoist;
      case DRY: return !nextToWater && !soilMoist;
      case WET: return nextToWater;
    }
  }

  public PlantType getPlantType() {
    return plantType;
  }

  public float getPerTickGrowthChance() {
    switch (growthRate) {
      case LOWEST: return 1f/10f;
      case LOW: return 1f/7f;
      case NORMAL:
      default: return 1f/5f;
      case HIGH: return 1f/4f;
      case HIGHEST: return 1f/3f;
    }
  }

  public Block getValidBlock() {
    return validBlock;
  }

  public boolean isValidBlock(Block test) {
    return test == validBlock;
  }

  public Item getProduce() {
    return produce;
  }

  public String getProduceName() {
    return produce.getRegistryName().getPath();
  }

  public ItemStack generateHarvest(Random rgen) {
    return new ItemStack(produce,
        rgen.nextInt(getProductionMax()-getProductionMin()) + getProductionMin());
  }

  public String getProperty(String key) {
    return otherProperties.get(key);
  }

  public boolean hasProperty(String key) {
    return otherProperties.containsKey(key);
  }

  public void addProperty(String key, String value) {
    otherProperties.put(key, value);
  }

  @Override
  public CompoundNBT serializeNBT() {
    CompoundNBT tag = new CompoundNBT();
    tag.putString("plant_type", plantType.name());
    tag.putString("production_level", productionLevel.name());
    tag.putString("light_level", lightLevel.name());
    tag.putString("water_level", waterLevel.name());
    tag.putString("growth_rate", growthRate.name());
    tag.putString("valid_block", validBlock.getRegistryName().toString());
    tag.putString("produce", produce.getRegistryName().toString());

    for (Entry<String, String> entry : otherProperties.entrySet()) {
      tag.putString(entry.getKey(), entry.getValue());
    }

    return tag;
  }

  @Override
  public void deserializeNBT(CompoundNBT tag) {
    this.plantType = PlantType.valueOf(tag.getString("plant_type"));
    this.productionLevel = Degree.valueOf(tag.getString("production_level"));
    this.lightLevel = LightLevel.valueOf(tag.getString("light_level"));
    this.waterLevel = WaterLevel.valueOf(tag.getString("water_level"));
    this.growthRate = Degree.valueOf(tag.getString("growth_rate"));

    ResourceLocation vb = new ResourceLocation(tag.getString("valid_block"));
    ResourceLocation pd = new ResourceLocation(tag.getString("produce"));

    this.validBlock = GameRegistry.findRegistry(Block.class).getValue(vb);
    this.produce = GameRegistry.findRegistry(Item.class).getValue(pd);

    tag.keySet().stream().filter(k -> !CORE_KEYS.contains(k))
        .forEach(k -> this.otherProperties.put(k, tag.getString(k)));
  }

  public enum PlantType {
    CROP, BUSH, VINE, TREE
  }

  public enum Degree {
    LOWEST, LOW, NORMAL, HIGH, HIGHEST
  }

  public enum LightLevel {
    DARK, ANY, BRIGHT
  }

  public enum WaterLevel {
    WET, DAMP, DRY
  }
}
