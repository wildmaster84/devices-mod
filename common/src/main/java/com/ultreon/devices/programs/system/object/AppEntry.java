package com.ultreon.devices.programs.system.object;

import javax.annotation.Nullable;

/**
 * @author MrCrayfish
 */
public interface AppEntry {
    String id();

    String name();

    String author();

    String description();

    @Nullable
    String version();

    @Nullable
    String icon();

    @Nullable
    String[] screenshots();
}
