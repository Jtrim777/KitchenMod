package jake.kitchenmod.screens.components;

import jake.kitchenmod.screens.KMScreen.CutoffDirection;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class NamedValueComponent extends ValueComponent implements ITooltipProvider {

  private String name;
  private TextFormatting nameColor;

  public NamedValueComponent(String name, TextureBinding componentLocation,
      CutoffDirection direction,
      Supplier<Float> levelGetter) {
    super(componentLocation, direction, levelGetter);

    this.name = name;
  }

  public NamedValueComponent setTooltipColor(TextFormatting c) {
    this.nameColor = c;

    return this;
  }

  @Override
  public boolean shouldShowTooltip(int mouseX, int mouseY) {
    return isInside(mouseX, mouseY);
  }

  @Override
  public List<ITextComponent> getTooltipLines() {
    ITextComponent line1 = new StringTextComponent(name);
    if (nameColor != null) line1.applyTextStyle(nameColor);

    ITextComponent line2 = new StringTextComponent((int)(dataSupplier.get() * 100) + "%");
    line2.applyTextStyle(TextFormatting.GRAY);

    return Arrays.asList(line1, line2);
  }
}
