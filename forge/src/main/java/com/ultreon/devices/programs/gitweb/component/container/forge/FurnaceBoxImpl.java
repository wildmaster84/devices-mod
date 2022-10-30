package com.ultreon.devices.programs.gitweb.component.container.forge;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class FurnaceBoxImpl {
    public static int getBurnTime(ItemStack stack, RecipeType<?> type) {
        return stack.getBurnTime(type);
    }
}
