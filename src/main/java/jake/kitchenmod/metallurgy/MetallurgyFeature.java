package jake.kitchenmod.metallurgy;

import jake.kitchenmod.KitchenMod;
import jake.kitchenmod.blocks.KMOreBlock;
import jake.kitchenmod.blocks.ModBlocks;
import jake.kitchenmod.command.KMCommand;
import jake.kitchenmod.containers.ModContainers;
import jake.kitchenmod.fluid.KMFluidBundle;
import jake.kitchenmod.handlers.EntityHandler;
import jake.kitchenmod.items.ModItems;
import jake.kitchenmod.metallurgy.blocks.MeteorBlock;
import jake.kitchenmod.metallurgy.blocks.MllgyBlocks;
import jake.kitchenmod.metallurgy.blocks.SmelteryBlock;
import jake.kitchenmod.metallurgy.commands.SummonMeteorCommand;
import jake.kitchenmod.metallurgy.containers.MllgyContainers;
import jake.kitchenmod.metallurgy.containers.SmelteryContainer;
import jake.kitchenmod.metallurgy.data.KMMetal;
import jake.kitchenmod.metallurgy.data.KMMetal.Forms;
import jake.kitchenmod.metallurgy.data.MetalUtil;
import jake.kitchenmod.metallurgy.data.MllgyMetals;
import jake.kitchenmod.metallurgy.gen.MeteorSpawner;
import jake.kitchenmod.metallurgy.items.MetalMoldItem;
import jake.kitchenmod.metallurgy.items.MllgyItems;
import jake.kitchenmod.metallurgy.recipes.MllgyRecipes;
import jake.kitchenmod.metallurgy.screens.SmelteryScreen;
import jake.kitchenmod.metallurgy.tiles.SmelteryTile;
import jake.kitchenmod.recipes.ModRecipes;
import jake.kitchenmod.screens.ModScreens;
import jake.kitchenmod.setup.RegistrySetup;
import jake.kitchenmod.tiles.ModTileEntities;
import jake.kitchenmod.util.Executor;
import jake.kitchenmod.util.FeatureManager.FeaturePackage;
import jake.kitchenmod.util.ModLogger.LogLevel;
import jake.kitchenmod.util.ModUtil;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistry;

public class MetallurgyFeature implements FeaturePackage {

  @Override
  public ResourceLocation getFeatureID() {
    return new ResourceLocation(KitchenMod.MODID, "metallurgy");
  }

  @Override
  public List<Block> getBlocks() {
    return Arrays.asList(
        ModBlocks.entry("meteor", new MeteorBlock()),
        ModBlocks.entry("smeltery", new SmelteryBlock()),
        ModBlocks.entry("copper_ore",
            KMOreBlock.create().xp(3).vein(20, 9, 0, 80).build()),
        ModBlocks.entry("tin_ore",
            KMOreBlock.create().xp(1, 4).vein(20, 6, 10, 90).build()),
        ModBlocks.entry("bauxite", KMOreBlock.create().drop(MllgyItems.BAUXITE_CHUNK, 2, 4)
            .xp(2, 5).vein(18, 6, 0, 64).build()),
        ModBlocks.entry("galena", KMOreBlock.create().drop(MllgyItems.GALENA_CHUNK, 2, 4)
            .xp(2, 5).vein(18, 6, 0, 64).build()),
        ModBlocks.metalBlock("copper"),
        ModBlocks.metalBlock("tin"),
        ModBlocks.metalBlock("nickel"),
        ModBlocks.metalBlock("lead"),
        ModBlocks.metalBlock("pewter"),
        ModBlocks.metalBlock("silver"),
        ModBlocks.metalBlock("aluminum"),
        ModBlocks.metalBlock("bronze"),
        ModBlocks.metalBlock("red_gold"),
        ModBlocks.metalBlock("steel")
    );
  }

