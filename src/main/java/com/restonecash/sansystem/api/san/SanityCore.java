package com.restonecash.sansystem.api.san;

/**
 * 理智核心模块接口
 * 负责持久化的核心值管理，与运行时状态分离
 * 注意：此类不依赖 Minecraft API，可在纯 Java 环境中测试
 */
public interface SanityCore
{
    // ==================== 核心值操作 ====================

    /**
     * 获取当前 San 值
     */
    float getSanity();

    /**
     * 设置 San 值（自动限制在 0~最大值之间）
     * @param value 新的 San 值
     */
    void setSanity(float value);

    /**
     * 获取最大 San 值
     */
    float getMaxSanity();

    /**
     * 设置最大 San 值（同时调整当前值不超过新最大值）
     * @param value 新的最大值
     */
    void setMaxSanity(float value);

    /**
     * 增加 San 值（正数）
     * @param amount 增加量
     */
    void addSanity(float amount);

    /**
     * 扣除 San 值（考虑韧性减免）
     * @param baseAmount 基础伤害值
     * @param attackerPollution 攻击方污染值
     * @param defenderResilience 防御方韧性值
     */
    void decreaseSanity(float baseAmount, float attackerPollution, float defenderResilience);

    /**
     * 每 tick 恢复 San 值
     * @param mentalRecovery 精神恢复属性值
     */
    void tickRecovery(float mentalRecovery);

    // ==================== 初始化状态 ====================

    /**
     * 是否已完成首次初始化
     */
    boolean isInitialized();

    /**
     * 标记为已初始化
     */
    void setInitialized();
}
