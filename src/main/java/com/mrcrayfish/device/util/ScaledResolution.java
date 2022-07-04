package com.mrcrayfish.device.util;

import net.minecraft.client.Minecraft;

public class ScaledResolution {
    private final int scaleFactor;
    private final int scale;
    private int scaledWidth;
    private int scaledHeight;

    public ScaledResolution(Minecraft mc) {
        this.scaleFactor = mc.options.guiScale;
        this.scale = this.scaleFactor == 0 ? 1 : this.scaleFactor;
        this.updateScaledWidthAndHeight();
    }

    private void updateScaledWidthAndHeight() {
        this.scaledWidth = this.scale * Minecraft.getInstance().getWindow().getScreenWidth();
        this.scaledHeight = this.scale * Minecraft.getInstance().getWindow().getScreenHeight();
    }

    public int getScaleFactor() {
        return scaleFactor;
    }

    public int getScale() {
        return scale;
    }

    public int getScaledWidth() {
        return scaledWidth;
    }

    public int getScaledHeight() {
        return scaledHeight;
    }
}
