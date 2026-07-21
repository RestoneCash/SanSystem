package com.restonecash.sansystem.entity;

import com.restonecash.sansystem.registries.ShadowRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ShadowRegistryImpl 单元测试
 * 验证注册表的注册、注销、查询、清理逻辑
 *
 * 【测试说明】
 * 此类不依赖 Minecraft API，可在纯 Java 环境中运行
 * 使用 MockShadowEntity 模拟真实影怪实体行为
 */
public class ShadowRegistryTest
{
    private ShadowRegistry registry;
    private UUID playerId;
    private MockShadowEntity shadow1;
    private MockShadowEntity shadow2;

    @BeforeEach
    void setUp()
    {
        // 使用新的注册表实例进行测试
        registry = new ShadowRegistryImpl();
        playerId = UUID.randomUUID();
        shadow1 = new MockShadowEntity();
        shadow2 = new MockShadowEntity();
    }

    // ==================== 注册/注销测试 ====================

    @Test
    void testRegisterShadow()
    {
        // 初始状态：没有影怪
        assertEquals(0, registry.getShadowCount(playerId));
        assertFalse(registry.hasShadows(playerId));

        // 注册第一个影怪
        registry.registerShadow(playerId, shadow1);
        assertEquals(1, registry.getShadowCount(playerId));
        assertTrue(registry.hasShadows(playerId));

        // 注册第二个影怪
        registry.registerShadow(playerId, shadow2);
        assertEquals(2, registry.getShadowCount(playerId));
    }

    @Test
    void testUnregisterShadow()
    {
        // 注册两个影怪
        registry.registerShadow(playerId, shadow1);
        registry.registerShadow(playerId, shadow2);
        assertEquals(2, registry.getShadowCount(playerId));

        // 注销第一个影怪
        registry.unregisterShadow(playerId, shadow1);
        assertEquals(1, registry.getShadowCount(playerId));
        assertTrue(registry.hasShadows(playerId));

        // 注销第二个影怪
        registry.unregisterShadow(playerId, shadow2);
        assertEquals(0, registry.getShadowCount(playerId));
        assertFalse(registry.hasShadows(playerId));
    }

    @Test
    void testUnregisterNonExistentShadow()
    {
        // 尝试注销不存在的影怪，不应抛出异常
        assertDoesNotThrow(() -> registry.unregisterShadow(playerId, shadow1));
        assertEquals(0, registry.getShadowCount(playerId));
    }

    @Test
    void testUnregisterFromNonExistentPlayer()
    {
        // 尝试从不存在的玩家注销影怪，不应抛出异常
        assertDoesNotThrow(() -> registry.unregisterShadow(UUID.randomUUID(), shadow1));
    }

    // ==================== 查询测试 ====================

    @Test
    void testGetShadowsForPlayer()
    {
        Set<ShadowEntity> shadows = registry.getShadowsForPlayer(playerId);
        assertNotNull(shadows);
        assertTrue(shadows.isEmpty());

        // 注册影怪后查询
        registry.registerShadow(playerId, shadow1);
        shadows = registry.getShadowsForPlayer(playerId);
        assertEquals(1, shadows.size());
        assertTrue(shadows.contains(shadow1));
    }

    @Test
    void testGetShadowsForNonExistentPlayer()
    {
        Set<ShadowEntity> shadows = registry.getShadowsForPlayer(UUID.randomUUID());
        assertNotNull(shadows);
        assertTrue(shadows.isEmpty());
    }

    // ==================== 清理测试 ====================

    @Test
    void testClearPlayerShadows()
    {
        // 注册两个影怪
        registry.registerShadow(playerId, shadow1);
        registry.registerShadow(playerId, shadow2);
        assertEquals(2, registry.getShadowCount(playerId));

        // 清除玩家的所有影怪
        registry.clearPlayerShadows(playerId);

        // 验证注册表为空
        assertEquals(0, registry.getShadowCount(playerId));
        assertFalse(registry.hasShadows(playerId));
    }

    @Test
    void testClearPlayerShadowsNonExistentPlayer()
    {
        // 尝试清除不存在玩家的影怪，不应抛出异常
        assertDoesNotThrow(() -> registry.clearPlayerShadows(UUID.randomUUID()));
    }

    @Test
    void testClearAllShadows()
    {
        UUID playerId2 = UUID.randomUUID();
        MockShadowEntity shadow3 = new MockShadowEntity();

        // 在两个玩家下注册影怪
        registry.registerShadow(playerId, shadow1);
        registry.registerShadow(playerId, shadow2);
        registry.registerShadow(playerId2, shadow3);

        assertEquals(2, registry.getShadowCount(playerId));
        assertEquals(1, registry.getShadowCount(playerId2));

        // 清除所有影怪
        registry.clearAllShadows();

        // 验证注册表为空
        assertEquals(0, registry.getShadowCount(playerId));
        assertEquals(0, registry.getShadowCount(playerId2));
    }

    @Test
    void testClearAllShadowsEmpty()
    {
        // 尝试清除空注册表，不应抛出异常
        assertDoesNotThrow(() -> registry.clearAllShadows());
    }

    // ==================== 边界测试 ====================

    @Test
    void testRegisterSameShadowMultipleTimes()
    {
        // 同一影怪多次注册应只计数一次
        registry.registerShadow(playerId, shadow1);
        registry.registerShadow(playerId, shadow1);
        registry.registerShadow(playerId, shadow1);

        assertEquals(1, registry.getShadowCount(playerId));
    }

    @Test
    void testShadowMultiplePlayers()
    {
        UUID playerId2 = UUID.randomUUID();

        // 同一影怪注册到不同玩家
        registry.registerShadow(playerId, shadow1);
        registry.registerShadow(playerId2, shadow1);

        assertEquals(1, registry.getShadowCount(playerId));
        assertEquals(1, registry.getShadowCount(playerId2));
    }

    // ==================== 内部类：Mock 影怪实体 ====================

    /**
     * Mock 影怪实体，用于测试 discard() 调用
     */
    private static class MockShadowEntity extends ShadowEntity
    {
        public MockShadowEntity()
        {
            super(null, null);
        }
    }
}
