package jake.kitchenmod.horticulture.items;

import jake.kitchenmod.horticulture.PlantGene;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class KMSeed extends Item {

  public KMSeed() {
    super(new Item.Properties().group(ItemGroup.MATERIALS));
  }

  public static PlantGene getGene(ItemStack stack) {
    CompoundNBT tag = stack.getChildTag("gene");
    PlantGene gene = PlantGene.getDefault();

    if (tag != null) {
      gene.deserializeNBT(tag);
    }

    return gene;
  }

  @Override
  public String getTranslationKey(ItemStack stack) {
    PlantGene gene = getGene(stack);

    return "item.kitchenmod." + gene.getProduceName() + "_seeds";
  }

  public static ItemStack withGene(PlantGene g) {
    ItemStack out = new ItemStack(PlantItems.SEEDS);

    out.getOrCreateTag().put("gene", g.serializeNBT());

    return out;
  }
}
