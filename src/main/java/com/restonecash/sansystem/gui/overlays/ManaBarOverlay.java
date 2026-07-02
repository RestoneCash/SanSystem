package com.restonecash.sansystem.gui.overlays;

import com.restonecash.sansystem.capability.SanityCapability;
import com.restonecash.sansystem.config.ClientConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class ManaBarOverlay implements IGuiOverlay
{
    private static final int COLOR_NORMAL = 0xFF4A90D9;
    private static final int COLOR_WARNING = 0xFFFF9800;
    private static final int COLOR_DANGER = 0xFFFF0000;
    private static final int COLOR_BG = 0xAA000000;

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight)
    {
        if (!ClientConfig.isShowSanBar()) return;

        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) return;

        player.getCapability(SanityCapability.SANITY).ifPresent(sanity -> {
            float currentSan = sanity.getCore().getSanity();
            float maxSan = sanity.getCore().getMaxSanity();

            int x = ClientConfig.getSanBarX();
            int y = ClientConfig.getSanBarY();
            int width = ClientConfig.getSanBarWidth();
            int height = ClientConfig.getSanBarHeight();

            float percent = maxSan > 0 ? currentSan / maxSan : 0;
            int barWidth = (int) (width * percent);

            int color = COLOR_NORMAL;
            if (percent < ClientConfig.getDangerThreshold()) {
                color = COLOR_DANGER;
            } else if (percent < ClientConfig.getWarningThreshold()) {
                color = COLOR_WARNING;
            }

            boolean shouldBlink = percent < ClientConfig.getDangerThreshold() && 
                    (player.level().getGameTime() / 10 % 2 == 0);

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            guiGraphics.fill(x, y, x + width, y + height, COLOR_BG);

            if (!shouldBlink && barWidth > 0) {
                guiGraphics.fill(x, y, x + barWidth, y + height, color);
            }

            if (ClientConfig.isShowSanText()) {
                Font font = minecraft.font;
                String text = String.format("San: %.0f/%.0f", currentSan, maxSan);
                int textWidth = font.width(text);
                int textX = x + (width - textWidth) / 2;
                int textY = y - 10;
                guiGraphics.drawString(font, text, textX, textY, 0xFFFFFFFF);
            }
        });
    }
}