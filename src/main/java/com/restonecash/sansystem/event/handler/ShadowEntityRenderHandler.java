package com.restonecash.sansystem.event.handler;

import com.restonecash.sansystem.SanSystem;
import com.restonecash.sansystem.entity.ShadowEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SanSystem.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ShadowEntityRenderHandler
{
    @SubscribeEvent
    public static void onRenderLivingPre(RenderLivingEvent.Pre<ShadowEntity, ?> event)
    {
        Entity entity = event.getEntity();
        if (!(entity instanceof ShadowEntity shadow)) return;

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null)
        {
            event.setCanceled(true);
            return;
        }

        if (!player.getUUID().equals(shadow.getTargetPlayerId()))
        {
            event.setCanceled(true);
        }
    }
}