package com.restonecash.sansystem.effect;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class BlindnessEffectApplier implements EffectApplier
{
    @Override
    public EffectType getEffectType()
    {
        return EffectType.BLINDNESS;
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
        player.removeEffect(MobEffects.BLINDNESS);
        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200, amplifier, false, false, true));
    }

    @Override
    public void remove(Player player)
    {
        if (player != null) player.removeEffect(MobEffects.BLINDNESS);
    }

    @Override
    public boolean hasEffect(Player player)
    {
        return player != null && player.hasEffect(MobEffects.BLINDNESS);
    }
}
