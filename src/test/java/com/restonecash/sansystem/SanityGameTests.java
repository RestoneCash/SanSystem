package com.restonecash.sansystem;

import com.restonecash.sansystem.api.san.ISanity;
import com.restonecash.sansystem.capability.SanityCapability;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.gametest.GameTestHolder;

@GameTestHolder(SanSystem.MODID)
public class SanityGameTests
{
    @GameTest(template = "empty")
    public void testDecreaseSanityBasic(GameTestHelper helper)
    {
        SanityCapability.register();
        ISanity sanity = new SanityCapability();
        sanity.getCore().setMaxSanity(100.0f);
        sanity.getCore().setSanity(100.0f);

        sanity.getCore().decreaseSanity(4.0f, 20.0f, 1.0f);

        float expected = 100.0f - (4.0f * 20.0f / 4.0f * (1.0f - 1.0f / (20.0f + 1.0f)));
        helper.assertTrue(Math.abs(sanity.getCore().getSanity() - expected) < 0.01f,
                "Expected: " + expected + ", Actual: " + sanity.getCore().getSanity());
        helper.succeed();
    }

    @GameTest(template = "empty")
    public void testDecreaseSanityResilienceEqualsPollution(GameTestHelper helper)
    {
        SanityCapability.register();
        ISanity sanity = new SanityCapability();
        sanity.getCore().setMaxSanity(100.0f);
        sanity.getCore().setSanity(100.0f);

        sanity.getCore().decreaseSanity(4.0f, 20.0f, 20.0f);

        float expected = 100.0f - (4.0f * 20.0f / 4.0f * 0.5f);
        helper.assertTrue(Math.abs(sanity.getCore().getSanity() - expected) < 0.01f,
                "Expected: " + expected + ", Actual: " + sanity.getCore().getSanity());
        helper.succeed();
    }

    @GameTest(template = "empty")
    public void testDecreaseSanityResilienceGreaterThanPollution(GameTestHelper helper)
    {
        SanityCapability.register();
        ISanity sanity = new SanityCapability();
        sanity.getCore().setMaxSanity(100.0f);
        sanity.getCore().setSanity(100.0f);

        sanity.getCore().decreaseSanity(4.0f, 20.0f, 30.0f);

        float expected = 100.0f - (4.0f * 20.0f / 4.0f * (1.0f - 30.0f / 50.0f));
        helper.assertTrue(Math.abs(sanity.getCore().getSanity() - expected) < 0.01f,
                "Expected: " + expected + ", Actual: " + sanity.getCore().getSanity());
        helper.succeed();
    }

    @GameTest(template = "empty")
    public void testDecreaseSanityZeroResilience(GameTestHelper helper)
    {
        SanityCapability.register();
        ISanity sanity = new SanityCapability();
        sanity.getCore().setMaxSanity(100.0f);
        sanity.getCore().setSanity(100.0f);

        sanity.getCore().decreaseSanity(4.0f, 20.0f, 0.0f);

        float expected = 80.0f;
        helper.assertTrue(Math.abs(sanity.getCore().getSanity() - expected) < 0.01f,
                "Expected: " + expected + ", Actual: " + sanity.getCore().getSanity());
        helper.succeed();
    }

    @GameTest(template = "empty")
    public void testDecreaseSanityZeroDenominator(GameTestHelper helper)
    {
        SanityCapability.register();
        ISanity sanity = new SanityCapability();
        sanity.getCore().setMaxSanity(100.0f);
        sanity.getCore().setSanity(100.0f);

        sanity.getCore().decreaseSanity(4.0f, 0.0f, 0.0f);

        float expected = 100.0f;
        helper.assertTrue(Math.abs(sanity.getCore().getSanity() - expected) < 0.01f,
                "Expected: " + expected + ", Actual: " + sanity.getCore().getSanity());
        helper.succeed();
    }

    @GameTest(template = "empty")
    public void testDecreaseSanityCannotGoNegative(GameTestHelper helper)
    {
        SanityCapability.register();
        ISanity sanity = new SanityCapability();
        sanity.getCore().setMaxSanity(100.0f);
        sanity.getCore().setSanity(5.0f);

        sanity.getCore().decreaseSanity(100.0f, 20.0f, 0.0f);

        helper.assertTrue(sanity.getCore().getSanity() == 0.0f,
                "Sanity should be 0, but was: " + sanity.getCore().getSanity());
        helper.succeed();
    }

    @GameTest(template = "empty")
    public void testAddSanityCannotExceedMax(GameTestHelper helper)
    {
        SanityCapability.register();
        ISanity sanity = new SanityCapability();
        sanity.getCore().setMaxSanity(100.0f);
        sanity.getCore().setSanity(90.0f);

        sanity.getCore().addSanity(20.0f);

        helper.assertTrue(sanity.getCore().getSanity() == 100.0f,
                "Sanity should be 100, but was: " + sanity.getCore().getSanity());
        helper.succeed();
    }

    @GameTest(template = "empty")
    public void testTickRecovery(GameTestHelper helper)
    {
        SanityCapability.register();
        ISanity sanity = new SanityCapability();
        sanity.getCore().setMaxSanity(100.0f);
        sanity.getCore().setSanity(90.0f);

        sanity.getCore().tickRecovery(1.0f);

        float expected = 90.0f + 0.01f * 2.0f;
        helper.assertTrue(Math.abs(sanity.getCore().getSanity() - expected) < 0.001f,
                "Expected: " + expected + ", Actual: " + sanity.getCore().getSanity());
        helper.succeed();
    }

    @GameTest(template = "empty")
    public void testChangedFlag(GameTestHelper helper)
    {
        SanityCapability.register();
        ISanity sanity = new SanityCapability();
        sanity.getCore().setMaxSanity(100.0f);
        sanity.getCore().setSanity(100.0f);

        helper.assertFalse(sanity.getSyncTracker().hasChanged(), "Changed should be false initially");

        sanity.getCore().decreaseSanity(4.0f, 20.0f, 1.0f);

        helper.assertTrue(sanity.getSyncTracker().hasChanged(), "Changed should be true after decrease");

        sanity.getSyncTracker().clearChanged();

        helper.assertFalse(sanity.getSyncTracker().hasChanged(), "Changed should be false after clear");
        helper.succeed();
    }
}