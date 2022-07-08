package com.ultreon.devices.item;

import com.ultreon.devices.MrCrayfishDeviceMod;
import net.minecraft.world.item.Item;

public class ComponentItem extends Item {
    public ComponentItem(Properties pProperties) {
        super(pProperties.tab(MrCrayfishDeviceMod.TAB_DEVICE));
    }
}
