package com.restonecash.sansystem.api.san;

/**
 * 理智效果状态接口
 * 管理运行时效果状态（非持久化）
 * 包括：恶心强度、失明强度、输入反转、缓慢层数、宽限期计时器
 */
public interface SanityEffects
{
    // ==================== 效果强度 ====================

    /**
     * 获取恶心强度（0-1）
     */
    float getNauseaIntensity();

    /**
     * 设置恶心强度
     * @param value 强度值（自动限制在 0~1）
     */
    void setNauseaIntensity(float value);

    /**
     * 获取失明强度（0-1）
     */
    float getBlindnessIntensity();

    /**
     * 设置失明强度
     * @param value 强度值（自动限制在 0~1）
     */
    void setBlindnessIntensity(float value);

    // ==================== 输入反转 ====================

    /**
     * 获取输入反转状态
     */
    boolean getInputInverted();

    /**
     * 设置输入反转状态
     */
    void setInputInverted(boolean inverted);

    // ==================== 缓慢层数 ====================

    /**
     * 获取缓慢层数（0-5）
     */
    int getSlownessStacks();

    /**
     * 设置缓慢层数
     * @param stacks 层数（自动限制在 0~5）
     */
    void setSlownessStacks(int stacks);

    /**
     * 获取上次缓慢衰减 tick
     */
    long getLastSlownessDecayTick();

    /**
     * 设置上次缓慢衰减 tick
     */
    void setLastSlownessDecayTick(long tick);

    // ==================== 宽限期计时器 ====================

    /**
     * 获取恶心效果宽限期开始 tick
     */
    long getGracePeriodStartNausea();

    /**
     * 设置恶心效果宽限期开始 tick
     */
    void setGracePeriodStartNausea(long tick);

    /**
     * 获取失明效果宽限期开始 tick
     */
    long getGracePeriodStartBlindness();

    /**
     * 设置失明效果宽限期开始 tick
     */
    void setGracePeriodStartBlindness(long tick);

    /**
     * 获取输入反转宽限期开始 tick
     */
    long getGracePeriodStartInput();

    /**
     * 设置输入反转宽限期开始 tick
     */
    void setGracePeriodStartInput(long tick);

    /**
     * 检查是否在宽限期内
     * @param gracePeriodStart 宽限期开始 tick
     * @param currentTick 当前 tick
     * @return true 如果在宽限期内（当前tick - 开始tick < 100）
     */
    boolean isInGracePeriod(long gracePeriodStart, long currentTick);

    // ==================== 状态重置 ====================

    /**
     * 重置所有运行时状态
     * 用于死亡恢复、san值归零等场景
     */
    void resetRuntimeState();
}
