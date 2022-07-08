package com.ultreon.devices.util;

import net.minecraft.world.item.DyeColor;

public interface IColored extends IHasColor {
    DyeColor getColor();

    void setColor(DyeColor color);
}
