package com.ultreon.devices.api.app.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;

public abstract class ListItemRenderer<E> {
    private final int height;

    public ListItemRenderer(int height) {
        this.height = height;
    }

    public final int getHeight() {
        return height;
    }

    public abstract void render(PoseStack pose, E e, GuiComponent gui, Minecraft mc, int x, int y, int width, int height, boolean selected);
}