  @Override
  public List<Item> getItems() {
    return Arrays.asList(
        ModItems.blockItem(MllgyBlocks.METEOR, ItemGroup.MISC),
        ModItems.blockItem(MllgyBlocks.SMELTERY, ItemGroup.DECORATIONS),
        ModItems.blockItem(MllgyBlocks.COPPER_ORE, ItemGroup.BUILDING_BLOCKS),
        ModItems.blockItem(MllgyBlocks.TIN_ORE, ItemGroup.BUILDING_BLOCKS),
        ModItems.blockItem(MllgyBlocks.BAUXITE, ItemGroup.BUILDING_BLOCKS),
        ModItems.blockItem(MllgyBlocks.GALENA, ItemGroup.BUILDING_BLOCKS),
        ModItems.blockItem(MllgyBlocks.COPPER_BLOCK, ItemGroup.BUILDING_BLOCKS),
        ModItems.blockItem(MllgyBlocks.TIN_BLOCK, ItemGroup.BUILDING_BLOCKS),
        ModItems.blockItem(MllgyBlocks.NICKEL_BLOCK, ItemGroup.BUILDING_BLOCKS),
        ModItems.blockItem(MllgyBlocks.LEAD_BLOCK, ItemGroup.BUILDING_BLOCKS),
        ModItems.blockItem(MllgyBlocks.PEWTER_BLOCK, ItemGroup.BUILDING_BLOCKS),
        ModItems.blockItem(MllgyBlocks.SILVER_BLOCK, ItemGroup.BUILDING_BLOCKS),
        ModItems.blockItem(MllgyBlocks.ALUMINUM_BLOCK, ItemGroup.BUILDING_BLOCKS),
        ModItems.blockItem(MllgyBlocks.BRONZE_BLOCK, ItemGroup.BUILDING_BLOCKS),
        ModItems.blockItem(MllgyBlocks.RED_GOLD_BLOCK, ItemGroup.BUILDING_BLOCKS),
        ModItems.blockItem(MllgyBlocks.STEEL_BLOCK, ItemGroup.BUILDING_BLOCKS),
        ModItems.item("slag", ItemGroup.MATERIALS),
        ModItems.material("copper_ingot"),
        ModItems.material("copper_nugget"),
        ModItems.material("tin_ingot"),
        ModItems.material("tin_nugget"),
        ModItems.material("nickel_ingot"),
        ModItems.material("nickel_nugget"),
        ModItems.material("lead_ingot"),
        ModItems.material("lead_nugget"),
        ModItems.material("pewter_ingot"),
        ModItems.material("pewter_nugget"),
        ModItems.material("silver_ingot"),
        ModItems.material("silver_nugget"),
        ModItems.material("aluminum_ingot"),
        ModItems.material("aluminum_nugget"),
        ModItems.material("bronze_ingot"),
        ModItems.material("bronze_nugget"),
        ModItems.material("red_gold_ingot"),
        ModItems.material("red_gold_nugget"),
        ModItems.material("steel_ingot"),
        ModItems.material("steel_nugget"),
        ModItems.material("lead_chunk"),
        ModItems.material("galena_chunk"),
        ModItems.material("bauxite_chunk"),
        ModItems.entry("ingot_mold", new MetalMoldItem(Forms.INGOT)),
        ModItems.entry("block_mold", new MetalMoldItem(Forms.BLOCK)),
        ModItems.entry("nugget_mold", new MetalMoldItem(Forms.NUGGET))
    );
  }

  @Override
  public List<KMCommand> getCommands() {
    return Collections.singletonList(
        new SummonMeteorCommand()
    );
  }

  @Override
  public void registerEntityHandlers() {
    EntityHandler.registerPlayerHandler(MeteorSpawner::tick);
  }

  @Override
  public List<IRecipeSerializer<?>> getRecipeFactories() {
    return Arrays.asList(
        ModRecipes.factory("smeltery", MllgyRecipes.SMELT_FACTORY)
    );
  }

  @Override
  public List<TileEntityType<?>> getTileEntities() {
    return Arrays.asList(
        ModTileEntities.tileEntity("smeltery", SmelteryTile::new, MllgyBlocks.SMELTERY)
    );
  }

  @Override
  public List<ContainerType<?>> getContainers() {
    return Arrays.asList(
        ModContainers.container("smeltery", MllgyContainers.SMELTERY_CT,
            MllgyBlocks.SMELTERY, SmelteryContainer::new)
    );
  }

  @Override
  public List<Executor> getScreenFactories() {
    return Arrays.asList(
        ModScreens.screen(MllgyContainers.SMELTERY_CT, SmelteryScreen::new)
    );
  }

