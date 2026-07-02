package com.restonecash.sansystem.event.handler;

import com.restonecash.sansystem.api.registry.AttributeRegistry;
import com.restonecash.sansystem.capability.SanityCapability;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * 伤害系统处理器（核心系统，手动注册）
 * 注册位置：SanSystem.commonSetup()
 * 功能：监听LivingDamageEvent，根据攻击方污染值和受击方韧性计算掉San量
 */
public class SanityDamageHandler
{
    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent event)
    {
        if (event.getEntity().level().isClientSide) return;

        LivingEntity defender = event.getEntity();
        DamageSource source = event.getSource();

        final float attackerPollution;
        if (source.getEntity() instanceof LivingEntity attacker) {
            attackerPollution = getPollution(attacker);
        } else {
            attackerPollution = 0.0f;
        }

        if (attackerPollution <= 0) return;

        final float defenderResilience = getResilience(defender);
        final float damage = event.getAmount();

        defender.getCapability(SanityCapability.SANITY).ifPresent(sanity -> {
            sanity.getCore().decreaseSanity(damage, attackerPollution, defenderResilience);
        });
    }

    private float getPollution(LivingEntity entity)
    {
        var attr = entity.getAttribute(AttributeRegistry.POLLUTION);
        return attr != null ? (float) attr.getValue() : 0.0f;
    }

    private float getResilience(LivingEntity entity)
    {
        var attr = entity.getAttribute(AttributeRegistry.MENTAL_RESILIENCE);
        return attr != null ? (float) attr.getValue() : 0.0f;
    }
}