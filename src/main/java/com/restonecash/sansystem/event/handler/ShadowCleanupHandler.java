package com.restonecash.sansystem.event.handler;

import com.restonecash.sansystem.SanSystem;
import com.restonecash.sansystem.entity.ShadowEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 影子实体清理处理器
 * 负责在玩家离开或世界卸载时清理影子实体，防止内存泄漏
 */
@Mod.EventBusSubscriber(modid = SanSystem.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ShadowCleanupHandler
{
    @SubscribeEvent
    public static void onEntityRemoved(EntityLeaveLevelEvent event)
    {
        Entity entity = event.getEntity();
        if (entity instanceof ShadowEntity shadow)
        {
            shadow.onRemoved();
        }
    }

    /**
     * 当玩家离开维度或服务器时，清除该玩家的所有影子实体
     */
    @SubscribeEvent
    public static void onPlayerLeave(EntityLeaveLevelEvent event)
    {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;

        Level level = event.getLevel();
        // 只在服务端处理
        if (level.isClientSide()) return;

        // 清除该玩家的所有影子
        ShadowEntity.clearPlayerShadows(player.getUUID());
    }

    /**
     * 当世界卸载时，清除所有影子实体
     */
    @SubscribeEvent
    public static void onWorldUnload(LevelEvent.Unload event)
    {
        // 只处理 Level（维度）卸载，不处理其他 LevelAccessor
        if (!(event.getLevel() instanceof Level level)) return;

        // 只在服务端处理
        if (level.isClientSide()) return;

        // 清除所有影子
        ShadowEntity.clearAllShadows();
    }
}