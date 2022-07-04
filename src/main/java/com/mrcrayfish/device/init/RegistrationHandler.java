package com.mrcrayfish.device.init;

import net.minecraftforge.eventbus.api.IEventBus;

/**
 * Author: MrCrayfish
 */
public class RegistrationHandler {
    public static void init(IEventBus modEventBus) {
        DeviceBlockEntities.register(modEventBus);
        DeviceBlocks.register(modEventBus);
        DeviceItems.register(modEventBus);
        DeviceSounds.register(modEventBus);
    }
}
