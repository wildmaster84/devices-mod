package com.ultreon.devices;

import com.ultreon.devices.init.DeviceItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class DeviceTab extends CreativeModeTab {
    public DeviceTab(String label) {
        super(label);
    }

    @NotNull
    @Override
    public ItemStack makeIcon() {
        return new ItemStack(DeviceItems.RED_LAPTOP.get());
    }
}
