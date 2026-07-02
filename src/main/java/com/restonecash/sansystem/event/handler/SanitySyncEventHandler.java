package com.restonecash.sansystem.event.handler;

import com.restonecash.sansystem.network.PacketHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SanitySyncEventHandler
{
    @SubscribeEvent
    public void onLivingTick(LivingEvent.LivingTickEvent event)
    {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) return;

        if (!(entity instanceof Player))
        {
            PacketHandler.syncSanity(entity);
        }
    }
}