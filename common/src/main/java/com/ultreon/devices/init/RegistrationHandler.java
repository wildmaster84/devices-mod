package com.ultreon.devices.init;

/**
 * @author MrCrayfish
 */
public class RegistrationHandler {
    public static void register() {
        DeviceBlockEntities.register();
        DeviceBlocks.register();
        DeviceItems.register();
        DeviceSounds.register();
    }
}
