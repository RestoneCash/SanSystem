package com.restonecash.sansystem.effect;

import com.restonecash.sansystem.capability.SanityCapability;
import net.minecraft.world.entity.player.Player;

/**
 * 输入反转效果应用器
 * 实现 EffectApplier 接口，处理输入反转的状态管理
 *
 * 注意：输入反转不是药水效果，而是通过 SanityInputHandler 在客户端拦截输入实现
 * 此应用器主要用于标记状态和提供查询接口
 */
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
        // 输入反转由 SanityInputHandler 在客户端处理
        // 此处只需要标记状态，实际反转逻辑在客户端执行
        player.getCapability(SanityCapability.SANITY)
            .ifPresent(sanity -> sanity.getEffects().setInputInverted(true));
    }

    @Override
    public void remove(Player player)
    {
        player.getCapability(SanityCapability.SANITY)
            .ifPresent(sanity -> sanity.getEffects().setInputInverted(false));
    }

    @Override
    public boolean hasEffect(Player player)
    {
        return player.getCapability(SanityCapability.SANITY)
            .map(sanity -> sanity.getEffects().getInputInverted())
            .orElse(false);
    }
}
