package jake.kitchenmod.metallurgy.data;

import jake.kitchenmod.KitchenMod;
import jake.kitchenmod.metallurgy.MllgyFluids;
import jake.kitchenmod.metallurgy.data.KMMetal.Forms;
import jake.kitchenmod.metallurgy.items.MllgyItems;
import java.util.Arrays;
import java.util.List;
import net.minecraft.item.Items;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(KitchenMod.MODID)
public class MllgyMetals {

  @ObjectHolder("iron")
  public static final KMMetal IRON = null;

  @ObjectHolder("gold")
  public static final KMMetal GOLD = null;

  @ObjectHolder("copper")
  public static final KMMetal COPPER = null;

  @ObjectHolder("tin")
  public static final KMMetal TIN = null;

  @ObjectHolder("nickel")
  public static final KMMetal NICKEL = null;

  @ObjectHolder("lead")
  public static final KMMetal LEAD = null;

  @ObjectHolder("pewter")
  public static final KMMetal PEWTER = null;

  @ObjectHolder("silver")
  public static final KMMetal SILVER = null;

  @ObjectHolder("aluminum")
  public static final KMMetal ALUMINUM = null;

  @ObjectHolder("bronze")
  public static final KMMetal BRONZE = null;

  @ObjectHolder("red_gold")
  public static final KMMetal RED_GOLD = null;

  @ObjectHolder("steel")
  public static final KMMetal STEEL = null;

  public static List<KMMetal> createMetals() {
    return Arrays.asList(
        new KMMetal("iron", Items.IRON_INGOT, MllgyFluids.MOLTEN_IRON)
            .addForm(Forms.BLOCK, Items.IRON_BLOCK)
            .addForm(Forms.NUGGET, Items.IRON_NUGGET)
            .addForm(Forms.ORE, Items.IRON_ORE),

        new KMMetal("gold", Items.GOLD_INGOT, MllgyFluids.MOLTEN_GOLD)
            .addForm(Forms.BLOCK, Items.GOLD_BLOCK)
            .addForm(Forms.NUGGET, Items.GOLD_NUGGET)
            .addForm(Forms.ORE, Items.GOLD_ORE),

        new KMMetal("copper", MllgyItems.COPPER_INGOT, MllgyFluids.MOLTEN_COPPER)
            .addForm(Forms.BLOCK, MllgyItems.COPPER_BLOCK)
            .addForm(Forms.NUGGET, MllgyItems.COPPER_NUGGET)
            .addForm(Forms.ORE, MllgyItems.COPPER_ORE),

        new KMMetal("tin", MllgyItems.TIN_INGOT, MllgyFluids.MOLTEN_TIN)
            .addForm(Forms.BLOCK, MllgyItems.TIN_BLOCK)
            .addForm(Forms.NUGGET, MllgyItems.TIN_NUGGET)
            .addForm(Forms.ORE, MllgyItems.TIN_ORE),

        new KMMetal("nickel", MllgyItems.NICKEL_INGOT, MllgyFluids.MOLTEN_NICKEL)
            .addForm(Forms.BLOCK, MllgyItems.NICKEL_BLOCK)
            .addForm(Forms.NUGGET, MllgyItems.NICKEL_NUGGET),

        new KMMetal("lead", MllgyItems.LEAD_INGOT, MllgyFluids.MOLTEN_LEAD)
            .addForm(Forms.BLOCK, MllgyItems.LEAD_BLOCK)
            .addForm(Forms.NUGGET, MllgyItems.LEAD_NUGGET),

        new KMMetal("pewter", MllgyItems.PEWTER_INGOT, MllgyFluids.MOLTEN_PEWTER)
            .addForm(Forms.BLOCK, MllgyItems.PEWTER_BLOCK)
            .addForm(Forms.NUGGET, MllgyItems.PEWTER_NUGGET),

        new KMMetal("silver", MllgyItems.SILVER_INGOT, MllgyFluids.MOLTEN_SILVER)
            .addForm(Forms.BLOCK, MllgyItems.SILVER_BLOCK)
            .addForm(Forms.NUGGET, MllgyItems.SILVER_NUGGET),

        new KMMetal("aluminum", MllgyItems.ALUMINUM_INGOT, MllgyFluids.MOLTEN_ALUMINUM)
            .addForm(Forms.BLOCK, MllgyItems.ALUMINUM_BLOCK)
            .addForm(Forms.NUGGET, MllgyItems.ALUMINUM_NUGGET),

        new KMMetal("bronze", MllgyItems.BRONZE_INGOT, MllgyFluids.MOLTEN_BRONZE)
            .addForm(Forms.BLOCK, MllgyItems.BRONZE_BLOCK)
            .addForm(Forms.NUGGET, MllgyItems.BRONZE_NUGGET),

        new KMMetal("red_gold", MllgyItems.RED_GOLD_INGOT, MllgyFluids.MOLTEN_RED_GOLD)
            .addForm(Forms.BLOCK, MllgyItems.RED_GOLD_BLOCK)
            .addForm(Forms.NUGGET, MllgyItems.RED_GOLD_NUGGET),

        new KMMetal("steel", MllgyItems.STEEL_INGOT, MllgyFluids.MOLTEN_STEEL)
            .addForm(Forms.BLOCK, MllgyItems.STEEL_BLOCK)
            .addForm(Forms.NUGGET, MllgyItems.STEEL_NUGGET)

    );
  }

//  public static KMMetal getMetal(String name) {
//    return RegistryManager.ACTIVE.getRegistry(KMMetal.class)
//        .getValue(new ResourceLocation(KitchenMod.MODID, name));
//  }
}
