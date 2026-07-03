package com.restonecash.sansystem.util;

import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class EntityClassificationHelper {
    public static EntityCategory getCategory(EntityType<?> type){

        if(type.equals(EntityType.PLAYER)){
            return EntityCategory.PLAYER;
        }

        else if(type.getCategory() == MobCategory.WATER_CREATURE
                ||type.equals(EntityType.AXOLOTL)
                ||type.is(EntityTypeTags.BEEHIVE_INHABITORS)
                ||type.equals(EntityType.PARROT)
                ||type.equals(EntityType.BAT)
                ||type.equals(EntityType.CHICKEN)
                ||type.equals(EntityType.RABBIT)
                ||type.equals(EntityType.FOX)
                ||type.equals(EntityType.CAT)
                ||type.equals(EntityType.OCELOT)){
            return EntityCategory.WEAKFRIENDLY;
        }

        else if(type.getCategory() == MobCategory.CREATURE
                && !type.equals(EntityType.IRON_GOLEM)){
            return EntityCategory.FRIENDLY;
        }

        else if(type.equals(EntityType.IRON_GOLEM)){
            return EntityCategory.NEUTRAL;
        }

        else if(){
            return EntityCategory.COMMONMOBS;
        }

        else if (type.equals(EntityType.ENDER_DRAGON)) {
            return EntityCategory.ENDERDRAGON;
        }

        else if (type.equals(EntityType.WITHER)) {
            return EntityCategory.WITHER;
        }

        else if (type.equals(EntityType.WARDEN)) {
            return EntityCategory.WARDEN;
        }

        else return EntityCategory.OTHER;
    }

}