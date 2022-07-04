package com.mrcrayfish.device.core.network.task;

import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.block.entity.NetworkDeviceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Author: MrCrayfish
 */
public class TaskPing extends Task {
    private BlockPos sourceDevicePos;
    private int strength;

    private TaskPing() {
        super("ping");
    }

    public TaskPing(BlockPos sourceDevicePos) {
        this();
        this.sourceDevicePos = sourceDevicePos;
    }

    @Override
    public void prepareRequest(CompoundTag nbt) {
        nbt.putLong("sourceDevicePos", sourceDevicePos.asLong());
    }

    @Override
    public void processRequest(CompoundTag nbt, Level level, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(BlockPos.of(nbt.getLong("sourceDevicePos")));
        if (blockEntity instanceof NetworkDeviceBlockEntity networkDevice) {
            if (networkDevice.isConnected()) {
                this.strength = networkDevice.getSignalStrength();
                this.setSuccessful();
            }
        }
    }

    @Override
    public void prepareResponse(CompoundTag nbt) {
        if (this.isSucessful()) {
            nbt.putInt("strength", strength);
        }
    }

    @Override
    public void processResponse(CompoundTag nbt) {

    }
}
