package com.mrcrayfish.device.network.task;

import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.api.task.TaskManager;
import com.mrcrayfish.device.network.Packet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ResponsePacket extends Packet<ResponsePacket> {
    private final int id;
    private final Task request;
    private CompoundTag nbt;

    public ResponsePacket(FriendlyByteBuf buf) {
        this.id = buf.readInt();
        boolean successful = buf.readBoolean();
        this.request = TaskManager.getTaskAndRemove(this.id);
        if (successful) this.request.setSuccessful();
        String name = buf.readUtf();
        this.nbt = buf.readNbt();
    }

    public ResponsePacket(int id, Task request) {
        this.id = id;
        this.request = request;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.id);
        buf.writeBoolean(this.request.isSucessful());
        buf.writeUtf(this.request.getName());
        CompoundTag nbt = new CompoundTag();
        this.request.prepareResponse(nbt);
        buf.writeNbt(nbt);
        this.request.complete();
    }

    @Override
    public boolean onMessage(Supplier<NetworkEvent.Context> ctx) {
        request.processResponse(nbt);
        request.callback(nbt);
        return false;
    }
}
