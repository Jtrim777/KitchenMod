package jake.kitchenmod.screens.components;

import jake.kitchenmod.screens.KMScreen;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.util.text.ITextComponent;

public class ComponentManager {
  private final KMScreen<?,?> screen;
  private final List<ScreenComponent<?>> components;

  public ComponentManager(KMScreen<?, ?> screen) {
    this.screen = screen;
    this.components = new ArrayList<>();
  }

  public void addComponent(ScreenComponent<?> sc) {
    this.components.add(sc);
  }

  public void drawForeground(int mouseX, int mouseY) {
    for (ScreenComponent<?> c : this.components) {
      c.drawOnForeground(screen);

      if (c instanceof ITooltipProvider) {
        ITooltipProvider provider = (ITooltipProvider)c;

        if (provider.shouldShowTooltip(mouseX-screen.getGuiLeft(), mouseY-screen.getGuiTop())) {
          List<String> lines = provider.getTooltipLines().stream()
              .map(ITextComponent::getFormattedText).collect(Collectors.toList());

          screen.renderTooltip(lines, mouseX, mouseY);
        }
      }
    }
  }

  public void drawBackground(int mouseX, int mouseY) {
    this.components.forEach(c -> c.drawOnBackground(screen));
  }

  private List<IClickableComponent> componentsAtPos(int mx, int my) {
    return this.components.stream().filter(c -> c instanceof IClickableComponent)
        .map(c -> (IClickableComponent)c).filter(c -> c.isMouseOver(mx, my))
        .collect(Collectors.toList());
  }

  public void handleMouseClick(int mouseX, int mouseY) {
    List<IClickableComponent> icomponents = componentsAtPos(mouseX, mouseY);

    icomponents.forEach(c -> c.handleMouseClick(mouseX, mouseY));
  }

  public void handleMouseRelease(int mouseX, int mouseY) {
    List<IClickableComponent> icomponents = componentsAtPos(mouseX, mouseY);

    icomponents.forEach(c -> c.handleMouseRelease(mouseX, mouseY));
  }
}
