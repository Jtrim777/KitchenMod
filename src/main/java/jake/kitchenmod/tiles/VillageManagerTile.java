package jake.kitchenmod.tiles;

import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class VillageManagerTile extends TileEntity implements ITickableTileEntity,
    INamedContainerProvider {

  private String villageName;
  private UUID villageIdentifier;

  private int villageXLength;
  private int villageYLength;

  private int villagePopulation;

  private Map<UUID, Map<String, PlayerVillageData>> playersData;

  public VillageManagerTile() {
    super(ModTileEntities.VILLAGE_MANAGER_TILE);


  }

  @Override
  public void read(CompoundNBT compound) {
    super.read(compound);
  }

  @Override
  public CompoundNBT write(CompoundNBT compound) {
    return super.write(compound);
  }

  @Override
  public void tick() {

  }

  @Nullable
  @Override
  public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_,
      PlayerEntity p_createMenu_3_) {
    return null;
  }

  @Override
  public ITextComponent getDisplayName() {
    return new TranslationTextComponent("block.kitchenmod.village_manager");
  }

  @Override
  @Nullable
  public SUpdateTileEntityPacket getUpdatePacket() {
    return new SUpdateTileEntityPacket(this.pos, 3, this.getUpdateTag());
  }

  @Override
  public CompoundNBT getUpdateTag() {
    return this.write(new CompoundNBT());
  }

  @Override
  public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
    super.onDataPacket(net, pkt);
    handleUpdateTag(pkt.getNbtCompound());
  }

  private void sendUpdates() {
    world.notifyBlockUpdate(pos, getState(), getState(), 2);

    markDirty();
  }

  private BlockState getState() {
    return world.getBlockState(pos);
  }

  public static class PlayerVillageData {

  }
}
