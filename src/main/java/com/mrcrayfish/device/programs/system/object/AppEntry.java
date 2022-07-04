package com.mrcrayfish.device.programs.system.object;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
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
