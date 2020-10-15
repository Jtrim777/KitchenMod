package jake.kitchenmod.screens.components;

public interface IClickableComponent {
  boolean isMouseOver(int mouseX, int mouseY);

  void handleMouseClick(int mouseX, int mouseY);
  void handleMouseRelease(int mouseX, int mouseY);
}
