package com.ultreon.devices;

import org.jetbrains.annotations.Contract;

public final class DeviceType implements IDeviceType {
    public static final DeviceType LAPTOP = new DeviceType();
    public static final DeviceType PRINTER = new DeviceType();
    public static final DeviceType FLASH_DRIVE = new DeviceType();
    public static final DeviceType ROUTER = new DeviceType();

    @Override
    @Contract(pure = true, value = "-> this")
    public DeviceType getDeviceType() {
        return this;
    }
}
