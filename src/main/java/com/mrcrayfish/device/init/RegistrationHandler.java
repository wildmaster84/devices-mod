package com.mrcrayfish.device.init;

import net.minecraftforge.eventbus.api.IEventBus;

/**
 * @author MrCrayfish
 */
public class RegistrationHandler {
    public static void register(IEventBus modEventBus) {
        DeviceBlockEntities.register(modEventBus);
        DeviceBlocks.register(modEventBus);
        DeviceItems.register(modEventBus);
        DeviceSounds.register(modEventBus);
    }
}
