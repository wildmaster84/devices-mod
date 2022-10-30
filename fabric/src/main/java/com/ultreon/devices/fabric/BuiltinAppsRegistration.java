package com.ultreon.devices.fabric;

import com.ultreon.devices.BuiltinApps;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class BuiltinAppsRegistration implements FabricApplicationRegistration {
    @Override
    public void registerApplications() {
        BuiltinApps.registerBuiltinApps();
    }
}
