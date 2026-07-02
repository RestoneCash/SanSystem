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
        sanity.setMaxSanity(100.0f);
        sanity.setSanity(100.0f);

        sanity.decreaseSanity(4.0f, 20.0f, 1.0f);

        float expected = 100.0f - (4.0f * 20.0f / 4.0f * (1.0f - 1.0f / (20.0f + 1.0f)));
        helper.assertTrue(Math.abs(sanity.getSanity() - expected) < 0.01f,
                "Expected: " + expected + ", Actual: " + sanity.getSanity());
        helper.succeed();
    }

    @GameTest(template = "empty")
    public void testDecreaseSanityResilienceEqualsPollution(GameTestHelper helper)
    {
        SanityCapability.register();
        ISanity sanity = new SanityCapability();
        sanity.setMaxSanity(100.0f);
        sanity.setSanity(100.0f);

        sanity.decreaseSanity(4.0f, 20.0f, 20.0f);

        float expected = 100.0f - (4.0f * 20.0f / 4.0f * 0.5f);
        helper.assertTrue(Math.abs(sanity.getSanity() - expected) < 0.01f,
                "Expected: " + expected + ", Actual: " + sanity.getSanity());
        helper.succeed();
    }

    @GameTest(template = "empty")
    public void testDecreaseSanityResilienceGreaterThanPollution(GameTestHelper helper)
    {
        SanityCapability.register();
        ISanity sanity = new SanityCapability();
        sanity.setMaxSanity(100.0f);
        sanity.setSanity(100.0f);

        sanity.decreaseSanity(4.0f, 20.0f, 30.0f);

        float expected = 100.0f - (4.0f * 20.0f / 4.0f * (1.0f - 30.0f / 50.0f));
        helper.assertTrue(Math.abs(sanity.getSanity() - expected) < 0.01f,
                "Expected: " + expected + ", Actual: " + sanity.getSanity());
        helper.succeed();
    }

    @GameTest(template = "empty")
    public void testDecreaseSanityZeroResilience(GameTestHelper helper)
    {
        SanityCapability.register();
        ISanity sanity = new SanityCapability();
        sanity.setMaxSanity(100.0f);
        sanity.setSanity(100.0f);

        sanity.decreaseSanity(4.0f, 20.0f, 0.0f);

        float expected = 80.0f;
        helper.assertTrue(Math.abs(sanity.getSanity() - expected) < 0.01f,
                "Expected: " + expected + ", Actual: " + sanity.getSanity());
        helper.succeed();
    }

    @GameTest(template = "empty")
    public void testDecreaseSanityZeroDenominator(GameTestHelper helper)
    {
        SanityCapability.register();
        ISanity sanity = new SanityCapability();
        sanity.setMaxSanity(100.0f);
        sanity.setSanity(100.0f);

        sanity.decreaseSanity(4.0f, 0.0f, 0.0f);

        float expected = 100.0f;
        helper.assertTrue(Math.abs(sanity.getSanity() - expected) < 0.01f,
                "Expected: " + expected + ", Actual: " + sanity.getSanity());
        helper.succeed();
    }

    @GameTest(template = "empty")
    public void testDecreaseSanityCannotGoNegative(GameTestHelper helper)
    {
        SanityCapability.register();
        ISanity sanity = new SanityCapability();
        sanity.setMaxSanity(100.0f);
        sanity.setSanity(5.0f);

        sanity.decreaseSanity(100.0f, 20.0f, 0.0f);

        helper.assertTrue(sanity.getSanity() == 0.0f,
                "Sanity should be 0, but was: " + sanity.getSanity());
        helper.succeed();
    }

    @GameTest(template = "empty")
    public void testAddSanityCannotExceedMax(GameTestHelper helper)
    {
        SanityCapability.register();
        ISanity sanity = new SanityCapability();
        sanity.setMaxSanity(100.0f);
        sanity.setSanity(90.0f);

        sanity.addSanity(20.0f);

        helper.assertTrue(sanity.getSanity() == 100.0f,
                "Sanity should be 100, but was: " + sanity.getSanity());
        helper.succeed();
    }

    @GameTest(template = "empty")
    public void testTickRecovery(GameTestHelper helper)
    {
        SanityCapability.register();
        ISanity sanity = new SanityCapability();
        sanity.setMaxSanity(100.0f);
        sanity.setSanity(90.0f);

        sanity.tickRecovery(1.0f);

        float expected = 90.0f + 0.01f * 2.0f;
        helper.assertTrue(Math.abs(sanity.getSanity() - expected) < 0.001f,
                "Expected: " + expected + ", Actual: " + sanity.getSanity());
        helper.succeed();
    }

    @GameTest(template = "empty")
    public void testChangedFlag(GameTestHelper helper)
    {
        SanityCapability.register();
        ISanity sanity = new SanityCapability();
        sanity.setMaxSanity(100.0f);
        sanity.setSanity(100.0f);

        helper.assertFalse(sanity.hasChanged(), "Changed should be false initially");

        sanity.decreaseSanity(4.0f, 20.0f, 1.0f);

        helper.assertTrue(sanity.hasChanged(), "Changed should be true after decrease");

        sanity.clearChanged();

        helper.assertFalse(sanity.hasChanged(), "Changed should be false after clear");
        helper.succeed();
    }
}