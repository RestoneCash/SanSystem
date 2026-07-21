package com.restonecash.sansystem.effect;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class NauseaEffectApplier implements EffectApplier
{
    @Override
    public EffectType getEffectType()
    {
        return EffectType.NAUSEA;
    }

    @Override
    public void apply(Player player, float intensity)
    {
        if (player == null) return;
        if (intensity <= 0.0f) {
            remove(player);
            return;
        }
        int amplifier = Math.min(4, (int) (intensity * 5));
        player.removeEffect(MobEffects.CONFUSION);
        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, amplifier, false, false, true));
    }

    @Override
    public void remove(Player player)
    {
        if (player != null) player.removeEffect(MobEffects.CONFUSION);
    }

    @Override
    public boolean hasEffect(Player player)
    {
        return player != null && player.hasEffect(MobEffects.CONFUSION);
    }
}
