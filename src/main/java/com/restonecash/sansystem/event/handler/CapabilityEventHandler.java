package com.restonecash.sansystem.event.handler;

import com.restonecash.sansystem.api.events.AttributeEventHandler;
import com.restonecash.sansystem.capability.SanityCapability;
import com.restonecash.sansystem.capability.SanityStorage;
import com.restonecash.sansystem.config.AttributeConfig;
import com.restonecash.sansystem.config.ServerConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CapabilityEventHandler
{
    private static final ResourceLocation SANITY_CAP = new ResourceLocation("san_system", "sanity");

    @SubscribeEvent
    public void onAttachCapabilities(AttachCapabilitiesEvent<LivingEntity> event)
    {
        event.addCapability(SANITY_CAP, new SanityStorage());
    }

    @SubscribeEvent
    public void onEntityJoinLevel(EntityJoinLevelEvent event)
    {
        if (!(event.getEntity() instanceof LivingEntity livingEntity)) return;
        if (event.getLevel().isClientSide) return;

        AttributeEventHandler.applyDefaultAttributes(livingEntity);

        livingEntity.getCapability(SanityCapability.SANITY).ifPresent(sanity -> {
            if (!sanity.getCore().isInitialized()) {
                float maxSanity = (float) AttributeConfig.INSTANCE.getAttributes(livingEntity.getType()).maxSan;
                sanity.getCore().setMaxSanity(maxSanity);
                sanity.getCore().setSanity(maxSanity);
                sanity.getCore().setInitialized();
            }
        });
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event)
    {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) return;

        entity.getCapability(SanityCapability.SANITY).ifPresent(sanity -> {
            sanity.getCore().setSanity(sanity.getCore().getMaxSanity());
            // 【重要】重置所有运行时状态，防止复活后仍有负面效果
            sanity.getEffects().resetRuntimeState();
        });
    }
}