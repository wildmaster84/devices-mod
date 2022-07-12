package com.ultreon.devices.fabric;

import com.ultreon.devices.DeviceConfig;
import net.minecraftforge.api.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

import static com.ultreon.devices.Devices.MOD_ID;

public class DeviceConfigImpl {

    public static void register(Object context) {
        ModLoadingContext.registerConfig(MOD_ID, ModConfig.Type.CLIENT, DeviceConfig.CONFIG);
    }
}
