package com.mrcrayfish.device.item;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import net.minecraft.world.item.Item;

/**
 * Author: MrCrayfish
 */
public class BasicItem extends Item {
    public BasicItem(Properties pProperties) {
        super(pProperties.tab(MrCrayfishDeviceMod.TAB_DEVICE));
    }
}
