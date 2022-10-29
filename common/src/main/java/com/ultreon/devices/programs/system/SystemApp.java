package com.ultreon.devices.programs.system;

import com.ultreon.devices.api.app.Application;
import com.ultreon.devices.core.Laptop;

import javax.annotation.Nullable;

/**
 * Created by Casey on 03-Aug-17.
 */
public abstract class SystemApp extends Application {
    private Laptop laptop;

    SystemApp() {
    }

    @Nullable
    public Laptop getLaptop() {
        return laptop;
    }

    public void setLaptop(@Nullable Laptop laptop) {
        this.laptop = laptop;
    }
}
