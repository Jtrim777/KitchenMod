package jake.kitchenmod.items;

public interface IFertilizer {
  /**
   * @return The multiplier for modifying the chance of growing a plant.
   * Baseline is BONEMEAL, with a value of 1.0
   */
  float getFertilityLevel();
}
