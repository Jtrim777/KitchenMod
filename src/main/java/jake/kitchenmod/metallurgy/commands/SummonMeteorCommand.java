package jake.kitchenmod.metallurgy.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import jake.kitchenmod.command.KMCommand;
import jake.kitchenmod.metallurgy.blocks.MllgyBlocks;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class SummonMeteorCommand extends KMCommand {

  public SummonMeteorCommand() { }

  @Override
  public void register(CommandDispatcher<CommandSource> dispatcher) {
    dispatcher.register(
        Commands.literal("summonmeteor").requires((executor) -> executor.hasPermissionLevel(2))
            .then(Commands.argument("pos", BlockPosArgument.blockPos())
                .executes((executor) -> execute(executor.getSource(),
                    BlockPosArgument.getLoadedBlockPos(executor, "pos"))))
    );
  }

  public static int execute(CommandSource source, BlockPos pos) throws CommandSyntaxException {
    ServerWorld world = source.getWorld();

    world.setBlockState(pos, MllgyBlocks.METEOR.getDefaultState());

    return 1;
  }
}
