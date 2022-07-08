package com.ultreon.devices;

import net.minecraftforge.common.util.MavenVersionStringHelper;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;

public class Reference {
    public static final String MOD_ID = "devices";
    public static final String VERSION;

    static {
        ModContainer container = ModList.get().getModContainerById(MOD_ID).orElseThrow();
        IModInfo info = container.getModInfo();
        VERSION = MavenVersionStringHelper.artifactVersionToString(info.getVersion());
    }
}
