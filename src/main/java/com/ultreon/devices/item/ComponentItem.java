package com.ultreon.devices.item;

import com.ultreon.devices.DevicesMod;
import net.minecraft.world.item.Item;

public class ComponentItem extends Item {
    public ComponentItem(Properties pProperties) {
        super(pProperties.tab(DevicesMod.TAB_DEVICE));
    }
}
