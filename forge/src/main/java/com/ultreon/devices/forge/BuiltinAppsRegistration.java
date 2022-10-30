package com.ultreon.devices.forge;

import com.ultreon.devices.BuiltinApps;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class BuiltinAppsRegistration {
    @ApiStatus.Internal
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void registerBuiltinApps(ForgeApplicationRegistration event) {
        BuiltinApps.registerBuiltinApps();
    }
}
