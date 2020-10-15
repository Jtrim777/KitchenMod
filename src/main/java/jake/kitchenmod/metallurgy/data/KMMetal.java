package jake.kitchenmod.metallurgy.data;

import jake.kitchenmod.KitchenMod;
import jake.kitchenmod.metallurgy.items.MllgyItems;
import jake.kitchenmod.metallurgy.recipes.SmelteryRecipe;
import jake.kitchenmod.util.ModLogger.LogLevel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class KMMetal extends ForgeRegistryEntry<KMMetal> {
  private static final int INGOT_VOL = 120;

  private Item ingot;
  private Fluid moltenForm;
  private String name;

  private Map<FormType, Item> forms;

  public KMMetal(String name,Item ingot, Fluid moltenForm) {
    this.setRegistryName(name);

    this.name = name;

    this.ingot = ingot;
    this.moltenForm = moltenForm;

    this.forms = new HashMap<>();
  }

  public KMMetal addForm(FormType type, Item form) {
    this.forms.put(type, form);

    return this;
  }

  public boolean hasForm(FormType form) {
    return form == Forms.INGOT || forms.containsKey(form);
  }

  public boolean hasBlock() {
    return hasForm(Forms.BLOCK);
  }

  public boolean hasOre() {
    return hasForm(Forms.ORE);
  }

  public boolean hasNugget() {
    return hasForm(Forms.NUGGET);
  }

  public Item getIngot() {
    return ingot;
  }

  public BlockItem getBlock() {
    return hasBlock() ? (BlockItem)forms.get(Forms.BLOCK) : null;
  }

  public Item getForm(FormType form) {
    return hasForm(form) ? form == Forms.INGOT ? ingot : forms.get(form) : null;
  }

  public boolean formsContains(Item check) {
    return ingot == check || forms.containsValue(check);
  }

  public boolean isMoltenForm(Fluid check) {
    return moltenForm == check;
  }

  public static int getVolumeForType(FormType type) {
    return type.moltenQuantity;
  }

  public List<SmelteryRecipe.FinishedForm> getSmeltingRecipes() {
    KitchenMod.LOGGER.log("Generating recipes for metal: " + this.toString(), "KMetal",
        LogLevel.DEBUG);

    List<SmelteryRecipe.FinishedForm> start = forms.keySet().stream().map(this::makeRecipe).map(SmelteryRecipe::finish)
        .collect(Collectors.toList());

    start.add(makeRecipe(Forms.INGOT).finish());

    return start;
  }

  private SmelteryRecipe makeRecipe(FormType form) {
    ResourceLocation rnm = new ResourceLocation(KitchenMod.MODID, "smelt_"+name+"_"+form.name);

    ItemStack byproduct = null;
    if (form == Forms.ORE) {
      byproduct = new ItemStack(MllgyItems.SLAG, 3);
    }

    return new SmelteryRecipe(rnm, getForm(form), new FluidStack(moltenForm, form.moltenQuantity),
        byproduct);
  }

  @Override
  public String toString() {
    return "KMMetal{" +
        "ingot=" + ingot +
        ", moltenForm=" + moltenForm +
        ", name='" + name + '\'' +
        ", forms=" + forms +
        '}';
  }

  public static class FormType{
    String name;
    int moltenQuantity;

    private FormType(String name, int moltenQuantity) {
      this.name = name;
      this.moltenQuantity = moltenQuantity;
    }

    @Override
    public String toString() {
      return "FormType{" +
          "name='" + name + '\'' +
          ", moltenQuantity=" + moltenQuantity +
          '}';
    }
  }

  public static class Forms {
    public static final FormType INGOT = create("ingot", INGOT_VOL);
    public static final FormType NUGGET = create("nugget", INGOT_VOL/4);
    public static final FormType ORE = create("ore", INGOT_VOL + 30);
    public static final FormType BLOCK = create("block", INGOT_VOL * 9);

    public static FormType create(String name, int moltenQuantity) {
      return new FormType(name, moltenQuantity);
    }
  }
}
