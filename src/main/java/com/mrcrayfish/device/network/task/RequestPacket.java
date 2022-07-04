package com.mrcrayfish.device.network.task;

import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.api.task.TaskManager;
import com.mrcrayfish.device.network.Packet;
import com.mrcrayfish.device.network.PacketHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class RequestPacket extends Packet<RequestPacket> {
    private final int id;
    private final Task request;
    private CompoundTag tag;

    public RequestPacket(FriendlyByteBuf buf) {
        this.id = buf.readInt();
        String name = buf.readUtf();
        this.request = TaskManager.getTask(name);
        this.tag = buf.readNbt();
    }

    public RequestPacket(int id, Task request) {
        this.id = id;
        this.request = request;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.id);
        buf.writeUtf(this.request.getName());
        CompoundTag nbt = new CompoundTag();
        this.request.prepareRequest(nbt);
        buf.writeNbt(nbt);
    }

    @Override
    public boolean onMessage(Supplier<NetworkEvent.Context> ctx) {
        request.processRequest(tag, Objects.requireNonNull(ctx.get().getSender()).level, ctx.get().getSender());
        PacketHandler.sendToClient(new ResponsePacket(id, request), ctx.get().getSender());
        return true;
    }

    public int getId() {
        return id;
    }

}
