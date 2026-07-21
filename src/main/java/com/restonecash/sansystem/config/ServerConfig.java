package com.restonecash.sansystem.config;

import com.restonecash.sansystem.SanSystem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模组服务端配置类（非实体属性部分）
 * 管理合成限制、物品/方块掉San等配置
 */
@Mod.EventBusSubscriber(modid = SanSystem.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ServerConfig
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // ---- 实体默认属性已迁移到 AttributeConfig，此处移除 ----
    // 保留合成、物品、方块掉San配置

    private static final ForgeConfigSpec.ConfigValue<String> CRAFTING_SANITY_REQUIREMENTS = BUILDER
            .comment("合成物品San值下限配置，格式: item_id:minSanity")
            .define("craftingSanityRequirements", "minecraft:ender_eye:50.0;minecraft:ender_pearl:30.0;minecraft:totem_of_undying:60.0");

    private static final ForgeConfigSpec.ConfigValue<String> ITEM_SANITY_DRAIN = BUILDER
            .comment("物品掉San配置（手持或使用时每秒消耗的San值），格式: item_id:ticksPerDrain,sanityPerDrain")
            .define("itemSanityDrain", "minecraft:ender_eye:20,1.0;minecraft:ender_pearl:40,0.5;minecraft:totem_of_undying:60,0.3");

    private static final ForgeConfigSpec.ConfigValue<String> BLOCK_SANITY_DRAIN = BUILDER
            .comment("方块掉San配置（站在上方时每秒消耗的San值），格式: block_id:ticksPerDrain,sanityPerDrain")
            .define("blockSanityDrain", "minecraft:end_stone:40,1.0;minecraft:obsidian:60,0.5;minecraft:netherrack:80,0.3");

    // ---- 玩法机制配置（阈值、影怪、恢复）----
    private static final ForgeConfigSpec.DoubleValue NAUSEA_THRESHOLD = BUILDER
            .comment("恶心效果触发阈值（San百分比，0.0~1.0）")
            .defineInRange("nauseaThreshold", 0.25, 0.0, 1.0);
    private static final ForgeConfigSpec.DoubleValue BLINDNESS_THRESHOLD = BUILDER
            .comment("失明效果触发阈值（San百分比，0.0~1.0）")
            .defineInRange("blindnessThreshold", 0.10, 0.0, 1.0);
    private static final ForgeConfigSpec.DoubleValue INPUT_INVERSION_THRESHOLD = BUILDER
            .comment("输入反转触发阈值（San百分比，0.0~1.0）")
            .defineInRange("inputInversionThreshold", 0.15, 0.0, 1.0);
    private static final ForgeConfigSpec.DoubleValue SHADOW_SPAWN_THRESHOLD_50 = BUILDER
            .comment("影怪开始生成阈值（San百分比，低于此值开始生成）")
            .defineInRange("shadowSpawnThreshold", 0.50, 0.0, 1.0);
    private static final ForgeConfigSpec.DoubleValue SHADOW_SPAWN_THRESHOLD_20 = BUILDER
            .comment("影怪大量生成阈值（San百分比，低于此值大量生成）")
            .defineInRange("shadowSpawnCriticalThreshold", 0.20, 0.0, 1.0);
    private static final ForgeConfigSpec.IntValue SPAWN_INTERVAL_LOW_SAN = BUILDER
            .comment("低San时影怪生成间隔（tick）")
            .defineInRange("shadowSpawnInterval", 100, 1, 72000);
    private static final ForgeConfigSpec.IntValue SPAWN_INTERVAL_CRITICAL_SAN = BUILDER
            .comment("危急San时影怪生成间隔（tick）")
            .defineInRange("shadowSpawnCriticalInterval", 20, 1, 72000);
    private static final ForgeConfigSpec.IntValue MAX_SHADOWS_LOW_SAN = BUILDER
            .comment("低San时影怪最大数量")
            .defineInRange("shadowMaxCount", 3, 0, 100);
    private static final ForgeConfigSpec.IntValue MAX_SHADOWS_CRITICAL_SAN = BUILDER
            .comment("危急San时影怪最大数量")
            .defineInRange("shadowMaxCriticalCount", 10, 0, 100);
    private static final ForgeConfigSpec.IntValue PEACEFUL_THRESHOLD_TICKS = BUILDER
            .comment("进入安全区所需脱战时间（tick，受击后需等待此时间才开始恢复）")
            .defineInRange("peacefulTicks", 600, 0, 72000);
    private static final ForgeConfigSpec.IntValue SLOWNESS_DECAY_INTERVAL = BUILDER
            .comment("缓慢效果衰减间隔（tick，每过此间隔减少一层缓慢）")
            .defineInRange("slownessDecayInterval", 200, 20, 72000);
    private static final ForgeConfigSpec.IntValue GRACE_PERIOD_TICKS = BUILDER
            .comment("效果宽限期（tick，San恢复后效果渐隐的时长）")
            .defineInRange("gracePeriodTicks", 100, 0, 600);
    private static final ForgeConfigSpec.DoubleValue BASE_RECOVERY_PER_TICK = BUILDER
            .comment("基础理智恢复速率（每tick恢复量）")
            .defineInRange("baseRecoveryPerTick", 0.01, 0.0, 100.0);

    private static final ForgeConfigSpec.DoubleValue SHADOW_ATTACK_DAMAGE = BUILDER
            .comment("影怪攻击伤害")
            .defineInRange("shadowAttackDamage", 2.0, 0.0, 1000.0);
    private static final ForgeConfigSpec.DoubleValue SHADOW_MAX_HEALTH = BUILDER
            .comment("影怪最大生命值")
            .defineInRange("shadowMaxHealth", 1.0, 0.0, 10000.0);
    private static final ForgeConfigSpec.DoubleValue SHADOW_MOVE_SPEED = BUILDER
            .comment("影怪移动速度")
            .defineInRange("shadowMoveSpeed", 0.5, 0.0, 100.0);
    private static final ForgeConfigSpec.IntValue SHADOW_ATTACK_WINDUP = BUILDER
            .comment("影怪攻击前摇（tick）")
            .defineInRange("shadowAttackWindups", 16, 0, 200);
    private static final ForgeConfigSpec.IntValue SHADOW_TELEPORT_COOLDOWN = BUILDER
            .comment("影怪瞬移冷却（tick）")
            .defineInRange("shadowTeleportCooldown", 600, 0, 72000);
    private static final ForgeConfigSpec.DoubleValue SHADOW_FOLLOW_RANGE = BUILDER
            .comment("影怪追踪范围（格）")
            .defineInRange("shadowFollowRange", 30.0, 1.0, 1000.0);
    private static final ForgeConfigSpec.DoubleValue SHADOW_DESPAWN_SAN_THRESHOLD = BUILDER
            .comment("影怪消失阈值（目标San百分比，高于此值影怪消失）")
            .defineInRange("shadowDespawnSanThreshold", 0.50, 0.0, 1.0);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    // 缓存（实体属性已移除）
    public static Map<Item, Float> craftingSanityRequirements = new ConcurrentHashMap<>();
    public static Map<Item, SanityDrainConfig> itemSanityDrain = new ConcurrentHashMap<>();
    public static Map<Block, SanityDrainConfig> blockSanityDrain = new ConcurrentHashMap<>();

    public static float nauseaThreshold = 0.25f;
    public static float blindnessThreshold = 0.10f;
    public static float inputInversionThreshold = 0.15f;
    public static float shadowSpawnThreshold = 0.50f;
    public static float shadowSpawnCriticalThreshold = 0.20f;
    public static int shadowSpawnInterval = 100;
    public static int shadowSpawnCriticalInterval = 20;
    public static int shadowMaxCount = 3;
    public static int shadowMaxCriticalCount = 10;
    public static int peacefulTicks = 600;
    public static int slownessDecayInterval = 200;
    public static int gracePeriodTicks = 100;
    public static float baseRecoveryPerTick = 0.01f;
    public static double shadowAttackDamage = 2.0;
    public static double shadowMaxHealth = 1.0;
    public static double shadowMoveSpeed = 0.5;
    public static int shadowAttackWindups = 16;
    public static int shadowTeleportCooldown = 600;
    public static double shadowFollowRange = 30.0;
    public static double shadowDespawnSanThreshold = 0.50;

    public record SanityDrainConfig(int ticksPerDrain, float sanityPerDrain) {}

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        if (event.getConfig().getSpec() != SPEC) return;

        craftingSanityRequirements.clear();
        itemSanityDrain.clear();
        blockSanityDrain.clear();

        parseCraftingRequirements(CRAFTING_SANITY_REQUIREMENTS.get());
        parseSanityDrain(ITEM_SANITY_DRAIN.get(), ForgeRegistries.ITEMS, itemSanityDrain);
        parseSanityDrain(BLOCK_SANITY_DRAIN.get(), ForgeRegistries.BLOCKS, blockSanityDrain);

        nauseaThreshold = NAUSEA_THRESHOLD.get().floatValue();
        blindnessThreshold = BLINDNESS_THRESHOLD.get().floatValue();
        inputInversionThreshold = INPUT_INVERSION_THRESHOLD.get().floatValue();
        shadowSpawnThreshold = SHADOW_SPAWN_THRESHOLD_50.get().floatValue();
        shadowSpawnCriticalThreshold = SHADOW_SPAWN_THRESHOLD_20.get().floatValue();
        shadowSpawnInterval = SPAWN_INTERVAL_LOW_SAN.get();
        shadowSpawnCriticalInterval = SPAWN_INTERVAL_CRITICAL_SAN.get();
        shadowMaxCount = MAX_SHADOWS_LOW_SAN.get();
        shadowMaxCriticalCount = MAX_SHADOWS_CRITICAL_SAN.get();
        peacefulTicks = PEACEFUL_THRESHOLD_TICKS.get();
        slownessDecayInterval = SLOWNESS_DECAY_INTERVAL.get();
        gracePeriodTicks = GRACE_PERIOD_TICKS.get();
        baseRecoveryPerTick = BASE_RECOVERY_PER_TICK.get().floatValue();
        shadowAttackDamage = SHADOW_ATTACK_DAMAGE.get();
        shadowMaxHealth = SHADOW_MAX_HEALTH.get();
        shadowMoveSpeed = SHADOW_MOVE_SPEED.get();
        shadowAttackWindups = SHADOW_ATTACK_WINDUP.get();
        shadowTeleportCooldown = SHADOW_TELEPORT_COOLDOWN.get();
        shadowFollowRange = SHADOW_FOLLOW_RANGE.get();
        shadowDespawnSanThreshold = SHADOW_DESPAWN_SAN_THRESHOLD.get();
    }

    private static void parseCraftingRequirements(String config)
    {
        List<ConfigEntryParser.ParsedEntry> entries = ConfigEntryParser.parseEntriesWithValueCount(config, 1);
        for (ConfigEntryParser.ParsedEntry entry : entries) {
            ResourceLocation id = ResourceLocation.tryParse(entry.id());
            if (id == null) continue;
            Item item = ForgeRegistries.ITEMS.getValue(id);
            if (item == null) continue;
            try {
                float minSanity = Float.parseFloat(entry.values()[0]);
                craftingSanityRequirements.put(item, minSanity);
            } catch (NumberFormatException e) {
                SanSystem.LOGGER.warn("Invalid crafting sanity requirement: {}", entry.id());
            }
        }
    }

    // ★ 修正泛型方法，支持 Item 和 Block
    private static <T> void parseSanityDrain(String config, IForgeRegistry<T> registry, Map<T, SanityDrainConfig> targetMap)
    {
        List<ConfigEntryParser.ParsedEntry> entries = ConfigEntryParser.parseEntriesWithValueCount(config, 2);
        for (ConfigEntryParser.ParsedEntry entry : entries) {
            ResourceLocation id = ResourceLocation.tryParse(entry.id());
            if (id == null) continue;
            T obj = registry.getValue(id);
            if (obj == null) continue;
            try {
                int ticksPerDrain = Integer.parseInt(entry.values()[0]);
                float sanityPerDrain = Float.parseFloat(entry.values()[1]);
                targetMap.put(obj, new SanityDrainConfig(ticksPerDrain, sanityPerDrain));
            } catch (NumberFormatException e) {
                SanSystem.LOGGER.warn("Invalid sanity drain config: {}", entry.id());
            }
        }
    }

    // ---- 移除 getEntityDefaults，如需获取实体属性请使用 AttributeConfig.INSTANCE.getAttributes() ----
    public static float getCraftingSanityRequirement(Item item) {
        return craftingSanityRequirements.getOrDefault(item, 0.0f);
    }

    public static SanityDrainConfig getItemSanityDrain(Item item) {
        return itemSanityDrain.get(item);
    }

    public static SanityDrainConfig getBlockSanityDrain(Block block) {
        return blockSanityDrain.get(block);
    }
}