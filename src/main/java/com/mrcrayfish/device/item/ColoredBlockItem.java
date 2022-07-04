package com.mrcrayfish.device.item;

import com.mrcrayfish.device.util.IHasColor;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class ColoredBlockItem extends BlockItem implements IHasColor {
    private final DyeColor color;

    public ColoredBlockItem(@NotNull Block block, Item.Properties tab, DyeColor color) {
        super(block, tab);
        this.color = color;
    }

    public DyeColor getColor() {
        return color;
    }
}
