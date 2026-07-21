package com.restonecash.sansystem.capability;

import com.restonecash.sansystem.SanSystem;
import com.restonecash.sansystem.api.san.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

/**
 * 理智能力实现类（门面模式）
 *
 * 【架构说明】
 * 此类作为门面（Facade），将操作委托到三个子模块：
 * - core: 持久化核心值管理
 * - effects: 运行时效果状态
 * - sanityTracker: 脏标记追踪
 *
 * 注意：运行时状态（effects 中的字段）不持久化到 NBT
 */
public class SanityCapability implements ISanity
{
    public static Capability<ISanity> SANITY;

    public static void register()
    {
        SANITY = CapabilityManager.get(new CapabilityToken<ISanity>() {});
    }

    // ==================== 子模块实例 ====================

    private final SanityTracker sanityTracker = new SanityTrackerImpl();
    private final SanityCore core = new SanityCoreImpl(this.sanityTracker);
    private final SanityEffects effects = new SanityEffectsImpl(this.sanityTracker);
    private boolean sessionInitDone = false;

    public void markSessionInitDone() {
        this.sessionInitDone = true;
    }

    public boolean isSessionInitDone() {
        return this.sessionInitDone;
    }

    // ==================== 子模块访问器 ====================

    @Override
    public SanityCore getCore()
    {
        return this.core;
    }

    @Override
    public SanityEffects getEffects()
    {
        return this.effects;
    }

    @Override
    public SanityTracker getSyncTracker()
    {
        return this.sanityTracker;
    }

    // ==================== NBT 持久化（核心模块独立保存）====================

    @Override
    public void saveNBT(CompoundTag tag)
    {
        tag.putFloat("Sanity", this.core.getSanity());
        tag.putFloat("MaxSanity", this.core.getMaxSanity());
        tag.putBoolean("Initialized", this.core.isInitialized());
    }

    @Override
    public void loadNBT(CompoundTag tag)
    {
        if (sessionInitDone) return;
        if (tag.contains("Sanity")) this.core.setSanity(tag.getFloat("Sanity"));
        if (tag.contains("MaxSanity")) this.core.setMaxSanity(tag.getFloat("MaxSanity"));
        if (tag.contains("Initialized"))
        {
            if (tag.getBoolean("Initialized"))
            {
                this.core.setInitialized();
            }
        }
    }

}
