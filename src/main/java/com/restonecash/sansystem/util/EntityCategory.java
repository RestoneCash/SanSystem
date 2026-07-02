package com.restonecash.sansystem.util;

public enum EntityCategory
{
    FRIENDLY,    // 友好生物（牛、羊、村民等）
    NEUTRAL,     // 中立生物（僵尸猪灵、末影人等，攻击条件触发）
    HOSTILE,     // 敌对生物（僵尸、骷髅、蜘蛛等）
    BOSS,        // BOSS生物（末影龙、凋灵等）
    OTHER;       // 其他（如蝙蝠、鱿鱼等不重要的）
}
