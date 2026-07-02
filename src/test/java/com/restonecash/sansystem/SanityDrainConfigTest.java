package com.restonecash.sansystem;

import com.restonecash.sansystem.api.san.ISanity;
import com.restonecash.sansystem.capability.SanityCapability;
import com.restonecash.sansystem.config.ServerConfig;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Sanity drain configuration tests - verify item and block drain configs
 */
public class SanityDrainConfigTest
{
    private ISanity sanity;

    @BeforeEach
    void setUp()
    {
        SanityCapability.register();
        sanity = new SanityCapability();
        sanity.getCore().setMaxSanity(100.0f);
        sanity.getCore().setSanity(100.0f);

        // Populate config for testing
        ServerConfig.itemSanityDrain.clear();
        ServerConfig.blockSanityDrain.clear();

        ServerConfig.itemSanityDrain.put(Items.ENDER_EYE, new ServerConfig.SanityDrainConfig(20, 1.0f));
        ServerConfig.itemSanityDrain.put(Items.ENDER_PEARL, new ServerConfig.SanityDrainConfig(40, 0.5f));
        ServerConfig.itemSanityDrain.put(Items.TOTEM_OF_UNDYING, new ServerConfig.SanityDrainConfig(60, 0.3f));

        ServerConfig.blockSanityDrain.put(Blocks.END_STONE, new ServerConfig.SanityDrainConfig(40, 1.0f));
        ServerConfig.blockSanityDrain.put(Blocks.OBSIDIAN, new ServerConfig.SanityDrainConfig(60, 0.5f));
        ServerConfig.blockSanityDrain.put(Blocks.NETHERRACK, new ServerConfig.SanityDrainConfig(80, 0.3f));
    }

    @Test
    void testItemDrainConfig_Retrieval()
    {
        ServerConfig.SanityDrainConfig enderEyeConfig = ServerConfig.getItemSanityDrain(Items.ENDER_EYE);
        assertNotNull(enderEyeConfig, "ENDER_EYE should have drain config");
        assertEquals(20, enderEyeConfig.ticksPerDrain());
        assertEquals(1.0f, enderEyeConfig.sanityPerDrain(), 0.01f);

        ServerConfig.SanityDrainConfig enderPearlConfig = ServerConfig.getItemSanityDrain(Items.ENDER_PEARL);
        assertNotNull(enderPearlConfig);
        assertEquals(40, enderPearlConfig.ticksPerDrain());
        assertEquals(0.5f, enderPearlConfig.sanityPerDrain(), 0.01f);

        ServerConfig.SanityDrainConfig appleConfig = ServerConfig.getItemSanityDrain(Items.APPLE);
        assertNull(appleConfig, "Items without config should return null");
    }

    @Test
    void testBlockDrainConfig_Retrieval()
    {
        ServerConfig.SanityDrainConfig endStoneConfig = ServerConfig.getBlockSanityDrain(Blocks.END_STONE);
        assertNotNull(endStoneConfig, "END_STONE should have drain config");
        assertEquals(40, endStoneConfig.ticksPerDrain());
        assertEquals(1.0f, endStoneConfig.sanityPerDrain(), 0.01f);

        ServerConfig.SanityDrainConfig obsidianConfig = ServerConfig.getBlockSanityDrain(Blocks.OBSIDIAN);
        assertNotNull(obsidianConfig);
        assertEquals(60, obsidianConfig.ticksPerDrain());
        assertEquals(0.5f, obsidianConfig.sanityPerDrain(), 0.01f);

        ServerConfig.SanityDrainConfig stoneConfig = ServerConfig.getBlockSanityDrain(Blocks.STONE);
        assertNull(stoneConfig, "Blocks without config should return null");
    }

    @Test
    void testDrainConfig_RecordStructure()
    {
        ServerConfig.SanityDrainConfig config = new ServerConfig.SanityDrainConfig(100, 2.5f);
        assertEquals(100, config.ticksPerDrain(), "ticksPerDrain should match");
        assertEquals(2.5f, config.sanityPerDrain(), 0.01f, "sanityPerDrain should match");
    }

    @Test
    void testDrainLogic_SimulatedItemDrain()
    {
        // Simulate holding ender eye for 20 ticks
        ServerConfig.SanityDrainConfig config = ServerConfig.getItemSanityDrain(Items.ENDER_EYE);
        assertNotNull(config);

        // After 20 ticks, drain should occur
        float sanityPerDrain = config.sanityPerDrain();

        // Simulate one drain cycle
        sanity.setSanity(sanity.getSanity() - sanityPerDrain);
        assertEquals(99.0f, sanity.getSanity(), 0.01f, "Sanity should decrease by 1.0 after one drain cycle");
    }

    @Test
    void testDrainLogic_SimulatedBlockDrain()
    {
        // Simulate standing on end stone for 40 ticks
        ServerConfig.SanityDrainConfig config = ServerConfig.getBlockSanityDrain(Blocks.END_STONE);
        assertNotNull(config);

        float sanityPerDrain = config.sanityPerDrain();
        sanity.getCore().setSanity(sanity.getCore().etSanity() - sanityPerDrain);
        assertEquals(99.0f, sanity.getCore().getSanity(), 0.01f, "Sanity should decrease by 1.0 after one drain cycle");
    }

    @Test
    void testDrainLogic_MultipleDrains()
    {
        // Simulate 10 drain cycles of ender eye
        ServerConfig.SanityDrainConfig config = ServerConfig.getItemSanityDrain(Items.ENDER_EYE);
        float sanityPerDrain = config.sanityPerDrain();

        for (int i = 0; i < 10; i++) {
            sanity.getCore().setSanity(sanity.getCore().getSanity() - sanityPerDrain);
        }

        assertEquals(90.0f, sanity.getCore().getSanity(), 0.01f, "After 10 drains of 1.0, sanity should be 90");
    }

    @Test
    void testDrainLogic_CannotGoNegative()
    {
        ServerConfig.SanityDrainConfig config = ServerConfig.getItemSanityDrain(Items.ENDER_EYE);
        float sanityPerDrain = config.sanityPerDrain();

        // Drain 200 times (would go to -100 without clamping)
        for (int i = 0; i < 200; i++) {
            sanity.getCore().setSanity(sanity.getCore().getSanity() - sanityPerDrain);
        }

        assertEquals(0.0f, sanity.getCore().getSanity(), 0.01f, "Sanity should be clamped to 0, not negative");
    }
}