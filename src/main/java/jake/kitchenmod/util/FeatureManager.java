package jake.kitchenmod.util;

import jake.kitchenmod.command.KMCommand;
import jake.kitchenmod.fluid.KMFluidBundle;
import jake.kitchenmod.gen.ModStructureGen.PoolEntry;
import jake.kitchenmod.util.ModColors.KMBlockColor;
import jake.kitchenmod.util.ModColors.KMItemColor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ScreenManager.IScreenFactory;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class FeatureManager {

  private List<FeaturePackage> features;
  private List<ResourceLocation> disabledFeatures;

  public FeatureManager() {
    features = new ArrayList<>();
    disabledFeatures = new ArrayList<>();
  }

  public void registerFeature(FeaturePackage feature) {
    features.add(feature);
  }

  public void disableFeature(String featureID) {
    disabledFeatures.add(new ResourceLocation(featureID));
  }

  private void doInit(FeaturePackage feature) {
    feature.setup();

    if (feature.shouldRegisterForForgeEvents()) {
      MinecraftForge.EVENT_BUS.register(feature);
    }

    if (feature.shouldRegisterForModEvents()) {
      FMLJavaModLoadingContext.get().getModEventBus().register(feature);
    }
  }

  public void initializeFeatures() {
    this.features.stream().filter(this::featureIsEnabled).forEach(this::doInit);
  }

  private boolean featureIsEnabled(FeaturePackage f) {
    return !disabledFeatures.contains(f.getFeatureID());
  }

  public <RT> List<RT> getListFromFeatures(Function<FeaturePackage, List<RT>> retrievalFunc) {
    return features.stream().filter(this::featureIsEnabled).map(retrievalFunc)
        .flatMap(Collection::stream).collect(Collectors.toList());
  }

  public <KT, VT> Map<KT, List<VT>> getMapFromFeatures(Function<FeaturePackage, Map<KT, List<VT>>> retrievalFunc) {
    List<Map<KT, List<VT>>> step1 = features.stream().filter(this::featureIsEnabled).map(retrievalFunc)
        .collect(Collectors.toList());

    Map<KT, List<VT>> out = new HashMap<>();

    for (Map<KT, List<VT>> map : step1) {
      for (Entry<KT, List<VT>> elem : map.entrySet()) {
        if (out.containsKey(elem.getKey())) {
          out.get(elem.getKey()).addAll(elem.getValue());
        } else {
          out.put(elem.getKey(), elem.getValue());
        }
      }
    }

    return out;
  }

  public void triggerRegistration(Consumer<FeaturePackage> actor) {
    features.stream().filter(this::featureIsEnabled).forEach(actor);
  }

  public void dumpFeatures(ModLogger logger) {
    logger.log("Loaded " + features.size() + "features: ", "FeatureManager");
    logger.startTask("featureDump");

    int i = 0;
    for (FeaturePackage fp : features) {
      String enableStr = featureIsEnabled(fp) ?
          "\u001b[32m[ENABLED]\u001b[0m" : "\u001b[31m[DISABLED]\u001b[0m";

      logger.log("(" + i + ") " + fp.getFeatureID().toString() + " " + enableStr,
          "FeatureManager");
      i++;
    }

    logger.endTask("featureDump");
  }

  public interface FeaturePackage {

    ResourceLocation getFeatureID();

    default void setup() {}

    /**
     * @return All the blocks that should be registered
     */
    default List<Block> getBlocks() {
      return new ArrayList<>();
    }

    /**
     * @return All items to be registered
     */
    default List<Item> getItems() {
      return new ArrayList<>();
    }

    /**
     * @return All container types to be registered
     */
    default List<ContainerType<?>> getContainers() {
      return new ArrayList<>();
    }

    /**
     * @return All tile entity types to be registered
     */
    default List<TileEntityType<?>> getTileEntities() {
      return new ArrayList<>();
    }

    /**
     * Use this method to generate all relevant screen factories
     * (use {@link jake.kitchenmod.screens.ModScreens#screen(ContainerType, IScreenFactory)})
     * @return All screen factories to be registered
     */
    default List<Executor> getScreenFactories() {
      return new ArrayList<>();
    }

    /**
     * @return All the entity types to be registered
     */
    default List<EntityType<?>> getEntities() {
      return new ArrayList<>();
    }

    /**
     * @return All the mod fluids to be registered
     */
    default List<KMFluidBundle> getFluids() {
      return new ArrayList<>();
    }

    /**
     * Use this method to create all generation veins
     * (use {@link jake.kitchenmod.gen.ModWorldGen#createOreFeature(Block, int, int, int, int)}
     * @return All the block generation veins to be registered
     */
    default List<ConfiguredFeature<?>> getOreVeins() {
      return new ArrayList<>();
    }

    /**
     * @return All world generation events
     */
    default List<Executor> getGenerationEvents() {
      return new ArrayList<>();
    }

    /**
     * Use {@link jake.kitchenmod.gen.ModStructureGen#poolEntry(String, List, int)}
     * @return All additions to the village pools, in the format "pool" : [(new_entry, weight)]
     */
    default Map<String, List<PoolEntry>> getVillagePoolOverrides() {
      return new HashMap<>();
    }

    default List<KMBlockColor> getBlockColors() { return new ArrayList<>(); }

    default List<KMItemColor> getItemColors() { return new ArrayList<>(); }

    default List<KMCommand> getCommands() { return new ArrayList<>(); }

    default List<IRecipeSerializer<?>> getRecipeFactories() { return new ArrayList<>(); }

    default List<Executor> getRegistryFactories() { return new ArrayList<>(); }

    default List<Consumer<AttachCapabilitiesEvent<ItemStack>>> getItemCaps() { return new ArrayList<>(); }

    default boolean shouldRegisterForModEvents() { return false; }

    default boolean shouldRegisterForForgeEvents() { return false; }

    /**
     * Opportunity for features to register any on-entity-update event handlers.
     */
    default void registerEntityHandlers() {}
  }
}
