package jake.kitchenmod.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;

public abstract class KMCommand {
  public abstract void register(CommandDispatcher<CommandSource> dispatcher);
}
