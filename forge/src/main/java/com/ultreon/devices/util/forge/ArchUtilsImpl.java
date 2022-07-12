package com.ultreon.devices.util.forge;

import net.minecraftforge.fml.loading.FMLEnvironment;

public class ArchUtilsImpl {
    public static boolean isProduction() {
        return FMLEnvironment.production;
    }
}
