package jake.kitchenmod.metallurgy.items;

import jake.kitchenmod.metallurgy.data.KMMetal;
import jake.kitchenmod.metallurgy.data.KMMetal.FormType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

public class MetalMoldItem extends Item {

  private KMMetal.FormType metalForm;

  public MetalMoldItem(FormType metalForm) {
    super(new Item.Properties().group(ItemGroup.TOOLS).maxDamage(5));
    this.metalForm = metalForm;
  }

  public FormType getMetalForm() {
    return metalForm;
  }
}
