package com.ultreon.devices.core.network.task.forge;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class TaskGetDevicesImpl {
    public static Class<?> getClass(String string) throws ClassNotFoundException {
        return Class.forName(string);
    }
}
