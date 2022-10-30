package com.ultreon.devices.fabric;

import com.ultreon.devices.DeviceConfig;
import com.ultreon.devices.Devices;
import com.ultreon.devices.init.RegistrationHandler;
import net.fabricmc.api.ModInitializer;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class DevicesFabric implements ModInitializer {
    @Override
    @ApiStatus.Internal
    public void onInitialize() {
        DeviceConfig.register(null);
        Devices.init();
        RegistrationHandler.register();
    }
}