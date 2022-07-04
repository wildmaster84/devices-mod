package com.mrcrayfish.device.block.entity;

import com.mrcrayfish.device.api.print.IPrint;
import com.mrcrayfish.device.init.DeviceBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class PaperBlockEntity extends SyncBlockEntity {
    private IPrint print;
    private byte rotation;

    public PaperBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(DeviceBlockEntities.PAPER.get(), pWorldPosition, pBlockState);
    }

    public void nextRotation() {
        rotation++;
        if (rotation > 7) {
            rotation = 0;
        }
        pipeline.putByte("rotation", rotation);
        sync();
        playSound(SoundEvents.ITEM_FRAME_ROTATE_ITEM);
    }

    public float getRotation() {
        return rotation * 45f;
    }

    @Nullable
    public IPrint getPrint() {
        return print;
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        if (compound.contains("print", Tag.TAG_COMPOUND)) {
            print = IPrint.load(compound.getCompound("print"));
        }
        if (compound.contains("rotation", Tag.TAG_BYTE)) {
            rotation = compound.getByte("rotation");
        }
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        if (print != null) {
            compound.put("print", IPrint.save(print));
        }
        compound.putByte("rotation", rotation);
    }

    @Override
    public CompoundTag saveSyncTag() {
        CompoundTag tag = new CompoundTag();
        if (print != null) {
            tag.put("print", IPrint.save(print));
        }
        tag.putByte("rotation", rotation);
        return tag;
    }

    private void playSound(SoundEvent sound) {
        level.playSound(null, worldPosition, sound, SoundSource.BLOCKS, 1f, 1f);
    }
}