  @Override
  public List<KMFluidBundle> getFluids() {
    KitchenMod.LOGGER.log("Registering fluids", "MetallurgyFeature",
        LogLevel.DEBUG);

    return Arrays.asList(
        new KMFluidBundle("molten_iron").setField(f -> MllgyFluids.MOLTEN_IRON = f)
            .initialize("kitchenmod:block/lava_overlay", (builder) ->
                builder.color(ModUtil.colorFromRGB(194, 149, 58)).temperature(400)),

        new KMFluidBundle("molten_gold").setField(f -> MllgyFluids.MOLTEN_GOLD = f)
            .initialize("kitchenmod:block/lava_overlay", (builder) ->
                builder.color(ModUtil.colorFromRGB(197, 159, 0)).temperature(400)),

        new KMFluidBundle("molten_copper").setField(f -> MllgyFluids.MOLTEN_COPPER = f)
            .initialize("kitchenmod:block/lava_overlay", (builder) ->
                builder.color(ModUtil.colorFromRGB(243, 120, 0)).temperature(400)),

        new KMFluidBundle("molten_tin").setField(f -> MllgyFluids.MOLTEN_TIN = f)
            .initialize("kitchenmod:block/lava_overlay", (builder) ->
                builder.color(ModUtil.colorFromRGB(120, 120, 140)).temperature(400)),

        new KMFluidBundle("molten_nickel").setField(f -> MllgyFluids.MOLTEN_NICKEL = f)
            .initialize("kitchenmod:block/lava_overlay", (builder) ->
                builder.color(ModUtil.colorFromRGB(100, 85, 85)).temperature(400)),

        new KMFluidBundle("molten_lead").setField(f -> MllgyFluids.MOLTEN_LEAD = f)
            .initialize("kitchenmod:block/lava_overlay", (builder) ->
                builder.color(ModUtil.colorFromRGB(60, 60, 85)).temperature(400)),

        new KMFluidBundle("molten_pewter").setField(f -> MllgyFluids.MOLTEN_PEWTER = f)
            .initialize("kitchenmod:block/lava_overlay", (builder) ->
                builder.color(ModUtil.colorFromRGB(100, 100, 140)).temperature(400)),

        new KMFluidBundle("molten_silver").setField(f -> MllgyFluids.MOLTEN_SILVER = f)
            .initialize("kitchenmod:block/lava_overlay", (builder) ->
                builder.color(ModUtil.colorFromRGB(160, 170, 170)).temperature(400)),

        new KMFluidBundle("molten_aluminum").setField(f -> MllgyFluids.MOLTEN_ALUMINUM = f)
            .initialize("kitchenmod:block/lava_overlay", (builder) ->
                builder.color(ModUtil.colorFromRGB(130, 170, 170)).temperature(400)),

        new KMFluidBundle("molten_bronze").setField(f -> MllgyFluids.MOLTEN_BRONZE = f)
            .initialize("kitchenmod:block/lava_overlay", (builder) ->
                builder.color(ModUtil.colorFromRGB(180, 100, 30)).temperature(400)),

        new KMFluidBundle("molten_red_gold").setField(f -> MllgyFluids.MOLTEN_RED_GOLD = f)
            .initialize("kitchenmod:block/lava_overlay", (builder) ->
                builder.color(ModUtil.colorFromRGB(255, 85, 10)).temperature(400)),

        new KMFluidBundle("molten_steel").setField(f -> MllgyFluids.MOLTEN_STEEL = f)
            .initialize("kitchenmod:block/lava_overlay", (builder) ->
                builder.color(ModUtil.colorFromRGB(50, 50, 50)).temperature(400))
    );
  }

  @Override
  public List<Executor> getRegistryFactories() {
    return Collections.singletonList(RegistrySetup.registryFactory(KMMetal.class, "metals",
        (r) -> MetalUtil.REGISTRY = r));
  }

  @Override
  public boolean shouldRegisterForModEvents() {
    return true;
  }

  @SubscribeEvent
  public void registerMetals(RegistryEvent.Register<KMMetal> event) {
    Executor task = () -> {
      KitchenMod.LOGGER.log("Registering metals", "MetallurgyFeature",
          LogLevel.DEBUG);

      ForgeRegistry<KMMetal> registry = ((ForgeRegistry<KMMetal>) event.getRegistry());

      registry.unfreeze();

      MllgyMetals.createMetals().forEach(registry::register);

      registry.freeze();
    };

    KitchenMod.LOGGER.log("Deferring metal registration", "MetallurgyFeature",
        LogLevel.DEBUG);
    KitchenMod.TASK_MANAGER.addTask(RegistrySetup.REGISTER_FLUIDS, task);
  }
}
