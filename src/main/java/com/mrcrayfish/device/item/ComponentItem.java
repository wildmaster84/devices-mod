package com.mrcrayfish.device.item;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import net.minecraft.world.item.Item;

public class ComponentItem extends Item {
    public ComponentItem(Properties pProperties) {
        super(pProperties.tab(MrCrayfishDeviceMod.TAB_DEVICE));
    }
}
