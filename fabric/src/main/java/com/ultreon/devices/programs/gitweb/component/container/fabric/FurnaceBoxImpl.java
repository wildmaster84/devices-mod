package com.ultreon.devices.programs.gitweb.component.container.fabric;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class FurnaceBoxImpl {
    public static int getBurnTime(ItemStack stack, RecipeType<?> type) {
        var a = AbstractFurnaceBlockEntity.getFuel().get(stack.getItem());
        return a == null ? 1600 : a;
    }
}
