package com.restonecash.sansystem.effect;

import com.restonecash.sansystem.api.san.SanityEffects;
import com.restonecash.sansystem.api.san.SanityTracker;
import com.restonecash.sansystem.capability.SanityEffectsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SanityEffects 接口行为测试
 * 通过 SanityEffectsImpl 验证公共接口契约
 */
public class SanityEffectsTest
{
    private SanityEffects effects;
    private final SanityTracker stubTracker = new SanityTracker()
    {
        @Override public boolean hasChanged() { return false; }
        @Override public void clearChanged() {}
        @Override public void markChanged() {}
    };

    @BeforeEach
    void setUp()
    {
        effects = new SanityEffectsImpl(stubTracker);
    }

    @Test
    void testDefaultValues()
    {
        assertEquals(0.0f, effects.getNauseaIntensity(), 0.001f);
        assertEquals(0.0f, effects.getBlindnessIntensity(), 0.001f);
        assertFalse(effects.getInputInverted());
        assertEquals(0, effects.getSlownessStacks());
        assertEquals(0L, effects.getLastSlownessDecayTick());
        assertEquals(0L, effects.getGracePeriodStartNausea());
        assertEquals(0L, effects.getGracePeriodStartBlindness());
        assertEquals(0L, effects.getGracePeriodStartInput());
    }

    @Test
    void testNauseaIntensityClampsToBounds()
    {
        effects.setNauseaIntensity(1.5f);
        assertEquals(1.0f, effects.getNauseaIntensity(), 0.001f);

        effects.setNauseaIntensity(-0.5f);
        assertEquals(0.0f, effects.getNauseaIntensity(), 0.001f);

        effects.setNauseaIntensity(0.5f);
        assertEquals(0.5f, effects.getNauseaIntensity(), 0.001f);
    }

    @Test
    void testBlindnessIntensityClampsToBounds()
    {
        effects.setBlindnessIntensity(1.5f);
        assertEquals(1.0f, effects.getBlindnessIntensity(), 0.001f);

        effects.setBlindnessIntensity(-0.5f);
        assertEquals(0.0f, effects.getBlindnessIntensity(), 0.001f);

        effects.setBlindnessIntensity(0.5f);
        assertEquals(0.5f, effects.getBlindnessIntensity(), 0.001f);
    }

    @Test
    void testSlownessStacksClampsToBounds()
    {
        effects.setSlownessStacks(10);
        assertEquals(5, effects.getSlownessStacks());

        effects.setSlownessStacks(-3);
        assertEquals(0, effects.getSlownessStacks());

        effects.setSlownessStacks(3);
        assertEquals(3, effects.getSlownessStacks());
    }

    @Test
    void testInputInvertedGetterSetter()
    {
        assertFalse(effects.getInputInverted());

        effects.setInputInverted(true);
        assertTrue(effects.getInputInverted());

        effects.setInputInverted(false);
        assertFalse(effects.getInputInverted());
    }

    @Test
    void testLastSlownessDecayTickGetterSetter()
    {
        assertEquals(0L, effects.getLastSlownessDecayTick());

        effects.setLastSlownessDecayTick(100L);
        assertEquals(100L, effects.getLastSlownessDecayTick());
    }

    @Test
    void testGracePeriodStartGetterSetter()
    {
        effects.setGracePeriodStartNausea(100L);
        assertEquals(100L, effects.getGracePeriodStartNausea());

        effects.setGracePeriodStartBlindness(200L);
        assertEquals(200L, effects.getGracePeriodStartBlindness());

        effects.setGracePeriodStartInput(300L);
        assertEquals(300L, effects.getGracePeriodStartInput());
    }

    @Test
    void testIsInGracePeriodGracePeriodNotStarted()
    {
        assertFalse(effects.isInGracePeriod(0L, 100L));
        assertFalse(effects.isInGracePeriod(-1L, 100L));
    }

    @Test
    void testResetRuntimeStateResetsNauseaIntensity()
    {
        effects.setNauseaIntensity(0.8f);
        effects.resetRuntimeState();
        assertEquals(0.0f, effects.getNauseaIntensity(), 0.001f);
    }

    @Test
    void testResetRuntimeStateResetsBlindnessIntensity()
    {
        effects.setBlindnessIntensity(0.8f);
        effects.resetRuntimeState();
        assertEquals(0.0f, effects.getBlindnessIntensity(), 0.001f);
    }

    @Test
    void testResetRuntimeStateResetsInputInverted()
    {
        effects.setInputInverted(true);
        effects.resetRuntimeState();
        assertFalse(effects.getInputInverted());
    }

    @Test
    void testResetRuntimeStateResetsSlowness()
    {
        effects.setSlownessStacks(3);
        effects.setLastSlownessDecayTick(100L);
        effects.resetRuntimeState();
        assertEquals(0, effects.getSlownessStacks());
        assertEquals(0L, effects.getLastSlownessDecayTick());
    }

    @Test
    void testResetRuntimeStateResetsGracePeriods()
    {
        effects.setGracePeriodStartNausea(100L);
        effects.setGracePeriodStartBlindness(200L);
        effects.setGracePeriodStartInput(300L);
        effects.resetRuntimeState();
        assertEquals(0L, effects.getGracePeriodStartNausea());
        assertEquals(0L, effects.getGracePeriodStartBlindness());
        assertEquals(0L, effects.getGracePeriodStartInput());
    }
}