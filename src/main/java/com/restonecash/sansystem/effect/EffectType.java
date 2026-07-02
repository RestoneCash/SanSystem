package com.restonecash.sansystem.effect;

/**
 * 效果类型枚举
 * 定义游戏中所有可能的理智效果类型
 *
 * 【架构说明】
 * 每个效果类型对应一个独立的 EffectApplier 实现
 * 新增效果类型需要：
 * 1. 在此处添加枚举值
 * 2. 实现 EffectApplier 接口
 * 3. 在 EffectRegistry 中注册
 */
public enum EffectType
{
    /** 恶心效果 - 显示屏幕摇晃 */
    NAUSEA(0.25f),

    /** 失明效果 - 完全黑屏 */
    BLINDNESS(0.10f),

    /** 输入反转 - WASD 方向反转 */
    INPUT_INVERSION(0.15f);

    private final float threshold;

    EffectType(float threshold)
    {
        this.threshold = threshold;
    }

    /**
     * 获取触发此效果的 San 值百分比阈值
     */
    public float getThreshold()
    {
        return this.threshold;
    }
}
