package com.restonecash.sansystem.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

public class AttributeConfig
{
    // 1. 定义配置规范（spec）和配置实例（instance）
    public static final ForgeConfigSpec SPEC;
    public static final AttributeConfig INSTANCE;

    static {
        // 使用Pair将配置实例和规范绑定在一起
        final Pair<AttributeConfig, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder()
                .configure(AttributeConfig::new);
        SPEC = pair.getRight();
        INSTANCE = pair.getLeft();
    }

    // 2. 在构造函数中定义配置项
    public AttributeConfig(ForgeConfigSpec.Builder builder) {
        // 创建一个配置分类，让配置文件结构更清晰
        builder.push("entity_attributes");

        // 为不同实体定义基础值，使用 defineInRange 限定数值范围
        zombiePollution = builder
                .comment("基础污染值 for Zombie")
                .defineInRange("zombiePollution", 5.0, 0.0, 100.0);

        skeletonPollution = builder
                .comment("基础污染值 for Skeleton")
                .defineInRange("skeletonPollution", 6.0, 0.0, 100.0);

        // 方式A：定义一个包含所有实体配置的Map，方便统一管理
        // 这里以字符串为键，方便在配置文件中阅读
        customValues = builder
                .comment("自定义实体属性值映射，格式: \"entity_id\": value")
                .define("custom_values", new HashMap<String, Double>());

        builder.pop();
    }

    // 3. 声明配置字段
    public final ForgeConfigSpec.DoubleValue zombiePollution;
    public final ForgeConfigSpec.DoubleValue skeletonPollution;
    public final ForgeConfigSpec.ConfigValue<Map<String, Double>> customValues;
}
