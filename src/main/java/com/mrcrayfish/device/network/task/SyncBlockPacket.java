package com.mrcrayfish.device.network.task;

import com.mrcrayfish.device.block.entity.RouterBlockEntity;
import com.mrcrayfish.device.network.Packet;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author MrCrayfish
 */
public class SyncBlockPacket extends Packet<SyncBlockPacket> {
    private final BlockPos routerPos;

    public SyncBlockPacket(FriendlyByteBuf buf) {
        this.routerPos = buf.readBlockPos();
    }

    public SyncBlockPacket(BlockPos routerPos) {
        this.routerPos = routerPos;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(routerPos);
    }

    @Override
    public boolean onMessage(Supplier<NetworkEvent.Context> ctx) {
        Level world = Objects.requireNonNull(ctx.get().getSender()).level;
        BlockEntity blockEntity = world.getBlockEntity(routerPos);
        if (blockEntity instanceof RouterBlockEntity router) {
            router.syncDevicesToClient();
        }
        return true;
    }
}
