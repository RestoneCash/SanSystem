package com.restonecash.sansystem.capability;

import com.restonecash.sansystem.api.san.SanityCore;
import com.restonecash.sansystem.capability.SanityTrackerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SanityCoreImpl 单元测试
 * 验证核心值管理逻辑（不依赖 Minecraft API）
 */
public class SanityCoreTest
{
    private SanityCore core;

    @BeforeEach
    void setUp()
    {
        core = new SanityCoreImpl(new SanityTrackerImpl());
        core.setMaxSanity(100.0f);
        core.setSanity(100.0f);
    }

    // ==================== 基本操作测试 ====================

    @Test
    void testSanityValueBounds()
    {
        // 测试上限
        core.setSanity(150.0f);
        assertEquals(100.0f, core.getSanity(), 0.001f);

        // 测试下限
        core.setSanity(-50.0f);
        assertEquals(0.0f, core.getSanity(), 0.001f);

        // 测试正常值
        core.setSanity(50.0f);
        assertEquals(50.0f, core.getSanity(), 0.001f);
    }

    @Test
    void testAddSanity()
    {
        core.setSanity(50.0f);
        core.addSanity(30.0f);
        assertEquals(80.0f, core.getSanity(), 0.001f);

        // 测试上限限制
        core.addSanity(50.0f);
        assertEquals(100.0f, core.getSanity(), 0.001f);

        // 测试负数不生效
        core.addSanity(-10.0f);
        assertEquals(100.0f, core.getSanity(), 0.001f);
    }

    @Test
    void testSetMaxSanity()
    {
        core.setMaxSanity(150.0f);
        assertEquals(150.0f, core.getMaxSanity(), 0.001f);

        // 当前值应保持不变
        assertEquals(100.0f, core.getSanity(), 0.001f);
    }

    @Test
    void testSetMaxSanityClampsCurrent()
    {
        core.setSanity(80.0f);
        core.setMaxSanity(50.0f);

        assertEquals(50.0f, core.getMaxSanity(), 0.001f);
        assertEquals(50.0f, core.getSanity(), 0.001f); // 被限制到最大值
    }

    @Test
    void testMaxSanityBounds()
    {
        core.setMaxSanity(-10.0f);
        assertEquals(0.0f, core.getMaxSanity(), 0.001f);

        core.setMaxSanity(0.0f);
        assertEquals(0.0f, core.getMaxSanity(), 0.001f);
    }

    // ==================== 掉 San 公式测试 ====================

    @Test
    void testDecreaseSanityBasic()
    {
        core.decreaseSanity(4.0f, 20.0f, 1.0f);
        float expected = 100.0f - (4.0f * 20.0f / 4.0f * (1.0f - 1.0f / 21.0f));
        assertEquals(expected, core.getSanity(), 0.01f);
    }

    @Test
    void testDecreaseSanityResilienceEqualsPollution()
    {
        core.decreaseSanity(4.0f, 20.0f, 20.0f);
        assertEquals(95.0f, core.getSanity(), 0.01f);
    }

    @Test
    void testDecreaseSanityResilienceGreaterThanPollution()
    {
        core.decreaseSanity(4.0f, 20.0f, 30.0f);
        assertEquals(96.8f, core.getSanity(), 0.01f);
    }

    @Test
    void testDecreaseSanityZeroResilience()
    {
        core.decreaseSanity(4.0f, 20.0f, 0.0f);
        assertEquals(80.0f, core.getSanity(), 0.01f);
    }

    @Test
    void testDecreaseSanityZeroDenominator()
    {
        core.decreaseSanity(4.0f, 0.0f, 0.0f);
        assertEquals(100.0f, core.getSanity(), 0.01f);
    }

    @Test
    void testDecreaseSanityCannotGoNegative()
    {
        core.setSanity(5.0f);
        core.decreaseSanity(100.0f, 20.0f, 0.0f);
        assertEquals(0.0f, core.getSanity(), 0.01f);
    }

    @Test
    void testDecreaseSanityZeroBaseAmount()
    {
        core.setSanity(50.0f);
        core.decreaseSanity(0.0f, 20.0f, 1.0f);
        assertEquals(50.0f, core.getSanity(), 0.001f);
    }

    // ==================== 恢复测试 ====================

    @Test
    void testTickRecovery()
    {
        core.setSanity(90.0f);
        core.tickRecovery(1.0f);
        float expected = 90.0f + 0.01f * 2.0f;
        assertEquals(expected, core.getSanity(), 0.001f);
    }

    // ==================== 初始化测试 ====================

    @Test
    void testInitializationFlag()
    {
        assertFalse(core.isInitialized());
        core.setInitialized();
        assertTrue(core.isInitialized());
    }
}
