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

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    // 缓存（实体属性已移除）
    public static Map<Item, Float> craftingSanityRequirements = new ConcurrentHashMap<>();
    public static Map<Item, SanityDrainConfig> itemSanityDrain = new ConcurrentHashMap<>();
    public static Map<Block, SanityDrainConfig> blockSanityDrain = new ConcurrentHashMap<>();

    public record SanityDrainConfig(int ticksPerDrain, float sanityPerDrain) {}

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        craftingSanityRequirements.clear();
        itemSanityDrain.clear();
        blockSanityDrain.clear();

        parseCraftingRequirements(CRAFTING_SANITY_REQUIREMENTS.get());
        parseSanityDrain(ITEM_SANITY_DRAIN.get(), ForgeRegistries.ITEMS, itemSanityDrain);
        parseSanityDrain(BLOCK_SANITY_DRAIN.get(), ForgeRegistries.BLOCKS, blockSanityDrain);
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