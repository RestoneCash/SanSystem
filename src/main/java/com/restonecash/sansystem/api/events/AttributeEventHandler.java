package com.restonecash.sansystem.api.events;

import com.restonecash.sansystem.SanSystem;
import com.restonecash.sansystem.api.registry.AttributeRegistry;
import com.restonecash.sansystem.config.AttributeConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

/**
 * 实体属性注册事件处理器
 */
@Mod.EventBusSubscriber(modid = SanSystem.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AttributeEventHandler
{
    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public static void onEntityAttributeModification(EntityAttributeModificationEvent event)
    {
        for (net.minecraft.world.entity.EntityType<?> entityType : net.minecraftforge.registries.ForgeRegistries.ENTITY_TYPES.getValues()) {
            if (net.minecraft.world.entity.LivingEntity.class.isAssignableFrom(entityType.getBaseClass()))
            {
                event.add((net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.LivingEntity>) entityType, AttributeRegistry.POLLUTION.get());
                event.add((net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.LivingEntity>) entityType, AttributeRegistry.MENTAL_RESILIENCE.get());
                event.add((net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.LivingEntity>) entityType, AttributeRegistry.MENTAL_RECOVERY.get());
                event.add((net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.LivingEntity>) entityType, AttributeRegistry.MAX_SANITY.get());
            }
        }
    }

    /**
     * 实体生成时应用配置文件定义的属性默认基础值
     */
    public static void applyDefaultAttributes(LivingEntity entity)
    {
        // ★ 使用 AttributeConfig 获取属性值（包含覆盖逻辑）
        AttributeConfig.AttributeValues values = AttributeConfig.INSTANCE.getAttributes(entity.getType());

        // 设置四个属性
        setAttribute(entity, AttributeRegistry.POLLUTION, values.pollution);
        setAttribute(entity, AttributeRegistry.MENTAL_RECOVERY, values.mentalRecover);
        setAttribute(entity, AttributeRegistry.MENTAL_RESILIENCE, values.mentalResilience);
        setAttribute(entity, AttributeRegistry.MAX_SANITY, values.maxSan);

        // ★ 存储 ifSanKill 标志到实体的持久数据（或 Capability）
        // 这里使用 PersistentData，便于后续在死亡判定时读取
        entity.getPersistentData().putBoolean("SanIfKill", values.ifSanKill);
    }

    private static void setAttribute(LivingEntity entity, RegistryObject<Attribute> attr, double value) {
        AttributeInstance instance = entity.getAttribute(attr.get());
        if (instance != null) {
            instance.setBaseValue(value);
        }
    }
}