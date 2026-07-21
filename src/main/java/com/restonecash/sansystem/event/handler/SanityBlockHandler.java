package com.restonecash.sansystem.event.handler;

import com.restonecash.sansystem.SanSystem;
import com.restonecash.sansystem.capability.SanityCapability;
import com.restonecash.sansystem.config.ServerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

/**
 * 方块理智流失处理器
 * 处理玩家站在方块上方时的理智流失逻辑
 *
 * 【架构说明】
 * 核心冷却和流失判断逻辑已下沉到 SanityDrainTracker（纯 Java，可独立测试）
 * 本类只负责：
 * 1. 订阅游戏事件
 * 2. 检测玩家脚下方块
 * 3. 调用 tracker.tryDrain() 判断是否执行流失
 * 4. 根据返回结果执行实际的理智修改
 */
@Mod.EventBusSubscriber(modid = SanSystem.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SanityBlockHandler
{
    /**
     * 理智流失跟踪器实例
     * 封装冷却判断、多玩家隔离、累计统计等逻辑
     */
    private static final SanityDrainTracker drainTracker = new SanityDrainTracker();

    /**
     * 玩家tick事件处理
     * 检测玩家脚下方块，判断是否触发理智流失
     */
    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event)
    {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;

        UUID playerId = player.getUUID();
        long currentTick = player.level().getGameTime();

        BlockPos feetPos = player.blockPosition().below();
        BlockState blockState = player.level().getBlockState(feetPos);
        Block block = blockState.getBlock();

        checkBlockDrain(player, block, playerId, currentTick);
    }

    /**
     * 玩家离开事件处理
     * 清理该玩家的冷却记录，避免内存泄漏
     */
    @SubscribeEvent
    public static void onPlayerLeave(EntityLeaveLevelEvent event)
    {
        if (!(event.getEntity() instanceof Player player)) return;
        drainTracker.removePlayer(player.getUUID());
    }

    /**
     * 检查方块的理智流失
     * 检测方块配置，调用跟踪器判断是否执行流失
     */
    private static void checkBlockDrain(Player player, Block block, UUID playerId, long currentTick)
    {
        ServerConfig.SanityDrainConfig drainConfig = ServerConfig.getBlockSanityDrain(block);

        if (drainConfig == null) return;

        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
        if (blockId == null) return;

        // 【委托说明】冷却判断由 drainTracker.tryDrain() 完成
        // 返回 true 表示冷却过期，应执行流失
        if (drainTracker.tryDrain(playerId, blockId.toString(), currentTick,
                drainConfig.ticksPerDrain(), drainConfig.sanityPerDrain()))
        {
            // 执行实际的理智修改
            player.getCapability(SanityCapability.SANITY).ifPresent(sanity -> {
                float current = sanity.getCore().getSanity();
                if (current > 0)
                {
                    sanity.getCore().setSanity(current - drainConfig.sanityPerDrain());
                }
            });
        }
    }
}
