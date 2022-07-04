package com.mrcrayfish.device.network.task;

import com.google.common.collect.ImmutableList;
import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.api.ApplicationManager;
import com.mrcrayfish.device.network.Packet;
import com.mrcrayfish.device.object.AppInfo;
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
