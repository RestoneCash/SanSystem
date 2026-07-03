package com.restonecash.sansystem.config;

import com.restonecash.sansystem.api.config.DefaultConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class AttributeConfig
{
    // 1. 定义配置规范（spec）和配置实例（instance）
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final AttributeConfigParameters DEFAULT_CONFIG = new AttributeConfigParameters(null, () -> 0,  () -> 0, () ->0, () -> 150, () -> 0,  () -> true);

    // 每个分类的基础值（带范围限制）
    public final ForgeConfigSpec.DoubleValue playerBase;
    public final ForgeConfigSpec.DoubleValue weakFriendlyBase;
    public final ForgeConfigSpec.DoubleValue friendlyBase;
    public final ForgeConfigSpec.DoubleValue neutralBase;
    public final ForgeConfigSpec.DoubleValue commonMobsBase;
    public final ForgeConfigSpec.DoubleValue strongMobsBase;
    public final ForgeConfigSpec.DoubleValue enderdragonBase;
    public final ForgeConfigSpec.DoubleValue witherBase;
    public final ForgeConfigSpec.DoubleValue wardenBase;
    public final ForgeConfigSpec.DoubleValue otherBase;

    //https://forge.gemwire.uk/wiki/Configs
    private static final Map<String, AttributeConfigParameters> ATTRIBUTE_CONFIGS = new HashMap<>();

    static {

    }

    // 个别实体的覆盖值（键为实体注册名，如 "minecraft:zombie"）
    public final ForgeConfigSpec.ConfigValue<Map<String, Double>> entityOverrides;



    public AttributeConfig(ForgeConfigSpec.Builder builder) {
        builder.push("attribute_values");

        friendlyBase = builder
                .comment("友好生物的基础属性值")
                .defineInRange("friendly", 2.0, 0.0, 100.0);

        neutralBase = builder
                .comment("中立生物的基础属性值")
                .defineInRange("neutral", 5.0, 0.0, 100.0);

        hostileBase = builder
                .comment("敌对生物的基础属性值")
                .defineInRange("hostile", 10.0, 0.0, 100.0);

        bossBase = builder
                .comment("BOSS生物的基础属性值")
                .defineInRange("boss", 25.0, 0.0, 100.0);

        otherBase = builder
                .comment("其他生物的基础属性值")
                .defineInRange("other", 1.0, 0.0, 100.0);

        builder.pop();
        builder.push("entity_overrides");

        entityOverrides = builder
                .comment("为特定实体单独指定基础值（会覆盖分类值）",
                        "格式：\"注册名\": 数值，例如 \"minecraft:zombie\": 12.0")
                .define("overrides", new HashMap<>());

        builder.pop();
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
