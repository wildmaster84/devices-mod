package com.ultreon.devices.network.task;

import com.google.common.collect.ImmutableList;
import com.ultreon.devices.MrCrayfishDeviceMod;
import com.ultreon.devices.api.ApplicationManager;
import com.ultreon.devices.network.Packet;
import com.ultreon.devices.object.AppInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

/**
 * @author MrCrayfish
 */
public class SyncApplicationPacket extends Packet<SyncApplicationPacket> {
    private final List<AppInfo> allowedApps;

    public SyncApplicationPacket(FriendlyByteBuf buf) {
        int size = buf.readInt();
        ImmutableList.Builder<AppInfo> builder = ImmutableList.builder();
        for (int i = 0; i < size; i++) {
            String appId = buf.readUtf();
            AppInfo info = ApplicationManager.getApplication(appId);
            if (info != null) {
                builder.add(info);
            } else {
                MrCrayfishDeviceMod.LOGGER.error("Missing application '" + appId + "'");
            }
        }

        allowedApps = builder.build();
    }

    public SyncApplicationPacket(List<AppInfo> allowedApps) {
        this.allowedApps = allowedApps;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(allowedApps.size());
        for (AppInfo appInfo : allowedApps) {
            buf.writeResourceLocation(appInfo.getId());
        }
    }

    @Override
    public boolean onMessage(Supplier<NetworkEvent.Context> ctx) {
        ObfuscationReflectionHelper.setPrivateValue(MrCrayfishDeviceMod.class, MrCrayfishDeviceMod.getInstance(), allowedApps, "allowedApps");
        return true;
    }
}
