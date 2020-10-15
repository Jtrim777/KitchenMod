package jake.kitchenmod.entities;

import jake.kitchenmod.util.EnumSerializer;
import jake.kitchenmod.util.TimeHelper;
import jake.kitchenmod.util.TimeHelper.TimeSegment;
import java.util.EnumSet;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class LionEntity extends AnimalEntity {

  private static final DataParameter<PackPosition> PACK_POS =
      EntityDataManager.createKey(LionEntity.class, PackPosition.SERIALIZER);
  private static final DataParameter<Byte> LION_FLAGS =
      EntityDataManager.createKey(LionEntity.class, DataSerializers.BYTE);
  private static final DataParameter<Boolean> IS_MALE =
      EntityDataManager.createKey(LionEntity.class, DataSerializers.BOOLEAN);

  private static final Predicate<LivingEntity> SHOULD_ATTACK = (entity) ->
      entity instanceof SheepEntity
      || entity instanceof CowEntity
      || entity instanceof LlamaEntity
      || entity instanceof ChickenEntity
      || entity instanceof PigEntity
      || entity instanceof RabbitEntity;
  private static final double TARGETING_RANGE = 36;


  public LionEntity(EntityType<LionEntity> entityType ,World worldIn) {
    super(entityType, worldIn);

    this.moveController = new MoveHelperController();
    this.lookController = new LookHelperController();
  }

  // region Entity Setup
  /**
   * Register any AI tasks for this entity, using the syntax
   * <code>this.goalSelector.addGoal(priority, goal)</code>, where <code>priority</code> is an
   * integer with 0 having the highest priority.
   */
  @Override
  protected void registerGoals() {
    this.goalSelector.addGoal(0, new SwimGoal(this));
    this.goalSelector.addGoal(2, new LionEntity.HuntGoal());
    this.goalSelector.addGoal(3, new LionEntity.BiteGoal(1.2, true));
    this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4f));
    this.goalSelector.addGoal(6, new LionEntity.LazyGoal());
    this.goalSelector.addGoal(10, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
  }

  /**
   * Register base values for this entity, such as health and speed. Use the syntax
   * <code>this.getAttribute(attrName).setBaseValue(val)</code>.
   */
  @Override
  protected void registerAttributes() {
    super.registerAttributes();
  }

  /**
   * Register any unique fields this entity possesses, using the syntax <code>
   *   this.dataManager.register(parameter, defaultValue)
   * </code>
   */
  @Override
  protected void registerData() {
    this.dataManager.register(PACK_POS, PackPosition.GAMMA);
    this.dataManager.register(LION_FLAGS, (byte)0);
    this.dataManager.register(IS_MALE, false);
    super.registerData();
  }
  // endregion

  // region Behavior
  private static boolean canAttackTarget(LionEntity self, LivingEntity target) {
    return false;
  }
  // endregion

  // region Breeding Behavior
  @Nullable
  @Override
  public AgeableEntity createChild(AgeableEntity ageable) {
    return ModEntities.LION.create(this.world);
  }
  // endregion

  // region Getters
  boolean getLionFlag(int flag) {
    return (this.dataManager.get(LION_FLAGS) & flag) > 0;
  }

  boolean anyFlagTrue(int... flags) {
    for (int flag : flags) {
      if (getLionFlag(flag)) {
        return true;
      }
    }

    return false;
  }

  public boolean isSleeping() {
    return getLionFlag(Flags.SLEEPING);
  }

  boolean isMale() {
    return this.dataManager.get(IS_MALE);
  }

  PackPosition getPackPosition() {
    return this.dataManager.get(PACK_POS);
  }
  //endregion

  // region Setters
  private void setLionFlag(int flag, boolean value) {
    if (value) {
      this.dataManager.set(LION_FLAGS, (byte)(this.dataManager.get(LION_FLAGS) | flag));
    } else {
      this.dataManager.set(LION_FLAGS, (byte)(this.dataManager.get(LION_FLAGS) & ~flag));
    }
  }
  // endregion

  // region Goals
  private class HuntGoal extends Goal {

    public HuntGoal() {
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean shouldExecute() {
      if (LionEntity.this.isSleeping()) {
        return false;
      } else {
        LivingEntity target = LionEntity.this.getAttackTarget();
        return target != null
            && target.isAlive()
            && LionEntity.SHOULD_ATTACK.test(target)
            && LionEntity.this.getDistanceSq(target) > LionEntity.TARGETING_RANGE
            && !LionEntity.this.isJumping;
      }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting() {
      LionEntity.this.setLionFlag(Flags.SITTING, false);
      LionEntity.this.setLionFlag(Flags.HUNTING, true);
    }

    @Override
    public void resetTask() {
      LivingEntity target = LionEntity.this.getAttackTarget();
      if (target != null && LionEntity.canAttackTarget(LionEntity.this, target)) {
        LionEntity.this.setLionFlag(Flags.HUNTING, false);
        LionEntity.this.getNavigator().clearPath();
        LionEntity.this.getLookController().setLookPositionWithEntity(target,
            (float)LionEntity.this.getHorizontalFaceSpeed(),
            (float)LionEntity.this.getVerticalFaceSpeed());
      } else {
        LionEntity.this.setLionFlag(Flags.HUNTING, false);
      }
    }

    public void tick() {
      LivingEntity target = LionEntity.this.getAttackTarget();
      LionEntity.this.getLookController().setLookPositionWithEntity(target,
          (float)LionEntity.this.getHorizontalFaceSpeed(),
          (float)LionEntity.this.getVerticalFaceSpeed());
      if (LionEntity.this.getDistanceSq(target) <= LionEntity.TARGETING_RANGE) {
        LionEntity.this.getNavigator().clearPath();
      } else {
        LionEntity.this.getNavigator().tryMoveToEntityLiving(target, 1.5D);
      }

    }
  }

  private class BiteGoal extends MeleeAttackGoal {
    public BiteGoal(double attackSpeed, boolean useLongMemory) {
      super(LionEntity.this, attackSpeed, useLongMemory);
    }

    protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
      double d0 = this.getAttackReachSqr(enemy);
      if (distToEnemySqr <= d0 && this.attackTick <= 0) {
        this.attackTick = 20;
        this.attacker.attackEntityAsMob(enemy);
        LionEntity.this.playSound(SoundEvents.ENTITY_PANDA_BITE, 1.0F, 1.0F);
      }

    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting() {
      LionEntity.this.setLionFlag(Flags.HUNTING, false);
      LionEntity.this.setLionFlag(Flags.ATTACKING, true);
      super.startExecuting();
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute() {
      return !LionEntity.this.getLionFlag(Flags.SITTING)
          && !LionEntity.this.isSleeping()
          && super.shouldExecute();
    }
  }

  private class LazyGoal extends Goal {
    private int toggleCooldown = 80 + LionEntity.this.rand.nextInt(300);
    private int lookCooldown = 0;

    private int lazeTimeRemaining = 250 + LionEntity.this.rand.nextInt(2500);

    private double lookX;
    private double lookY;

    public LazyGoal() {
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
    }

    @Override
    public boolean shouldExecute() {
      if (LionEntity.this.moveStrafing == 0.0F
          && LionEntity.this.moveVertical == 0.0F
          && LionEntity.this.moveForward == 0.0F) {
        return shouldStartLazing();
      } else {
        return false;
      }
    }

    @Override
    public boolean shouldContinueExecuting() {
      return lazeTimeRemaining > 0
          && !LionEntity.this.isSleeping();
    }

    private boolean shouldStartLazing() {
      if (LionEntity.this.isSleeping()) {
        return false;
      }

      long worldTime = TimeHelper.getTime(LionEntity.this.world);
      return !TimeHelper.timeIs(worldTime, TimeSegment.MORNING)
          && !TimeHelper.timeIs(worldTime, TimeSegment.EVENING);
    }

    @Override
    public void startExecuting() {
      setLooks();
      LionEntity.this.setLionFlag(Flags.SITTING, true);
      LionEntity.this.getNavigator().clearPath();
    }



    private void setLooks() {
      double d0 = (Math.PI * 2D) * LionEntity.this.getRNG().nextDouble();
      this.lookX = Math.cos(d0);
      this.lookY = Math.sin(d0);
      this.lookCooldown = 80 + LionEntity.this.getRNG().nextInt(20);
    }
  }
  // endregion

  // region Controller Overrides
  public class LookHelperController extends LookController {
    public LookHelperController() {
      super(LionEntity.this);
    }

    /**
     * Updates look
     */
    public void tick() {
      if (!LionEntity.this.isSleeping()) {
        super.tick();
      }

    }

    protected boolean func_220680_b() {
      return !LionEntity.this.anyFlagTrue(Flags.SLEEPING, Flags.HUNTING);
    }
  }

  class MoveHelperController extends MovementController {
    public MoveHelperController() {
      super(LionEntity.this);
    }

    public void tick() {
      if (!LionEntity.this.anyFlagTrue(Flags.SLEEPING, Flags.SITTING)) {
        super.tick();
      }

    }
  }
  // endregion

  // region Associated Values
  public enum PackPosition {
    ALPHA,
    BETA,
    GAMMA;

    public static final EnumSerializer<PackPosition> SERIALIZER =
        EnumSerializer.create(PackPosition.class);
  }

  public static class Flags {
    public static final int AGRO = 1;
    public static final int SLEEPING = 2;
    public static final int SITTING = 4;
    public static final int HUNTING = 8;
    public static final int ATTACKING = 16;
  }
  // endregion
}
