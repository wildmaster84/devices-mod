package com.ultreon.devices.fabric;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@FunctionalInterface
public interface FabricApplicationRegistration {
    void registerApplications();
}
