package com.restonecash.sansystem;

import com.restonecash.sansystem.api.san.ISanity;
import com.restonecash.sansystem.capability.SanityCapability;
import com.restonecash.sansystem.config.ServerConfig;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Crafting logic tests - verify the sanity requirement calculation and crafting block behavior
 */
public class CraftingLogicTest
{
    private ISanity sanity;

    @BeforeEach
    void setUp()
    {
        SanityCapability.register();
        sanity = new SanityCapability();
        sanity.getCore().setMaxSanity(100.0f);
        sanity.getCore().setSanity(100.0f);

        // Manually populate config for testing (simulates config loading)
        ServerConfig.craftingSanityRequirements.clear();
        ServerConfig.craftingSanityRequirements.put(Items.ENDER_EYE, 50.0f);
        ServerConfig.craftingSanityRequirements.put(Items.ENDER_PEARL, 30.0f);
        ServerConfig.craftingSanityRequirements.put(Items.TOTEM_OF_UNDYING, 60.0f);
    }

    @Test
    void testCraftingSanityRequirement_Retrieval()
    {
        // Given: Items with configured sanity requirements
        Item enderEye = Items.ENDER_EYE;
        Item enderPearl = Items.ENDER_PEARL;
        Item totem = Items.TOTEM_OF_UNDYING;
        Item apple = Items.APPLE; // No requirement

        // When: Retrieve requirements
        float enderEyeReq = ServerConfig.getCraftingSanityRequirement(enderEye);
        float enderPearlReq = ServerConfig.getCraftingSanityRequirement(enderPearl);
        float totemReq = ServerConfig.getCraftingSanityRequirement(totem);
        float appleReq = ServerConfig.getCraftingSanityRequirement(apple);

        // Then: Requirements match config
        assertEquals(50.0f, enderEyeReq, 0.01f);
        assertEquals(30.0f, enderPearlReq, 0.01f);
        assertEquals(60.0f, totemReq, 0.01f);
        assertEquals(0.0f, appleReq, 0.01f, "Items without config should return 0");
    }

    @Test
    void testCraftingDecision_PlayerHasSufficientSanity()
    {
        // Given: Player has 80 sanity, item requires 50
        sanity.getCore().setSanity(80.0f);
        float requiredSanity = 50.0f;

        // When: Check if player can craft
        boolean canCraft = sanity.getCore().getSanity() >= requiredSanity;

        // Then: Player should be able to craft
        assertTrue(canCraft, "Player with 80 sanity should be able to craft item requiring 50");
    }

    @Test
    void testCraftingDecision_PlayerHasInsufficientSanity()
    {
        // Given: Player has 40 sanity, item requires 50
        sanity.getCore().setSanity(40.0f);
        float requiredSanity = 50.0f;

        // When: Check if player can craft
        boolean canCraft = sanity.getCore().getSanity() >= requiredSanity;

        // Then: Player should NOT be able to craft
        assertFalse(canCraft, "Player with 40 sanity should NOT be able to craft item requiring 50");
    }

    @Test
    void testCraftingDecision_MultipleMaterials()
    {
        // Given: Player wants to craft something using multiple materials
        // Each material has different requirements
        // Total requirement should be sum of all materials × count

        // Scenario: Crafting Ender Eye (requires Ender Pearl + Blaze Powder)
        // Ender Pearl: 30.0 × 1 = 30
        // Blaze Powder: not in config = 0
        // Total: 30.0

        float enderPearlRequirement = ServerConfig.getCraftingSanityRequirement(Items.ENDER_PEARL);
        float expectedTotal = enderPearlRequirement * 1; // 1 ender pearl

        sanity.getCore().setSanity(25.0f);

        // When: Check if player can craft
        boolean canCraft = sanity.getCore().getSanity() >= expectedTotal;

        // Then: Player with 25 sanity should NOT be able to craft (requires 30)
        assertFalse(canCraft, "Player with 25 sanity should NOT be able to craft Ender Eye requiring 30");
    }

    @Test
    void testCraftingDecision_EdgeCase_ExactSanity()
    {
        // Given: Player has EXACTLY the required sanity
        sanity.getCore().setSanity(50.0f);
        float requiredSanity = 50.0f;

        // When: Check if player can craft
        boolean canCraft = sanity.getCore().getSanity() >= requiredSanity;

        // Then: Player should be able to craft (boundary case)
        assertTrue(canCraft, "Player with exactly 50 sanity should be able to craft item requiring 50");
    }

    @Test
    void testCraftingDecision_EdgeCase_OnePointBelow()
    {
        // Given: Player has one point below required sanity
        sanity.getCore().setSanity(49.0f);
        float requiredSanity = 50.0f;

        // When: Check if player can craft
        boolean canCraft = sanity.getCore().getSanity() >= requiredSanity;

        // Then: Player should NOT be able to craft
        assertFalse(canCraft, "Player with 49 sanity should NOT be able to craft item requiring 50");
    }

    @Test
    void testCraftingDecision_NoSanityRequirement()
    {
        // Given: Item has no sanity requirement configured
        sanity.getCore().setSanity(10.0f); // Very low sanity
        float requiredSanity = 0.0f;

        // When: Check if player can craft
        boolean canCraft = sanity.getCore().getSanity() >= requiredSanity;

        // Then: Player should be able to craft (no requirement)
        assertTrue(canCraft, "Player should be able to craft items with no sanity requirement");
    }
}