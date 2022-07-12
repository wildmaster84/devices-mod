package com.ultreon.devices.fabric;

import net.fabricmc.loader.api.FabricLoader;

import static com.ultreon.devices.Devices.MOD_ID;

public class ReferenceImpl {
    public static String getVersion() {
        return FabricLoader.getInstance().getModContainer(MOD_ID).get().getMetadata().getVersion().getFriendlyString();
    }
}
