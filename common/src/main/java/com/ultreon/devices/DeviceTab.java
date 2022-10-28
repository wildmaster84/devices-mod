package com.ultreon.devices;

import com.ultreon.devices.init.DeviceItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class DeviceTab extends CreativeModeTab {
    public DeviceTab(String label) {
        super(0, Component.literal("A"));
        throw new AssertionError();
    }

    @NotNull
    @Override
    public ItemStack makeIcon() {
        return new ItemStack(DeviceItems.RED_LAPTOP.get());
    }

    @Override
    protected void generateDisplayItems(FeatureFlagSet featureFlagSet, Output output) {

    }
}
