package com.ultreon.devices.forge;

import com.ultreon.devices.api.app.Application;
import com.ultreon.devices.api.print.IPrint;
import com.ultreon.devices.api.print.PrintingManager;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.object.AppInfo;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.util.List;
import java.util.Map;

public class DevicesImpl {
    public static void updateIcon(AppInfo info, int iconU, int iconV) {
        ObfuscationReflectionHelper.setPrivateValue(AppInfo.class, info, iconU, "iconU");
        ObfuscationReflectionHelper.setPrivateValue(AppInfo.class, info, iconV, "iconV");
    }

    public static List<Application> getAPPLICATIONS() {
        return ObfuscationReflectionHelper.getPrivateValue(Laptop.class, null, "APPLICATIONS");
    }

    public static Map<String, IPrint.Renderer> getRegisteredRenders(){
        return ObfuscationReflectionHelper.getPrivateValue(PrintingManager.class, null, "registeredRenders");
    }

    public static void setRegisteredRenders(Map<String, IPrint.Renderer> map){
        ObfuscationReflectionHelper.setPrivateValue(PrintingManager.class, null, map, "registeredRenders");
    }
}
