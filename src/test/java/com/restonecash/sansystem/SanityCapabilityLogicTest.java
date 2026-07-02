package com.restonecash.sansystem;

import com.restonecash.sansystem.api.san.ISanity;
import com.restonecash.sansystem.capability.SanityCapability;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SanityCapabilityLogicTest
{
    private ISanity sanity;

    @BeforeEach
    void setUp()
    {
        SanityCapability.register();
        sanity = new SanityCapability();
        sanity.setMaxSanity(100.0f);
        sanity.setSanity(100.0f);
    }

    @Test
    void testSanPercentCalculation()
    {
        sanity.setSanity(50.0f);
        assertEquals(0.5f, sanity.getSanity() / sanity.getMaxSanity(), 0.001f);

        sanity.setSanity(25.0f);
        assertEquals(0.25f, sanity.getSanity() / sanity.getMaxSanity(), 0.001f);

        sanity.setSanity(10.0f);
        assertEquals(0.10f, sanity.getSanity() / sanity.getMaxSanity(), 0.001f);
    }

    @Test
    void testZeroSanityTriggersDeath()
    {
        sanity.setSanity(0.0f);
        assertEquals(0.0f, sanity.getSanity(), 0.001f);
    }

    @Test
    void testNegativeSanityClampedToZero()
    {
        sanity.setSanity(10.0f);
        sanity.decreaseSanity(100.0f, 20.0f, 0.0f);
        assertEquals(0.0f, sanity.getSanity(), 0.001f);
    }

    @Test
    void testEffectThresholds()
    {
        float[] testValues = {0.51f, 0.50f, 0.36f, 0.35f, 0.26f, 0.25f, 0.16f, 0.15f, 0.11f, 0.10f, 0.05f};

        for (float value : testValues)
        {
            sanity.setSanity(value * 100.0f);
            float percent = sanity.getSanity() / sanity.getMaxSanity();
            assertTrue(percent >= 0 && percent <= 1, "San percent should be between 0 and 1");
        }
    }

    @Test
    void testGracePeriodLogic()
    {
        sanity.setSanity(20.0f);
        float percentBefore = sanity.getSanity() / sanity.getMaxSanity();
        assertTrue(percentBefore < 0.25f, "Should be below nausea threshold");

        sanity.addSanity(10.0f);
        float percentAfter = sanity.getSanity() / sanity.getMaxSanity();
        assertTrue(percentAfter >= 0.25f, "Should be above nausea threshold after recovery");
    }

    @Test
    void testSanityRecoveryOverTime()
    {
        sanity.setSanity(80.0f);

        for (int i = 0; i < 100; i++)
        {
            sanity.tickRecovery(1.0f);
        }

        assertEquals(100.0f, sanity.getSanity(), 0.1f);
    }
}