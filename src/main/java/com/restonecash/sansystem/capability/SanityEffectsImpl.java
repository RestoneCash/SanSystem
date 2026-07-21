package com.restonecash.sansystem.capability;

import com.restonecash.sansystem.api.san.SanityEffects;
import com.restonecash.sansystem.api.san.SanityTracker;
import com.restonecash.sansystem.config.ServerConfig;

/**
 * 理智效果状态实现
 * 管理所有运行时效果状态（非持久化到 NBT）
 */
public class SanityEffectsImpl implements SanityEffects
{
    private float nauseaIntensity = 0.0f;
    private float blindnessIntensity = 0.0f;
    private boolean inputInverted = false;
    private int slownessStacks = 0;
    private long lastSlownessDecayTick = 0L;

    private long gracePeriodStartNausea = 0L;
    private long gracePeriodStartBlindness = 0L;
    private long gracePeriodStartInput = 0L;

    private final SanityTracker tracker;

    public SanityEffectsImpl(SanityTracker tracker)
    {
        this.tracker = tracker;
    }

    @Override
    public float getNauseaIntensity()
    {
        return this.nauseaIntensity;
    }

    @Override
    public void setNauseaIntensity(float value)
    {
        float old = this.nauseaIntensity;
        this.nauseaIntensity = Math.max(0.0f, Math.min(value, 1.0f));
        if (old != this.nauseaIntensity) this.tracker.markChanged();
    }

    @Override
    public float getBlindnessIntensity()
    {
        return this.blindnessIntensity;
    }

    @Override
    public void setBlindnessIntensity(float value)
    {
        float old = this.blindnessIntensity;
        this.blindnessIntensity = Math.max(0.0f, Math.min(value, 1.0f));
        if (old != this.blindnessIntensity) this.tracker.markChanged();
    }

    @Override
    public boolean getInputInverted()
    {
        return this.inputInverted;
    }

    @Override
    public void setInputInverted(boolean inverted)
    {
        if (this.inputInverted != inverted) {
            this.inputInverted = inverted;
            this.tracker.markChanged();
        }
    }

    @Override
    public int getSlownessStacks()
    {
        return this.slownessStacks;
    }

    @Override
    public void setSlownessStacks(int stacks)
    {
        int old = this.slownessStacks;
        this.slownessStacks = Math.max(0, Math.min(stacks, 5));
        if (old != this.slownessStacks) this.tracker.markChanged();
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
        return (currentTick - gracePeriodStart) < ServerConfig.gracePeriodTicks;
    }

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
