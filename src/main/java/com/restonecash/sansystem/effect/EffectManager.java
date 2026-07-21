package com.restonecash.sansystem.effect;

import com.restonecash.sansystem.api.san.ISanity;
import com.restonecash.sansystem.capability.SanityCapability;
import com.restonecash.sansystem.config.ServerConfig;
import net.minecraft.world.entity.player.Player;

import java.util.EnumMap;
import java.util.Map;

/**
 * 效果管理器
 * 统一管理所有效果的状态和应用逻辑
 *
 * 【架构说明】
 * 此类作为效果系统的中央协调器：
 * 1. 持有所有效果的 EffectApplier（应用逻辑）
 * 2. 提供统一的 update 方法，在 LivingTickEvent 中调用
 * 3. 每个效果类型的阈值可通过构造函数配置（支持动态修改）
 * 4. 效果状态存储在 Capability 中（ISanity / SanityEffects），而非 EffectManager 内部
 *
 * 【重要设计决策】
 * 效果状态（强度、宽限期等）不存储在 EffectManager 内部，因为：
 * - EffectManager 是全局单例，所有玩家共享
 * - 每个玩家的效果状态必须独立
 * - Capability 已经是状态的权威来源（SanityEffects）
 * - 避免状态重复存储和同步问题
 *
 * 新增效果类型：
 * 1. 在 EffectType 中添加枚举值
 * 2. 在 SanityEffects 中添加对应状态字段
 * 3. 创建对应的 EffectApplier 实现
 * 4. 在构造函数中注册
 */
public class EffectManager
{
    @Deprecated
    public static final int GRACE_PERIOD_TICKS = 100;

    private final Map<EffectType, EffectApplier> effectAppliers = new EnumMap<>(EffectType.class);

    private final Map<EffectType, Float> thresholds = new EnumMap<>(EffectType.class);

    /**
     * 默认构造函数，使用 EffectType 枚举中的默认阈值
     */
    public EffectManager()
    {
        // 初始化阈值
        for (EffectType type : EffectType.values())
        {
            thresholds.put(type, type.getThreshold());
        }

        // 注册所有效果应用器
        registerEffectApplier(new NauseaEffectApplier());
        registerEffectApplier(new BlindnessEffectApplier());
        registerEffectApplier(new InputInversionEffectApplier());
    }

    /**
     * 带阈值配置的构造函数
     * @param nauseaThreshold 恶心效果阈值（san百分比）
     * @param blindnessThreshold 失明效果阈值（san百分比）
     * @param inputInversionThreshold 输入反转阈值（san百分比）
     */
    public EffectManager(float nauseaThreshold, float blindnessThreshold, float inputInversionThreshold)
    {
        // 初始化阈值
        thresholds.put(EffectType.NAUSEA, nauseaThreshold);
        thresholds.put(EffectType.BLINDNESS, blindnessThreshold);
        thresholds.put(EffectType.INPUT_INVERSION, inputInversionThreshold);

        // 注册所有效果应用器
        registerEffectApplier(new NauseaEffectApplier());
        registerEffectApplier(new BlindnessEffectApplier());
        registerEffectApplier(new InputInversionEffectApplier());
    }

    /**
     * 注册效果应用器
     */
    public void registerEffectApplier(EffectApplier applier)
    {
        effectAppliers.put(applier.getEffectType(), applier);
    }

    /**
     * 获取指定效果的阈值
     */
    public float getThreshold(EffectType type)
    {
        return thresholds.getOrDefault(type, type.getThreshold());
    }

    /**
     * 更新所有效果
     * 在 LivingTickEvent 中调用
     *
     * 【状态存储说明】
     * 效果状态（强度、宽限期）直接从 Capability 读取和写入，
     * EffectManager 本身不维护任何玩家状态，确保多玩家隔离。
     *
     * @param player 玩家
     * @param currentTick 当前 tick
     */
    public void update(Player player, long currentTick)
    {
        if (player == null) return;

        player.getCapability(SanityCapability.SANITY).ifPresent(sanity -> {
            float sanityPercent = sanity.getCore().getMaxSanity() > 0
                ? sanity.getCore().getSanity() / sanity.getCore().getMaxSanity()
                : 0.0f;

            for (EffectType type : EffectType.values())
            {
                updateEffect(player, type, sanityPercent, sanity, currentTick);
            }
        });
    }

