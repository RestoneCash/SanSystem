package com.restonecash.sansystem.config;

import com.restonecash.sansystem.SanSystem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
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
 * 模组服务端配置类
 * 管理理智系统的服务端可配置项，核心功能是定义并解析不同实体的默认理智属性
 * 订阅MOD总线的配置加载事件，在配置加载/重载时自动解析配置字符串并缓存结果
 *
 * 【架构说明】
 * 配置解析逻辑已下沉到 ConfigEntryParser
 * 本类只负责：
 * 1. 定义 Forge 配置项
 * 2. 使用 ConfigEntryParser 解析字符串
 * 3. 将解析结果解析到 Minecraft 注册表
 */
@Mod.EventBusSubscriber(modid = SanSystem.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ServerConfig
{
    // Forge配置规范构建器，用于声明配置项结构并生成最终配置文件
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // 实体默认属性配置项：字符串格式存储多个实体的初始理智属性
    // 配置格式：实体ID:污染值,韧性值,恢复值; 多个实体配置之间用分号分隔
    private static final ForgeConfigSpec.ConfigValue<String> ENTITY_DEFAULTS = BUILDER
            .comment("实体默认属性配置，格式: entity_id:pollution,resilience,recovery,maxSanity")
            .define("entityDefaults", "minecraft:player:0.0,1.0,1.0,100.0;minecraft:zombie:20.0,1.0,0.5,100.0;minecraft:enderman:20.0,2.0,0.3,100.0");

    private static final ForgeConfigSpec.ConfigValue<String> CRAFTING_SANITY_REQUIREMENTS = BUILDER
            .comment("合成物品San值下限配置，格式: item_id:minSanity")
            .define("craftingSanityRequirements", "minecraft:ender_eye:50.0;minecraft:ender_pearl:30.0;minecraft:totem_of_undying:60.0");

    private static final ForgeConfigSpec.ConfigValue<String> ITEM_SANITY_DRAIN = BUILDER
            .comment("物品掉San配置（手持或使用时每秒消耗的San值），格式: item_id:ticksPerDrain,sanityPerDrain")
            .define("itemSanityDrain", "minecraft:ender_eye:20,1.0;minecraft:ender_pearl:40,0.5;minecraft:totem_of_undying:60,0.3");

    private static final ForgeConfigSpec.ConfigValue<String> BLOCK_SANITY_DRAIN = BUILDER
            .comment("方块掉San配置（站在上方时每秒消耗的San值），格式: block_id:ticksPerDrain,sanityPerDrain")
            .define("blockSanityDrain", "minecraft:end_stone:40,1.0;minecraft:obsidian:60,0.5;minecraft:netherrack:80,0.3");

    // 构建完成的配置规范对象，用于注册到Forge配置系统
    public static final ForgeConfigSpec SPEC = BUILDER.build();

    // 解析后的实体默认属性缓存：键为实体类型，值为封装好的三项理智属性默认值
    public static Map<EntityType<?>, EntityAttributeDefaults> entityDefaults = new ConcurrentHashMap<>();

    // 解析后的合成物品San值下限缓存：键为物品，值为最低San值要求
    public static Map<Item, Float> craftingSanityRequirements = new ConcurrentHashMap<>();

    // 解析后的物品掉San配置缓存：键为物品，值为掉San配置
    public static Map<Block, SanityDrainConfig> itemSanityDrain = new ConcurrentHashMap<>();

    // 解析后方块掉San配置缓存：键为方块，值为掉San配置
    public static Map<Block, SanityDrainConfig> blockSanityDrain = new ConcurrentHashMap<>();

    /**
     * 实体默认属性记录类
     * 封装单个实体对应的三项理智属性默认数值，不可变数据载体
     */
    public record EntityAttributeDefaults(float pollution, float resilience, float recovery, float maxSanity) {}

    /**
     * San值消耗配置记录类
     * ticksPerDrain - 每次消耗的间隔tick数（20tick=1秒）
     * sanityPerDrain - 每次消耗的San值
     */
    public record SanityDrainConfig(int ticksPerDrain, float sanityPerDrain) {}

    /**
     * 配置加载/重载事件处理方法
     * 在服务端配置加载或重载时触发，将字符串格式的配置解析为「实体类型-属性」映射并缓存
     * @param event 配置加载事件对象
     */
    //@SubscribeEvent
    //static void onLoad(final ModConfigEvent event)
    //{
        // 清空原有缓存，避免重载配置时残留旧数据
    //    entityDefaults.clear();
    //   craftingSanityRequirements.clear();
    //    itemSanityDrain.clear();
    //    blockSanityDrain.clear();

        // 解析实体默认属性配置
        // 【委托说明】字符串解析由 ConfigEntryParser 完成，本方法只负责注册表解析
    //    parseEntityDefaults(ENTITY_DEFAULTS.get());

        // 解析合成物品San值下限配置
    //    parseCraftingRequirements(CRAFTING_SANITY_REQUIREMENTS.get());

        // 解析物品掉San配置
    //    parseSanityDrain(ITEM_SANITY_DRAIN.get(), ForgeRegistries.ITEMS, itemSanityDrain);

        // 解析方块掉San配置
        //parseSanityDrain(BLOCK_SANITY_DRAIN.get(), ForgeRegistries.BLOCKS, blockSanityDrain);
    //}

    /**
     * 解析实体默认属性配置
     * 格式：entity_id:pollution,resilience,recovery,maxSanity
     */
    private static void parseEntityDefaults(String config)
    {
        List<ConfigEntryParser.ParsedEntry> entries = ConfigEntryParser.parseEntriesWithValueCount(config, 4);

        for (ConfigEntryParser.ParsedEntry entry : entries)
        {
            ResourceLocation entityId = ResourceLocation.tryParse(entry.id());
            if (entityId == null) continue;

            EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(entityId);
            if (entityType == null) continue;

            try
            {
                float pollution = Float.parseFloat(entry.values()[0]);
                float resilience = Float.parseFloat(entry.values()[1]);
                float recovery = Float.parseFloat(entry.values()[2]);
                float maxSanity = Float.parseFloat(entry.values()[3]);
                entityDefaults.put(entityType, new EntityAttributeDefaults(pollution, resilience, recovery, maxSanity));
            }
            catch (NumberFormatException e)
            {
                SanSystem.LOGGER.warn("Invalid entity defaults config: {}", entry.id());
            }
        }
    }

    /**
     * 解析合成物品San值下限配置
     * 格式：item_id:minSanity
     */
    private static void parseCraftingRequirements(String config)
    {
        List<ConfigEntryParser.ParsedEntry> entries = ConfigEntryParser.parseEntriesWithValueCount(config, 1);

        for (ConfigEntryParser.ParsedEntry entry : entries)
        {
            ResourceLocation itemId = ResourceLocation.tryParse(entry.id());
            if (itemId == null) continue;

            Item item = ForgeRegistries.ITEMS.getValue(itemId);
            if (item == null) continue;

            try
            {
                float minSanity = Float.parseFloat(entry.values()[0]);
                craftingSanityRequirements.put(item, minSanity);
            }
            catch (NumberFormatException e)
            {
                SanSystem.LOGGER.warn("Invalid crafting sanity requirement: {}", entry.id());
            }
        }
    }

    /**
     * 解析掉San配置（物品/方块通用）
     * 格式：id:ticksPerDrain,sanityPerDrain
     *
     * @param config 配置字符串
     * @param registry Forge注册表（ITEMS 或 BLOCKS）
     * @param targetMap 目标缓存Map
     * @param <T> 注册表类型（Item 或 Block）
     */
    private static <T> void parseSanityDrain(String config, IForgeRegistry<Item> registry, Map<Block, SanityDrainConfig> targetMap)
    {
        List<ConfigEntryParser.ParsedEntry> entries = ConfigEntryParser.parseEntriesWithValueCount(config, 2);

        for (ConfigEntryParser.ParsedEntry entry : entries)
        {
            ResourceLocation id = ResourceLocation.tryParse(entry.id());
            if (id == null) continue;

            T obj = (T) registry.getValue(id);
            if (obj == null) continue;

            try
            {
                int ticksPerDrain = Integer.parseInt(entry.values()[0]);
                float sanityPerDrain = Float.parseFloat(entry.values()[1]);
                targetMap.put((Block) obj, new SanityDrainConfig(ticksPerDrain, sanityPerDrain));
            }
            catch (NumberFormatException e)
            {
                SanSystem.LOGGER.warn("Invalid sanity drain config: {}", entry.id());
            }
        }
    }

    /**
     * 获取指定实体类型的默认理智属性
     * 若该实体未在配置中定义，则返回通用兜底默认值
     * @param entityType 待查询的实体类型
     * @return 对应的实体默认属性对象
     */
    public static EntityAttributeDefaults getEntityDefaults(EntityType<?> entityType)
    {
        return entityDefaults.getOrDefault(entityType, new EntityAttributeDefaults(0.0f, 1.0f, 1.0f, 100.0f));
    }

    public static float getCraftingSanityRequirement(Item item)
    {
        return craftingSanityRequirements.getOrDefault(item, 0.0f);
    }

    public static SanityDrainConfig getItemSanityDrain(Item item)
    {
        return itemSanityDrain.get(item);
    }

    public static SanityDrainConfig getBlockSanityDrain(Block block)
    {
        return blockSanityDrain.get(block);
    }
}
