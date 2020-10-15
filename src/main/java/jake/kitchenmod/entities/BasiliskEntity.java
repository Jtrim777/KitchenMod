package jake.kitchenmod.entities;

import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class BasiliskEntity extends MonsterEntity {


  public BasiliskEntity(EntityType<? extends BasiliskEntity> etyp, World worldIn) {
    super(etyp, worldIn);
  }

  public BasiliskEntity(World worldIn) {
    this(ModEntities.BASILISK, worldIn);
  }

  // ~~~~~~~~~~~~~~~~ BEGIN CONFIG

  @Override
  protected void registerGoals() {
    this.getNavigator().setCanSwim(false);

    this.goalSelector.addGoal(0, new LookAtGoal(this, PlayerEntity.class, 8F));
    this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0d, false));
    this.goalSelector.addGoal(2, new LookAtGoal(this, AbstractVillagerEntity.class, 8.0F));
    this.targetSelector
        .addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
    this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this,
        AbstractVillagerEntity.class, true));
    this.goalSelector.addGoal(5, new LookRandomlyGoal(this));
    this.targetSelector.addGoal(6, new HurtByTargetGoal(this));
  }

  @Override
  protected void registerAttributes() {
    super.registerAttributes();
  }

  @Override
  protected void registerData() {
    super.registerData();
  }

  @Override
  public void writeAdditional(CompoundNBT compound) {
    super.writeAdditional(compound);
  }

  @Override
  public void readAdditional(CompoundNBT compound) {
    super.readAdditional(compound);
  }

  // ~~~~~~~~~~~~~~~~ BEGIN LOGIC

  @Override
  public void tick() {

//    if (this.getAttackTarget() != null)

    super.tick();
  }

  @Override
  public void fall(float distance, float damageMultiplier) {
    super.fall(distance, damageMultiplier);
  }

  @Override
  public boolean attackEntityFrom(DamageSource source, float amount) {
    return super.attackEntityFrom(source, amount);
  }

  protected boolean shouldAttackEntity(LivingEntity in) {
    return false;
  }

  // ~~~~~~~~~~~~~~~~ BEGIN GETTERS/SETTERS

  @Override
  public int getMaxFallHeight() {
    return super.getMaxFallHeight();
  }

  @Override
  protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
    return super.getHurtSound(damageSourceIn);
  }

  @Override
  protected SoundEvent getDeathSound() {
    return super.getDeathSound();
  }

  protected static class FindTargetGoal<T extends LivingEntity> extends
      NearestAttackableTargetGoal<T> {

    private final BasiliskEntity basilisk;
    private T target;
    private final EntityPredicate selectorPredicate;
    private final EntityPredicate lineOfSitePredicate = (new EntityPredicate())
        .setLineOfSiteRequired();
    public final Class<T> targetClass;

    public FindTargetGoal(BasiliskEntity self, Class<T> targetType) {
      super(self, targetType, false);
      this.basilisk = self;
      this.selectorPredicate = (new EntityPredicate())
          .setDistance(this.getTargetDistance())
          .setCustomPredicate(self::shouldAttackEntity);
      this.targetClass = targetType;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute() {
      this.target = this.basilisk.world
          .getClosestEntityWithinAABB(targetClass, selectorPredicate, basilisk, basilisk.posX,
              basilisk.posY + basilisk.getEyeHeight(), basilisk.posZ,
              this.getTargetableArea(this.getTargetDistance()));
      return this.target != null;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting() {
      super.startExecuting();
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask() {
      this.target = null;
      super.resetTask();
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting() {
      if (this.target != null) {
        if (!this.basilisk.shouldAttackEntity(this.target)) {
          return false;
        } else {
          this.basilisk.faceEntity(this.target, 10.0F, 10.0F);
          return true;
        }
      } else {
        return this.nearestTarget != null && this.lineOfSitePredicate.canTarget(this.basilisk,
            this.nearestTarget) || super.shouldContinueExecuting();
      }
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick() {
      super.tick();
    }
  }
}
