package jake.kitchenmod.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.CakeBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class ChocolateCake extends CakeBlock {

  protected ChocolateCake() {
    super(Block.Properties.create(Material.CAKE)
        .hardnessAndResistance(0.5F).sound(SoundType.CLOTH));
  }
}