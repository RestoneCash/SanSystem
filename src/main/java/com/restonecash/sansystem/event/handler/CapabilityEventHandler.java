package com.restonecash.sansystem.event.handler;

import com.restonecash.sansystem.SanSystem;
import com.restonecash.sansystem.api.events.AttributeEventHandler;
import com.restonecash.sansystem.capability.SanityCapability;
import com.restonecash.sansystem.capability.SanityStorage;
import com.restonecash.sansystem.config.AttributeConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class CapabilityEventHandler
{
    private static final ResourceLocation SANITY_CAP = new ResourceLocation(SanSystem.MODID, "sanity");

    @SubscribeEvent
    public void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event)
    {
        if (event.getObject() instanceof LivingEntity) {
            event.addCapability(SANITY_CAP, new SanityStorage());
        }
    }

    @SubscribeEvent
    public void onEntityJoinLevel(EntityJoinLevelEvent event)
    {
        if (!(event.getEntity() instanceof LivingEntity livingEntity)) return;
        if (event.getLevel().isClientSide) return;

        AttributeEventHandler.applyDefaultAttributes(livingEntity);

        livingEntity.getCapability(SanityCapability.SANITY).ifPresent(sanity -> {
            float cfgMax = (float) AttributeConfig.INSTANCE.getAttributes(livingEntity.getType()).maxSan;
            sanity.getCore().setMaxSanity(cfgMax);

            if (!(sanity instanceof SanityCapability sc && sc.isSessionInitDone())) {
                float startSan = readOrInit(livingEntity, cfgMax);
                sanity.getCore().setSanity(startSan);
                if (sanity instanceof SanityCapability sc2) sc2.markSessionInitDone();
            }
            sanity.getCore().setInitialized();
            sanity.getCore().setMaxSanity(cfgMax);
        });
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event)
    {
        if (event.getEntity().level().isClientSide) return;
        if (event.getEntity() instanceof ServerPlayer sp) {
            writeSanFile(sp);
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event)
    {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) return;

        entity.getCapability(SanityCapability.SANITY).ifPresent(sanity -> {
            sanity.getCore().setSanity(sanity.getCore().getMaxSanity());
            sanity.getEffects().resetRuntimeState();
        });
    }

    private static float readSanFile(ServerPlayer player, float defaultVal) {
        try {
            Path f = player.getServer().getWorldPath(net.minecraft.world.level.storage.LevelResource.ROOT)
                .resolve("san_" + player.getStringUUID() + ".dat");
            if (Files.exists(f)) {
                Properties p = new Properties();
                try (InputStream in = Files.newInputStream(f)) { p.load(in); }
                return Float.parseFloat(p.getProperty("sanity", String.valueOf(defaultVal)));
            }
        } catch (Exception ignored) {}
        return defaultVal;
    }

    private static float readOrInit(LivingEntity entity, float cfgMax) {
        if (entity instanceof ServerPlayer sp) {
            return Math.min(readSanFile(sp, cfgMax), cfgMax);
        }
        return cfgMax;
    }

    private static void writeSanFile(ServerPlayer player) {
        player.getCapability(SanityCapability.SANITY).ifPresent(sanity -> {
            try {
                Path f = player.getServer().getWorldPath(net.minecraft.world.level.storage.LevelResource.ROOT)
                    .resolve("san_" + player.getStringUUID() + ".dat");
                Properties p = new Properties();
                p.setProperty("sanity", String.valueOf(sanity.getCore().getSanity()));
                p.setProperty("maxSanity", String.valueOf(sanity.getCore().getMaxSanity()));
                try (OutputStream out = Files.newOutputStream(f)) { p.store(out, null); }
            } catch (Exception ignored) {}
        });
    }
}
