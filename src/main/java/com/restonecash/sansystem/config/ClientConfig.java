package com.restonecash.sansystem.config;

import com.restonecash.sansystem.SanSystem;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = SanSystem.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientConfig
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.IntValue SAN_BAR_X = BUILDER
            .comment("San条X坐标（从左向右）")
            .defineInRange("sanBarX", 10, 0, 1920);

    public static final ForgeConfigSpec.IntValue SAN_BAR_Y = BUILDER
            .comment("San条Y坐标（从上向下）")
            .defineInRange("sanBarY", 30, 0, 1080);

    public static final ForgeConfigSpec.IntValue SAN_BAR_WIDTH = BUILDER
            .comment("San条宽度")
            .defineInRange("sanBarWidth", 100, 20, 500);

    public static final ForgeConfigSpec.IntValue SAN_BAR_HEIGHT = BUILDER
            .comment("San条高度")
            .defineInRange("sanBarHeight", 8, 2, 50);

    public static final ForgeConfigSpec.BooleanValue SHOW_SAN_BAR = BUILDER
            .comment("是否显示San条")
            .define("showSanBar", true);

    public static final ForgeConfigSpec.BooleanValue SHOW_SAN_TEXT = BUILDER
            .comment("是否显示San值文字")
            .define("showSanText", true);

    public static final ForgeConfigSpec.DoubleValue WARNING_THRESHOLD = BUILDER
            .comment("San值警告阈值（百分比，低于此值时San条变红）")
            .defineInRange("warningThreshold", 0.3, 0.0, 1.0);

    public static final ForgeConfigSpec.DoubleValue DANGER_THRESHOLD = BUILDER
            .comment("San值危险阈值（百分比，低于此值时San条闪烁）")
            .defineInRange("dangerThreshold", 0.1, 0.0, 1.0);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    private static int sanBarX;
    private static int sanBarY;
    private static int sanBarWidth;
    private static int sanBarHeight;
    private static boolean showSanBar;
    private static boolean showSanText;
    private static double warningThreshold;
    private static double dangerThreshold;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        load();
    }

    public static void load()
    {
        sanBarX = SAN_BAR_X.get();
        sanBarY = SAN_BAR_Y.get();
        sanBarWidth = SAN_BAR_WIDTH.get();
        sanBarHeight = SAN_BAR_HEIGHT.get();
        showSanBar = SHOW_SAN_BAR.get();
        showSanText = SHOW_SAN_TEXT.get();
        warningThreshold = WARNING_THRESHOLD.get();
        dangerThreshold = DANGER_THRESHOLD.get();
    }

    public static int getSanBarX() { return sanBarX; }
    public static int getSanBarY() { return sanBarY; }
    public static int getSanBarWidth() { return sanBarWidth; }
    public static int getSanBarHeight() { return sanBarHeight; }
    public static boolean isShowSanBar() { return showSanBar; }
    public static boolean isShowSanText() { return showSanText; }
    public static double getWarningThreshold() { return warningThreshold; }
    public static double getDangerThreshold() { return dangerThreshold; }
}