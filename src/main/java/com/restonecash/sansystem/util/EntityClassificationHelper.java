package com.restonecash.sansystem.util;

import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class EntityClassificationHelper {
    public static EntityCategory getCategory(EntityType<?> type){

        if(type.equals(EntityType.PLAYER)){
            return EntityCategory.PLAYER;
        }

        else if (type.equals(EntityType.ENDER_DRAGON)) {
            return EntityCategory.ENDERDRAGON;
        }
        
        else return EntityCategory.OTHER;
    }

}