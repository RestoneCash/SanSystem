package com.restonecash.sansystem.network;

import com.restonecash.sansystem.SanSystem;
import com.restonecash.sansystem.capability.SanityCapability;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler
{
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new net.minecraft.resources.ResourceLocation(SanSystem.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int id = 0;

    public static void register()
    {
        INSTANCE.messageBuilder(SanitySyncPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SanitySyncPacket::new)
                .encoder(SanitySyncPacket::toBytes)
                .consumerMainThread(SanitySyncPacket::handle)
                .add();
    }

    public static void syncSanity(LivingEntity entity)
    {
        if (!(entity.level() instanceof net.minecraft.server.level.ServerLevel)) return;

        entity.getCapability(SanityCapability.SANITY).ifPresent(sanity -> {
            if (!sanity.getSyncTracker().hasChanged()) return;

            SanitySyncPacket packet = new SanitySyncPacket(
                    entity.getId(),
                    sanity.getCore().getSanity(),
                    sanity.getCore().getMaxSanity(),
                    sanity.getEffects().getInputInverted(),
                    sanity.getEffects().getSlownessStacks(),
                    sanity.getEffects().getNauseaIntensity(),
                    sanity.getEffects().getBlindnessIntensity()
            );

            if (entity instanceof ServerPlayer player) {
                INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
            } else {
                INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), packet);
            }

            sanity.getSyncTracker().clearChanged();
        });
    }

    public static void syncSanityToAll(LivingEntity entity)
    {
        if (!(entity.level() instanceof net.minecraft.server.level.ServerLevel)) return;

        entity.getCapability(SanityCapability.SANITY).ifPresent(sanity -> {
            SanitySyncPacket packet = new SanitySyncPacket(
                    entity.getId(),
                    sanity.getCore().getSanity(),
                    sanity.getCore().getMaxSanity(),
                    sanity.getEffects().getInputInverted(),
                    sanity.getEffects().getSlownessStacks(),
                    sanity.getEffects().getNauseaIntensity(),
                    sanity.getEffects().getBlindnessIntensity()
            );
            INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), packet);
            sanity.getSyncTracker().clearChanged();
        });
    }
}