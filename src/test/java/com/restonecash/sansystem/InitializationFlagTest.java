package com.restonecash.sansystem;

import com.restonecash.sansystem.api.san.ISanity;
import com.restonecash.sansystem.capability.SanityCapability;
import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Initialization flag tests - verify that sanity is only initialized once
 */
public class InitializationFlagTest
{
    private ISanity sanity;

    @BeforeEach
    void setUp()
    {
        SanityCapability.register();
        sanity = new SanityCapability();
    }

    @Test
    void testInitialState_NotInitialized()
    {
        assertFalse(sanity.getCore().isInitialized(), "New SanityCapability should not be initialized");
    }

    @Test
    void testSetInitialized_MarksAsInitialized()
    {
        sanity.getCore().setInitialized();
        assertTrue(sanity.getCore().isInitialized(), "After setInitialized(), should be initialized");
    }

    @Test
    void testSetInitialized_PersistedInNBT()
    {
        sanity.getCore().setInitialized();
        sanity.getCore().setMaxSanity(100.0f);
        sanity.getCore().setSanity(80.0f);

        CompoundTag tag = new CompoundTag();
        sanity.saveNBT(tag);

        assertTrue(tag.contains("Initialized"), "NBT should contain Initialized flag");
        assertTrue(tag.getBoolean("Initialized"), "NBT Initialized flag should be true");
    }

    @Test
    void testLoadNBT_RestoresInitializedFlag()
    {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("Initialized", true);
        tag.putFloat("Sanity", 50.0f);
        tag.putFloat("MaxSanity", 100.0f);

        sanity.loadNBT(tag);

        assertTrue(sanity.getCore().isInitialized(), "After loading NBT with Initialized=true, should be initialized");
        assertEquals(50.0f, sanity.getCore().getSanity(), 0.01f);
    }

    @Test
    void testLoadNBT_WithoutInitializedFlag_DefaultsToFalse()
    {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("Sanity", 50.0f);
        tag.putFloat("MaxSanity", 100.0f);

        sanity.loadNBT(tag);

        assertFalse(sanity.getCore().isInitialized(), "If NBT lacks Initialized flag, should default to false");
    }

    @Test
    void testInitializationScenario_FirstJoinOnly()
    {
        // Simulate first join: set default values
        assertFalse(sanity.getCore().isInitialized());
        sanity.getCore().setMaxSanity(100.0f);
        sanity.getCore().setSanity(100.0f);
        sanity.getCore().setInitialized();

        assertTrue(sanity.getCore().isInitialized());
        assertEquals(100.0f, sanity.getCore().getSanity(), 0.01f);

        // Simulate later operations that reduce sanity
        sanity.getCore().setSanity(50.0f);
        assertEquals(50.0f, sanity.getCore().getSanity(), 0.01f);

        // Simulate chunk reload: should NOT reset because initialized flag is true
        if (!sanity.getCore().isInitialized()) {
            sanity.getCore().setSanity(100.0f); // This should NOT happen
        }
        assertEquals(50.0f, sanity.getCore().getSanity(), 0.01f, "Sanity should NOT reset after chunk reload");
    }
}