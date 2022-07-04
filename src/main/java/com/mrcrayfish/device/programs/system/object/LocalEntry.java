package com.mrcrayfish.device.programs.system.object;

import com.mrcrayfish.device.object.AppInfo;

/**
 * @author MrCrayfish
 */
public record LocalEntry(AppInfo info) implements AppEntry {

    @Override
    public String id() {
        return info.getId().toString();
    }

    @Override
    public String name() {
        return info.getName();
    }

    @Override
    public String author() {
        return info.getAuthor();
    }

    @Override
    public String description() {
        return info.getDescription();
    }

    @Override
    public String version() {
        return info.getVersion();
    }

    @Override
    public String icon() {
        return info.getIcon();
    }

    @Override
    public String[] screenshots() {
        return info.getScreenshots();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AppEntry) {
            return ((AppEntry) obj).id().equals(id());
        }
        return false;
    }
}
