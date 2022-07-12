package com.ultreon.devices.util;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class ArchUtils {
    @ExpectPlatform
    public static boolean isProduction() {
        throw new AssertionError();
    }
}
