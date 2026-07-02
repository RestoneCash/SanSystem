package com.restonecash.sansystem.entity;

import com.restonecash.sansystem.SanSystem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities
{
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, SanSystem.MODID);

    public static final RegistryObject<EntityType<ShadowEntity>> SHADOW_ENTITY = ENTITIES.register("shadow_entity",
            () -> EntityType.Builder.of(ShadowEntity::new, MobCategory.MONSTER)
                    .sized(0.8f, 2.0f)
                    .clientTrackingRange(8)
                    .build("shadow_entity"));
}