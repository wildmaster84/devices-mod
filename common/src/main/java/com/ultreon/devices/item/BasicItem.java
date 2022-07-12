package com.ultreon.devices.item;

import com.ultreon.devices.Devices;
import net.minecraft.world.item.Item;

/**
 * @author MrCrayfish
 */
public class BasicItem extends Item {
    public BasicItem(Properties pProperties) {
        super(pProperties.tab(Devices.TAB_DEVICE));
    }
}
