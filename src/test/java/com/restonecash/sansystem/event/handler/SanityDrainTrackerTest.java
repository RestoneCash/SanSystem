package com.restonecash.sansystem.event.handler;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SanityDrainTracker 单元测试
 * 验证理智流失跟踪器的冷却逻辑（纯 Java，无 Minecraft 依赖）
 */
public class SanityDrainTrackerTest
{
    // ==================== 冷却逻辑测试 ====================

    @Test
    void testTryDrain_CooldownNotExpired()
    {
        SanityDrainTracker tracker = new SanityDrainTracker();
        UUID playerId = UUID.randomUUID();
        String sourceId = "test:item1";
        long cooldownTicks = 20;
        float drainAmount = 1.0f;

        // 首次调用应该成功
        boolean drained1 = tracker.tryDrain(playerId, sourceId, 100, cooldownTicks, drainAmount);
        assertTrue(drained1);

        // 冷却未过期时不应流失（只过了 10 tick）
        boolean drained2 = tracker.tryDrain(playerId, sourceId, 110, cooldownTicks, drainAmount);
        assertFalse(drained2);

        // 再次调用仍不应流失
        boolean drained3 = tracker.tryDrain(playerId, sourceId, 115, cooldownTicks, drainAmount);
        assertFalse(drained3);
    }

    @Test
    void testTryDrain_CooldownExpired()
    {
        SanityDrainTracker tracker = new SanityDrainTracker();
        UUID playerId = UUID.randomUUID();
        String sourceId = "test:item1";
        long cooldownTicks = 20;
        float drainAmount = 1.0f;

        // 首次调用成功（tick 100）
        assertTrue(tracker.tryDrain(playerId, sourceId, 100, cooldownTicks, drainAmount));

        // 冷却刚好过期（tick 120）
        assertTrue(tracker.tryDrain(playerId, sourceId, 120, cooldownTicks, drainAmount));

        // 再过一个冷却周期（tick 140）
        assertTrue(tracker.tryDrain(playerId, sourceId, 140, cooldownTicks, drainAmount));
    }

    // ==================== 多玩家隔离测试 ====================

    @Test
    void testMultiplePlayers_Isolated()
    {
        SanityDrainTracker tracker = new SanityDrainTracker();
        UUID player1 = UUID.randomUUID();
        UUID player2 = UUID.randomUUID();
        String sourceId = "test:item1";
        long cooldownTicks = 20;
        float drainAmount = 1.0f;

        // 玩家1首次调用成功（tick 100）
        assertTrue(tracker.tryDrain(player1, sourceId, 100, cooldownTicks, drainAmount));

        // 玩家1冷却未过期（tick 110）
        assertFalse(tracker.tryDrain(player1, sourceId, 110, cooldownTicks, drainAmount));

        // 玩家2不受影响，应该成功（tick 110）
        assertTrue(tracker.tryDrain(player2, sourceId, 110, cooldownTicks, drainAmount));

        // 玩家2冷却未过期（tick 115）
        assertFalse(tracker.tryDrain(player2, sourceId, 115, cooldownTicks, drainAmount));
    }

    // ==================== 多来源隔离测试 ====================

    @Test
    void testMultipleSources_Isolated()
    {
        SanityDrainTracker tracker = new SanityDrainTracker();
        UUID playerId = UUID.randomUUID();
        long cooldownTicks = 20;
        float drainAmount = 1.0f;

        // 来源1首次调用成功（tick 100）
        assertTrue(tracker.tryDrain(playerId, "test:item1", 100, cooldownTicks, drainAmount));

        // 来源1冷却未过期（tick 110）
        assertFalse(tracker.tryDrain(playerId, "test:item1", 110, cooldownTicks, drainAmount));

        // 来源2不受影响，应该成功（tick 110）
        assertTrue(tracker.tryDrain(playerId, "test:item2", 110, cooldownTicks, drainAmount));

        // 来源2冷却未过期（tick 115）
        assertFalse(tracker.tryDrain(playerId, "test:item2", 115, cooldownTicks, drainAmount));

        // 来源3也不受影响（tick 115）
        assertTrue(tracker.tryDrain(playerId, "test:block1", 115, cooldownTicks, drainAmount));
    }

    // ==================== 玩家清理测试 ====================

    @Test
    void testRemovePlayer_ClearsCooldowns()
    {
        SanityDrainTracker tracker = new SanityDrainTracker();
        UUID playerId = UUID.randomUUID();
        String sourceId = "test:item1";
        long cooldownTicks = 20;
        float drainAmount = 1.0f;

        // 首次调用成功（tick 100）
        assertTrue(tracker.tryDrain(playerId, sourceId, 100, cooldownTicks, drainAmount));

        // 冷却未过期（tick 110）
        assertFalse(tracker.tryDrain(playerId, sourceId, 110, cooldownTicks, drainAmount));

        // 移除玩家
        tracker.removePlayer(playerId);

        // 移除后应该可以再次流失（tick 110）
        assertTrue(tracker.tryDrain(playerId, sourceId, 110, cooldownTicks, drainAmount));
    }

    @Test
    void testRemovePlayer_NonExistent()
    {
        SanityDrainTracker tracker = new SanityDrainTracker();
        UUID playerId = UUID.randomUUID();

        // 移除不存在的玩家不应抛异常
        assertDoesNotThrow(() -> tracker.removePlayer(playerId));
    }

    // ==================== 边界条件测试 ====================

    @Test
    void testTryDrain_NegativeCooldown()
    {
        SanityDrainTracker tracker = new SanityDrainTracker();
        UUID playerId = UUID.randomUUID();

        // 负冷却应该立即流失（每次都能流失，因为差值 >= 负数）
        assertTrue(tracker.tryDrain(playerId, "test:item", 100, -1, 1.0f));
        assertTrue(tracker.tryDrain(playerId, "test:item", 100, -1, 1.0f));
    }

