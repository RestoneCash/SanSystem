package com.restonecash.sansystem.effect;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

/**
 * 失明效果应用器
 * 实现 EffectApplier 接口，处理失明的应用和移除
 */
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
        if (!hasEffect(player))
        {
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200, 0, false, false, true));
        }
    }

    @Override
    public void remove(Player player)
    {
        player.removeEffect(MobEffects.BLINDNESS);
    }

    @Override
    public boolean hasEffect(Player player)
    {
        return player.hasEffect(MobEffects.BLINDNESS);
    }
}
