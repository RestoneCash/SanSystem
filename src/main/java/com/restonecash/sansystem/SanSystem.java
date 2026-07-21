package com.restonecash.sansystem;

import com.mojang.logging.LogUtils;
import com.restonecash.sansystem.api.registry.AttributeRegistry;
import com.restonecash.sansystem.api.san.ISanity;
import com.restonecash.sansystem.capability.SanityCapability;
import com.restonecash.sansystem.config.AttributeConfig;
import com.restonecash.sansystem.config.ClientConfig;
import com.restonecash.sansystem.config.ServerConfig;
import com.restonecash.sansystem.entity.ModEntities;
import com.restonecash.sansystem.entity.ShadowEntity;
import com.restonecash.sansystem.event.handler.CapabilityEventHandler;
import com.restonecash.sansystem.event.handler.SanityCraftingHandler;
import com.restonecash.sansystem.event.handler.SanityDamageHandler;
import com.restonecash.sansystem.event.handler.SanitySyncEventHandler;
import com.restonecash.sansystem.gui.overlays.ManaBarOverlay;
import com.restonecash.sansystem.gui.overlays.SanityVisualOverlay;
import com.restonecash.sansystem.network.PacketHandler;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
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

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AttributeConfig.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModEntities.ENTITIES.register(modEventBus);
        AttributeRegistry.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(new CapabilityEventHandler());

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
            PacketHandler.register();

            MinecraftForge.EVENT_BUS.register(new SanitySyncEventHandler());
            MinecraftForge.EVENT_BUS.register(new SanityDamageHandler());
            MinecraftForge.EVENT_BUS.register(new SanityCraftingHandler());
        });
    }

    /**
     * 注册屏幕叠加UI层（客户端渲染事件，仅客户端执行）
     * 在原版饥饿值UI上方渲染两条自定义界面：理智条 + 负面视觉特效层
     */
    private void registerGuiOverlays(final RegisterGuiOverlaysEvent event)
    {
        event.registerAbove(
                VanillaGuiOverlay.FOOD_LEVEL.id(),
                "san_bar",
                new ManaBarOverlay()
        );
        event.registerAbove(
                VanillaGuiOverlay.FOOD_LEVEL.id(),
                "san_visual",
                new SanityVisualOverlay()
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