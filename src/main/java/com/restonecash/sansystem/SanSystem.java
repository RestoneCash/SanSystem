package com.restonecash.sansystem;

import com.mojang.logging.LogUtils;
import com.restonecash.sansystem.api.san.ISanity;
import com.restonecash.sansystem.capability.SanityCapability;
import com.restonecash.sansystem.config.ClientConfig;
import com.restonecash.sansystem.config.ServerConfig;
import com.restonecash.sansystem.entity.ModEntities;
import com.restonecash.sansystem.entity.ShadowEntity;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/**
 * SanSystem 模组主入口类
 * 模组启动总入口，负责配置注册、实体注册、全生命周期事件绑定、全局事件处理器挂载
 * 对应之前架构评审的 San 理智值核心模组总调度类
 */
@Mod(SanSystem.MODID)
public class SanSystem
{
    public static final String MODID = "san_system";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static SanSystem INSTANCE;

    /**
     * 模组构造函数，模组加载时自动执行
     * 必须无参数！！！
     */
    public SanSystem() {
        INSTANCE = this;

        // 注册配置文件（不需要事件总线，保持不变）
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);

        // 获取模组事件总线
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 注册实体（需要 modEventBus）
        ModEntities.ENTITIES.register(modEventBus);

        // 绑定模组生命周期各类回调方法
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerGuiOverlays);
        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::registerEntityAttributes);
    }

    /**
     * 通用初始化事件（FML模组加载中后期执行）
     * 负责网络包注册、全局游戏事件总线处理器手动挂载
     * event.enqueueWork 保证同步线程安全操作
     */
    private void commonSetup(final FMLCommonSetupEvent event){
        event.enqueueWork(() ->
        {
            com.restonecash.sansystem.network.PacketHandler.register();

            // ===== 核心系统监听器（手动注册）=====

            // 工具类/初始化性质的监听器使用 @Mod.EventBusSubscriber 注解注册

            // Capability系统：绑定实体Capability、初始化属性、死亡重置
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new com.restonecash.sansystem.event.handler.CapabilityEventHandler());
            // 同步系统：检测San值变化并触发网络同步
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new com.restonecash.sansystem.event.handler.SanitySyncEventHandler());
            // 伤害系统：处理实体受击掉San
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new com.restonecash.sansystem.event.handler.SanityDamageHandler());
            // 恢复系统：处理脱战San值恢复（已整合到 SanityCentralHandler）
            // SanityCentralHandler 使用 @Mod.EventBusSubscriber 自动注册，无需手动注册
            // 合成系统：处理合成San值下限检测
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new com.restonecash.sansystem.event.handler.SanityCraftingHandler());
        });
    }

    /**
     * 注册屏幕叠加UI层（客户端渲染事件，仅客户端执行）
     * 在原版饥饿值UI上方渲染两条自定义界面：理智条 + 负面视觉特效层
     */
    private void registerGuiOverlays(final RegisterGuiOverlaysEvent event)
    {
        event.registerAbove(
                net.minecraftforge.client.gui.overlay.VanillaGuiOverlay.FOOD_LEVEL.id(),
                "san_bar",
                new com.restonecash.sansystem.gui.overlays.ManaBarOverlay()
        );
        event.registerAbove(
                net.minecraftforge.client.gui.overlay.VanillaGuiOverlay.FOOD_LEVEL.id(),
                "san_visual",
                new com.restonecash.sansystem.gui.overlays.SanityVisualOverlay()
        );
    }

    /**
     * 注册Capability能力系统
     * 绑定ISanity理智能力注册器，让Forge识别玩家实体可挂载理智数据
     */
    private void registerCapabilities(final RegisterCapabilitiesEvent event)
    {
        SanityCapability.register();
        event.register(ISanity.class);
    }

    /**
     * 注册自定义实体基础属性
     * 为影怪ShadowEntity注册生命值、移速、攻击伤害等原生属性
     */
    private void registerEntityAttributes(final EntityAttributeCreationEvent event)
    {
        event.put(ModEntities.SHADOW_ENTITY.get(), ShadowEntity.createAttributes().build());
    }
}