package com.mrcrayfish.device.item;

import com.mrcrayfish.device.DeviceType;
import com.mrcrayfish.device.util.IHasColor;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class ColoredDeviceItem extends DeviceItem implements IHasColor {
    private final DyeColor color;

    public ColoredDeviceItem(@NotNull Block block, Properties tab, DyeColor color, DeviceType deviceType) {
        super(block, tab, deviceType);
        this.color = color;
    }

    public DyeColor getColor() {
        return color;
    }
}
