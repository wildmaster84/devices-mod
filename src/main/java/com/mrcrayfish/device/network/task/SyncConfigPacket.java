package com.mrcrayfish.device.network.task;

import com.mrcrayfish.device.DeviceConfig;
import com.mrcrayfish.device.network.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class SyncConfigPacket extends Packet<SyncConfigPacket> {
    public SyncConfigPacket() {

    }

    public SyncConfigPacket(FriendlyByteBuf buf) {
        DeviceConfig.readSyncTag(Objects.requireNonNull(buf.readNbt()));
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(DeviceConfig.writeSyncTag());
    }

    @Override
    public boolean onMessage(Supplier<NetworkEvent.Context> ctx) {
        return true;
    }
}
