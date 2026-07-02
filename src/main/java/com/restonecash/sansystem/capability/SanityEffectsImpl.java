package com.restonecash.sansystem.capability;

import com.restonecash.sansystem.api.san.SanityEffects;

/**
 * 理智效果状态实现
 * 管理所有运行时效果状态（非持久化到 NBT）
 */
public class SanityEffectsImpl implements SanityEffects
{
    // ==================== 效果强度字段 ====================

    private float nauseaIntensity = 0.0f;
    private float blindnessIntensity = 0.0f;
    private boolean inputInverted = false;
    private int slownessStacks = 0;
    private long lastSlownessDecayTick = 0L;

    // ==================== 宽限期计时器字段 ====================

    private long gracePeriodStartNausea = 0L;
    private long gracePeriodStartBlindness = 0L;
    private long gracePeriodStartInput = 0L;

    // ==================== 效果强度实现 ====================

    @Override
    public float getNauseaIntensity()
    {
        return this.nauseaIntensity;
    }

    @Override
    public void setNauseaIntensity(float value)
    {
        this.nauseaIntensity = Math.max(0.0f, Math.min(value, 1.0f));
    }

    @Override
    public float getBlindnessIntensity()
    {
        return this.blindnessIntensity;
    }

    @Override
    public void setBlindnessIntensity(float value)
    {
        this.blindnessIntensity = Math.max(0.0f, Math.min(value, 1.0f));
    }

    // ==================== 输入反转实现 ====================

    @Override
    public boolean getInputInverted()
    {
        return this.inputInverted;
    }

    @Override
    public void setInputInverted(boolean inverted)
    {
        this.inputInverted = inverted;
    }

    // ==================== 缓慢层数实现 ====================

    @Override
    public int getSlownessStacks()
    {
        return this.slownessStacks;
    }

    @Override
    public void setSlownessStacks(int stacks)
    {
        this.slownessStacks = Math.max(0, Math.min(stacks, 5));
    }

    @Override
    public long getLastSlownessDecayTick()
    {
        return this.lastSlownessDecayTick;
    }

    @Override
    public void setLastSlownessDecayTick(long tick)
    {
        this.lastSlownessDecayTick = tick;
    }

    // ==================== 宽限期计时器实现 ====================

    @Override
    public long getGracePeriodStartNausea()
    {
        return this.gracePeriodStartNausea;
    }

    @Override
    public void setGracePeriodStartNausea(long tick)
    {
        this.gracePeriodStartNausea = tick;
    }

    @Override
    public long getGracePeriodStartBlindness()
    {
        return this.gracePeriodStartBlindness;
    }

    @Override
    public void setGracePeriodStartBlindness(long tick)
    {
        this.gracePeriodStartBlindness = tick;
    }

    @Override
    public long getGracePeriodStartInput()
    {
        return this.gracePeriodStartInput;
    }

    @Override
    public void setGracePeriodStartInput(long tick)
    {
        this.gracePeriodStartInput = tick;
    }

    @Override
    public boolean isInGracePeriod(long gracePeriodStart, long currentTick)
    {
        if (gracePeriodStart <= 0) return false;
        return (currentTick - gracePeriodStart) < 100;
    }

    // ==================== 状态重置实现 ====================

    @Override
    public void resetRuntimeState()
    {
        this.nauseaIntensity = 0.0f;
        this.blindnessIntensity = 0.0f;
        this.inputInverted = false;
        this.slownessStacks = 0;
        this.lastSlownessDecayTick = 0L;
        this.gracePeriodStartNausea = 0L;
        this.gracePeriodStartBlindness = 0L;
        this.gracePeriodStartInput = 0L;
    }
}
