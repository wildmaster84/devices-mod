package com.mrcrayfish.device.network.task;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.api.app.Notification;
import com.mrcrayfish.device.network.Packet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * @author MrCrayfish
 */
public class NotificationPacket extends Packet<NotificationPacket> {
    private final CompoundTag notificationTag;

    public NotificationPacket(FriendlyByteBuf buf) {
        notificationTag = buf.readNbt();
    }

    public NotificationPacket(Notification notification) {
        this.notificationTag = notification.toTag();
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(notificationTag);
    }

    @Override
    public boolean onMessage(Supplier<NetworkEvent.Context> ctx) {
        MrCrayfishDeviceMod.getInstance().showNotification(notificationTag);
        return true;
    }
}
