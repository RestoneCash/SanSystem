package com.restonecash.sansystem.api.events;

import com.restonecash.sansystem.SanSystem;
import com.restonecash.sansystem.api.registry.AttributeRegistry;
import com.restonecash.sansystem.config.ServerConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 实体属性注册事件处理器（工具类，注解注册）
 * 注册方式：@Mod.EventBusSubscriber (MOD总线)
 * 功能：为指定实体注册模组自定义理智相关属性，以及实体生成时赋予配置默认属性值
 * 绑定模组总线，仅在模组加载阶段执行一次属性注册逻辑
 */
@Mod.EventBusSubscriber(modid = SanSystem.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AttributeEventHandler
{
    /**
     * 实体属性修改注册事件
     * 游戏启动、实体类型初始化时触发，用于给实体挂载自定义属性
     * 只有在这里add的属性，实体才会拥有该属性实例，否则getAttribute会返回null
     * @param event 属性注册事件对象，提供add方法绑定实体与属性
     */
    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public static void onEntityAttributeModification(EntityAttributeModificationEvent event)
    {
        for (net.minecraft.world.entity.EntityType<?> entityType : net.minecraftforge.registries.ForgeRegistries.ENTITY_TYPES.getValues()) {
            if (net.minecraft.world.entity.LivingEntity.class.isAssignableFrom(entityType.getBaseClass()))
            {
                event.add((net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.LivingEntity>) entityType, AttributeRegistry.POLLUTION);
                event.add((net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.LivingEntity>) entityType, AttributeRegistry.MENTAL_RESILIENCE);
                event.add((net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.LivingEntity>) entityType, AttributeRegistry.MENTAL_RECOVERY);
            }
        }
    }

    /**
     * 实体生成时应用配置文件定义的属性默认基础值
     * 【重要修复】移除 getBaseValue() == 0.0 的检查，因为：
     * 1. POLLUTION 默认值是 0.0，但配置值也可能是 0.0（如玩家）
     * 2. MENTAL_RESILIENCE 和 MENTAL_RECOVERY 默认值是 1.0，条件永远不满足
     * 此方法只在 EntityJoinLevelEvent 中被调用一次（首次加入世界），
     * 此时实体还没有装备/附魔，不会覆盖任何修改后的值。
     * @param entity 需要初始化属性的活体实体（玩家/僵尸/末影人等）
     */
    public static void applyDefaultAttributes(LivingEntity entity)
    {
        // 根据实体类型，从服务端配置读取该物种预设属性数值
        ServerConfig.EntityAttributeDefaults defaults = ServerConfig.getEntityDefaults(entity.getType());
        
        AttributeInstance pollution = entity.getAttribute(AttributeRegistry.POLLUTION);
        if (pollution != null) {
            pollution.setBaseValue(defaults.pollution());
        }
        
        AttributeInstance resilience = entity.getAttribute(AttributeRegistry.MENTAL_RESILIENCE);
        if (resilience != null) {
            resilience.setBaseValue(defaults.resilience());
        }
        
        AttributeInstance recovery = entity.getAttribute(AttributeRegistry.MENTAL_RECOVERY);
        if (recovery != null) {
            recovery.setBaseValue(defaults.recovery());
        }
    }
}