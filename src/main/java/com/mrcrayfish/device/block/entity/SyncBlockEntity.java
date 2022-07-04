package com.mrcrayfish.device.block.entity;

import com.mrcrayfish.device.util.BlockEntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class SyncBlockEntity extends BlockEntity {
    protected CompoundTag pipeline = new CompoundTag();

    public SyncBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);
    }

    public void sync() {
        assert level != null;
        BlockEntityUtil.markBlockForUpdate(level, worldPosition);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.load(Objects.requireNonNull(pkt.getTag(), "The data packet for the block entity contained no data"));
    }

    @Override
    public CompoundTag getUpdateTag() {
        if (!pipeline.isEmpty()) {
            CompoundTag updateTag = pipeline;
            saveAdditional(updateTag);
            pipeline = new CompoundTag();
            return updateTag;
        }
        CompoundTag updateTag = saveSyncTag();
        super.saveAdditional(updateTag);
        return updateTag;
    }

    public abstract CompoundTag saveSyncTag();

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, BlockEntity::getUpdateTag);
    }

    public CompoundTag getPipeline() {
        return pipeline;
    }
}
