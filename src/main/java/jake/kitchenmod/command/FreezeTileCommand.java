package jake.kitchenmod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import jake.kitchenmod.tiles.KMTileBase;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

public class FreezeTileCommand extends KMCommand {

  private static DynamicCommandExceptionType NO_TILE_ENTITY = new DynamicCommandExceptionType((pos) ->
      new TranslationTextComponent("commands.kitchenmod.freeze.bad_pos", pos));

  private static DynamicCommandExceptionType UNFREEZABLE = new DynamicCommandExceptionType((pos) ->
      new TranslationTextComponent("commands.kitchenmod.freeze.cant_freeze", pos));

  @Override
  public void register(CommandDispatcher<CommandSource> dispatcher) {
    dispatcher.register(
        Commands.literal("freezetile").requires((executor) -> executor.hasPermissionLevel(2))
            .then(Commands.argument("pos", BlockPosArgument.blockPos())
                .executes((executor) -> execute(executor.getSource(),
                    BlockPosArgument.getLoadedBlockPos(executor, "pos"))))
    );
  }

  public static int execute(CommandSource source, BlockPos pos) throws CommandSyntaxException {
    ServerWorld world = source.getWorld();

    TileEntity tileEntity = world.getTileEntity(pos);

    if (tileEntity == null) {
      throw NO_TILE_ENTITY.create(pos);
    }

    if (tileEntity instanceof KMTileBase) {
      KMTileBase<?> trueTile = (KMTileBase<?>)tileEntity;

      trueTile.freeze();
    } else {
      throw UNFREEZABLE.create(pos);
    }

    return 1;
  }
}
