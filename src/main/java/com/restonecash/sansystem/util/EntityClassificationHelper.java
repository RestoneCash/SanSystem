package com.restonecash.sansystem.util;

import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityClassificationHelper {

    private static final Map<EntityType<?>, EntityCategory> CACHE = new ConcurrentHashMap<>();

    public static EntityCategory getCategory(EntityType<?> type) {
        if (type == null) return EntityCategory.OTHER;
        return CACHE.computeIfAbsent(type, EntityClassificationHelper::computeCategory);
    }

    private static EntityCategory computeCategory(EntityType<?> type) {
        if (type.equals(EntityType.PLAYER)) {
            return EntityCategory.PLAYER;
        }

        if (type.getCategory() == MobCategory.WATER_CREATURE
                || type.equals(EntityType.AXOLOTL)
                || type.is(EntityTypeTags.BEEHIVE_INHABITORS)
                || type.equals(EntityType.PARROT)
                || type.equals(EntityType.BAT)
                || type.equals(EntityType.CHICKEN)
                || type.equals(EntityType.RABBIT)
                || type.equals(EntityType.FOX)
                || type.equals(EntityType.CAT)
                || type.equals(EntityType.OCELOT)
                || type.equals(EntityType.TURTLE)
                || type.equals(EntityType.BEE)) {
            return EntityCategory.WEAK;
        }

        if (type.getCategory() == MobCategory.CREATURE
                && !type.equals(EntityType.IRON_GOLEM)) {
            return EntityCategory.FRIENDLY;
        }

        if (type.equals(EntityType.IRON_GOLEM)
                || type.equals(EntityType.POLAR_BEAR)
                || type.equals(EntityType.WOLF)
                || type.equals(EntityType.DOLPHIN)
                || type.equals(EntityType.LLAMA)
                || type.equals(EntityType.TRADER_LLAMA)
                || type.equals(EntityType.PANDA)
                || type.equals(EntityType.GOAT)) {
            return EntityCategory.NEUTRAL;
        }

        if (type.equals(EntityType.ZOMBIE)
                || type.equals(EntityType.ZOMBIE_VILLAGER)
                || type.equals(EntityType.ZOMBIE_HORSE)
                || type.equals(EntityType.HUSK)
                || type.equals(EntityType.DROWNED)
                || type.equals(EntityType.SKELETON)
                || type.equals(EntityType.SKELETON_HORSE)
                || type.equals(EntityType.STRAY)
                || type.equals(EntityType.CREEPER)
                || type.equals(EntityType.SPIDER)
                || type.equals(EntityType.CAVE_SPIDER)
                || type.equals(EntityType.SLIME)
                || type.equals(EntityType.GUARDIAN)
                || type.equals(EntityType.SILVERFISH)
                || type.equals(EntityType.PHANTOM)
                || type.equals(EntityType.VEX)
                || type.equals(EntityType.WITCH)) {
            return EntityCategory.COMMONMOBS;
        }

        if (type.equals(EntityType.WITHER_SKELETON)
                || type.equals(EntityType.ELDER_GUARDIAN)
                || type.is(EntityTypeTags.RAIDERS)
                || type.equals(EntityType.HOGLIN)
                || type.equals(EntityType.ZOGLIN)
                || type.equals(EntityType.PIGLIN)
                || type.equals(EntityType.PIGLIN_BRUTE)
                || type.equals(EntityType.ZOMBIFIED_PIGLIN)
                || type.equals(EntityType.GHAST)
                || type.equals(EntityType.BLAZE)
                || type.equals(EntityType.MAGMA_CUBE)
                || type.equals(EntityType.ENDERMAN)
                || type.equals(EntityType.ENDERMITE)
                || type.equals(EntityType.SHULKER)
                || type.equals(EntityType.EVOKER)) {
            return EntityCategory.STRONGMOBS;
        }

        if (type.equals(EntityType.ENDER_DRAGON)) {
            return EntityCategory.ENDERDRAGON;
        }

        if (type.equals(EntityType.WITHER)) {
            return EntityCategory.WITHER;
        }

        if (type.equals(EntityType.WARDEN)) {
            return EntityCategory.WARDEN;
        }

        return EntityCategory.OTHER;
    }
}
