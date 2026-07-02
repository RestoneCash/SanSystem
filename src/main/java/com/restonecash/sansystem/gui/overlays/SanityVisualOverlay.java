package com.restonecash.sansystem.gui.overlays;

import com.restonecash.sansystem.capability.SanityCapability;
import com.restonecash.sansystem.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class SanityVisualOverlay implements IGuiOverlay
{
    private static final float THRESHOLD_50 = 0.50f;

    @Override
    public void render(net.minecraftforge.client.gui.overlay.ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int width, int height)
    {
        if (!ClientConfig.isShowSanBar()) return;

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) return;

        player.getCapability(SanityCapability.SANITY).ifPresent(sanity -> {
            float currentSan = sanity.getCore().getSanity();
            float maxSan = sanity.getCore().getMaxSanity();
            if (maxSan <= 0) return;

            float sanPercent = currentSan / maxSan;
            if (sanPercent >= THRESHOLD_50) return;

            float intensity = calculateIntensity(sanPercent);
            long gameTime = player.level().getGameTime();
            float time = (gameTime + partialTick) * 0.05f;

            drawDistortionOverlay(guiGraphics, width, height, intensity, time);
        });
    }

    private float calculateIntensity(float sanPercent)
    {
        if (sanPercent >= THRESHOLD_50) return 0.0f;
        if (sanPercent <= 0) return 1.0f;

        return 1.0f - (sanPercent / THRESHOLD_50);
    }

    private void drawDistortionOverlay(GuiGraphics guiGraphics, int width, int height, float intensity, float time)
    {
        float vignetteIntensity = intensity * 0.3f;
        int vignetteColor = (int) (vignetteIntensity * 60) << 24 | 0x1a1a1a;

        int centerX = width / 2;
        int centerY = height / 2;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(centerX, centerY, 0);
        float distortAmount = intensity * 5.0f;
        float distortX = (float) Math.sin(time * 0.06) * distortAmount;
        float distortY = (float) Math.cos(time * 0.08) * distortAmount;
        guiGraphics.pose().translate(distortX, distortY, 0);

        int vignetteRadius = Math.max(width, height);
        guiGraphics.fillGradient(-vignetteRadius, -vignetteRadius, vignetteRadius, vignetteRadius,
                0, vignetteColor);

        guiGraphics.pose().popPose();
    }
}