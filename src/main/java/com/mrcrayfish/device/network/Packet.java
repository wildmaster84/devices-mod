package com.mrcrayfish.device.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class Packet<T extends Packet<T>> {
    public Packet() {

    }

    public abstract void toBytes(FriendlyByteBuf buf);

    @Deprecated
    public void fromBytes(FriendlyByteBuf buf) {

    }

    public abstract boolean onMessage(Supplier<NetworkEvent.Context> ctx);
}
