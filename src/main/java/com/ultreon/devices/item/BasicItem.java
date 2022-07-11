package com.ultreon.devices.item;

import com.ultreon.devices.DevicesMod;
import net.minecraft.world.item.Item;

/**
 * @author MrCrayfish
 */
public class BasicItem extends Item {
    public BasicItem(Properties pProperties) {
        super(pProperties.tab(DevicesMod.TAB_DEVICE));
    }
}
