package com.ultreon.devices;

import com.ultreon.devices.init.DeviceItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Devices Mod creative tab.
 *
 * @see Devices#TAB_DEVICE
 */
public class DeviceTab extends CreativeModeTab {
    /**
     * Create the creative tabs instance.
     * <p>
     * NOTE: Internal API
     *
     * @param label the creative tab label.
     */
    @ApiStatus.Internal
    public DeviceTab(String label) {
        super(0, "A");
        throw new AssertionError();
    }

    /**
     * Create an icon for the creative tab.
     *
     * @return the item icon.
     */
    @NotNull
    @Override
    public ItemStack makeIcon() {
        return new ItemStack(DeviceItems.RED_LAPTOP.get());
    }
}
