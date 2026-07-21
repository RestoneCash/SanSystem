package com.restonecash.sansystem;

import com.restonecash.sansystem.api.san.ISanity;
import com.restonecash.sansystem.capability.SanityCapability;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Formula verification tests - validate the sanity drop formula matches specification
 * Formula: drop = baseAmount × attackerPollution ÷ 4 × (1 - defenderResilience ÷ (attackerPollution + defenderResilience))
 */
public class FormulaVerificationTest
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
    void testFormulaManualCalculation()
    {
        // Given: damage=4, pollution=20, resilience=1
        float damage = 4.0f;
        float pollution = 20.0f;
        float resilience = 1.0f;

        // Manual calculation following formula:
        // Step 1: pollution + resilience = 21
        float denominator = pollution + resilience;

        // Step 2: reduction factor = 1 - resilience/denominator = 1 - 1/21 ≈ 0.9524
        float reduction = 1.0f - resilience / denominator;

        // Step 3: base drop = damage × pollution ÷ 4 = 4 × 20 ÷ 4 = 20
        float baseDrop = damage * pollution / 4.0f;

        // Step 4: actual drop = baseDrop × reduction = 20 × 0.9524 ≈ 19.05
        float expectedDrop = baseDrop * reduction;

        // Expected sanity after drop: 100 - 19.05 ≈ 80.95
        float expectedSanity = 100.0f - expectedDrop;

        // When
        sanity.getCore().decreaseSanity(damage, pollution, resilience);

        // Then
        assertEquals(expectedSanity, sanity.getCore().getSanity(), 0.1f,
            "Sanity should drop according to formula: damage × pollution ÷ 4 × reduction");
    }

    @Test
    void testFormulaEdgeCase_ResilienceMuchGreaterThanPollution()
    {
        // Edge case: resilience >> pollution (high defense)
        float damage = 10.0f;
        float pollution = 5.0f;    // Low pollution attacker
        float resilience = 50.0f;  // High resilience defender

        // Manual calculation:
        // denominator = 5 + 50 = 55
        // reduction = 1 - 50/55 = 1 - 0.909 = 0.091
        // baseDrop = 10 × 5 ÷ 4 = 12.5
        // actualDrop = 12.5 × 0.091 ≈ 1.14

        float expectedDrop = (damage * pollution / 4.0f) * (1.0f - resilience / (pollution + resilience));
        float expectedSanity = 100.0f - expectedDrop;

        sanity.getCore().decreaseSanity(damage, pollution, resilience);

        // High resilience should significantly reduce sanity drop
        assertTrue(sanity.getCore().getSanity() > 95.0f,
            "High resilience (50) vs low pollution (5) should result in minimal sanity drop");
        assertEquals(expectedSanity, sanity.getCore().getSanity(), 0.5f);
    }

    @Test
    void testFormulaEdgeCase_PollutionMuchGreaterThanResilience()
    {
        // Edge case: pollution >> resilience (high offense)
        float damage = 5.0f;
        float pollution = 50.0f;   // High pollution attacker
        float resilience = 1.0f;   // Low resilience defender

        // Manual calculation:
        // denominator = 50 + 1 = 51
        // reduction = 1 - 1/51 ≈ 0.980
        // baseDrop = 5 × 50 ÷ 4 = 62.5
        // actualDrop = 62.5 × 0.980 ≈ 61.25

        float expectedDrop = (damage * pollution / 4.0f) * (1.0f - resilience / (pollution + resilience));
        float expectedSanity = 100.0f - expectedDrop;

        sanity.getCore().decreaseSanity(damage, pollution, resilience);

        // High pollution should cause significant sanity drop
        assertTrue(sanity.getCore().getSanity() < 40.0f,
            "High pollution (50) vs low resilience (1) should cause significant sanity drop");
        assertEquals(expectedSanity, sanity.getCore().getSanity(), 1.0f);
    }

    @Test
    void testFormulaEdgeCase_EqualPollutionAndResilience()
    {
        // Edge case: pollution = resilience (neutral)
        float damage = 8.0f;
        float pollution = 20.0f;
        float resilience = 20.0f;

        // Manual calculation:
        // denominator = 20 + 20 = 40
        // reduction = 1 - 20/40 = 0.5
        // baseDrop = 8 × 20 ÷ 4 = 40
        // actualDrop = 40 × 0.5 = 20

        sanity.getCore().decreaseSanity(damage, pollution, resilience);

        assertEquals(80.0f, sanity.getCore().getSanity(), 0.01f,
            "Equal pollution and resilience should result in 50% reduction");
    }
}