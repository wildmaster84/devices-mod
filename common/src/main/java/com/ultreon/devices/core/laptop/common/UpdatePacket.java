package com.ultreon.devices.core.laptop.common;

import com.ultreon.devices.core.laptop.client.ClientLaptop;
import com.ultreon.devices.network.Packet;
import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;
import java.util.function.Supplier;

public class UpdatePacket extends Packet<UpdatePacket> {
    private final CompoundTag nbt;

    public UpdatePacket(UUID laptop, String type, CompoundTag nbt) {
        this.nbt = new CompoundTag();
        this.nbt.putUUID("uuid", laptop); // laptop uuid
        this.nbt.putString("type", type);
        this.nbt.put("data", nbt);
    }

    @Deprecated // do not call
    public UpdatePacket(FriendlyByteBuf buf) {
        this.nbt = buf.readNbt();
    }
    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(nbt);
    }

    @Override
    public boolean onMessage(Supplier<NetworkManager.PacketContext> ctx) {
        if (ctx.get().getEnv().equals(EnvType.CLIENT)) {
            ClientLaptop.laptops.get(this.nbt.getUUID("laptop")).handlePacket(this.nbt.getString("type"), this.nbt.getCompound("data"));
        }
        return false;
    }
}
