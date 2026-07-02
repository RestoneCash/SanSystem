package com.restonecash.sansystem.entity;

import com.restonecash.sansystem.capability.SanityCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ShadowEntity extends Monster
{
    private static final EntityDataAccessor<Boolean> ATTACKING = SynchedEntityData.defineId(ShadowEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> AGGRESSIVE = SynchedEntityData.defineId(ShadowEntity.class, EntityDataSerializers.BOOLEAN);

    // 攻击前摇和瞬移冷却常量
    private static final int ATTACK_WINDUP_TICKS = 16;
    private static final int TELEPORT_COOLDOWN_TICKS = 600;
    private static final double MAX_FOLLOW_DISTANCE = 20.0;
    private static final double TELEPORT_DISTANCE = 30.0;

    // 运行时状态
    private int attackWindup = 0;
    private long lastTeleportTick = 0;
    private UUID targetPlayerId;

    public ShadowEntity(EntityType<? extends Monster> type, Level level)
    {
        super(type, level);
        this.setNoGravity(true);
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1.0)
                .add(Attributes.MOVEMENT_SPEED, 0.5)
                .add(Attributes.FOLLOW_RANGE, 30.0);
    }

    // ==================== 影子注册表委托方法 ====================
    // 【委托说明】注册表逻辑已下沉到 ShadowRegistryImpl
    // 以下方法为向后兼容保留，内部委托到注册表单例

    /**
     * 注册影子到指定玩家
     * 【委托说明】内部调用 ShadowRegistryImpl.getInstance().registerShadow()
     */
    public static void registerShadow(UUID playerId, ShadowEntity shadow)
    {
        ShadowRegistryImpl.getInstance().registerShadow(playerId, shadow);
    }

    /**
     * 从指定玩家注销影子
     * 【委托说明】内部调用 ShadowRegistryImpl.getInstance().unregisterShadow()
     */
    public static void unregisterShadow(UUID playerId, ShadowEntity shadow)
    {
        ShadowRegistryImpl.getInstance().unregisterShadow(playerId, shadow);
    }

    /**
     * 获取指定玩家的所有影子实体
     * 【委托说明】内部调用 ShadowRegistryImpl.getInstance().getShadowsForPlayer()
     */
    public static Set<ShadowEntity> getShadowsForPlayer(UUID playerId)
    {
        return ShadowRegistryImpl.getInstance().getShadowsForPlayer(playerId);
    }

    /**
     * 清除指定玩家的所有影子（玩家离开时调用）
     * 【委托说明】内部调用 ShadowRegistryImpl.getInstance().clearPlayerShadows()
     */
    public static void clearPlayerShadows(UUID playerId)
    {
        ShadowRegistryImpl.getInstance().clearPlayerShadows(playerId);
    }

    /**
     * 清除所有影子（世界卸载时调用）
     * 【委托说明】内部调用 ShadowRegistryImpl.getInstance().clearAllShadows()
     */
    public static void clearAllShadows()
    {
        ShadowRegistryImpl.getInstance().clearAllShadows();
    }

    // ==================== 实体生命周期方法 ====================

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(ATTACKING, false);
        this.entityData.define(AGGRESSIVE, false);
    }

    @Override
    protected void registerGoals()
    {
        this.goalSelector.addGoal(1, new ShadowAttackGoal(this));
        this.goalSelector.addGoal(2, new ShadowFollowGoal(this));
        this.targetSelector.addGoal(1, new ShadowTargetGoal(this));
    }

    @Override
    public void tick()
    {
        super.tick();

        if (!this.level().isClientSide)
        {
            checkTeleport();
            checkDespawnCondition();
        }

        if (this.getEntityData().get(ATTACKING))
        {
            attackWindup++;
            if (attackWindup >= ATTACK_WINDUP_TICKS)
            {
                performAttack();
            }
        }
    }

    /**
     * 检查是否需要瞬移到目标玩家附近
     * 瞬移冷却：600 tick = 30秒
     * 最大瞬移距离：30格
     */
    private void checkTeleport()
    {
        if (targetPlayerId == null) return;

        Player target = level().getPlayerByUUID(targetPlayerId);
        if (target == null) return;

        long currentTick = level().getGameTime();
        if (currentTick - lastTeleportTick < TELEPORT_COOLDOWN_TICKS) return;

        double distance = this.distanceTo(target);
        if (distance > TELEPORT_DISTANCE)
        {
            teleportToTarget(target);
            lastTeleportTick = currentTick;
        }
    }

    /**
     * 瞬移到目标玩家附近（偏移 ±2 格）
     */
    private void teleportToTarget(Player target)
    {
        double x = target.getX() + (random.nextDouble() - 0.5) * 4.0;
        double y = target.getY();
        double z = target.getZ() + (random.nextDouble() - 0.5) * 4.0;

        this.moveTo(x, y, z, this.getYRot(), this.getXRot());

        if (level() instanceof ServerLevel serverLevel)
        {
            serverLevel.sendParticles(ParticleTypes.PORTAL, x, y + 1, z, 20, 0.5, 0.5, 0.5, 0.1);
        }
    }

    /**
     * 检查是否应该消失（目标玩家不存在或 san 值恢复）
     */
    private void checkDespawnCondition()
    {
        if (targetPlayerId == null)
        {
            this.discard();
            return;
        }

        Player target = level().getPlayerByUUID(targetPlayerId);
        if (target == null)
        {
            this.discard();
            return;
        }

        target.getCapability(SanityCapability.SANITY).ifPresent(sanity -> {
            float sanPercent = sanity.getCore().getSanity() / sanity.getCore().getMaxSanity();
            if (sanPercent >= 0.50f)
            {
                this.discard();
            }
        });
    }

    /**
     * 执行攻击：2格范围，命中叠加5%减速，最多5层，攻击后死亡
     */
    private void performAttack()
    {
        this.entityData.set(ATTACKING, false);
        attackWindup = 0;

        List<LivingEntity> targets = level().getEntitiesOfClass(LivingEntity.class,
                new AABB(this.getX() - 2, this.getY() - 1, this.getZ() - 2,
                        this.getX() + 2, this.getY() + 2, this.getZ() + 2));

        for (LivingEntity target : targets)
        {
            if (target instanceof Player player && player.getUUID().equals(targetPlayerId))
            {
                target.hurt(this.damageSources().mobAttack(this), 2.0f);

                player.getCapability(SanityCapability.SANITY).ifPresent(sanity -> {
                    int currentStacks = sanity.getEffects().getSlownessStacks();
                    sanity.getEffects().setSlownessStacks(currentStacks + 1);
                });
            }
        }

        this.discard();
    }

    /**
     * 设置目标玩家并注册到注册表
     * 【重要】NBT 加载后会重新注册（见 readAdditionalSaveData）
     */
    public void setTargetPlayer(Player player)
    {
        this.targetPlayerId = player.getUUID();
        this.setTarget(player);
        // 注册影子到玩家
        registerShadow(this.targetPlayerId, this);
    }

    public UUID getTargetPlayerId()
    {
        return targetPlayerId;
    }

    /**
     * 开始攻击前摇（0.8秒）
     */
    public void startAttackWindup()
    {
        if (!this.entityData.get(ATTACKING))
        {
            this.entityData.set(ATTACKING, true);
            attackWindup = 0;
        }
    }

    public boolean isAttacking()
    {
        return this.entityData.get(ATTACKING);
    }

    public void setAggressive(boolean aggressive)
    {
        this.entityData.set(AGGRESSIVE, aggressive);
    }

    public boolean isAggressive()
    {
        return this.entityData.get(AGGRESSIVE);
    }

    /**
     * 实体移除时自动注销
     * 【生命周期】在 EntityEvent.Remove 事件中被调用（见 ShadowCleanupHandler）
     */
    public void onRemoved()
    {
        if (targetPlayerId != null)
        {
            unregisterShadow(targetPlayerId, this);
        }
    }

    // ==================== 碰撞规则 ====================

    /**
     * 只有目标玩家可以与此影子碰撞
     */
    @Override
    public boolean canCollideWith(net.minecraft.world.entity.Entity entity)
    {
        if (targetPlayerId == null) return false;
        if (entity instanceof Player player)
        {
            return player.getUUID().equals(targetPlayerId);
        }
        return false;
    }

    /**
     * 允许碰撞，具体碰撞规则由 canCollideWith(Entity) 控制
     */
    @Override
    public boolean canBeCollidedWith()
    {
        return true;
    }

    /**
     * 影子对非目标玩家不可见
     */
    @Override
    public boolean isInvisibleTo(Player player)
    {
        if (targetPlayerId == null) return true;
        return !player.getUUID().equals(targetPlayerId);
    }

    // ==================== NBT 持久化 ====================

    @Override
    public void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("TargetPlayer"))
        {
            this.targetPlayerId = tag.getUUID("TargetPlayer");
            // 从 NBT 加载后重新注册影子
            // 【重要】这是必要的，因为世界加载时实体需要重新注册到注册表
            registerShadow(this.targetPlayerId, this);
        }
        this.entityData.set(AGGRESSIVE, tag.getBoolean("Aggressive"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        if (targetPlayerId != null)
        {
            tag.putUUID("TargetPlayer", targetPlayerId);
        }
        tag.putBoolean("Aggressive", this.entityData.get(AGGRESSIVE));
    }

    public static boolean canSpawn(EntityType<ShadowEntity> type, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random)
    {
        return true;
    }

    // ==================== AI 目标 ====================

    static class ShadowAttackGoal extends Goal
    {
        private final ShadowEntity shadow;
        private int attackCooldown = 0;

        public ShadowAttackGoal(ShadowEntity shadow)
        {
            this.shadow = shadow;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse()
        {
            if (!shadow.isAggressive()) return false;
            LivingEntity target = shadow.getTarget();
            return target != null && target.isAlive() && shadow.distanceTo(target) < 5.0;
        }

        @Override
        public void start()
        {
            attackCooldown = 0;
        }

        @Override
        public void tick()
        {
            attackCooldown++;
            if (attackCooldown >= 20)
            {
                shadow.startAttackWindup();
                attackCooldown = 0;
            }
        }
    }

    static class ShadowFollowGoal extends Goal
    {
        private final ShadowEntity shadow;
        private Player targetPlayer;

        public ShadowFollowGoal(ShadowEntity shadow)
        {
            this.shadow = shadow;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse()
        {
            if (shadow.targetPlayerId == null) return false;
            targetPlayer = shadow.level().getPlayerByUUID(shadow.targetPlayerId);
            return targetPlayer != null && targetPlayer.isAlive() && shadow.distanceTo(targetPlayer) > 3.0;
        }

        @Override
        public void tick()
        {
            if (targetPlayer == null) return;

            double distance = shadow.distanceTo(targetPlayer);
            if (distance > MAX_FOLLOW_DISTANCE) return;

            shadow.getNavigation().moveTo(targetPlayer, 1.2);
        }
    }

    static class ShadowTargetGoal extends NearestAttackableTargetGoal<Player>
    {
        private final ShadowEntity shadow;

        public ShadowTargetGoal(ShadowEntity shadow)
        {
            super(shadow, Player.class, false);
            this.shadow = shadow;
        }

        @Override
        public boolean canUse()
        {
            if (shadow.targetPlayerId == null) return false;
            Player target = shadow.level().getPlayerByUUID(shadow.targetPlayerId);
            return target != null && target.isAlive();
        }
    }
}
