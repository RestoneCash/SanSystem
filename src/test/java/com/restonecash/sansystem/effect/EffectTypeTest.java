package com.restonecash.sansystem.effect;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * EffectType 枚举测试
 * 验证效果类型的阈值定义
 */
public class EffectTypeTest
{
    @Test
    void testNauseaThreshold()
    {
        assertEquals(0.25f, EffectType.NAUSEA.getThreshold(), 0.001f);
    }

    @Test
    void testBlindnessThreshold()
    {
        assertEquals(0.10f, EffectType.BLINDNESS.getThreshold(), 0.001f);
    }

    @Test
    void testInputInversionThreshold()
    {
        assertEquals(0.15f, EffectType.INPUT_INVERSION.getThreshold(), 0.001f);
    }

    @Test
    void testThresholdOrder()
    {
        // 恶心 > 输入反转 > 失明（按阈值从高到低）
        assertTrue(EffectType.NAUSEA.getThreshold() > EffectType.INPUT_INVERSION.getThreshold());
        assertTrue(EffectType.INPUT_INVERSION.getThreshold() > EffectType.BLINDNESS.getThreshold());
    }

    @Test
    void testEnumValues()
    {
        EffectType[] values = EffectType.values();
        assertEquals(3, values.length);

        // 确认所有枚举值存在
        assertNotNull(EffectType.valueOf("NAUSEA"));
        assertNotNull(EffectType.valueOf("BLINDNESS"));
        assertNotNull(EffectType.valueOf("INPUT_INVERSION"));
    }
}
