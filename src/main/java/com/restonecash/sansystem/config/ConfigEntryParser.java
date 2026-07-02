package com.restonecash.sansystem.config;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置条目解析器
 * 负责将配置字符串解析为结构化的条目列表
 *
 * 【设计说明】
 * 此解析器是纯 Java 实现，不依赖任何 Minecraft API，可在纯 Java 环境中测试
 * 
 * 配置格式：id:value1,value2;id:value1,value2,...
 * 其中：
 * - 分号(;)分隔多个条目
 * - 冒号(:)分隔 ID 和值部分
 * - 逗号(,)分隔多个值
 * 
 * 解析规则：
 * 1. 空白字符会被自动去除
 * 2. 格式错误的条目会被跳过（不会抛出异常）
 * 3. 返回解析成功的条目列表
 */
public class ConfigEntryParser
{
    /**
     * 解析后的配置条目记录类
     * @param id 资源位置 ID（如 "minecraft:player"）
     * @param values 值数组（原始字符串，未进行类型转换）
     */
    public record ParsedEntry(String id, String[] values) {}

    // 默认分隔符
    private static final String ENTRY_DELIMITER = ";";
    private static final String ID_VALUE_DELIMITER = ":";
    private static final String VALUE_DELIMITER = ",";

    /**
     * 解析配置字符串（使用默认分隔符）
     * @param config 配置字符串，格式: id:value1,value2;id:value1,value2,...
     * @return 解析成功的条目列表
     */
    public static List<ParsedEntry> parseEntries(String config)
    {
        return parseEntries(config, ENTRY_DELIMITER, ID_VALUE_DELIMITER, VALUE_DELIMITER);
    }

    /**
     * 解析配置字符串（自定义分隔符）
     * @param config 配置字符串
     * @param entryDelimiter 条目分隔符（如 ";"）
     * @param idValueDelimiter ID与值分隔符（如 ":"）
     * @param valueDelimiter 值分隔符（如 ","）
     * @return 解析成功的条目列表
     */
    public static List<ParsedEntry> parseEntries(String config, String entryDelimiter,
                                                 String idValueDelimiter, String valueDelimiter)
    {
        List<ParsedEntry> entries = new ArrayList<>();

        // 空配置直接返回空列表
        if (config == null || config.trim().isEmpty())
        {
            return entries;
        }

        // 按条目分隔符拆分
        String[] rawEntries = config.split(entryDelimiter);

        for (String rawEntry : rawEntries)
        {
            // 跳过空条目
            if (rawEntry == null || rawEntry.trim().isEmpty())
            {
                continue;
            }

            // 按 ID-值分隔符拆分
            String[] parts = rawEntry.split(idValueDelimiter);

            // 格式校验：必须有 ID 和值两部分
            if (parts.length != 2)
            {
                continue;
            }

            String id = parts[0].trim();
            String valuesPart = parts[1].trim();

            // 跳过空 ID 或空值部分
            if (id.isEmpty() || valuesPart.isEmpty())
            {
                continue;
            }

            // 按值分隔符拆分
            String[] values = valuesPart.split(valueDelimiter);

            // 去除每个值的空白
            for (int i = 0; i < values.length; i++)
            {
                values[i] = values[i].trim();
            }

            // 添加解析成功的条目
            entries.add(new ParsedEntry(id, values));
        }

        return entries;
    }

    /**
     * 解析配置字符串并验证值的数量
     * @param config 配置字符串
     * @param expectedValueCount 期望的值数量（-1 表示不验证）
     * @return 解析成功且值数量符合要求的条目列表
     */
    public static List<ParsedEntry> parseEntriesWithValueCount(String config, int expectedValueCount)
    {
        List<ParsedEntry> allEntries = parseEntries(config);
        List<ParsedEntry> filtered = new ArrayList<>();

        for (ParsedEntry entry : allEntries)
        {
            if (expectedValueCount < 0 || entry.values().length == expectedValueCount)
            {
                filtered.add(entry);
            }
        }

        return filtered;
    }
}
