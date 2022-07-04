package com.mrcrayfish.device.item;

import com.mrcrayfish.device.util.KeyboardHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public class MotherboardItem extends ComponentItem {
    public MotherboardItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, @NotNull List<net.minecraft.network.chat.Component> tooltip, @NotNull TooltipFlag isAdvanced) {
        CompoundTag tag = stack.getTag();
        if (!KeyboardHelper.isShiftDown()) {
            tooltip.add(new TextComponent("CPU: " + getComponentStatus(tag, "cpu")));
            tooltip.add(new TextComponent("RAM: " + getComponentStatus(tag, "ram")));
            tooltip.add(new TextComponent("GPU: " + getComponentStatus(tag, "gpu")));
            tooltip.add(new TextComponent("WIFI: " + getComponentStatus(tag, "wifi")));
            tooltip.add(new TextComponent(ChatFormatting.YELLOW + "Hold shift for help"));
        } else {
            tooltip.add(new TextComponent("To add the required components"));
            tooltip.add(new TextComponent("place the motherboard and the"));
            tooltip.add(new TextComponent("corresponding component into a"));
            tooltip.add(new TextComponent("crafting table to combine them."));
        }
    }

    private String getComponentStatus(CompoundTag tag, String component) {
        if (tag != null && tag.contains("components", Tag.TAG_COMPOUND)) {
            CompoundTag components = tag.getCompound("components");
            if (components.contains(component, Tag.TAG_BYTE)) {
                return ChatFormatting.GREEN + "Added";
            }
        }
        return ChatFormatting.RED + "Missing";
    }

    public static class Component extends ComponentItem {
        public Component(Properties properties) {
            super(properties);
        }
    }
}
