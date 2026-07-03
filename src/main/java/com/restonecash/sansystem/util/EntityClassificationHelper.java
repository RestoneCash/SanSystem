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

        else if(type.equals(EntityType.ZOMBIE)
                ||type.equals(EntityType.ZOMBIE_VILLAGER)
                ||type.equals(EntityType.ZOMBIE_HORSE)
                ||type.equals(EntityType.HUSK)
                ||type.equals(EntityType.DROWNED)   //僵尸
                ||type.equals(EntityType.SKELETON)
                ||type.equals(EntityType.SKELETON_HORSE)
                ||type.equals(EntityType.STRAY)     //骷髅
                ||type.equals(EntityType.CREEPER)   //苦力怕
                ||type.equals(EntityType.SPIDER)
                ||type.equals(EntityType.CAVE_SPIDER)//蜘蛛
                ||type.equals(EntityType.SLIME)      //史莱姆
                ||type.equals(EntityType.GUARDIAN)   //守卫者
                ||type.equals(EntityType.SILVERFISH) //蠹虫
                ||type.equals(EntityType.PHANTOM)    //幻翼
                ||type.equals(EntityType.VEX)        //恼鬼
        ){
            return EntityCategory.COMMONMOBS;
        }

        else if (type.equals(EntityType.WITHER_SKELETON)//凋灵骷髅
                ||type.equals(EntityType.ELDER_GUARDIAN)//远古守卫者
                ||type.is(EntityTypeTags.RAIDERS)       //村庄袭击小队
                ||type.equals(EntityType.HOGLIN)
                ||type.equals(EntityType.ZOGLIN)        //疣猪兽
                ||type.equals(EntityType.PIGLIN)
                ||type.equals(EntityType.PIGLIN_BRUTE)
                ||type.equals(EntityType.ZOMBIFIED_PIGLIN)//猪灵们
                ||type.equals(EntityType.GHAST)         //恶魂
                ||type.equals(EntityType.BLAZE)         //烈焰人
                ||type.equals(EntityType.MAGMA_CUBE)    //岩浆怪
                ||type.equals(EntityType.ENDERMAN)      //末影人
                ||type.equals(EntityType.ENDERMITE)     //末影螨
                ||type.equals(EntityType.SHULKER)       //潜影贝
        ) {
            return EntityCategory.STRONGMOBS;
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