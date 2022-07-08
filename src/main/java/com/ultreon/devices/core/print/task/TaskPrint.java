package com.ultreon.devices.core.print.task;

import com.ultreon.devices.api.print.IPrint;
import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.block.entity.NetworkDeviceBlockEntity;
import com.ultreon.devices.block.entity.PrinterBlockEntity;
import com.ultreon.devices.core.network.NetworkDevice;
import com.ultreon.devices.core.network.Router;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.UUID;

/**
 * @author MrCrayfish
 */
public class TaskPrint extends Task {
    private BlockPos devicePos;
    private UUID printerId;
    private IPrint print;

    private TaskPrint() {
        super("print");
    }

    public TaskPrint(BlockPos devicePos, NetworkDevice printer, IPrint print) {
        this();
        this.devicePos = devicePos;
        this.printerId = printer.getId();
        this.print = print;
    }

    @Override
    public void prepareRequest(CompoundTag nbt) {
        nbt.putLong("devicePos", devicePos.asLong());
        nbt.putUUID("printerId", printerId);
        nbt.put("print", IPrint.save(print));
    }

    @Override
    public void processRequest(CompoundTag nbt, Level level, Player player) {
        BlockEntity tileEntity = level.getBlockEntity(BlockPos.of(nbt.getLong("devicePos")));
        if (tileEntity instanceof NetworkDeviceBlockEntity device) {
            Router router = device.getRouter();
            if (router != null) {
                NetworkDeviceBlockEntity printer = router.getDevice(level, nbt.getUUID("printerId"));
                if (printer instanceof PrinterBlockEntity) {
                    IPrint print = IPrint.load(nbt.getCompound("print"));
                    ((PrinterBlockEntity) printer).addToQueue(print);
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
