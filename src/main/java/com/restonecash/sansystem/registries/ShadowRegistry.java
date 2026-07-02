package com.restonecash.sansystem.registries;

import com.restonecash.sansystem.entity.ShadowEntity;

import java.util.Set;
import java.util.UUID;

/**
 * 影怪注册表接口
 * 负责管理玩家与影怪实体之间的映射关系
 *
 * 【设计说明】
 * 此接口将注册表逻辑从 ShadowEntity 中分离出来，实现职责单一化：
 * - ShadowEntity：专注于 AI 行为、攻击逻辑、渲染等实体特性
 * - ShadowRegistry：专注于生命周期管理、玩家-影怪映射
 *
 * 此接口不依赖 Minecraft API，可在纯 Java 环境中测试
 */
public interface ShadowRegistry
{
    /**
     * 注册影怪到指定玩家
     * @param playerId 玩家 UUID
     * @param shadow 影怪实体
     */
    void registerShadow(UUID playerId, ShadowEntity shadow);

    /**
     * 从指定玩家注销影怪
     * @param playerId 玩家 UUID
     * @param shadow 影怪实体
     */
    void unregisterShadow(UUID playerId, ShadowEntity shadow);

    /**
     * 获取指定玩家的所有影怪实体
     * @param playerId 玩家 UUID
     * @return 影怪实体集合（空集合如果没有）
     */
    Set<ShadowEntity> getShadowsForPlayer(UUID playerId);

    /**
     * 清除指定玩家的所有影怪
     * 会调用每个影怪的 discard() 方法
     * @param playerId 玩家 UUID
     */
    void clearPlayerShadows(UUID playerId);

    /**
     * 清除所有影怪
     * 会调用每个影怪的 discard() 方法
     * 用于世界卸载等场景
     */
    void clearAllShadows();

    /**
     * 获取指定玩家的影怪数量
     * @param playerId 玩家 UUID
     * @return 影怪数量
     */
    int getShadowCount(UUID playerId);

    /**
     * 检查指定玩家是否有任何影怪
     * @param playerId 玩家 UUID
     * @return true 如果有影怪
     */
    boolean hasShadows(UUID playerId);
}
