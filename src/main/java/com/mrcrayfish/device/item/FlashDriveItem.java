package com.mrcrayfish.device.item;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.util.IHasColor;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class FlashDriveItem extends Item implements IHasColor {

    private final DyeColor color;

    public FlashDriveItem(DyeColor color) {
        super(new Properties().tab(MrCrayfishDeviceMod.TAB_DEVICE).rarity(Rarity.UNCOMMON));
        this.color = color;
    }

    @Override
    public DyeColor getColor() {
        return color;
    }
}
