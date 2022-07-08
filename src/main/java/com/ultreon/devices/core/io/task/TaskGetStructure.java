package com.ultreon.devices.core.io.task;

import com.ultreon.devices.api.io.Drive;
import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.block.entity.LaptopBlockEntity;
import com.ultreon.devices.core.io.FileSystem;
import com.ultreon.devices.core.io.ServerFolder;
import com.ultreon.devices.core.io.drive.AbstractDrive;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.UUID;

/**
 * @author MrCrayfish
 */
public class TaskGetStructure extends Task {
    private String uuid;
    private BlockPos pos;

    private ServerFolder folder;

    private TaskGetStructure() {
        super("get_folder_structure");
    }

    public TaskGetStructure(Drive drive, BlockPos pos) {
        this();
        this.uuid = drive.getUUID().toString();
        this.pos = pos;
    }

    @Override
    public void prepareRequest(CompoundTag nbt) {
        nbt.putString("uuid", uuid);
        nbt.putLong("pos", pos.asLong());
    }

    @Override
    public void processRequest(CompoundTag nbt, Level level, Player player) {
        BlockEntity tileEntity = level.getBlockEntity(BlockPos.of(nbt.getLong("pos")));
        if (tileEntity instanceof LaptopBlockEntity laptop) {
            FileSystem fileSystem = laptop.getFileSystem();
            UUID uuid = UUID.fromString(nbt.getString("uuid"));
            AbstractDrive serverDrive = fileSystem.getAvailableDrives(level, true).get(uuid);
            if (serverDrive != null) {
                folder = serverDrive.getDriveStructure();
                this.setSuccessful();
            }
        }
    }

    @Override
    public void prepareResponse(CompoundTag nbt) {
        if (folder != null) {
            nbt.putString("file_name", folder.getName());
            nbt.put("structure", folder.toTag());
        }
    }

    @Override
    public void processResponse(CompoundTag nbt) {

    }
}
