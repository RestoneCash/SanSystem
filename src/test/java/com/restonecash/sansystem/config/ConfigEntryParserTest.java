package com.restonecash.sansystem.config;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ConfigEntryParser 单元测试
 * 验证配置字符串解析逻辑（纯 Java，无 Minecraft 依赖）
 */
public class ConfigEntryParserTest
{
    // ==================== 基本解析测试 ====================

    @Test
    void testParseBasicFormat()
    {
        String config = "a:1,2;b:3,4";
        List<ConfigEntryParser.ParsedEntry> entries = ConfigEntryParser.parseEntries(config);

        assertEquals(2, entries.size());
        
        ConfigEntryParser.ParsedEntry entry1 = entries.get(0);
        assertEquals("a", entry1.id());
        assertArrayEquals(new String[]{"1", "2"}, entry1.values());

        ConfigEntryParser.ParsedEntry entry2 = entries.get(1);
        assertEquals("b", entry2.id());
        assertArrayEquals(new String[]{"3", "4"}, entry2.values());
    }

    @Test
    void testParseSingleEntry()
    {
        String config = "minecraft:player:0.0,1.0,1.0,100.0";
        List<ConfigEntryParser.ParsedEntry> entries = ConfigEntryParser.parseEntries(config);

        assertEquals(1, entries.size());
        assertEquals("minecraft:player", entries.get(0).id());
        assertArrayEquals(new String[]{"0.0", "1.0", "1.0", "100.0"}, entries.get(0).values());
    }

    // ==================== 空白处理测试 ====================

    @Test
    void testParseWithWhitespace()
    {
        String config = "  minecraft:player : 0.0 , 1.0 ;  minecraft:zombie : 20.0 , 1.0  ";
        List<ConfigEntryParser.ParsedEntry> entries = ConfigEntryParser.parseEntries(config);

        assertEquals(2, entries.size());
        assertEquals("minecraft:player", entries.get(0).id());
        assertArrayEquals(new String[]{"0.0", "1.0"}, entries.get(0).values());

        assertEquals("minecraft:zombie", entries.get(1).id());
        assertArrayEquals(new String[]{"20.0", "1.0"}, entries.get(1).values());
    }

    @Test
    void testParseWithExtraSpaces()
    {
        String config = "item1 :  10  ,  20  ;  item2 :  30  ,  40  ";
        List<ConfigEntryParser.ParsedEntry> entries = ConfigEntryParser.parseEntries(config);

        assertEquals(2, entries.size());
        assertEquals("item1", entries.get(0).id());
        assertArrayEquals(new String[]{"10", "20"}, entries.get(0).values());
    }

    // ==================== 错误处理测试 ====================

    @Test
    void testParseEmptyString()
    {
        List<ConfigEntryParser.ParsedEntry> entries = ConfigEntryParser.parseEntries("");
        assertTrue(entries.isEmpty());
    }

    @Test
    void testParseNullString()
    {
        List<ConfigEntryParser.ParsedEntry> entries = ConfigEntryParser.parseEntries(null);
        assertTrue(entries.isEmpty());
    }

    @Test
    void testParseMissingColon()
    {
        String config = "minecraft:player:0.0,1.0;minecraft:zombie";
        List<ConfigEntryParser.ParsedEntry> entries = ConfigEntryParser.parseEntries(config);

        assertEquals(1, entries.size());
        assertEquals("minecraft:player", entries.get(0).id());
    }

    @Test
    void testParseEmptyId()
    {
        String config = ":0.0,1.0;minecraft:zombie:20.0,1.0";
        List<ConfigEntryParser.ParsedEntry> entries = ConfigEntryParser.parseEntries(config);

        assertEquals(1, entries.size());
        assertEquals("minecraft:zombie", entries.get(0).id());
    }

    @Test
    void testParseEmptyValues()
    {
        String config = "minecraft:player:;minecraft:zombie:20.0,1.0";
        List<ConfigEntryParser.ParsedEntry> entries = ConfigEntryParser.parseEntries(config);

        assertEquals(1, entries.size());
        assertEquals("minecraft:zombie", entries.get(0).id());
    }

    @Test
    void testParseMissingValue()
    {
        String config = "minecraft:player:0.0;minecraft:zombie:20.0,1.0";
        List<ConfigEntryParser.ParsedEntry> entries = ConfigEntryParser.parseEntries(config);

        assertEquals(2, entries.size());
        assertEquals("minecraft:player", entries.get(0).id());
        assertArrayEquals(new String[]{"0.0"}, entries.get(0).values());
    }

    // ==================== 复杂场景测试 ====================

    @Test
    void testParseEntityDefaultsFormat()
    {
        String config = "minecraft:player:0.0,1.0,1.0,100.0;minecraft:zombie:20.0,1.0,0.5,100.0";
        List<ConfigEntryParser.ParsedEntry> entries = ConfigEntryParser.parseEntries(config);

        assertEquals(2, entries.size());
        assertEquals("minecraft:player", entries.get(0).id());
        assertArrayEquals(new String[]{"0.0", "1.0", "1.0", "100.0"}, entries.get(0).values());

        assertEquals("minecraft:zombie", entries.get(1).id());
        assertArrayEquals(new String[]{"20.0", "1.0", "0.5", "100.0"}, entries.get(1).values());
    }

    @Test
    void testParseCraftingRequirementsFormat()
    {
        String config = "minecraft:ender_eye:50.0;minecraft:ender_pearl:30.0";
        List<ConfigEntryParser.ParsedEntry> entries = ConfigEntryParser.parseEntries(config);

        assertEquals(2, entries.size());
        assertEquals("minecraft:ender_eye", entries.get(0).id());
        assertArrayEquals(new String[]{"50.0"}, entries.get(0).values());

        assertEquals("minecraft:ender_pearl", entries.get(1).id());
        assertArrayEquals(new String[]{"30.0"}, entries.get(1).values());
    }

    @Test
    void testParseSanityDrainFormat()
    {
        String config = "minecraft:ender_eye:20,1.0;minecraft:ender_pearl:40,0.5";
        List<ConfigEntryParser.ParsedEntry> entries = ConfigEntryParser.parseEntries(config);

        assertEquals(2, entries.size());
        assertEquals("minecraft:ender_eye", entries.get(0).id());
        assertArrayEquals(new String[]{"20", "1.0"}, entries.get(0).values());

        assertEquals("minecraft:ender_pearl", entries.get(1).id());
        assertArrayEquals(new String[]{"40", "0.5"}, entries.get(1).values());
    }

    @Test
    void testParseWithExtraSemicolons()
    {
        String config = "a:1,2;;b:3,4;;;c:5,6";
        List<ConfigEntryParser.ParsedEntry> entries = ConfigEntryParser.parseEntries(config);

        assertEquals(3, entries.size());
        assertEquals("a", entries.get(0).id());
        assertEquals("b", entries.get(1).id());
        assertEquals("c", entries.get(2).id());
    }

    @Test
    void testParseWithInvalidEntries()
    {
        String config = "valid:1,2;missingcolon;another:3,4;:emptyid;valid2:5,6";
        List<ConfigEntryParser.ParsedEntry> entries = ConfigEntryParser.parseEntries(config);

        assertEquals(3, entries.size());
        assertEquals("valid", entries.get(0).id());
        assertEquals("another", entries.get(1).id());
        assertEquals("valid2", entries.get(2).id());
    }
}
