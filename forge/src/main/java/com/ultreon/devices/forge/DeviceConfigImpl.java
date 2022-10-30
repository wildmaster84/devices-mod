package com.ultreon.devices.forge;

import com.ultreon.devices.DeviceConfig;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class DeviceConfigImpl {

    public static void register(Object context) {
        ((ModLoadingContext)context).registerConfig(ModConfig.Type.CLIENT, DeviceConfig.CONFIG);
    }
}
