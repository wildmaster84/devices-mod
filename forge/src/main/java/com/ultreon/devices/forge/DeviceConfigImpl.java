package com.ultreon.devices.forge;

import com.ultreon.devices.DeviceConfig;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.MavenVersionStringHelper;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.forgespi.language.IModInfo;

import static com.ultreon.devices.Devices.MOD_ID;

public class DeviceConfigImpl {

    public static void register(Object context) {
        ((ModLoadingContext)context).registerConfig(ModConfig.Type.CLIENT, DeviceConfig.CONFIG);
    }
}
