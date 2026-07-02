package com.restonecash.sansystem.effect;

/**
 * 效果状态封装
 * 封装单个效果的强度和宽限期计时器
 *
 * 【设计说明】
 * 每个效果有独立的强度和宽限期计时器
 * 强度从 0.0 到 1.0，表示效果的强烈程度
 * 宽限期用于 San 值回升后效果的渐变淡出
 */
public class EffectState
{
    // 效果强度（0-1）
    private float intensity = 0.0f;

    // 宽限期开始 tick（0 表示不在宽限期内）
    private long gracePeriodStart = 0L;

    // 宽限期持续时间（tick）
    private static final long GRACE_PERIOD_TICKS = 100L;

    /**
     * 获取效果强度
     */
    public float getIntensity()
    {
        return this.intensity;
    }

    /**
     * 设置效果强度
     * @param value 强度值（自动限制在 0~1）
     */
    public void setIntensity(float value)
    {
        this.intensity = Math.max(0.0f, Math.min(value, 1.0f));
    }

    /**
     * 获取宽限期开始 tick
     */
    public long getGracePeriodStart()
    {
        return this.gracePeriodStart;
    }

    /**
     * 设置宽限期开始 tick
     * @param tick 游戏 tick
     */
    public void setGracePeriodStart(long tick)
    {
        this.gracePeriodStart = tick;
    }

    /**
     * 检查是否在宽限期内
     * @param currentTick 当前 tick
     * @return true 如果在宽限期内
     */
    public boolean isInGracePeriod(long currentTick)
    {
        if (this.gracePeriodStart <= 0) return false;
        return (currentTick - this.gracePeriodStart) < GRACE_PERIOD_TICKS;
    }

    /**
     * 计算当前渐变强度
     * 在宽限期内从 1.0 线性衰减到 0.0
     * @param currentTick 当前 tick
     * @return 渐变强度（0.0 ~ 1.0）
     */
    public float calculateFadeIntensity(long currentTick)
    {
        if (this.gracePeriodStart <= 0)
        {
            return 1.0f;
        }

        long ticksSinceGraceStart = currentTick - this.gracePeriodStart;

        if (ticksSinceGraceStart < GRACE_PERIOD_TICKS)
        {
            return 1.0f - (float) ticksSinceGraceStart / GRACE_PERIOD_TICKS;
        }
        else
        {
            return 0.0f;
        }
    }

    /**
     * 重置状态
     */
    public void reset()
    {
        this.intensity = 0.0f;
        this.gracePeriodStart = 0L;
    }
}
