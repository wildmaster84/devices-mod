package com.ultreon.devices.core.laptop.server;

import com.ultreon.devices.core.laptop.common.UpdatePacket;
import com.ultreon.devices.network.PacketHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class ServerLaptop {
    private final UUID uuid = new UUID(430985038594038L, 493058808830598L);
    public void sendPacket(ServerPlayer player) {
        PacketHandler.sendToClient(new UpdatePacket(this.uuid, "screenUpdate", new CompoundTag()), player);
    }
}
