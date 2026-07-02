package com.restonecash.sansystem.event.handler;

import com.restonecash.sansystem.SanSystem;
import com.restonecash.sansystem.capability.SanityCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SanSystem.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class SanityInputHandler
{

    @SubscribeEvent
    public void onMovementInputUpdate(MovementInputUpdateEvent event)
    {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || !player.level().isClientSide) return;

        player.getCapability(SanityCapability.SANITY).ifPresent(sanity -> {
            if (sanity.getEffects().getInputInverted()) {
                invertMovementInput(event);
                addConfusionIndicator(player);
            }
        });
    }

    private void invertMovementInput(MovementInputUpdateEvent event)
    {
        event.getInput().forwardImpulse *= -1;
        event.getInput().leftImpulse *= -1;
    }

    private void addConfusionIndicator(LocalPlayer player)
    {
        if (!player.hasEffect(MobEffects.DIG_SLOWDOWN))
        {
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 40, 0, false, false, true));
        }
    }
}