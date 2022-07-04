package com.mrcrayfish.device.block.entity;

import com.mrcrayfish.device.core.io.FileSystem;
import com.mrcrayfish.device.init.DeviceBlockEntities;
import com.mrcrayfish.device.util.BlockEntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class LaptopBlockEntity extends NetworkDeviceBlockEntity.Colored {
    private static final int OPENED_ANGLE = 102;

    private boolean open = false;

    private CompoundTag applicationData;
    private CompoundTag systemData;
    private FileSystem fileSystem;

    @OnlyIn(Dist.CLIENT)
    private int rotation;

    @OnlyIn(Dist.CLIENT)
    private int prevRotation;

    @OnlyIn(Dist.CLIENT)
    private DyeColor externalDriveColor;

    public LaptopBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(DeviceBlockEntities.LAPTOP.get(), pWorldPosition, pBlockState);
    }

    @Override
    public String getDeviceName() {
        return "Laptop";
    }

    @Override
    public void tick() {
        super.tick();
        if (level.isClientSide) {
            prevRotation = rotation;
            if (!open) {
                if (rotation > 0) {
                    rotation -= 10F;
                }
            } else {
                if (rotation < OPENED_ANGLE) {
                    rotation += 10F;
                }
            }
        }
    }

    @Override
    public void load(@NotNull CompoundTag compound) {
        super.load(compound);
        if (compound.contains("open")) {
            this.open = compound.getBoolean("open");
        }
        if (compound.contains("system_data", Tag.TAG_COMPOUND)) {
            this.systemData = compound.getCompound("system_data");
        }
        if (compound.contains("application_data", Tag.TAG_COMPOUND)) {
            this.applicationData = compound.getCompound("application_data");
        }
        if (compound.contains("file_system")) {
            this.fileSystem = new FileSystem(this, compound.getCompound("file_system"));
        }
        if (compound.contains("external_drive_color", Tag.TAG_BYTE)) {
            this.externalDriveColor = null;
            if (compound.getByte("external_drive_color") != -1) {
                this.externalDriveColor = DyeColor.byId(compound.getByte("external_drive_color"));
            }
        }
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putBoolean("open", open);

        if (systemData != null) {
            compound.put("system_data", systemData);
        }

        if (applicationData != null) {
            compound.put("application_data", applicationData);
        }

        if (fileSystem != null) {
            compound.put("file_system", fileSystem.toTag());
        }
    }

    @Override
    public CompoundTag saveSyncTag() {
        CompoundTag tag = super.saveSyncTag();
        tag.putBoolean("open", open);
        tag.put("system_data", getSystemData());

        if (getFileSystem().getAttachedDrive() != null) {
            tag.putByte("external_drive_color", (byte) getFileSystem().getAttachedDriveColor().getId());
        } else {
            tag.putByte("external_drive_color", (byte) -1);
        }

        return tag;
    }

    // Todo: Port to 1.18.2 if possible
//    @Override
//    public double getMaxRenderDistanceSquared() {
//        return 16384;
//    }
//
//    public AxisAlignedBB getRenderBoundingBox() {
//        return INFINITE_EXTENT_AABB;
//    }

    public void openClose() {
        open = !open;
        pipeline.putBoolean("open", open);
        sync();
    }

    public boolean isOpen() {
        return open;
    }

    public CompoundTag getApplicationData() {
        return applicationData != null ? applicationData : new CompoundTag();
    }

    public CompoundTag getSystemData() {
        if (systemData == null) {
            systemData = new CompoundTag();
        }
        return systemData;
    }

    public void setSystemData(CompoundTag systemData) {
        this.systemData = systemData;
        setChanged();
        BlockEntityUtil.markBlockForUpdate(level, worldPosition);
    }

    public FileSystem getFileSystem() {
        if (fileSystem == null) {
            fileSystem = new FileSystem(this, new CompoundTag());
        }
        return fileSystem;
    }

    public void setApplicationData(String appId, CompoundTag applicationData) {
        this.applicationData = applicationData;
        setChanged();
        BlockEntityUtil.markBlockForUpdate(level, worldPosition);
    }

    @OnlyIn(Dist.CLIENT)
    public float getScreenAngle(float partialTicks) {
        return -OPENED_ANGLE * ((prevRotation + (rotation - prevRotation) * partialTicks) / OPENED_ANGLE);
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isExternalDriveAttached() {
        return externalDriveColor != null;
    }

    @OnlyIn(Dist.CLIENT)
    public DyeColor getExternalDriveColor() {
        return externalDriveColor;
    }
}