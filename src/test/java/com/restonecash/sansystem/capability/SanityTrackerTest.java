package com.restonecash.sansystem.capability;

import com.restonecash.sansystem.api.san.SanityTracker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SanityTrackerImpl 单元测试
 * 验证脏标记追踪逻辑（不依赖 Minecraft API）
 */
public class SanityTrackerTest
{
    private SanityTracker tracker;

    @BeforeEach
    void setUp()
    {
        tracker = new SanityTrackerImpl();
    }

    @Test
    void testInitialState()
    {
        assertFalse(tracker.hasChanged());
    }

    @Test
    void testMarkChanged()
    {
        assertFalse(tracker.hasChanged());
        tracker.markChanged();
        assertTrue(tracker.hasChanged());
    }

    @Test
    void testClearChanged()
    {
        tracker.markChanged();
        assertTrue(tracker.hasChanged());

        tracker.clearChanged();
        assertFalse(tracker.hasChanged());
    }

    @Test
    void testMultipleMarkChanged()
    {
        tracker.markChanged();
        tracker.markChanged();
        tracker.markChanged();
        assertTrue(tracker.hasChanged()); // 多次标记应为 true

        tracker.clearChanged();
        assertFalse(tracker.hasChanged());
    }

    @Test
    void testStateTransitions()
    {
        // 初始 -> 脏 -> 干净 -> 脏 -> 干净
        assertFalse(tracker.hasChanged());

        tracker.markChanged();
        assertTrue(tracker.hasChanged());

        tracker.clearChanged();
        assertFalse(tracker.hasChanged());

        tracker.markChanged();
        assertTrue(tracker.hasChanged());

        tracker.clearChanged();
        assertFalse(tracker.hasChanged());
    }
}
