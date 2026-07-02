package com.restonecash.sansystem.capability;

import com.restonecash.sansystem.api.san.SanityCore;

/**
 * 理智核心模块实现
 * 封装持久化的核心值管理逻辑
 * 不包含运行时状态（NBT 不保存）和同步标记
 */
public class SanityCoreImpl implements SanityCore
{
    // ==================== 持久化字段（NBT 保存）====================
    private float sanity = 100.0f;
    private float maxSanity = 100.0f;
    private boolean initialized = false;

    // ==================== 基础恢复常量 ====================

    private static final float BASE_RECOVERY_PER_TICK = 0.01f;

    // ==================== 核心值操作实现 ====================

    @Override
    public float getSanity()
    {
        return this.sanity;
    }

    @Override
    public void setSanity(float value)
    {
        this.sanity = Math.max(0.0f, Math.min(value, this.maxSanity));
    }

    @Override
    public float getMaxSanity()
    {
        return this.maxSanity;
    }

    @Override
    public void setMaxSanity(float value)
    {
        this.maxSanity = Math.max(0.0f, value);
        this.sanity = Math.max(0.0f, Math.min(this.sanity, this.maxSanity));
    }

    @Override
    public void addSanity(float amount)
    {
        if (amount <= 0) return;
        setSanity(this.sanity + amount);
    }

    @Override
    public void decreaseSanity(float baseAmount, float attackerPollution, float defenderResilience)
    {
        if (baseAmount <= 0) return;

        float denominator = attackerPollution + defenderResilience;
        if (denominator <= 0) {
            setSanity(this.sanity - baseAmount * attackerPollution / 4);
            return;
        }

        float reduction = 1.0f - defenderResilience / denominator;
        float actualDrop = baseAmount * attackerPollution / 4 * reduction;
        setSanity(this.sanity - actualDrop);
    }

    @Override
    public void tickRecovery(float mentalRecovery)
    {
        float recovery = BASE_RECOVERY_PER_TICK * (1.0f + mentalRecovery);
        addSanity(recovery);
    }

    // ==================== 初始化状态实现 ====================

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
