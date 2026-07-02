//能力接口

package com.restonecash.sansystem.api.san;

import net.minecraft.nbt.CompoundTag;

/**
 * 实体San值能力接口
 * 所有实体（LivingEntity）都将挂载此能力
 *
 * 【架构说明】
 * 此接口作为门面（Facade），委托到三个子模块：
 * - SanityCore: 持久化核心值管理
 * - SanityEffects: 运行时效果状态
 * - SanityTracker: san变化追踪
 *
 */
public interface ISanity
{
    // ==================== 核心模块访问 ====================

    SanityCore getCore();
    SanityEffects getEffects();
    SanityTracker getSyncTracker();



    // NBT读写（存档持久化用）
    void saveNBT(CompoundTag tag);
    void loadNBT(CompoundTag tag);




}
