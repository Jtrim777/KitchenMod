package jake.kitchenmod.blocks.work_blocks;

import jake.kitchenmod.tiles.PressTile;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;

public class Press extends KMMachine {

  private static Properties baseProperties = Properties
      .create(Blocks.STONE.getDefaultState().getMaterial())
      .hardnessAndResistance(3.5f, 17.5f)
      .sound(SoundType.STONE);

  public Press() {
    super(baseProperties, PressTile::new);
  }
}
