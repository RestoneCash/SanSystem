package com.restonecash.sansystem.event.handler;

import com.restonecash.sansystem.SanSystem;
import com.restonecash.sansystem.api.registry.AttributeRegistry;
import com.restonecash.sansystem.capability.SanityCapability;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * 伤害系统处理器（核心系统，手动注册）
 * 注册位置：SanSystem.commonSetup()
 * 功能：监听LivingDamageEvent，根据攻击方污染值和受击方韧性计算掉San量
 */
public class SanityDamageHandler
{
    private static boolean debugOnce = true;

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event)
    {
        if (event.getEntity().level().isClientSide) return;

        LivingEntity defender = event.getEntity();
        DamageSource source = event.getSource();

        final float attackerPollution;
        if (source.getEntity() instanceof LivingEntity attacker) {
            attackerPollution = getPollution(attacker);
            if (debugOnce) {
                debugOnce = false;
                SanSystem.LOGGER.info("SanityDamage: attacker={} pollution={} attrExists={}",
                    attacker.getClass().getSimpleName(), attackerPollution,
                    attacker.getAttribute(AttributeRegistry.POLLUTION.get()) != null);
            }
        } else {
            attackerPollution = 0.0f;
        }

        if (attackerPollution <= 0) return;

        final float defenderResilience = getResilience(defender);
        final float damage = event.getAmount();

        defender.getCapability(SanityCapability.SANITY).ifPresent(sanity -> {
            float before = sanity.getCore().getSanity();
            sanity.getCore().decreaseSanity(damage, attackerPollution, defenderResilience);
            float after = sanity.getCore().getSanity();
            SanSystem.LOGGER.info("SanityDamage: {} dealt {} san damage. {} -> {}",
                defender.getClass().getSimpleName(), before - after, before, after);
        });
    }

    private float getPollution(LivingEntity entity)
    {
        var attr = entity.getAttribute(AttributeRegistry.POLLUTION.get());
        return attr != null ? (float) attr.getValue() : 0.0f;
    }

    private float getResilience(LivingEntity entity)
    {
        var attr = entity.getAttribute(AttributeRegistry.MENTAL_RESILIENCE.get());
        return attr != null ? (float) attr.getValue() : 0.0f;
    }
}