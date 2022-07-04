package com.mrcrayfish.device.core.network.task;

import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.block.entity.NetworkDeviceBlockEntity;
import com.mrcrayfish.device.block.entity.RouterBlockEntity;
import com.mrcrayfish.device.core.network.Router;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * @author MrCrayfish
 */
public class TaskConnect extends Task {
    private BlockPos devicePos;
    private BlockPos routerPos;

    public TaskConnect() {
        super("connect");
    }

    public TaskConnect(BlockPos devicePos, BlockPos routerPos) {
        this();
        this.devicePos = devicePos;
        this.routerPos = routerPos;
    }

    @Override
    public void prepareRequest(CompoundTag nbt) {
        nbt.putLong("devicePos", devicePos.asLong());
        nbt.putLong("routerPos", routerPos.asLong());
    }

    @Override
    public void processRequest(CompoundTag nbt, Level level, Player player) {
        BlockEntity tileEntity = level.getBlockEntity(BlockPos.of(nbt.getLong("routerPos")));
        if (tileEntity instanceof RouterBlockEntity tileEntityRouter) {
            Router router = tileEntityRouter.getRouter();

            BlockEntity tileEntity1 = level.getBlockEntity(BlockPos.of(nbt.getLong("devicePos")));
            if (tileEntity1 instanceof NetworkDeviceBlockEntity tileEntityNetworkDevice) {
                if (router.addDevice(tileEntityNetworkDevice)) {
                    tileEntityNetworkDevice.connect(router);
                    this.setSuccessful();
                }
            }
        }
    }

    @Override
    public void prepareResponse(CompoundTag nbt) {

    }

    @Override
    public void processResponse(CompoundTag nbt) {

    }
}
