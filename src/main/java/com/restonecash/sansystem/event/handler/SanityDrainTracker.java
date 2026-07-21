package com.restonecash.sansystem.event.handler;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 理智流失跟踪器
 * 封装理智流失的冷却跟踪逻辑，支持多玩家、多来源隔离
 *
 * 【设计说明】
 * 此跟踪器是纯 Java 实现，不依赖任何 Minecraft API，可在纯 Java 环境中测试
 *
 * 【重要设计决策】
 * 不使用内部 tick 计数器，而是直接使用外部传入的 currentTick 参数，因为：
 * - LivingTickEvent 每个实体触发一次，内部计数器会按实体数量递增
 * - 游戏时间（level.getGameTime()）是全局统一的时钟
 * - 使用外部 tick 确保冷却时间与游戏时间同步，不受实体数量影响
 *
 * 核心功能：
 * 1. 跟踪每个玩家每个来源的上次流失时间
 * 2. 判断冷却是否过期，决定是否执行流失
 * 3. 统计累计流失量
 * 4. 提供玩家清理接口（断开连接时调用）
 *
 * 使用方式：
 * 1. 在事件处理器中创建实例
 * 2. 在来源检测逻辑中调用 tryDrain(playerId, sourceId, currentTick, cooldownTicks, drainAmount)
 * 3. 在玩家离开时调用 removePlayer() 清理数据
 */
public class SanityDrainTracker
{
    /**
     * 玩家冷却映射：playerId → (sourceId → lastDrainTick)
     * 存储每个玩家每个来源的上次流失时间（游戏 tick）
     */
    private final Map<UUID, Map<String, Long>> playerSourceLastDrainTick = new ConcurrentHashMap<>();

    /**
     * 玩家累计流失量映射：playerId → totalDrained
     * 统计每个玩家的累计理智流失量
     */
    private final Map<UUID, Float> playerTotalDrained = new ConcurrentHashMap<>();

    /**
     * 尝试执行理智流失
     * 判断冷却是否过期，若过期则执行流失并返回true
     *
     * 【参数说明】
     * currentTick 必须从游戏世界获取（level.getGameTime()），
     * 确保所有玩家使用统一的时钟，避免冷却时间偏差
     *
     * @param playerId 玩家UUID
     * @param sourceId 流失来源ID（如物品/方块的资源位置字符串）
     * @param currentTick 当前游戏tick（从 level.getGameTime() 获取）
     * @param cooldownTicks 冷却间隔（tick数）
     * @param drainAmount 流失量
     * @return 是否执行了流失
     */
    public boolean tryDrain(UUID playerId, String sourceId, long currentTick,
                            long cooldownTicks, float drainAmount)
    {
        if (playerId == null || sourceId == null || sourceId.trim().isEmpty() || cooldownTicks <= 0)
        {
            return false;
        }

        // 获取或创建玩家的来源映射
        Map<String, Long> sourceTicks = playerSourceLastDrainTick.computeIfAbsent(playerId, k -> new ConcurrentHashMap<>());

        // 获取上次流失时间（默认为0）
        long lastTick = sourceTicks.getOrDefault(sourceId, 0L);

        // 判断冷却是否过期
        if (currentTick - lastTick >= cooldownTicks)
        {
            // 更新上次流失时间
            sourceTicks.put(sourceId, currentTick);

            // 累加累计流失量
            playerTotalDrained.compute(playerId, (k, v) -> (v == null ? 0.0f : v) + drainAmount);

            return true;
        }

        return false;
    }

    /**
     * 移除玩家数据
     * 在玩家断开连接时调用，清理该玩家的所有冷却记录
     *
     * @param playerId 玩家UUID
     */
    public void removePlayer(UUID playerId)
    {
        if (playerId == null) return;

        playerSourceLastDrainTick.remove(playerId);
        playerTotalDrained.remove(playerId);
    }

    /**
     * 获取玩家累计流失量
     *
     * @param playerId 玩家UUID
     * @return 累计流失量，不存在则返回0
     */
    public float getTotalDrained(UUID playerId)
    {
        if (playerId == null) return 0.0f;

        return playerTotalDrained.getOrDefault(playerId, 0.0f);
    }

    /**
     * 重置跟踪器
     * 清除所有数据，恢复初始状态
     */
    public void reset()
    {
        playerSourceLastDrainTick.clear();
        playerTotalDrained.clear();
    }

    /**
     * 获取指定来源的上次流失时间
     * 主要用于测试和调试
     *
     * @param playerId 玩家UUID
     * @param sourceId 来源ID
     * @return 上次流失tick，不存在则返回0
     */
    public long getLastDrainTick(UUID playerId, String sourceId)
    {
        if (playerId == null || sourceId == null) return 0L;

        Map<String, Long> sourceTicks = playerSourceLastDrainTick.get(playerId);
        if (sourceTicks == null) return 0L;

        return sourceTicks.getOrDefault(sourceId, 0L);
    }
}
