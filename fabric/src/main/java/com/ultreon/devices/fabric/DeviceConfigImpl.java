package com.ultreon.devices.fabric;

import com.ultreon.devices.DeviceConfig;
import com.ultreon.devices.Devices;
import net.minecraftforge.api.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class DeviceConfigImpl {
    public static void register(Object context) {
        ModLoadingContext.registerConfig(Devices.MOD_ID, ModConfig.Type.CLIENT, DeviceConfig.CONFIG);
    }
}
