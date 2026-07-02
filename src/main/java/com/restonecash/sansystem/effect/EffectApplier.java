package com.restonecash.sansystem.effect;

import net.minecraft.world.entity.player.Player;

/**
 * 效果应用器接口
 * 定义如何将效果应用到玩家
 *
 * 【设计说明】
 * 每个效果类型对应一个 EffectApplier 实现
 * 这是一个策略模式的应用，允许：
 * 1. 效果的检测和触发条件在 EffectManager 中集中管理
 * 2. 效果的视觉/游戏效果通过 EffectApplier 独立实现
 * 3. 新增效果只需实现此接口，无需修改中央逻辑
 */
public interface EffectApplier
{
    /**
     * 获取对应的效果类型
     */
    EffectType getEffectType();

    /**
     * 应用效果到玩家
     * @param player 目标玩家
     * @param intensity 效果强度（0-1）
     */
    void apply(Player player, float intensity);

    /**
     * 移除效果
     * @param player 目标玩家
     */
    void remove(Player player);

    /**
     * 检查玩家是否已有此效果
     * @param player 目标玩家
     * @return true 如果玩家有此效果
     */
    boolean hasEffect(Player player);
}
