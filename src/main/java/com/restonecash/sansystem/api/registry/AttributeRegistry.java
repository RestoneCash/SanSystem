package com.restonecash.sansystem.api.registry;

import com.restonecash.sansystem.SanSystem;
import com.restonecash.sansystem.api.attribute.SanRangedAttribute;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = SanSystem.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AttributeRegistry
{
    /**
    * 实体属性延迟注册容器
    */
    private static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, SanSystem.MODID);
    public static void register(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);
    }


    //最大san值
    public static final RegistryObject<Attribute> MAX_SANITY=ATTRIBUTES.register("max_sanity",
            () -> (new SanRangedAttribute("attribute.san_system.max_sanity", 100.0D, 0.0D, 100000.0D).setSyncable(true)));
    //精神恢复
    public static final RegistryObject<Attribute> MENTAL_RECOVERY=ATTRIBUTES.register("mental_recovery",
            () -> (new SanRangedAttribute("attribute.name.san_system.mental_recovery", 1.0D, 0.0D, 100000.0D).setSyncable(true)));
    //精神韧性
    public static final RegistryObject<Attribute> MENTAL_RESILIENCE=ATTRIBUTES.register("mental_resilience",
            () -> (new SanRangedAttribute("attribute.san_system.mental_resilience", 1.0D, -100000.0D, 100000.0D).setSyncable(true)));
    //精神污染
    public static final RegistryObject<Attribute> POLLUTION=ATTRIBUTES.register("pollution",
            () -> (new SanRangedAttribute("attribute.san_system.pollution", 0.0D, -100000.0D, 100000.0D).setSyncable(true)));

}
