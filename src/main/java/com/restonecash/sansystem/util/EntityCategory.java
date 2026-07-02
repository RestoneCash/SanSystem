package com.restonecash.sansystem.util;

public enum EntityCategory
{
    WEAKFRIENDLY,   // 弱小的友好生物（鱼、鸟、蝙蝠等）
    FRIENDLY,       // 友好生物（牛、羊、村民等）
    COMMONMOBS,     // 普通怪物（僵尸，骷髅，苦力怕，女巫，普通掠夺者等）
    STRONGMOBS,     // 敌对生物（烈焰人，恶魂，僵尸猪人，凋零骷髅，卫道士，唤魔者，劫掠兽，末影人等）
    ENDERDRAGON,    //末影龙
    WITHER,         //凋零
    WARDEN,         //坚守者
    PLAYER,         //玩家
    OTHER;          // 其他（以防万一）
}
