package com.restonecash.sansystem.effect;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

/**
 * 恶心效果应用器
 * 实现 EffectApplier 接口，处理恶心的应用和移除
 */
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
        // 根据强度设置效果持续时间
        // 注意：这里只是示例，实际强度变化可以通过修改效果参数实现
        if (!hasEffect(player))
        {
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0, false, false, true));
        }
    }

    @Override
    public void remove(Player player)
    {
        player.removeEffect(MobEffects.CONFUSION);
    }

    @Override
    public boolean hasEffect(Player player)
    {
        return player.hasEffect(MobEffects.CONFUSION);
    }
}
