package com.restonecash.sansystem.event.handler;

import com.restonecash.sansystem.SanSystem;
import com.restonecash.sansystem.api.registry.AttributeRegistry;
import com.restonecash.sansystem.api.san.ISanity;
import com.restonecash.sansystem.capability.SanityCapability;
import com.restonecash.sansystem.entity.ModEntities;
import com.restonecash.sansystem.entity.ShadowEntity;
import com.restonecash.sansystem.effect.EffectManager;
import com.restonecash.sansystem.config.ServerConfig;
import com.restonecash.sansystem.network.PacketHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;
import java.util.UUID;

/**
 * 理智系统中央处理器
 * 合并了原有的 SanityEffectHandler、SanityRecoveryHandler、ShadowEntitySpawnHandler
 * 统一管理所有与理智值相关的 tick 处理逻辑，避免重复读取 capability
 *
 * 【架构说明】
 * 效果处理（恶心、失明、输入反转）已下沉到内部 EffectManager 实例
 * 上层只做调度，所有效果新增、参数调整、时长刷新在 EffectManager 内部实现
 */
@Mod.EventBusSubscriber(modid = SanSystem.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SanityCentralHandler
{
    private static final java.util.Map<UUID, Long> lastSpawnTick = new java.util.concurrent.ConcurrentHashMap<>();

    private static final EffectManager effectManager = new EffectManager(
        ServerConfig.nauseaThreshold,
        ServerConfig.blindnessThreshold,
        ServerConfig.inputInversionThreshold
    );

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event)
    {
        if (event.getEntity().level().isClientSide) return;

        LivingEntity entity = event.getEntity();

        // 处理玩家特定的理智逻辑
        if (entity instanceof Player player)
        {
            handlePlayerTick(player);
        }

        // 处理所有实体的恢复逻辑（包括玩家）
        handleRecovery(entity);
    }

    @SubscribeEvent
    public static void onPlayerLeave(EntityLeaveLevelEvent event)
    {
        if (!(event.getEntity() instanceof Player player)) return;
        UUID playerId = player.getUUID();
        lastSpawnTick.remove(playerId);
    }

    // ==================== 玩家核心处理 ====================

    private static void handlePlayerTick(Player player)
    {
        player.getCapability(SanityCapability.SANITY).ifPresent(sanity -> {
            float currentSan = sanity.getCore().getSanity();
            float maxSan = sanity.getCore().getMaxSanity();
            float sanPercent = maxSan > 0 ? currentSan / maxSan : 0.0f;
            long currentTick = player.level().getGameTime();

            // 处理死亡（san <= 0）
            if (handleDeath(player, sanPercent))
            {
                return;
            }

            // 处理效果（恶心、失明）带宽限期渐变
            // 委托给 effectManager 处理
            handleEffects(player, sanPercent, sanity, currentTick);

            // 处理缓慢效果
            handleSlowness(player, sanity, currentTick);

            // 处理影子生成
            handleShadowSpawn(player, sanPercent, currentTick);

            // 同步 San 值变化到客户端
            // 【修复说明】移到 capability 内部，确保 capability 存在时才调用
            PacketHandler.syncSanity(player);
        });
    }

    // ==================== 子系统：效果处理 ====================

    /**
     * 处理恶心和失明效果，带宽限期渐变
     * 【委托说明】具体逻辑已下沉到 effectManager.update()
     * 包括：阈值检测、强度计算、宽限期渐变、状态同步到 Capability
     */
    private static void handleEffects(Player player, float sanPercent, ISanity sanity, long currentTick)
    {
        // 调用效果管理器更新所有效果
        // 效果管理器会自动处理：
        // 1. 恶心效果（san < 25% 触发）
        // 2. 失明效果（san < 10% 触发）
        // 3. 输入反转效果（san < 15% 触发）
        effectManager.update(player, currentTick);
    }


    // ==================== 子系统：缓慢效果 ====================

    /**
     * 处理缓慢效果，根据 capability 中的 stacks 应用/移除，并实现缓慢衰减
     * 【注意】此方法使用栈衰减机制，不依赖 EffectManager
     */
    private static void handleSlowness(Player player, ISanity sanity, long currentTick)
    {
        int stacks = sanity.getEffects().getSlownessStacks();

        if (stacks > 0)
        {
            long lastDecay = sanity.getEffects().getLastSlownessDecayTick();
            if (currentTick - lastDecay >= ServerConfig.slownessDecayInterval)
            {
                sanity.getEffects().setSlownessStacks(stacks - 1);
                sanity.getEffects().setLastSlownessDecayTick(currentTick);
                stacks = sanity.getEffects().getSlownessStacks();
            }
        }

        if (stacks > 0)
        {
            int amplifier = Math.min(stacks - 1, 4);
            MobEffectInstance existing = player.getEffect(MobEffects.MOVEMENT_SLOWDOWN);

            if (existing == null || existing.getAmplifier() != amplifier)
            {
                player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, amplifier, false, false, true));
            }
        }
        else
        {
            player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
        }
    }

    // ==================== 子系统：输入反转 ====================

    /**
     * 公共静态方法：检查玩家是否应该反转输入
     * 【公共接口】外部模组可通过此方法查询输入反转状态
     */
    public static boolean shouldInvertInput(Player player)
    {
        if (player.level().isClientSide) return false;

        boolean[] result = new boolean[1];
        player.getCapability(SanityCapability.SANITY).ifPresent(sanity -> {
            result[0] = sanity.getEffects().getInputInverted();
        });
        return result[0];
    }

    // ==================== 子系统：影子生成 ====================

    /**
     * 处理影子实体生成
     */
    private static void handleShadowSpawn(Player player, float sanPercent, long currentTick)
    {
        // san >= 50% 时不生成影子
        if (sanPercent >= ServerConfig.shadowSpawnThreshold) return;

        UUID playerId = player.getUUID();
        Set<ShadowEntity> existingShadows = ShadowEntity.getShadowsForPlayer(playerId);
        int currentCount = existingShadows.size();

        long lastSpawn = lastSpawnTick.getOrDefault(playerId, 0L);

        if (sanPercent < ServerConfig.shadowSpawnCriticalThreshold)
        {
            // 危急状态：san < 20%
            if (currentTick - lastSpawn >= ServerConfig.shadowSpawnCriticalInterval && currentCount < ServerConfig.shadowMaxCriticalCount)
            {
                spawnShadowEntity(player, true);
                lastSpawnTick.put(playerId, currentTick);
            }
        }
        else
        {
            // 低理智状态：20% <= san < 50%
            if (currentTick - lastSpawn >= ServerConfig.shadowSpawnInterval
                && currentCount < ServerConfig.shadowMaxCount
                && player.getRandom().nextFloat() < 0.3f)
            {
                spawnShadowEntity(player, false);
                lastSpawnTick.put(playerId, currentTick);
            }
        }
    }

    private static void spawnShadowEntity(Player player, boolean aggressive)
    {
        Level level = player.level();
        if (!(level instanceof ServerLevel serverLevel)) return;

        double x = player.getX() + (player.getRandom().nextDouble() - 0.5) * 10.0;
        double y = player.getY() + 1.0;
        double z = player.getZ() + (player.getRandom().nextDouble() - 0.5) * 10.0;

        ShadowEntity shadow = ModEntities.SHADOW_ENTITY.get().create(serverLevel);
        if (shadow == null) return;

        shadow.moveTo(x, y, z, player.getRandom().nextFloat() * 360.0f, 0.0f);
        shadow.setTargetPlayer(player);
        shadow.setAggressive(aggressive);

        serverLevel.addFreshEntity(shadow);
    }

    // ==================== 子系统：恢复 ====================

    /**
     * 处理理智恢复，在安全区时恢复
     */
    private static void handleRecovery(LivingEntity entity)
    {
        if (!isInSafeZone(entity)) return;

        float mentalRecovery = getMentalRecovery(entity);
        entity.getCapability(SanityCapability.SANITY).ifPresent(sanity -> {
            sanity.getCore().tickRecovery(mentalRecovery);
        });
    }

    private static boolean isInSafeZone(LivingEntity entity)
    {
        if (entity.hurtTime > 0) return false;

        long lastHurtTime = entity.getLastHurtByMobTimestamp();
        long now = entity.level().getGameTime();

        if (lastHurtTime > 0 && now - lastHurtTime <= ServerConfig.peacefulTicks) return false;

        if (entity instanceof Mob mob && mob.getTarget() != null) return false;

        return true;
    }

    private static float getMentalRecovery(LivingEntity entity)
    {
        var attr = entity.getAttribute(AttributeRegistry.MENTAL_RECOVERY.get());
        return attr != null ? (float) attr.getValue() : 0.0f;
    }

    // ==================== 子系统：死亡处理 ====================

    /**
     * 处理理智归零死亡
     * @return true 如果玩家已死亡
     */
    private static boolean handleDeath(Player player, float sanPercent)
    {
        if (sanPercent <= 0.0f)
        {
            player.hurt(player.damageSources().magic(), Float.MAX_VALUE);
            return true;
        }
        return false;
    }
}
