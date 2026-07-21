package com.restonecash.sansystem.capability;

import com.restonecash.sansystem.api.san.SanityCore;
import com.restonecash.sansystem.api.san.SanityTracker;
import com.restonecash.sansystem.config.ServerConfig;

/**
 * 理智核心模块实现
 * 封装持久化的核心值管理逻辑
 * 不包含运行时状态（NBT 不保存）和同步标记
 */
public class SanityCoreImpl implements SanityCore
{
    private float sanity = 100.0f;
    private float maxSanity = 100.0f;
    private boolean initialized = false;

    private final SanityTracker tracker;

    public SanityCoreImpl(SanityTracker tracker)
    {
        this.tracker = tracker;
    }

    @Override
    public float getSanity()
    {
        return this.sanity;
    }

    @Override
    public void setSanity(float value)
    {
        float old = this.sanity;
        this.sanity = Math.max(0.0f, Math.min(value, this.maxSanity));
        if (old != this.sanity) this.tracker.markChanged();
    }

    @Override
    public float getMaxSanity()
    {
        return this.maxSanity;
    }

    @Override
    public void setMaxSanity(float value)
    {
        float old = this.maxSanity;
        this.maxSanity = Math.max(0.0f, value);
        this.sanity = Math.max(0.0f, Math.min(this.sanity, this.maxSanity));
        if (old != this.maxSanity) this.tracker.markChanged();
    }

    @Override
    public void addSanity(float amount)
    {
        if (amount <= 0) return;
        float old = this.sanity;
        setSanity(this.sanity + amount);
        if (old != this.sanity) this.tracker.markChanged();
    }

    @Override
    public void decreaseSanity(float baseAmount, float attackerPollution, float defenderResilience)
    {
        if (baseAmount <= 0) return;

        float denominator = attackerPollution + defenderResilience;
        float actualDrop;
        if (denominator <= 0) {
            actualDrop = baseAmount * attackerPollution / 4;
        } else {
            float reduction = 1.0f - defenderResilience / denominator;
            actualDrop = baseAmount * attackerPollution / 4 * reduction;
        }

        if (actualDrop <= 0) return;
        float old = this.sanity;
        setSanity(this.sanity - actualDrop);
        if (old != this.sanity) this.tracker.markChanged();
    }

    @Override
    public void tickRecovery(float mentalRecovery)
    {
        float recovery = ServerConfig.baseRecoveryPerTick * (1.0f + mentalRecovery);
        if (recovery <= 0) return;
        float old = this.sanity;
        addSanity(recovery);
        if (old != this.sanity) this.tracker.markChanged();
    }

    @Override
    public boolean isInitialized()
    {
        return this.initialized;
    }

    @Override
    public void setInitialized()
    {
        this.initialized = true;
    }
}
