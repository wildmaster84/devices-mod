package com.ultreon.devices.util.fabric;

import net.fabricmc.loader.api.FabricLoader;

public class ArchUtilsImpl {
    public static boolean isProduction() {
        return !FabricLoader.getInstance().isDevelopmentEnvironment();
    }
}
