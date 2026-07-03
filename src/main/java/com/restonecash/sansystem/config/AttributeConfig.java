package com.restonecash.sansystem.config;

import com.restonecash.sansystem.api.config.DefaultConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class AttributeConfig
{
    // 定义配置规范（spec）和配置实例（instance）
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final AttributeConfigParameters DEFAULT_CONFIG = new AttributeConfigParameters(null, () -> 0,  () -> 0, () ->0, () -> 150, () -> 0,  () -> true);
    public static final ForgeConfigSpec SPEC;
    public static final AttributeConfig INSTANCE;

    // 玩家属性
    public final ForgeConfigSpec.DoubleValue playerPollution;
    public final ForgeConfigSpec.DoubleValue playerMentalRecover;
    public final ForgeConfigSpec.DoubleValue playerMentalResilence;
    public final ForgeConfigSpec.DoubleValue playerMaxSan;
    public final ForgeConfigSpec.BooleanValue playerIfSanKill;



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
    // 存储所有独立配置：key=ID，value=单条参数
    private static final Map<String, AttributeConfigParameters> ATTRIBUTE_CONFIGS = new HashMap<>();


    static {
        final Pair<AttributeConfig, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(AttributeConfig::new);
        SPEC = pair.getRight();
        INSTANCE = pair.getLeft();
    }

    // 个别实体的覆盖值（键为实体注册名，如 "minecraft:zombie"）
    public final ForgeConfigSpec.ConfigValue<Map<String, Double>> entityOverrides;


    public AttributeConfig(ForgeConfigSpec.Builder builder) {
        builder.comment("san属性配置");
        builder.push("attribute_values");


        playerPollution = builder
                .comment("玩家污染值")
                .defineInRange("playerPollution", 0.0, 0.0, 100000.0);
        playerMentalRecover=builder
                .comment("玩家精神恢复")
                .defineInRange("playerMentalRecover", 1.0, 0.0, 100000.0);
        playerMentalResilence=builder
                .comment("玩家精神韧性")
                .defineInRange("playerMentalResilence", 3.0, 0.0, 100000.0);
        playerMaxSan=builder
                .comment("玩家最大san值")
                .defineInRange("playerMaxSan", 150.0, 0.0, 100000.0);
        playerIfSanKill=builder
                .comment("玩家san归零时是否死亡")
                .define("playerIfSanKill", true);



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
