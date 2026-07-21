package com.restonecash.sansystem.config;

import com.restonecash.sansystem.util.EntityCategory;
import com.restonecash.sansystem.util.EntityClassificationHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AttributeConfig
{
    public static final AttributeConfig INSTANCE;
    public static final ForgeConfigSpec SPEC;

    static {
        Pair<AttributeConfig, ForgeConfigSpec> pair =
                new ForgeConfigSpec.Builder().configure(AttributeConfig::new);
        INSTANCE = pair.getLeft();
        SPEC = pair.getRight();
    }


    // 玩家属性
    public final ForgeConfigSpec.DoubleValue playerPollution;
    public final ForgeConfigSpec.DoubleValue playerMentalRecover;
    public final ForgeConfigSpec.DoubleValue playerMentalResilience;
    public final ForgeConfigSpec.DoubleValue playerMaxSan;
    public final ForgeConfigSpec.BooleanValue playerIfSanKill;

    //弱小生物
    public final ForgeConfigSpec.DoubleValue weakPollution;
    public final ForgeConfigSpec.DoubleValue weakMentalRecover;
    public final ForgeConfigSpec.DoubleValue weakMentalResilience;
    public final ForgeConfigSpec.DoubleValue weakMaxSan;
    public final ForgeConfigSpec.BooleanValue weakIfSanKill;

    //友好生物
    public final ForgeConfigSpec.DoubleValue friendlyPollution;
    public final ForgeConfigSpec.DoubleValue friendlyMentalRecover;
    public final ForgeConfigSpec.DoubleValue friendlyMentalResilience;
    public final ForgeConfigSpec.DoubleValue friendlyMaxSan;
    public final ForgeConfigSpec.BooleanValue friendlyIfSanKill;

    //中立生物
    public final ForgeConfigSpec.DoubleValue neutralPollution;
    public final ForgeConfigSpec.DoubleValue neutralMentalRecover;
    public final ForgeConfigSpec.DoubleValue neutralMentalResilience;
    public final ForgeConfigSpec.DoubleValue neutralMaxSan;
    public final ForgeConfigSpec.BooleanValue neutralIfSanKill;

    //普通怪物
    public final ForgeConfigSpec.DoubleValue commonMobsPollution;
    public final ForgeConfigSpec.DoubleValue commonMobsMentalRecover;
    public final ForgeConfigSpec.DoubleValue commonMobsMentalResilience;
    public final ForgeConfigSpec.DoubleValue commonMobsMaxSan;
    public final ForgeConfigSpec.BooleanValue commonMobsIfSanKill;

    //强力怪物
    public final ForgeConfigSpec.DoubleValue strongMobsPollution;
    public final ForgeConfigSpec.DoubleValue strongMobsMentalRecover;
    public final ForgeConfigSpec.DoubleValue strongMobsMentalResilience;
    public final ForgeConfigSpec.DoubleValue strongMobsMaxSan;
    public final ForgeConfigSpec.BooleanValue strongMobsIfSanKill;

    //末影龙
    public final ForgeConfigSpec.DoubleValue enderdragonPollution;
    public final ForgeConfigSpec.DoubleValue enderdragonMentalRecover;
    public final ForgeConfigSpec.DoubleValue enderdragonMentalResilience;
    public final ForgeConfigSpec.DoubleValue enderdragonMaxSan;
    public final ForgeConfigSpec.BooleanValue enderdragonIfSanKill;

    //凋灵
    public final ForgeConfigSpec.DoubleValue witherPollution;
    public final ForgeConfigSpec.DoubleValue witherMentalRecover;
    public final ForgeConfigSpec.DoubleValue witherMentalResilience;
    public final ForgeConfigSpec.DoubleValue witherMaxSan;
    public final ForgeConfigSpec.BooleanValue witherIfSanKill;

    //监守者
    public final ForgeConfigSpec.DoubleValue wardenPollution;
    public final ForgeConfigSpec.DoubleValue wardenMentalRecover;
    public final ForgeConfigSpec.DoubleValue wardenMentalResilience;
    public final ForgeConfigSpec.DoubleValue wardenMaxSan;
    public final ForgeConfigSpec.BooleanValue wardenIfSanKill;

    //其他
    public final ForgeConfigSpec.DoubleValue otherPollution;
    public final ForgeConfigSpec.DoubleValue otherMentalRecover;
    public final ForgeConfigSpec.DoubleValue otherMentalResilience;
    public final ForgeConfigSpec.DoubleValue otherMaxSan;
    public final ForgeConfigSpec.BooleanValue otherIfSanKill;

    private final Map<String, Double> overridePollution = new HashMap<>();
    private final Map<String, Double> overrideMentalRecover = new HashMap<>();
    private final Map<String, Double> overrideMentalResilience = new HashMap<>();
    private final Map<String, Double> overrideMaxSan = new HashMap<>();
    private final Map<String, Boolean> overrideIfSanKill = new HashMap<>();



    public AttributeConfig(ForgeConfigSpec.Builder builder) {
        builder.comment("san属性配置");
        builder.push("attribute_values");

        builder.push("player");
        playerPollution = builder
                .comment("玩家污染值")
                .defineInRange("playerPollution", 0.0, 0.0, 100000.0);
        playerMentalRecover=builder
                .comment("玩家精神恢复")
                .defineInRange("playerMentalRecover", 1.0, 0.0, 100000.0);
        playerMentalResilience =builder
                .comment("玩家精神韧性")
                .defineInRange("playerMentalResilience", 5.0, 0.0, 100000.0);
        playerMaxSan=builder
                .comment("玩家最大san值")
                .defineInRange("playerMaxSan", 150.0, 0.0, 100000.0);
        playerIfSanKill=builder
                .comment("玩家san归零时是否死亡")
                .define("playerIfSanKill", true);


        weakPollution = builder
                .comment("弱小生物污染值")
                .defineInRange("weakPollution", 0.0, 0.0, 100000.0);
        weakMentalRecover=builder
                .comment("弱小生物精神恢复")
                .defineInRange("weakMentalRecover", 1.0, 0.0, 100000.0);
        weakMentalResilience =builder
                .comment("弱小生物精神韧性")
                .defineInRange("weakMentalResilience", 2.0, 0.0, 100000.0);
        weakMaxSan=builder
                .comment("弱小生物最大san值")
                .defineInRange("weakMaxSan", 50.0, 0.0, 100000.0);
        weakIfSanKill=builder
                .comment("弱小生物san归零时是否死亡")
                .define("weakIfSanKill", true);


        friendlyPollution = builder
                .comment("友好生物污染值")
                .defineInRange("friendlyPollution", 0.0, 0.0, 100000.0);
        friendlyMentalRecover=builder
                .comment("友好生物精神恢复")
                .defineInRange("friendlyMentalRecover", 1.0, 0.0, 100000.0);
        friendlyMentalResilience =builder
                .comment("友好生物精神韧性")
                .defineInRange("friendlyMentalResilience", 3.0, 0.0, 100000.0);
        friendlyMaxSan=builder
                .comment("友好生物最大san值")
                .defineInRange("friendlyMaxSan", 100.0, 0.0, 100000.0);
        friendlyIfSanKill=builder
                .comment("友好生物san归零时是否死亡")
                .define("friendlyIfSanKill", true);


        neutralPollution = builder
                .comment("中立生物污染值")
                .defineInRange("neutralPollution", 0.0, 0.0, 100000.0);
        neutralMentalRecover=builder
                .comment("中立生物精神恢复")
                .defineInRange("neutralMentalRecover", 10.0, 0.0, 100000.0);
        neutralMentalResilience =builder
                .comment("中立生物精神韧性")
                .defineInRange("neutralMentalResilience", 20.0, 0.0, 100000.0);
        neutralMaxSan=builder
                .comment("中立生物最大san值")
                .defineInRange("neutralMaxSan", 300.0, 0.0, 100000.0);
        neutralIfSanKill=builder
                .comment("中立生物san归零时是否死亡")
                .define("neutralIfSanKill", true);


        commonMobsPollution = builder
                .comment("普通怪物污染值")
                .defineInRange("commonMobsPollution", 1.0, 0.0, 100000.0);
        commonMobsMentalRecover=builder
                .comment("普通怪物精神恢复")
                .defineInRange("commonMobsMentalRecover", 5.0, 0.0, 100000.0);
        commonMobsMentalResilience =builder
                .comment("普通怪物精神韧性")
                .defineInRange("commonMobsMentalResilience", 10.0, 0.0, 100000.0);
        commonMobsMaxSan=builder
                .comment("普通怪物最大san值")
                .defineInRange("commonMobsMaxSan", 200.0, 0.0, 100000.0);
        commonMobsIfSanKill=builder
                .comment("普通怪物san归零时是否死亡")
                .define("commonMobsIfSanKill", true);


        strongMobsPollution = builder
                .comment("强力怪物污染值")
                .defineInRange("strongMobsPollution", 5.0, 0.0, 100000.0);
        strongMobsMentalRecover=builder
                .comment("强力怪物精神恢复")
                .defineInRange("strongMobsMentalRecover", 15.0, 0.0, 100000.0);
        strongMobsMentalResilience =builder
                .comment("强力怪物精神韧性")
                .defineInRange("strongMobsMentalResilience", 30.0, 0.0, 100000.0);
        strongMobsMaxSan=builder
                .comment("强力怪物最大san值")
                .defineInRange("strongMobsMaxSan", 500.0, 0.0, 100000.0);
        strongMobsIfSanKill=builder
                .comment("强力怪物san归零时是否死亡")
                .define("strongMobsIfSanKill", true);


        enderdragonPollution = builder
                .comment("末影龙污染值")
                .defineInRange("enderdragonPollution", 5.0, 0.0, 100000.0);
        enderdragonMentalRecover=builder
                .comment("末影龙精神恢复")
                .defineInRange("enderdragonMentalRecover", 0.0, 0.0, 100000.0);
        enderdragonMentalResilience =builder
                .comment("末影龙精神韧性")
                .defineInRange("enderdragonMentalResilience", 50.0, 0.0, 100000.0);
        enderdragonMaxSan=builder
                .comment("末影龙最大san值")
                .defineInRange("enderdragonMaxSan", 2000.0, 0.0, 100000.0);
        enderdragonIfSanKill=builder
                .comment("末影龙san归零时是否死亡")
                .define("enderdragonIfSanKill", true);


        witherPollution = builder
                .comment("凋灵污染值")
                .defineInRange("witherPollution", 10.0, 0.0, 100000.0);
        witherMentalRecover=builder
                .comment("凋灵精神恢复")
                .defineInRange("witherMentalRecover", 0.0, 0.0, 100000.0);
        witherMentalResilience =builder
                .comment("凋灵精神韧性")
                .defineInRange("witherMentalResilience", 80.0, 0.0, 100000.0);
        witherMaxSan=builder
                .comment("凋灵最大san值")
                .defineInRange("witherMaxSan", 1000.0, 0.0, 100000.0);
        witherIfSanKill=builder
                .comment("凋灵san归零时是否死亡")
                .define("witherIfSanKill", true);


        wardenPollution = builder
                .comment("监守者污染值")
                .defineInRange("wardenPollution", 6.0, 0.0, 100000.0);
        wardenMentalRecover=builder
                .comment("监守者精神恢复")
                .defineInRange("wardenMentalRecover", 0.0, 0.0, 100000.0);
        wardenMentalResilience =builder
                .comment("监守者精神韧性")
                .defineInRange("wardenMentalResilience", 10.0, 0.0, 100000.0);
        wardenMaxSan=builder
                .comment("监守者最大san值")
                .defineInRange("wardenMaxSan", 100.0, 0.0, 100000.0);
        wardenIfSanKill=builder
                .comment("监守者san归零时是否死亡")
                .define("wardenIfSanKill", false);


        otherPollution = builder
                .comment("其他污染值")
                .defineInRange("otherPollution", 0.0, 0.0, 100000.0);
        otherMentalRecover=builder
                .comment("其他精神恢复")
                .defineInRange("otherMentalRecover", 0.0, 0.0, 100000.0);
        otherMentalResilience =builder
                .comment("其他精神韧性")
                .defineInRange("otherMentalResilience", 0.0, 0.0, 100000.0);
        otherMaxSan=builder
                .comment("其他最大san值")
                .defineInRange("otherMaxSan", 100.0, 0.0, 100000.0);
        otherIfSanKill=builder
                .comment("其他san归零时是否死亡")
                .define("otherIfSanKill", false);

        builder.pop();
    }

    /**
     * 定义属性值容器
     */
    public static class AttributeValues {
        public final double pollution;
        public final double mentalRecover;
        public final double mentalResilience;
        public final double maxSan;
        public final boolean ifSanKill;

        public AttributeValues(double pollution, double mentalRecover,
                               double mentalResilience, double maxSan, boolean ifSanKill) {
            this.pollution = pollution;
            this.mentalRecover = mentalRecover;
            this.mentalResilience = mentalResilience;
            this.maxSan = maxSan;
            this.ifSanKill = ifSanKill;
        }
    }

    /**
     * 返回完整的属性包（支持覆盖）
     * @param type
     * @return
     */
    private final Map<String, AttributeValues> attributesCache = new ConcurrentHashMap<>();

    public AttributeValues getAttributes(EntityType<?> type) {
        String key = ForgeRegistries.ENTITY_TYPES.getKey(type).toString();
        return attributesCache.computeIfAbsent(key, k -> {
            double pollution = getOverrideOrFallback(k, overridePollution, getCategoryPollution(type));
            double mentalRecover = getOverrideOrFallback(k, overrideMentalRecover, getCategoryMentalRecover(type));
            double mentalResilience = getOverrideOrFallback(k, overrideMentalResilience, getCategoryMentalResilience(type));
            double maxSan = getOverrideOrFallback(k, overrideMaxSan, getCategoryMaxSan(type));
            boolean ifSanKill = overrideIfSanKill.containsKey(k)
                    ? overrideIfSanKill.get(k)
                    : getCategoryIfSanKill(type);
            return new AttributeValues(pollution, mentalRecover, mentalResilience, maxSan, ifSanKill);
        });
    }

    public void clearCache() {
        attributesCache.clear();
    }

    // 辅助方法：从覆盖Map取值，若无则返回默认值
    private double getOverrideOrFallback(String key, Map<String, Double> overrideMap, double defaultValue) {
        return overrideMap.containsKey(key) ? overrideMap.get(key) : defaultValue;
    }

    // 分类默认值获取方法
    private EntityCategory getCategory(EntityType<?> type) {
        return EntityClassificationHelper.getCategory(type);
    }

    private double getCategoryPollution(EntityType<?> type) {
        switch (getCategory(type)) {
            case PLAYER:       return playerPollution.get();
            case WEAK:         return weakPollution.get();
            case FRIENDLY:     return friendlyPollution.get();
            case NEUTRAL:      return neutralPollution.get();
            case COMMONMOBS:  return commonMobsPollution.get();
            case STRONGMOBS:  return strongMobsPollution.get();
            case ENDERDRAGON:  return enderdragonPollution.get();
            case WITHER:       return witherPollution.get();
            case WARDEN:       return wardenPollution.get();
            default:           return otherPollution.get();
        }
    }

    private double getCategoryMentalRecover(EntityType<?> type) {
        switch (getCategory(type)) {
            case PLAYER:       return playerMentalRecover.get();
            case WEAK:         return weakMentalRecover.get();
            case FRIENDLY:     return friendlyMentalRecover.get();
            case NEUTRAL:      return neutralMentalRecover.get();
            case COMMONMOBS:  return commonMobsMentalRecover.get();
            case STRONGMOBS:  return strongMobsMentalRecover.get();
            case ENDERDRAGON:  return enderdragonMentalRecover.get();
            case WITHER:       return witherMentalRecover.get();
            case WARDEN:       return wardenMentalRecover.get();
            default:           return otherMentalRecover.get();
        }
    }

    private double getCategoryMentalResilience(EntityType<?> type) {
        switch (getCategory(type)) {
            case PLAYER:       return playerMentalResilience.get();
            case WEAK:         return weakMentalResilience.get();
            case FRIENDLY:     return friendlyMentalResilience.get();
            case NEUTRAL:      return neutralMentalResilience.get();
            case COMMONMOBS:  return commonMobsMentalResilience.get();
            case STRONGMOBS:  return strongMobsMentalResilience.get();
            case ENDERDRAGON:  return enderdragonMentalResilience.get();
            case WITHER:       return witherMentalResilience.get();
            case WARDEN:       return wardenMentalResilience.get();
            default:           return otherMentalResilience.get();
        }
    }

    private double getCategoryMaxSan(EntityType<?> type) {
        switch (getCategory(type)) {
            case PLAYER:       return playerMaxSan.get();
            case WEAK:         return weakMaxSan.get();
            case FRIENDLY:     return friendlyMaxSan.get();
            case NEUTRAL:      return neutralMaxSan.get();
            case COMMONMOBS:  return commonMobsMaxSan.get();
            case STRONGMOBS:  return strongMobsMaxSan.get();
            case ENDERDRAGON:  return enderdragonMaxSan.get();
            case WITHER:       return witherMaxSan.get();
            case WARDEN:       return wardenMaxSan.get();
            default:           return otherMaxSan.get();
        }
    }

    private boolean getCategoryIfSanKill(EntityType<?> type) {
        switch (getCategory(type)) {
            case PLAYER:       return playerIfSanKill.get();
            case WEAK:         return weakIfSanKill.get();
            case FRIENDLY:     return friendlyIfSanKill.get();
            case NEUTRAL:      return neutralIfSanKill.get();
            case COMMONMOBS:  return commonMobsIfSanKill.get();
            case STRONGMOBS:  return strongMobsIfSanKill.get();
            case ENDERDRAGON:  return enderdragonIfSanKill.get();
            case WITHER:       return witherIfSanKill.get();
            case WARDEN:       return wardenIfSanKill.get();
            default:           return otherIfSanKill.get();
        }
    }

}
