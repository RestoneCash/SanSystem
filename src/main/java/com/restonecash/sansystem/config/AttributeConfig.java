package com.restonecash.sansystem.config;

import com.restonecash.sansystem.api.config.DefaultConfig;
import com.restonecash.sansystem.util.EntityCategory;
import com.restonecash.sansystem.util.EntityClassificationHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

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
    public final ForgeConfigSpec.DoubleValue playerMentalResilence;
    public final ForgeConfigSpec.DoubleValue playerMaxSan;
    public final ForgeConfigSpec.BooleanValue playerIfSanKill;

    //弱小生物
    public final ForgeConfigSpec.DoubleValue weakPollution;
    public final ForgeConfigSpec.DoubleValue weakMentalRecover;
    public final ForgeConfigSpec.DoubleValue weakMentalResilence;
    public final ForgeConfigSpec.DoubleValue weakMaxSan;
    public final ForgeConfigSpec.BooleanValue weakIfSanKill;

    //友好生物
    public final ForgeConfigSpec.DoubleValue friendlyPollution;
    public final ForgeConfigSpec.DoubleValue friendlyMentalRecover;
    public final ForgeConfigSpec.DoubleValue friendlyMentalResilence;
    public final ForgeConfigSpec.DoubleValue friendlyMaxSan;
    public final ForgeConfigSpec.BooleanValue friendlyIfSanKill;

    //中立生物
    public final ForgeConfigSpec.DoubleValue neutralPollution;
    public final ForgeConfigSpec.DoubleValue neutralMentalRecover;
    public final ForgeConfigSpec.DoubleValue neutralMentalResilence;
    public final ForgeConfigSpec.DoubleValue neutralMaxSan;
    public final ForgeConfigSpec.BooleanValue neutralIfSanKill;

    //普通怪物
    public final ForgeConfigSpec.DoubleValue commonMobsPollution;
    public final ForgeConfigSpec.DoubleValue commonMobsMentalRecover;
    public final ForgeConfigSpec.DoubleValue commonMobsMentalResilence;
    public final ForgeConfigSpec.DoubleValue commonMobsMaxSan;
    public final ForgeConfigSpec.BooleanValue commonMobsIfSanKill;

    //强力怪物
    public final ForgeConfigSpec.DoubleValue strongMobsPollution;
    public final ForgeConfigSpec.DoubleValue strongMobsMentalRecover;
    public final ForgeConfigSpec.DoubleValue strongMobsMentalResilence;
    public final ForgeConfigSpec.DoubleValue strongMobsMaxSan;
    public final ForgeConfigSpec.BooleanValue strongMobsIfSanKill;

    //末影龙
    public final ForgeConfigSpec.DoubleValue enderdragonPollution;
    public final ForgeConfigSpec.DoubleValue enderdragonMentalRecover;
    public final ForgeConfigSpec.DoubleValue enderdragonMentalResilence;
    public final ForgeConfigSpec.DoubleValue enderdragonMaxSan;
    public final ForgeConfigSpec.BooleanValue enderdragonIfSanKill;

    //凋灵
    public final ForgeConfigSpec.DoubleValue witherPollution;
    public final ForgeConfigSpec.DoubleValue witherMentalRecover;
    public final ForgeConfigSpec.DoubleValue witherMentalResilence;
    public final ForgeConfigSpec.DoubleValue witherMaxSan;
    public final ForgeConfigSpec.BooleanValue witherIfSanKill;

    //监守者
    public final ForgeConfigSpec.DoubleValue wardenPollution;
    public final ForgeConfigSpec.DoubleValue wardenMentalRecover;
    public final ForgeConfigSpec.DoubleValue wardenMentalResilence;
    public final ForgeConfigSpec.DoubleValue wardenMaxSan;
    public final ForgeConfigSpec.BooleanValue wardenIfSanKill;

    //其他
    public final ForgeConfigSpec.DoubleValue otherPollution;
    public final ForgeConfigSpec.DoubleValue otherMentalRecover;
    public final ForgeConfigSpec.DoubleValue otherMentalResilence;
    public final ForgeConfigSpec.DoubleValue otherMaxSan;
    public final ForgeConfigSpec.BooleanValue otherIfSanKill;

    //覆盖配置
    public final ForgeConfigSpec.ConfigValue<Map<String, Double>> overridePollution;
    public final ForgeConfigSpec.ConfigValue<Map<String, Double>> overrideMentalRecover;
    public final ForgeConfigSpec.ConfigValue<Map<String, Double>> overrideMentalResilience;
    public final ForgeConfigSpec.ConfigValue<Map<String, Double>> overrideMaxSan;
    public final ForgeConfigSpec.ConfigValue<Map<String, Boolean>> overrideIfSanKill;


    //https://forge.gemwire.uk/wiki/Configs
    // 存储所有独立配置：key=ID，value=单条参数
    private static final Map<String, AttributeConfigParameters> ATTRIBUTE_CONFIGS = new HashMap<>();



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
        playerMentalResilence=builder
                .comment("玩家精神韧性")
                .defineInRange("playerMentalResilence", 5.0, 0.0, 100000.0);
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
        weakMentalResilence=builder
                .comment("弱小生物精神韧性")
                .defineInRange("weakMentalResilence", 2.0, 0.0, 100000.0);
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
        friendlyMentalResilence=builder
                .comment("友好生物精神韧性")
                .defineInRange("friendlyMentalResilence", 3.0, 0.0, 100000.0);
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
        neutralMentalResilence=builder
                .comment("中立生物精神韧性")
                .defineInRange("neutralMentalResilence", 20.0, 0.0, 100000.0);
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
        commonMobsMentalResilence=builder
                .comment("普通怪物精神韧性")
                .defineInRange("commonMobsMentalResilence", 10.0, 0.0, 100000.0);
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
        strongMobsMentalResilence=builder
                .comment("强力怪物精神韧性")
                .defineInRange("strongMobsMentalResilence", 30.0, 0.0, 100000.0);
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
        enderdragonMentalResilence=builder
                .comment("末影龙精神韧性")
                .defineInRange("enderdragonMentalResilence", 50.0, 0.0, 100000.0);
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
        witherMentalResilence=builder
                .comment("凋灵精神韧性")
                .defineInRange("witherMentalResilence", 80.0, 0.0, 100000.0);
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
        wardenMentalResilence=builder
                .comment("监守者精神韧性")
                .defineInRange("wardenMentalResilence", 10.0, 0.0, 100000.0);
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
        otherMentalResilence=builder
                .comment("其他精神韧性")
                .defineInRange("otherMentalResilence", 0.0, 0.0, 100000.0);
        otherMaxSan=builder
                .comment("其他最大san值")
                .defineInRange("otherMaxSan", 100.0, 0.0, 100000.0);
        otherIfSanKill=builder
                .comment("其他san归零时是否死亡")
                .define("otherIfSanKill", false);

        builder.pop();


        builder.push("entity_overrides");

        overridePollution = builder
                .comment("覆盖指定实体的污染值", "格式: \"注册名\": 数值")
                .define("pollution", new HashMap<>());
        overrideMentalRecover = builder
                .comment("覆盖指定实体的精神恢复")
                .define("mentalRecover", new HashMap<>());
        overrideMentalResilience = builder
                .comment("覆盖指定实体的精神韧性")
                .define("mentalResilience", new HashMap<>());
        overrideMaxSan = builder
                .comment("覆盖指定实体的最大SAN值")
                .define("maxSan", new HashMap<>());
        overrideIfSanKill = builder
                .comment("覆盖指定实体的SAN归零是否死亡")
                .define("ifSanKill", new HashMap<>());
        builder.pop();
    }

    // ================== 修改点 6：核心方法，返回完整的属性包（支持覆盖） ==================
    public AttributeValues getAttributes(EntityType<?> type) {
        // 获取实体注册名
        String key = ForgeRegistries.ENTITY_TYPES.getKey(type).toString();

        // 先尝试从覆盖 Map 中取值，若没有则使用分类默认值
        double pollution = getOverrideOrFallback(key, overridePollution.get(), getCategoryPollution(type));
        double mentalRecover = getOverrideOrFallback(key, overrideMentalRecover.get(), getCategoryMentalRecover(type));
        double mentalResilience = getOverrideOrFallback(key, overrideMentalResilience.get(), getCategoryMentalResilience(type));
        double maxSan = getOverrideOrFallback(key, overrideMaxSan.get(), getCategoryMaxSan(type));
        boolean ifSanKill = overrideIfSanKill.get().containsKey(key)
                ? overrideIfSanKill.get().get(key)
                : getCategoryIfSanKill(type);

        return new AttributeValues(pollution, mentalRecover, mentalResilience, maxSan, ifSanKill);
    }

    // 辅助方法：从覆盖Map取值，若无则返回默认值
    private double getOverrideOrFallback(String key, Map<String, Double> overrideMap, double defaultValue) {
        return overrideMap.containsKey(key) ? overrideMap.get(key) : defaultValue;
    }

    // ================== 修改点 7：分类默认值获取方法（替代原来不可用的 switch） ==================
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


    public static class AttributeConfigParameters {

        final Supplier<Integer> POLLUTION;
        final Supplier<Integer> MENTALRECOVER;
        final Supplier<Integer> MENTALRESILIENCE;
        final Supplier<Integer> MAXSAN;
        final Supplier<Integer> MINSAN;
        final Supplier<Boolean> IFSANKILL;

        AttributeConfigParameters(
                DefaultConfig defaultConfig,
                Supplier<Integer> POLLUTION,
                Supplier<Integer> MENTALRECOVER,
                Supplier<Integer> MENTALRESILIENCE,
                Supplier<Integer> MAXSAN,
                Supplier<Integer> MINSAN,
                Supplier<Boolean> IFSANKILL) {
            this.POLLUTION=POLLUTION;
            this.MENTALRECOVER=MENTALRECOVER;
            this.MENTALRESILIENCE=MENTALRESILIENCE;
            this.MAXSAN=MAXSAN;
            this.MINSAN=MINSAN;
            this.IFSANKILL=IFSANKILL;
        }

        public int pollution() {
            return POLLUTION.get();
        }

        public int mentalRecover() {
            return MENTALRECOVER.get();
        }

        public int mentalReisilience() {
            return MENTALRESILIENCE.get();
        }

        public int maxSan() {
            return MAXSAN.get();
        }

        public int minSan() {
            return MINSAN.get();
        }

        public boolean ifSankill() {
            return IFSANKILL.get();
        }
    }

}
