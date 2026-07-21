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
        sanity.getCore().setMaxSanity(100.0f);
        sanity.getCore().setSanity(100.0f);
    }

    @Test
    void testDecreaseSanityBasic()
    {
        sanity.getCore().decreaseSanity(4.0f, 20.0f, 1.0f);
        float expected = 100.0f - (4.0f * 20.0f / 4.0f * (1.0f - 1.0f / 21.0f));
        assertEquals(expected, sanity.getCore().getSanity(), 0.01f);
    }

    @Test
    void testDecreaseSanityResilienceEqualsPollution()
    {
        sanity.getCore().decreaseSanity(4.0f, 20.0f, 20.0f);
        float expected = 95.0f;
        assertEquals(expected, sanity.getCore().getSanity(), 0.01f);
    }

    @Test
    void testDecreaseSanityResilienceGreaterThanPollution()
    {
        sanity.getCore().decreaseSanity(4.0f, 20.0f, 30.0f);
        float expected = 96.8f;
        assertEquals(expected, sanity.getCore().getSanity(), 0.01f);
    }

    @Test
    void testDecreaseSanityZeroResilience()
    {
        sanity.getCore().decreaseSanity(4.0f, 20.0f, 0.0f);
        assertEquals(80.0f, sanity.getCore().getSanity(), 0.01f);
    }

    @Test
    void testDecreaseSanityZeroDenominator()
    {
        sanity.getCore().decreaseSanity(4.0f, 0.0f, 0.0f);
        assertEquals(100.0f, sanity.getCore().getSanity(), 0.01f);
    }

    @Test
    void testDecreaseSanityCannotGoNegative()
    {
        sanity.getCore().setSanity(5.0f);
        sanity.getCore().decreaseSanity(100.0f, 20.0f, 0.0f);
        assertEquals(0.0f, sanity.getCore().getSanity(), 0.01f);
    }

    @Test
    void testAddSanityCannotExceedMax()
    {
        sanity.getCore().setSanity(90.0f);
        sanity.getCore().addSanity(20.0f);
        assertEquals(100.0f, sanity.getCore().getSanity(), 0.01f);
    }

    @Test
    void testTickRecovery()
    {
        sanity.getCore().setSanity(90.0f);
        sanity.getCore().tickRecovery(1.0f);
        float expected = 90.0f + 0.01f * 2.0f;
        assertEquals(expected, sanity.getCore().getSanity(), 0.001f);
    }

    @Test
    void testChangedFlag()
    {
        assertFalse(sanity.getSyncTracker().hasChanged());
        sanity.getCore().decreaseSanity(4.0f, 20.0f, 1.0f);
        assertTrue(sanity.getSyncTracker().hasChanged());
        sanity.getSyncTracker().clearChanged();
        assertFalse(sanity.getSyncTracker().hasChanged());
    }

    @Test
    void testSetMaxSanityTriggersChanged()
    {
        assertFalse(sanity.getSyncTracker().hasChanged());
        sanity.getCore().setMaxSanity(150.0f);
        assertTrue(sanity.getSyncTracker().hasChanged());
    }
}