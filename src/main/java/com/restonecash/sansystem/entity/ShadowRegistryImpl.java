package com.restonecash.sansystem.entity;

import com.restonecash.sansystem.registries.ShadowRegistry;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 影怪注册表实现类
 * 使用单例模式，管理玩家与影怪实体之间的映射关系
 *
 * 【实现说明】
 * - 使用 ConcurrentHashMap 保证线程安全
 * - 每个玩家的影怪集合使用 ConcurrentHashMap.newKeySet() 创建
 * - 提供 discard() 方法调用支持，用于清理时移除实体
 *
 * 【线程安全】
 * - 所有方法使用同步集合操作
 * - 遍历操作使用 new HashSet<>() 创建快照，避免并发修改异常
 */
public class ShadowRegistryImpl implements ShadowRegistry
{
    // ==================== 单例实例 ====================
    private static final ShadowRegistryImpl INSTANCE = new ShadowRegistryImpl();

    /**
     * 获取单例实例
     */
    public static ShadowRegistryImpl getInstance()
    {
        return INSTANCE;
    }

    // ==================== 注册表数据结构 ====================
    /**
     * 玩家UUID -> 影怪实体集合的映射
     * 使用 ConcurrentHashMap 保证线程安全
     */
    private final Map<UUID, Set<ShadowEntity>> playerShadows = new ConcurrentHashMap<>();

    // ==================== 注册/注销方法 ====================

    @Override
    public void registerShadow(UUID playerId, ShadowEntity shadow)
    {
        playerShadows.computeIfAbsent(playerId, k -> ConcurrentHashMap.newKeySet()).add(shadow);
    }

    @Override
    public void unregisterShadow(UUID playerId, ShadowEntity shadow)
    {
        Set<ShadowEntity> shadows = playerShadows.get(playerId);
        if (shadows != null)
        {
            shadows.remove(shadow);
            // 如果玩家的影怪集合为空，移除该玩家条目
            if (shadows.isEmpty())
            {
                playerShadows.remove(playerId);
            }
        }
    }

    // ==================== 查询方法 ====================

    @Override
    public Set<ShadowEntity> getShadowsForPlayer(UUID playerId)
    {
        Set<ShadowEntity> shadows = playerShadows.get(playerId);
        return shadows != null ? shadows : new HashSet<>();
    }

    @Override
    public int getShadowCount(UUID playerId)
    {
        Set<ShadowEntity> shadows = playerShadows.get(playerId);
        return shadows != null ? shadows.size() : 0;
    }

    @Override
    public boolean hasShadows(UUID playerId)
    {
        return getShadowCount(playerId) > 0;
    }

    // ==================== 清理方法 ====================

    @Override
    public void clearPlayerShadows(UUID playerId)
    {
        Set<ShadowEntity> shadows = playerShadows.remove(playerId);
        if (shadows != null)
        {
            // 使用快照遍历，避免并发修改异常
            for (ShadowEntity shadow : new HashSet<>(shadows))
            {
                shadow.discard();
            }
        }
    }

    @Override
    public void clearAllShadows()
    {
        // 使用快照遍历，避免并发修改异常
        for (Set<ShadowEntity> shadows : new HashSet<>(playerShadows.values()))
        {
            for (ShadowEntity shadow : new HashSet<>(shadows))
            {
                shadow.discard();
            }
        }
        playerShadows.clear();
    }
}