    /**
     * 更新单个效果
     *
     * 【状态来源】
     * 效果状态直接从 Capability 读取，更新后写回 Capability。
     * 这样 EffectManager 是无状态的，可以安全地作为单例使用。
     *
     * 【效果强度计算】
     * 效果强度根据 San 值动态调整：
     * - San 值越接近阈值，强度越低（接近 0）
     * - San 值越低，强度越高（接近 1）
     * - 强度 = (阈值 - 当前San%) / 阈值
     *
     * @param player 玩家
     * @param type 效果类型
     * @param sanityPercent 当前 san 值百分比
     * @param sanity capability 引用（用于读写状态）
     * @param currentTick 当前游戏 tick
     */
    private void updateEffect(Player player, EffectType type, float sanityPercent,
                              ISanity sanity, long currentTick)
    {
        EffectApplier applier = effectAppliers.get(type);
        if (applier == null) return;

        // 使用配置的阈值（而非枚举默认值）
        float threshold = thresholds.getOrDefault(type, type.getThreshold());
        boolean shouldApply = sanityPercent < threshold;

        if (shouldApply)
        {
            float intensity = (threshold - sanityPercent) / threshold;
            intensity = Math.max(0.0f, Math.min(intensity, 1.0f));

            setEffectIntensity(type, sanity, intensity);
            setGracePeriodStart(type, sanity, 0L);
            applier.apply(player, intensity);
        }
        else
        {
            long gracePeriodStart = getGracePeriodStart(type, sanity);
            if (gracePeriodStart == 0)
            {
                return;
            }

            float fadeIntensity = calculateFadeIntensity(gracePeriodStart, currentTick);
            setEffectIntensity(type, sanity, fadeIntensity);

            if (fadeIntensity <= 0.0f)
            {
                applier.remove(player);
                resetEffectState(type, sanity);
            }
            else
            {
                applier.apply(player, fadeIntensity);
            }
        }
    }

    // ==================== 状态读写辅助方法 ====================
    // 这些方法将 EffectType 映射到 Capability 中的对应字段
    // 新增效果类型时只需扩展这些方法即可

    /**
     * 从 Capability 读取效果强度
     */
    private float getEffectIntensity(EffectType type, ISanity sanity)
    {
        return switch (type)
        {
            case NAUSEA -> sanity.getEffects().getNauseaIntensity();
            case BLINDNESS -> sanity.getEffects().getBlindnessIntensity();
            case INPUT_INVERSION -> sanity.getEffects().getInputInverted() ? 1.0f : 0.0f;
        };
    }

    /**
     * 写入效果强度到 Capability
     */
    private void setEffectIntensity(EffectType type, ISanity sanity, float intensity)
    {
        switch (type)
        {
            case NAUSEA -> sanity.getEffects().setNauseaIntensity(intensity);
            case BLINDNESS -> sanity.getEffects().setBlindnessIntensity(intensity);
            case INPUT_INVERSION -> sanity.getEffects().setInputInverted(intensity > 0);
        }
    }

    /**
     * 从 Capability 读取宽限期开始时间
     */
    private long getGracePeriodStart(EffectType type, ISanity sanity)
    {
        return switch (type)
        {
            case NAUSEA -> sanity.getEffects().getGracePeriodStartNausea();
            case BLINDNESS -> sanity.getEffects().getGracePeriodStartBlindness();
            case INPUT_INVERSION -> sanity.getEffects().getGracePeriodStartInput();
        };
    }

    /**
     * 写入宽限期开始时间到 Capability
     */
    private void setGracePeriodStart(EffectType type, ISanity sanity, long tick)
    {
        switch (type)
        {
            case NAUSEA -> sanity.getEffects().setGracePeriodStartNausea(tick);
            case BLINDNESS -> sanity.getEffects().setGracePeriodStartBlindness(tick);
            case INPUT_INVERSION -> sanity.getEffects().setGracePeriodStartInput(tick);
        }
    }

    /**
     * 重置效果状态（效果完全消退时调用）
     */
    private void resetEffectState(EffectType type, ISanity sanity)
    {
        setEffectIntensity(type, sanity, 0.0f);
        setGracePeriodStart(type, sanity, 0L);
    }

    /**
     * 计算宽限期内的渐变强度
     */
    public static float calculateFadeIntensity(long gracePeriodStart, long currentTick)
    {
        if (gracePeriodStart <= 0)
        {
            return 1.0f;
        }

        long ticksSinceGraceStart = currentTick - gracePeriodStart;

        if (ticksSinceGraceStart < ServerConfig.gracePeriodTicks)
        {
            return 1.0f - (float) ticksSinceGraceStart / ServerConfig.gracePeriodTicks;
        }
        else
        {
            return 0.0f;
        }
    }

    /**
     * 重置所有效果状态
     * 用于玩家死亡、san值归零等场景
     *
     * 【注意】需要传入 player 或 sanity 引用，因为状态存储在 Capability 中
     */
    public void resetAll(Player player)
    {
        player.getCapability(SanityCapability.SANITY).ifPresent(sanity -> {
            for (EffectType type : EffectType.values())
            {
                resetEffectState(type, sanity);
                EffectApplier applier = effectAppliers.get(type);
                if (applier != null)
                {
                    applier.remove(player);
                }
            }
        });
    }
}