    @Test
    void testTryDrain_ZeroCooldown()
    {
        SanityDrainTracker tracker = new SanityDrainTracker();
        UUID playerId = UUID.randomUUID();

        // 零冷却应该每次都流失（同一个 tick 多次调用也会流失）
        assertTrue(tracker.tryDrain(playerId, "test:item", 100, 0, 1.0f));
        // 注意：零冷却且相同 tick 时，100 - 100 = 0 >= 0，所以会再次流失
        assertTrue(tracker.tryDrain(playerId, "test:item", 100, 0, 1.0f));
    }

    @Test
    void testTryDrain_NullPlayerId()
    {
        SanityDrainTracker tracker = new SanityDrainTracker();

        // null玩家ID应该被拒绝
        assertFalse(tracker.tryDrain(null, "test:item", 100, 20, 1.0f));
    }

    @Test
    void testTryDrain_NullSourceId()
    {
        SanityDrainTracker tracker = new SanityDrainTracker();
        UUID playerId = UUID.randomUUID();

        // null来源ID应该被拒绝
        assertFalse(tracker.tryDrain(playerId, null, 100, 20, 1.0f));
    }

    @Test
    void testTryDrain_EmptySourceId()
    {
        SanityDrainTracker tracker = new SanityDrainTracker();
        UUID playerId = UUID.randomUUID();

        // 空来源ID应该被拒绝
        assertFalse(tracker.tryDrain(playerId, "", 100, 20, 1.0f));
        assertFalse(tracker.tryDrain(playerId, "   ", 100, 20, 1.0f));
    }

    // ==================== 累计流失测试 ====================

    @Test
    void testGetTotalDrained()
    {
        SanityDrainTracker tracker = new SanityDrainTracker();
        UUID playerId = UUID.randomUUID();

        // 累计流失量应该正确统计（使用零冷却以确保每次都流失）
        assertTrue(tracker.tryDrain(playerId, "test:item1", 100, 0, 1.0f));
        assertTrue(tracker.tryDrain(playerId, "test:item1", 100, 0, 2.0f));
        assertTrue(tracker.tryDrain(playerId, "test:item2", 100, 0, 3.0f));

        assertEquals(6.0f, tracker.getTotalDrained(playerId), 0.001f);
    }

    @Test
    void testGetTotalDrained_NonExistentPlayer()
    {
        SanityDrainTracker tracker = new SanityDrainTracker();
        UUID playerId = UUID.randomUUID();

        // 不存在的玩家应该返回0
        assertEquals(0.0f, tracker.getTotalDrained(playerId), 0.001f);
    }

    // ==================== 重置测试 ====================

    @Test
    void testReset()
    {
        SanityDrainTracker tracker = new SanityDrainTracker();
        UUID playerId = UUID.randomUUID();

        // 添加数据
        assertTrue(tracker.tryDrain(playerId, "test:item1", 100, 20, 1.0f));

        // 重置后应该清除所有数据
        tracker.reset();

        // 可以再次流失（因为上次流失时间被重置为0）
        assertTrue(tracker.tryDrain(playerId, "test:item1", 100, 20, 1.0f));
        assertEquals(1.0f, tracker.getTotalDrained(playerId), 0.001f);
    }

    // ==================== 上次流失时间测试 ====================

    @Test
    void testGetLastDrainTick()
    {
        SanityDrainTracker tracker = new SanityDrainTracker();
        UUID playerId = UUID.randomUUID();
        String sourceId = "test:item1";

        // 初始状态应该返回0
        assertEquals(0L, tracker.getLastDrainTick(playerId, sourceId));

        // 流失后应该更新
        tracker.tryDrain(playerId, sourceId, 100, 20, 1.0f);
        assertEquals(100L, tracker.getLastDrainTick(playerId, sourceId));

        // 再过一个冷却周期
        tracker.tryDrain(playerId, sourceId, 120, 20, 1.0f);
        assertEquals(120L, tracker.getLastDrainTick(playerId, sourceId));
    }

    // ==================== 游戏时间一致性测试 ====================
    // 验证使用外部 currentTick 时，多个玩家在同一个游戏tick内调用
    // 不会影响彼此的冷却判断

    @Test
    void testGameTimeConsistency()
    {
        SanityDrainTracker tracker = new SanityDrainTracker();
        UUID player1 = UUID.randomUUID();
        UUID player2 = UUID.randomUUID();
        String sourceId = "test:item";
        long cooldownTicks = 20;

        // 同一个游戏 tick（100）内，两个玩家都调用
        assertTrue(tracker.tryDrain(player1, sourceId, 100, cooldownTicks, 1.0f));
        assertTrue(tracker.tryDrain(player2, sourceId, 100, cooldownTicks, 1.0f));

        // 同一个游戏 tick（110）内，两个玩家都还在冷却中
        assertFalse(tracker.tryDrain(player1, sourceId, 110, cooldownTicks, 1.0f));
        assertFalse(tracker.tryDrain(player2, sourceId, 110, cooldownTicks, 1.0f));

        // 游戏 tick 到 120（刚好过了冷却时间）
        assertTrue(tracker.tryDrain(player1, sourceId, 120, cooldownTicks, 1.0f));
        assertTrue(tracker.tryDrain(player2, sourceId, 120, cooldownTicks, 1.0f));

        // 验证上次流失时间都是 120
        assertEquals(120L, tracker.getLastDrainTick(player1, sourceId));
        assertEquals(120L, tracker.getLastDrainTick(player2, sourceId));
    }
}
