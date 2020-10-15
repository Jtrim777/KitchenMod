package jake.kitchenmod.metallurgy.screens;

import jake.kitchenmod.metallurgy.containers.SmelteryContainer;
import jake.kitchenmod.metallurgy.tiles.SmelteryTile;
import jake.kitchenmod.screens.KMScreen;
import jake.kitchenmod.screens.components.MultiProgressBar;
import jake.kitchenmod.screens.components.NamedValueComponent;
import jake.kitchenmod.screens.components.TankComponent;
import jake.kitchenmod.screens.components.TextureBinding;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public class SmelteryScreen extends KMScreen<SmelteryContainer, SmelteryTile> {

  private static final TextureBinding PROGRESS_OVERLAY_1 = new TextureBinding(
      64, 11, 176, 73, 21, 32
  );
  private static final TextureBinding PROGRESS_OVERLAY_2 = new TextureBinding(
      86, 16, 200, 78, 49, 23
  );
  private static final TextureBinding TEMP_BAR = new TextureBinding(
      46, 27, 180, 54, 18, 4
  );
  private static final TextureBinding COLD_BAR = new TextureBinding(
      103, 56, 176, 54, 4, 18
  );

  public SmelteryScreen(SmelteryContainer container,
      PlayerInventory inventory,
      ITextComponent name) {
    super(container, inventory, name, "kitchenmod:textures/gui/container/smeltery.png");

    manager.addComponent(new TankComponent(25, 5, CutoffDirection.UP, tileEntity::getLavaTank));
    manager.addComponent(new TankComponent(109, 24, CutoffDirection.UP,
        tileEntity::getOutputTank));
    manager.addComponent(new NamedValueComponent("Temperature", TEMP_BAR, CutoffDirection.RIGHT,
        tileEntity::getTemp).setTooltipColor(TextFormatting.DARK_RED));
    manager.addComponent(new NamedValueComponent("Cold Power", COLD_BAR, CutoffDirection.UP,
        tileEntity::getColdPower).setTooltipColor(TextFormatting.AQUA));

    manager.addComponent(new MultiProgressBar(tileEntity::getWorkProgress)
        .addPart(PROGRESS_OVERLAY_1, CutoffDirection.RIGHT, 0f, 0.28f)
        .addPart(PROGRESS_OVERLAY_2, CutoffDirection.RIGHT, 0.28f, 1f));
  }
}
