package com.ultreon.devices.programs.gitweb.module;

import java.util.Map;

/**
 * @author MrCrayfish
 */
public class ModuleEntry {
    private final Module module;
    private final Map<String, String> data;

    public ModuleEntry(Module module, Map<String, String> data) {
        this.module = module;
        this.data = data;
    }

    public Module getModule() {
        return module;
    }

    public Map<String, String> getData() {
        return data;
    }
}