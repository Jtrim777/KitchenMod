package jake.kitchenmod.blocks.overrides;

import jake.kitchenmod.items.ModItems;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class KMComposterBlock extends ComposterBlock {

  public KMComposterBlock() {
    super(Block.Properties.create(Material.WOOD).hardnessAndResistance(0.6F).sound(SoundType.WOOD));
  }

  public static void rinit() {
    CHANCES.put(ModItems.strawberry_seeds, 0.3f);
    CHANCES.put(ModItems.chocolate_cake, 1.0f);
    CHANCES.put(ModItems.cocoa_mass, 0.65f);
    CHANCES.put(ModItems.strawberry, 0.65f);
    CHANCES.put(ModItems.dough, 0.85f);
    CHANCES.put(ModItems.flour, 0.65f);
  }

  @Override
  public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos,
      PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
    int fillLevel = state.get(field_220298_a);

    if (fillLevel == 8) {
      if (!worldIn.isRemote) {
        double d0 = (double) (worldIn.rand.nextFloat() * 0.7F) + (double) 0.15F;
        double d1 = (double) (worldIn.rand.nextFloat() * 0.7F) + (double) 0.060000002F + 0.6D;
        double d2 = (double) (worldIn.rand.nextFloat() * 0.7F) + (double) 0.15F;
        ItemEntity itementity = new ItemEntity(worldIn, (double) pos.getX() + d0,
            (double) pos.getY() + d1, (double) pos.getZ() + d2, new ItemStack(
            ModItems.compost));
        itementity.setDefaultPickupDelay();
        worldIn.addEntity(itementity);
      }

      clear(worldIn, pos, state);
      worldIn.playSound(null, pos, SoundEvents.BLOCK_COMPOSTER_EMPTY,
          SoundCategory.BLOCKS, 1.0F, 1.0F);
      return true;
    } else {
      return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }
  }

  private static void clear(IWorld worldIn, BlockPos pos, BlockState state) {
    worldIn.setBlockState(pos, state.with(field_220298_a, 0), 3);
  }

  private static boolean addItem(BlockState state, IWorld worldIn, BlockPos pos,
      ItemStack itemStack) {
    int fillLevel = state.get(field_220298_a);
    float fillChance = CHANCES.getFloat(itemStack.getItem());
    if ((fillLevel != 0 || fillChance <= 0.0F) && !(worldIn.getRandom().nextDouble()
        < (double) fillChance)) {
      return false;
    } else {
      worldIn.setBlockState(pos, state.with(field_220298_a, fillLevel + 1), 3);
      if (fillLevel + 1 == 7) {
        worldIn.getPendingBlockTicks().scheduleTick(pos, state.getBlock(), 20);
      }

      return true;
    }
  }

  @Override
  public ISidedInventory createInventory(BlockState state, IWorld worldIn, BlockPos pos) {
    int i = state.get(field_220298_a);
    if (i == 8) {
      return new FullInventory(state, worldIn, pos, new ItemStack(ModItems.compost));
    } else {
      return (i < 7 ? new PartialInventory(state, worldIn, pos) : new EmptyInventory());
    }
  }

  static class EmptyInventory extends Inventory implements ISidedInventory {

    public EmptyInventory() {
      super(0);
    }

    public int[] getSlotsForFace(Direction side) {
      return new int[0];
    }

    public boolean canInsertItem(int index, ItemStack itemStackIn, @Nullable Direction direction) {
      return false;
    }

    public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
      return false;
    }
  }

  static class FullInventory extends Inventory implements ISidedInventory {

    private final BlockState state;
    private final IWorld world;
    private final BlockPos pos;
    private boolean extracted;

    public FullInventory(BlockState p_i50463_1_, IWorld p_i50463_2_, BlockPos p_i50463_3_,
        ItemStack p_i50463_4_) {
      super(p_i50463_4_);
      this.state = p_i50463_1_;
      this.world = p_i50463_2_;
      this.pos = p_i50463_3_;
    }

    public int getInventoryStackLimit() {
      return 1;
    }

    public int[] getSlotsForFace(Direction side) {
      return side == Direction.DOWN ? new int[]{0} : new int[0];
    }

    public boolean canInsertItem(int index, ItemStack itemStackIn, @Nullable Direction direction) {
      return false;
    }

    public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
      return !this.extracted && direction == Direction.DOWN && stack.getItem() == Items.BONE_MEAL;
    }

    public void markDirty() {
      KMComposterBlock.clear(this.world, this.pos, this.state);
      this.extracted = true;
    }
  }

  static class PartialInventory extends Inventory implements ISidedInventory {

    private final BlockState state;
    private final IWorld world;
    private final BlockPos pos;
    private boolean inserted;

    public PartialInventory(BlockState p_i50464_1_, IWorld p_i50464_2_, BlockPos p_i50464_3_) {
      super(1);
      this.state = p_i50464_1_;
      this.world = p_i50464_2_;
      this.pos = p_i50464_3_;
    }

    public int getInventoryStackLimit() {
      return 1;
    }

    public int[] getSlotsForFace(Direction side) {
      return side == Direction.UP ? new int[]{0} : new int[0];
    }

    public boolean canInsertItem(int index, ItemStack itemStackIn, @Nullable Direction direction) {
      return !this.inserted && direction == Direction.UP && ComposterBlock.CHANCES
          .containsKey(itemStackIn.getItem());
    }

    public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
      return false;
    }

    public void markDirty() {
      ItemStack itemstack = this.getStackInSlot(0);
      if (!itemstack.isEmpty()) {
        this.inserted = true;
        KMComposterBlock.addItem(this.state, this.world, this.pos, itemstack);
        this.removeStackFromSlot(0);
      }

    }
  }
}
