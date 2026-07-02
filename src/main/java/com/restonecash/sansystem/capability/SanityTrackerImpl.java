package com.restonecash.sansystem.capability;

import com.restonecash.sansystem.api.san.SanityTracker;

/**
 * 同步跟踪器实现
 * 简单状态机：干净 <-> 脏
 */
public class SanityTrackerImpl implements SanityTracker
{
    private boolean changed = false;

    @Override
    public boolean hasChanged()
    {
        return this.changed;
    }

    @Override
    public void clearChanged()
    {
        this.changed = false;
    }

    @Override
    public void markChanged()
    {
        this.changed = true;
    }
}
