package com.restonecash.sansystem;

import com.restonecash.sansystem.api.san.ISanity;
import com.restonecash.sansystem.capability.SanityCapability;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SanityCapabilityTest
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
    void testDecreaseSanityBasic()
    {
        sanity.decreaseSanity(4.0f, 20.0f, 1.0f);
        float expected = 100.0f - (4.0f * 20.0f / 4.0f * (1.0f - 1.0f / 21.0f));
        assertEquals(expected, sanity.getSanity(), 0.01f);
    }

    @Test
    void testDecreaseSanityResilienceEqualsPollution()
    {
        sanity.decreaseSanity(4.0f, 20.0f, 20.0f);
        float expected = 95.0f;
        assertEquals(expected, sanity.getSanity(), 0.01f);
    }

    @Test
    void testDecreaseSanityResilienceGreaterThanPollution()
    {
        sanity.decreaseSanity(4.0f, 20.0f, 30.0f);
        float expected = 96.8f;
        assertEquals(expected, sanity.getSanity(), 0.01f);
    }

    @Test
    void testDecreaseSanityZeroResilience()
    {
        sanity.decreaseSanity(4.0f, 20.0f, 0.0f);
        assertEquals(80.0f, sanity.getSanity(), 0.01f);
    }

    @Test
    void testDecreaseSanityZeroDenominator()
    {
        sanity.decreaseSanity(4.0f, 0.0f, 0.0f);
        assertEquals(100.0f, sanity.getSanity(), 0.01f);
    }

    @Test
    void testDecreaseSanityCannotGoNegative()
    {
        sanity.setSanity(5.0f);
        sanity.decreaseSanity(100.0f, 20.0f, 0.0f);
        assertEquals(0.0f, sanity.getSanity(), 0.01f);
    }

    @Test
    void testAddSanityCannotExceedMax()
    {
        sanity.setSanity(90.0f);
        sanity.addSanity(20.0f);
        assertEquals(100.0f, sanity.getSanity(), 0.01f);
    }

    @Test
    void testTickRecovery()
    {
        sanity.setSanity(90.0f);
        sanity.tickRecovery(1.0f);
        float expected = 90.0f + 0.01f * 2.0f;
        assertEquals(expected, sanity.getSanity(), 0.001f);
    }

    @Test
    void testChangedFlag()
    {
        assertFalse(sanity.hasChanged());
        sanity.decreaseSanity(4.0f, 20.0f, 1.0f);
        assertTrue(sanity.hasChanged());
        sanity.clearChanged();
        assertFalse(sanity.hasChanged());
    }

    @Test
    void testSetMaxSanityTriggersChanged()
    {
        assertFalse(sanity.hasChanged());
        sanity.setMaxSanity(150.0f);
        assertTrue(sanity.hasChanged());
    }
}