package com.restonecash.sansystem.effect;

import com.restonecash.sansystem.capability.SanityCapability;
import net.minecraft.world.entity.player.Player;

public class InputInversionEffectApplier implements EffectApplier
{
    @Override
    public EffectType getEffectType()
    {
        return EffectType.INPUT_INVERSION;
    }

    @Override
    public void apply(Player player, float intensity)
    {
        if (player == null) return;
        boolean shouldInvert = intensity > 0.5f;
        player.getCapability(SanityCapability.SANITY)
            .ifPresent(sanity -> sanity.getEffects().setInputInverted(shouldInvert));
    }

    @Override
    public void remove(Player player)
    {
        if (player == null) return;
        player.getCapability(SanityCapability.SANITY)
            .ifPresent(sanity -> sanity.getEffects().setInputInverted(false));
    }

    @Override
    public boolean hasEffect(Player player)
    {
        if (player == null) return false;
        return player.getCapability(SanityCapability.SANITY)
            .map(sanity -> sanity.getEffects().getInputInverted())
            .orElse(false);
    }
}
