package com.restonecash.sansystem.effect;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * EffectState 单元测试
 * 验证效果状态的强度和宽限期计算逻辑
 */
public class EffectStateTest
{
    private EffectState state;

    @BeforeEach
    void setUp()
    {
        state = new EffectState();
    }

    @Test
    void testDefaultState()
    {
        assertEquals(0.0f, state.getIntensity(), 0.001f);
        assertEquals(0L, state.getGracePeriodStart());
        assertFalse(state.isInGracePeriod(100L));
    }

    @Test
    void testIntensityBounds()
    {
        state.setIntensity(1.5f);
        assertEquals(1.0f, state.getIntensity(), 0.001f);

        state.setIntensity(-0.5f);
        assertEquals(0.0f, state.getIntensity(), 0.001f);

        state.setIntensity(0.5f);
        assertEquals(0.5f, state.getIntensity(), 0.001f);
    }

    @Test
    void testGracePeriodStart()
    {
        state.setGracePeriodStart(100L);
        assertEquals(100L, state.getGracePeriodStart());
        assertTrue(state.isInGracePeriod(150L));
        assertFalse(state.isInGracePeriod(50L));
    }

    @Test
    void testGracePeriodNotStarted()
    {
        assertFalse(state.isInGracePeriod(1000L));
    }

    @Test
    void testFadeIntensityCalculation()
    {
        // 宽限期刚开始时，强度为 1.0
        state.setGracePeriodStart(100L);
        assertEquals(1.0f, state.calculateFadeIntensity(100L), 0.001f);

        // 50 tick 后，强度应为 0.5
        assertEquals(0.5f, state.calculateFadeIntensity(150L), 0.001f);

        // 100 tick 后（宽限期结束），强度应为 0.0
        assertEquals(0.0f, state.calculateFadeIntensity(200L), 0.001f);

        // 超过宽限期，强度保持 0.0
        assertEquals(0.0f, state.calculateFadeIntensity(300L), 0.001f);
    }

    @Test
    void testFadeIntensityNoGracePeriod()
    {
        // 没有启动宽限期时，返回 1.0
        assertEquals(1.0f, state.calculateFadeIntensity(100L), 0.001f);
    }

    @Test
    void testReset()
    {
        state.setIntensity(0.8f);
        state.setGracePeriodStart(100L);
        state.reset();

        assertEquals(0.0f, state.getIntensity(), 0.001f);
        assertEquals(0L, state.getGracePeriodStart());
    }
}
