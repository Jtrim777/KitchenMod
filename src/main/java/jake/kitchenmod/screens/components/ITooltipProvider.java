package jake.kitchenmod.screens.components;

import java.util.List;
import net.minecraft.util.text.ITextComponent;

public interface ITooltipProvider {
  boolean shouldShowTooltip(int mouseX, int mouseY);

  List<ITextComponent> getTooltipLines();
}
